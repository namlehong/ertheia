package handler.bbs.custom;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;

import l2s.commons.dbutils.DbUtils;
import l2s.commons.lang.ArrayUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.handler.bbs.CommunityBoardManager;
import l2s.gameserver.handler.bbs.ICommunityBoardHandler;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.network.l2.s2c.ShowBoardPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.tables.SkillTreeTable;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.Strings;

public class CommunityBuffer implements ScriptFile, ICommunityBoardHandler
{
	private static final TIntObjectMap<Map<String, List<Skill>>> PLAYER_BUFF_SETS = new TIntObjectHashMap<Map<String, List<Skill>>>();
	private static final TIntObjectMap<Skill> AVAILABLE_BUFFS = new TIntObjectHashMap<Skill>();
	private static final List<Skill> ALL_BUFFS_SET = new ArrayList<Skill>();

	@Override
	public void onLoad()
	{
		PLAYER_BUFF_SETS.clear();
		AVAILABLE_BUFFS.clear();
		ALL_BUFFS_SET.clear();

		cleanUP();

		for(int i = 0; i < Config.BBS_BUFF_SET.length; i++)
		{
			int id = Config.BBS_BUFF_SET[i][0];
			int lvl = SkillTable.getInstance().getBaseLevel(id);
			int enchantGroup = Config.BBS_BUFF_SET[i][1];
			Skill skill = SkillTable.getInstance().getInfo(id, lvl);

			if(enchantGroup > 0)
			{
				lvl = SkillTreeTable.convertEnchantLevel(lvl, enchantGroup * 100 + skill.getEnchantLevelCount(), skill.getEnchantLevelCount());
				Skill enchantedSkill = SkillTable.getInstance().getInfo(id, lvl);
				if(enchantedSkill != null)
					skill = enchantedSkill;
			}

			AVAILABLE_BUFFS.put(id, skill);
			ALL_BUFFS_SET.add(skill);
		}

		//_log.info("CommunityBuffer: Loaded " + AVAILABLE_BUFFS.size() + " AVAILABLE_BUFFS count.]");
		if(Config.COMMUNITYBOARD_ENABLED && Config.BBS_BUFFER_ENABLED)
			CommunityBoardManager.getInstance().registerHandler(this);
	}

	@Override
	public void onReload()
	{
		PLAYER_BUFF_SETS.clear();
		AVAILABLE_BUFFS.clear();
		ALL_BUFFS_SET.clear();
		if(Config.COMMUNITYBOARD_ENABLED && Config.BBS_BUFFER_ENABLED)
			CommunityBoardManager.getInstance().removeHandler(this);
	}

	@Override
	public void onShutdown()
	{}

	@Override
	public String[] getBypassCommands()
	{
		return new String[] { "_cbbsbuffer", "_bbsrestore", "_bbscancel"};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		if(!CommunityFunctions.checkPlayer(player))
		{
			if(player.isLangRus())
			{
				player.sendMessage("Не соблюдены условия для использование данной функции.");
				return;
			}	
			else
			{
				player.sendMessage("You are not allowed to use this action in you current stance.");
				return;
			}
		}	
		String html = HtmCache.getInstance().getNotNull("scripts/handler/bbs/pages/buff.htm", player);
		String content = "";
		
		if(bypass.startsWith("_bbsrestore"))
		{
			if(player.isInCombat())
			{
				onBypassCommand(player, "_cbbsbuffer 0");
				return;			
			}
			player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());
			onBypassCommand(player, "_cbbsbuffer 0");
			return;
		}
		if(bypass.startsWith("_bbscancel"))
		{
			for(Effect eff : player.getEffectList().getEffects())
			{
				if(!eff.isOffensive() && !eff.getSkill().isMusic() && eff.getSkill().isSelfDispellable() && !eff.getSkill().hasEffect(EffectType.Transformation) && !eff.getSkill().hasEffect(EffectType.Hourglass) && !player.isSpecialEffect(eff.getSkill()))
					eff.exit();
			}
			onBypassCommand(player, "_cbbsbuffer 0");
			return;			
		}		
		
		if(bypass.startsWith("_cbbsbuffer"))
		{
			StringTokenizer bf = new StringTokenizer(bypass, " ");
			bf.nextToken();
			String[] arg = new String[0];
			while(bf.hasMoreTokens())
				arg = ArrayUtils.add(arg, bf.nextToken());

			content = BuffList(arg, player);
		}
		html = html.replace("%content%", content);
		ShowBoardPacket.separateAndSend(html, player);
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{}

	public static String htmlButton(String value, int width, int height, Object... args)
	{
		String action = "bypass _cbbsbuffer";
		for(Object arg : args)
			action += " " + arg.toString();
		return HtmlUtils.htmlButton(value, action, width, height);
	}

	//public static String htmlButton(String value, int width, Object... args)
	//{
	//	return htmlButton(value, width, 22, args);
	//}

	private static boolean takeItemsAndBuff(Playable player, List<Skill> buffs, boolean toPet)
	{
		int needCount = Config.BBS_BUFF_ITEM_COUNT * buffs.size();

		if(player.getLevel() > Config.BBS_BUFF_FREE_LVL && Functions.getItemCount(player, Config.BBS_BUFF_ITEM_ID) < needCount)
			return false;
		
		if(!toPet)
		{
			Playable target = player;
			if(target != null)
			{
				try
				{
					if(player.getLevel() > Config.BBS_BUFF_FREE_LVL)
						Functions.removeItem(player, Config.BBS_BUFF_ITEM_ID, needCount);
				}
				catch(Exception e)
				{
					return false;
				}
				for(Skill nextbuff : buffs)
				{
					if(nextbuff.isMusic())
						//songs and dances
						nextbuff.getEffects(target, target, false, Config.BBS_BUFF_TIME_MUSIC * 1000, Config.BBS_BUFF_TIME_MOD_MUSIC, false);
					//for special skill that last less than 20min	
					else if(nextbuff.getId() == 1355 || nextbuff.getId() == 1356 || nextbuff.getId() == 1357 || nextbuff.getId() == 1363 || nextbuff.getId() == 1413 || nextbuff.getId() == 1414)
						nextbuff.getEffects(target, target, false, Config.BBS_BUFF_TIME_SPECIAL * 1000, Config.BBS_BUFF_TIME_MOD_SPECIAL, false);
					//normal buff	
					else
						nextbuff.getEffects(target, target, false, Config.BBS_BUFF_TIME * 1000, Config.BBS_BUFF_TIME_MOD, false);
					try
					{
						Thread.sleep(10L);
					}
					catch(Exception e) {}							
				}		
			}
		}
		else
		{
			for(Servitor target2 : player.getServitors())
			{
				try
				{
					if(player.getLevel() > Config.BBS_BUFF_FREE_LVL)
						Functions.removeItem(player, Config.BBS_BUFF_ITEM_ID, needCount);
				}
				catch(Exception e)
				{
					return false;
				}
				for(Skill nextbuff : buffs)
				{
					if(nextbuff.isMusic())
						//songs and dances
						nextbuff.getEffects(target2, target2, false, Config.BBS_BUFF_TIME_MUSIC * 1000, Config.BBS_BUFF_TIME_MOD_MUSIC, false);
					//for special skill that last less than 20min	
					else if(nextbuff.getId() == 1355 || nextbuff.getId() == 1356 || nextbuff.getId() == 1357 || nextbuff.getId() == 1363 || nextbuff.getId() == 1413 || nextbuff.getId() == 1414)
						nextbuff.getEffects(target2, target2, false, Config.BBS_BUFF_TIME_SPECIAL * 1000, Config.BBS_BUFF_TIME_MOD_SPECIAL, false);
					//normal buff	
					else
						nextbuff.getEffects(target2, target2, false, Config.BBS_BUFF_TIME * 1000, Config.BBS_BUFF_TIME_MOD, false);
				}
			}			
		}
		return true;
	}

	private static int getSkillIdx(List<Skill> set, int skill_id)
	{
		for(int i = 0; i < set.size(); i++)
		{
			if(set.get(i).getId() == skill_id)
				return i;
		}
		return -1;
	}

	private static String pageGet(Player player, String[] var)
	{
		boolean buffallset = var[1].equalsIgnoreCase("0") || var[1].equalsIgnoreCase("2");
		String[] var2 = new String[var.length - (buffallset ? 1 : 2)];
		System.arraycopy(var, var.length - var2.length, var2, 0, var2.length);
		List<Skill> buffs_to_buff = new ArrayList<Skill>();

		if(buffallset)
		{
			String[] a = var[2].split("_");
			int listid = a[0].equalsIgnoreCase("2") ? player.getObjectId() : 0;
			String name = Strings.joinStrings(" ", var, 3);
			String localized_name = name;
			Map<String, List<Skill>> sets = getBuffSets(listid);
			if(listid == 0)
			{
				String[] langs = name.split(";");
				if(langs.length == 2)
					localized_name = langs[player.isLangRus() ? 1 : 0];
			}
			if(!sets.containsKey(name))
			{
				if(player.isLangRus())
					return "<center><font color=FF3355>Набор '" + localized_name + "' не найден</font></center>";
				else
					return "<center><font color=FF3355>'" + localized_name + "' set not found</font></center>";
			}
			buffs_to_buff.addAll(sets.get(name));
		}
		else
			buffs_to_buff.add(AVAILABLE_BUFFS.get(Integer.parseInt(var[2])));

		if(!takeItemsAndBuff(player, buffs_to_buff, var[1].equalsIgnoreCase("2")))
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);

		return pageList(player, var2);
	}

	private static final int pageRows = 9;
	private static final int pageCols = Config.MAX_BUFF_PER_SET;
	private static final int pageMax = pageRows * pageCols;

	private static String pageList(Player player, String[] var)
	{
		String[] a = var[1].split("_");
		int pageIdx = Integer.parseInt(a[1]);
		boolean _all = a[0].equalsIgnoreCase("0");
		int listid = a[0].equalsIgnoreCase("2") ? player.getObjectId() : 0;
		String name = "Все баффы";
		if(!player.isLangRus())
			name = "All buffs";
		String param1 = Strings.joinStrings(" ", var, 1);
		List<Skill> set = ALL_BUFFS_SET;

		String localized_name = name;
		if(!_all)
		{
			Map<String, List<Skill>> sets = getBuffSets(listid);
			name = Strings.joinStrings(" ", var, 2);
			localized_name = name;
			if(listid == 0)
			{
				String[] langs = name.split(";");
				if(langs.length == 2)
					localized_name = langs[player.isLangRus() ? 1 : 0];
			}

			if(!sets.containsKey(name))
			{
				if(player.isLangRus())
					return "<center><font color=FF3355>Набор '" + localized_name + "' не найден</font></center>";
				else
					return "<center><font color=FF3355>'" + localized_name + "' set not found</font></center>";
			}

			set = sets.get(name);
		}

		String pagePrev = pageIdx == 0 ? "" : htmlButton("&$543;", 80, 22, "list", param1.replaceFirst(var[1], a[0] + "_" + (pageIdx - 1)));
		String pageNext = "";
		List<String> tds = new ArrayList<String>();

		for(int i = pageIdx * pageMax; i < set.size(); i++)
		{
			if(tds.size() == pageMax)
			{
				pageNext = htmlButton("&$544;", 80, 22, "list", param1.replaceFirst(var[1], a[0] + "_" + (pageIdx + 1)));
				break;
			}
			Skill _buff = set.get(i);
			String buff_str = "<td width=32 valign=top><img src=\"" + _buff.getIcon() + "\" width=32 height=32></td>";
			buff_str += "<td>" + htmlButton("$", 22, 32, "get", 1, _buff.getId(), param1) + "</td>";
			if(player.isLangRus())
				buff_str += "<td><font color=3399FF>" + _buff.getName(player) + "</font><br1><font color=LEVEL> Уровень " + _buff.getLevel() + "</font></td>";
			else
				buff_str += "<td><font color=3399FF>" + _buff.getName(player) + "</font><br1><font color=LEVEL> Level " + _buff.getLevel() + "</font></td>";
			tds.add(buff_str);
		}

		String result = "";

		int cost;
		if(player.getLevel() > Config.BBS_BUFF_FREE_LVL)
			cost = set.size() * Config.BBS_BUFF_ITEM_COUNT;
		else
			cost = 0;
		result += "<tr>";
		String all = "All";
		if(player.isLangRus())
			all = "Все";
		result += "<td align=center><font color=33FF55>" + localized_name + (!_all && pageNext.isEmpty() && pagePrev.isEmpty() ? " [" + all +": " + cost + " " + HtmlUtils.htmlItemName(Config.BBS_BUFF_ITEM_ID) + "]" : "") + "</font></td>";
		if(!_all && pageNext.isEmpty() && pagePrev.isEmpty())
		{
			if(player.isLangRus())
			{
				result += "<td width=70>Себе: ";
				if(player.getServitors().length > 0)
					result += "<br>Питомцу: ";
			}
			else
			{
				result += "<td width=70>For me: ";
				if(player.getServitors().length > 0)
					result += "<br>For pet: ";
			}
			result += "</td>";

			result += "<td>";
			if(player.isLangRus())
			{
				result += htmlButton("Все", 50, 22, "get", 0, param1);
				if(player.getServitors().length > 0)
					result += "<br>" + htmlButton("Все", 50, 22, "get", 2, param1);
			}
			else
			{
				result += htmlButton("All", 50, 22, "get", 0, param1);
				if(player.getServitors().length > 0)
					result += "<br>" + htmlButton("All", 50, 22, "get", 2, param1);
			}
			result += "</td>";
		}
		if(listid != 0)
			if(player.isLangRus())
				result += "<td align=center>" + htmlButton("Редактировать", 125, 22, "editset", "edit", name) + "</td>";
			else
				result += "<td align=center>" + htmlButton("Edit", 125, 22, "editset", "edit", name) + "</td>";
		if(!pagePrev.isEmpty() || !pageNext.isEmpty())
		{
			result += "<td width=90 align=center>" + pagePrev + "</td>";
			result += "<td width=60 align=center>Page: " + (pageIdx + 1) + "</td>";
			result += "<td width=90 align=center>" + pageNext + "</td>";
		}
		result += "</tr>";

		if(tds.size() > 0)
		{
			result += "<br><img src=\"L2UI.SquareWhite\" width=600 height=1><br>";
			result += formatTable(tds, pageCols, false);
		}

		return result;
	}

	private static String pageEdit(Player player, String[] var)
	{
		int charId = player.getObjectId();
		Map<String, List<Skill>> sets = getBuffSets(charId);
		String name = "";

		if(var[1].equalsIgnoreCase("del"))
		{
			//Log.add("BUFF\tУдален набор: " + name, "service_buff", player);
			name = Strings.joinStrings(" ", var, 2);
			deleteBuffSet(charId, name);
			sets.remove(name);
			return pageMain(player);
		}

		String result = "";
		List<String> tds = new ArrayList<String>();

		if(var[1].equalsIgnoreCase("delconf"))
		{
			name = Strings.joinStrings(" ", var, 2);
			if(player.isLangRus())
				result += "<center><font color=FF3355>Вы действительно желаете удалить набор: " + name + "?</font><br>";
			else
				result += "<center><font color=FF3355>Are you sure you want to delete a set: " + name + "?</font><br>";			
			if(player.isLangRus())
			{
				result += htmlButton("ДА", 50, 22, "editset", "del", name);
				result += "<br>";
				result += htmlButton("НЕТ", 50, 22, "editset", "edit", name);
			}
			else
			{
				result += htmlButton("YES", 50, 22, "editset", "del", name);
				result += "<br>";
				result += htmlButton("NO", 50, 22, "editset", "edit", name);
			}

			//result += formatTable(tds, 2, false);
			return result;
		}

		List<Skill> set = null;

		if(var[1].equalsIgnoreCase("new"))
		{
			if(sets.size() >= Config.MAX_SETS_PER_CHAR)
			{
				if(player.isLangRus())
					return "<center><font color=FF3355>Вы достигли лимита наборов</font></center>";
				else
					return "<center><font color=FF3355>You have reached the limit set</font></center>";
			}

			name = trimHtml(Strings.joinStrings(" ", var, 2));
			if(name.length() > 16)
				name = name.substring(0, 15);
			if(name.isEmpty() || name.equalsIgnoreCase(" "))
			{
				if(player.isLangRus())
					return "<center><font color=FF3355>Необходимо указать имя набора</font></center>";
				else
					return "<center><font color=FF3355>You must specify the name of the set</font></center>";
			}
			set = new ArrayList<Skill>();
			sets.put(name, set);
			updateBuffSet(charId, name, set);
			//Log.add("BUFF\tСоздан набор: " + name, "service_buff", player);
		}
		else if(var[1].equalsIgnoreCase("edit"))
		{
			name = Strings.joinStrings(" ", var, 2);
			if(!sets.containsKey(name))
				if(player.isLangRus())
					return "<center><font color=FF3355>Набор '" + name + "' не найден</font></center>";
				else
					return "<center><font color=FF3355>'" + name + "' set not found</font></center>";
			set = sets.get(name);
		}
		else if(var[1].equalsIgnoreCase("rem"))
		{
			name = Strings.joinStrings(" ", var, 3);
			if(!sets.containsKey(name))
				if(player.isLangRus())
					return "<center><font color=FF3355>Набор '" + name + "' не найден</font></center>";
				else
					return "<center><font color=FF3355>'" + name + "' set not found</font></center>";
			set = sets.get(name);
			int skill_to_remove = Integer.valueOf(var[2]);
			int idx = getSkillIdx(set, skill_to_remove);
			if(idx != -1)
				set.remove(idx);
			updateBuffSet(charId, name, set);
		}
		else if(var[1].equalsIgnoreCase("add"))
		{
			name = Strings.joinStrings(" ", var, var[2].equalsIgnoreCase("x") ? 4 : 3);
			if(!sets.containsKey(name))
				if(player.isLangRus())
					return "<center><font color=FF3355>Набор '" + name + "' не найден</font></center>";
				else
					return "<center><font color=FF3355>'" + name + "' set not found</font></center>";
			set = sets.get(name);
			if(var[2].equalsIgnoreCase("x"))
			{
				set.add(AVAILABLE_BUFFS.get(Integer.valueOf(var[3])));
				updateBuffSet(charId, name, set);
			}
			else
			{
				int pageIdx = Integer.valueOf(var[2]);
				String pagePrev = pageIdx == 0 ? "" : htmlButton("&$543;", 80, 22, "editset", "add", pageIdx - 1, name);
				String pageNext = "";
				for(int i = pageIdx * pageMax; i < ALL_BUFFS_SET.size(); i++)
				{
					if(tds.size() == pageMax)
					{
						pageNext = htmlButton("&$544;", 80, 22, "editset", "add", pageIdx + 1, name);
						break;
					}
					Skill _buff = ALL_BUFFS_SET.get(i);
					int idx = getSkillIdx(set, _buff.getId());
					if(idx != -1)
						continue;
					String buff_str = "<td width=32 valign=top><img src=\"" + _buff.getIcon() + "\" width=32 height=32></td>";
					buff_str += "<td>" + htmlButton(">", 22, 32, "editset", "add", "x", _buff.getId(), name) + "</td>";
					if(player.isLangRus())
						buff_str += "<td fixwidth=140><font color=3399FF>" + _buff.getName(player) + "</font><br1><font color=LEVEL> Уровень " + _buff.getLevel() + "</font></td>";
					else
						buff_str += "<td fixwidth=140><font color=3399FF>" + _buff.getName(player) + "</font><br1><font color=LEVEL> Level " + _buff.getLevel() + "</font></td>";
					tds.add(buff_str);
				}

				result += "<tr>";
				if(player.isLangRus())
					result += "<td width=300 align=center><font color=33FF55>Редактирование набора: " + name + "</font></td>";
				else
					result += "<td width=300 align=center><font color=33FF55>Set editing: " + name + "</font></td>";
				if(!pagePrev.isEmpty() || !pageNext.isEmpty())
				{
					result += "<td width=90 align=center>" + pagePrev + "</td>";
					result += "<td width=60 align=center>Page: " + (pageIdx + 1) + "</td>";
					result += "<td width=90 align=center>" + pageNext + "</td>";
				}
				result += "</tr>";

				result += "<img src=\"L2UI.SquareWhite\" width=600 height=1><br>";
				result += formatTable(tds, pageCols, false);

				return result;
			}
		}
		else
			return pageMain(player);

		for(int i = 0; i < set.size(); i++)
		{
			Skill _buff = set.get(i);
			String buff_str = "<td width=32 valign=top><img src=\"" + _buff.getIcon() + "\" width=32 height=32></td>";
			buff_str += "<td>" + htmlButton("<", 22, 32, "editset", "rem", _buff.getId(), name) + "</td>";
			if(player.isLangRus())
				buff_str += "<td><font color=3399FF>" + _buff.getName(player) + "</font><br1><font color=LEVEL> Уровень " + _buff.getLevel() + "</font></td>";
			else
				buff_str += "<td0><font color=3399FF>" + _buff.getName(player) + "</font><br1><font color=LEVEL> Level " + _buff.getLevel() + "</font></td>";
			tds.add(buff_str);
		}

		result += "<tr>";
		if(player.isLangRus())
		{
			result += "<td align=center><font color=33FF55>Редактирование: " + name + "</font></td>";
			if(set.size() < pageMax)
				result += "<td width=150 align=center>" + htmlButton("Добавить бафф", 130, 22, "editset", "add", 0, name) + "</td>";
			result += "<td width=150 align=center>" + htmlButton("Удалить набор", 130, 22, "editset", "delconf", name) + "</td>";
			result += "<td width=90 align=center>" + htmlButton("Возврат", 80, 22, "list", "2_0", name) + "</td>";
			result += "</tr>";
		}
		else
		{
			result += "<td align=center><font color=33FF55>Editing: " + name + "</font></td>";
			if(set.size() < pageMax)
				result += "<td width=150 align=center>" + htmlButton("Add buff", 130, 22, "editset", "add", 0, name) + "</td>";
			result += "<td width=150 align=center>" + htmlButton("Delete set", 130, 22, "editset", "delconf", name) + "</td>";
			result += "<td width=90 align=center>" + htmlButton("Return", 80, 22, "list", "2_0", name) + "</td>";
			result += "</tr>";
		}

		if(tds.size() > 0)
		{
			result += "<img src=\"L2UI.SquareWhite\" width=600 height=1><br>";
			result += formatTable(tds, pageCols, false);
		}

		return result;
	}

	private static String pageMain(Player player)
	{
		String result = "<tr>";

		result += "<td>";
		if(player.isLangRus())
			result += htmlButton("Все Баффы", 150, 22, "list", "0_0") + "</td>";
		else
			result += htmlButton("All Buffs", 150, 22, "list", "0_0") + "</td>";
		Map<String, List<Skill>> sets = getBuffSets(0);
		int i = 1;
		for(String setname : sets.keySet())
		{
			String name = setname;
			String[] langs = setname.split(";");
			if(langs.length == 2)
				name = langs[player.isLangRus() ? 1 : 0];
			if(i == 4)
				result += "</tr><tr>";
			result += "<td>";
			result += htmlButton(name, 150, 22, "list", "1_0", setname) + "<br>";
			result += "</td>";
			if(i == 4)
				i = 1;
			else
				i++;
			
		}
		result += "</tr>";	
		if(player.isLangRus())
			result += "<tr><td>Ваши наборы:</td></tr>";
		else
			result += "<tr><td>Your sets:</td></tr>";
		sets = getBuffSets(player.getObjectId());
		for(String setname : sets.keySet())
		{
			result += "<tr><td>";
			result += htmlButton(setname, 175, 22, "list", "2_0", setname) + "<br>";
			result += "</td></tr>";
		}	
		if(player.isLangRus())
			result += "<tr><td><edit var=\"name\" width=125></td><td>" + htmlButton("Новый", 50, 22, "editset", "new", "$name") + "</td></tr>";
		else
			result += "<tr><td><edit var=\"name\" width=125></td><td>" + htmlButton("New", 50, 22, "editset", "new", "$name") + "</td></tr>";

		//result += "<tr>";
		return result;
	}

	public String BuffList(String[] var, Player player)
	{
		if(player.isLangRus())
		{
			if(player.isInOlympiadMode())
				return "Эта функция недоступна на олимпиаде";
			if(player.isChaosFestivalParticipant())
				return "Эта функция недоступна во время Фестиваля Хаоса";
			if(player.isInCombat())
				return "Эта функция недоступна во время боя";
		}
		else
		{
			if(player.isInOlympiadMode())
				return "This feature is not available at the Olympiad Game";
			if(player.isChaosFestivalParticipant())
				return "This feature is not available at the Chaos Festival";
			if(player.isInCombat())
				return "This feature is not available during the battle";
		}

		if(var[0].equalsIgnoreCase("get"))
			return pageGet(player, var);

		if(var[0].equalsIgnoreCase("list"))
			return pageList(player, var);

		if(var[0].equalsIgnoreCase("editset") && var.length > 1)
			return pageEdit(player, var);

		return pageMain(player);
	}

	private static synchronized Map<String, List<Skill>> getBuffSets(int charId)
	{
		if(PLAYER_BUFF_SETS.containsKey(charId))
			return PLAYER_BUFF_SETS.get(charId);

		Map<String, List<Skill>> _new = loadBuffSets(charId);
		PLAYER_BUFF_SETS.put(charId, _new);
		return _new;
	}

	private static void updateBuffSet(int charId, String setname, List<Skill> _set)
	{
		String skills = _set.size() == 0 ? "" : String.valueOf(_set.get(0).getId());
		for(Skill skill : _set)
			skills += "," + skill.getId();

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO bbs_buffs (char_id,name,skills) VALUES (?,?,?)");
			statement.setInt(1, charId);
			statement.setString(2, setname);
			statement.setString(3, skills);
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	private static void deleteBuffSet(int charId, String setname)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM bbs_buffs WHERE char_id=? AND name=?");
			statement.setInt(1, charId);
			statement.setString(2, setname);
			statement.execute();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	private static Map<String, List<Skill>> loadBuffSets(int charId)
	{
		Map<String, List<Skill>> result = new HashMap<String, List<Skill>>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT name,skills FROM bbs_buffs WHERE char_id=?");
			statement.setInt(1, charId);
			rset = statement.executeQuery();
			while(rset.next())
			{
				List<Skill> next_set = new ArrayList<Skill>();
				String skills = rset.getString("skills");
				if(skills != null && !skills.isEmpty())
				{
					if(!skills.contains(","))
						next_set.add(AVAILABLE_BUFFS.get(Integer.parseInt(skills)));
					else
					{
						String[] skill_ids = skills.split(",");
						for(String skill_id : skill_ids)
							if(!skill_id.isEmpty())
								next_set.add(AVAILABLE_BUFFS.get(Integer.parseInt(skill_id)));
					}
				}

				result.put(rset.getString("name"), next_set);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return result;
	}

	private static void cleanUP()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM `bbs_buffs` WHERE char_id != 0 AND char_id NOT IN(SELECT obj_id FROM characters);");
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	private static String formatTable(Collection<String> tds, int rows, boolean appendTD)
	{
		String result = "";
		int i = 0;
		for(String td : tds)
		{
			if(i == 0)
				result += "<tr>";
			result += appendTD ? "<td>" + td + "</td>" : td;
			i++;
			if(i == rows)
			{
				result += "</tr>";
				i = 0;
			}
		}
		if(i > 0 && i < rows)
		{
			while(i < rows)
			{
				result += "<td></td>";
				i++;
			}
			result += "</tr>";
		}
		return result;
	}

	/**
	 * кроме обычного trim, заменяет кавычки на нестандартные UTF-8, удяляет ВСЕ двойные пробелы, убирает символы <>
	 */
	private static String trimHtml(String s)
	{
		int i;
		s = s.trim().replaceAll("\"", "״").replaceAll("'", "´").replaceAll("<", "").replaceAll(">", "");
		do
		{
			i = s.length();
			s = s.replaceAll("  ", " ");
		}
		while(i > s.length());

		return s;
	}
}