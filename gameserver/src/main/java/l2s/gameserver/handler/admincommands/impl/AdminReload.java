package l2s.gameserver.handler.admincommands.impl;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.Config;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.dao.OlympiadNobleDAO;
import l2s.gameserver.data.StringHolder;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.xml.holder.BuyListHolder;
import l2s.gameserver.data.xml.parser.MultiSellParser;
import l2s.gameserver.data.xml.parser.NpcParser;
import l2s.gameserver.data.xml.parser.PetDataParser;
import l2s.gameserver.data.xml.parser.ProductDataParser;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.olympiad.OlympiadDatabase;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.tables.FishTable;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.utils.Strings;

public class AdminReload implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_reload,
		admin_reload_config,
		admin_reload_multisell,
		admin_reload_gmaccess,
		admin_reload_htm,
		admin_reload_qs,
		admin_reload_qs_help,
		admin_reload_skills,
		admin_reload_npc,
		admin_reload_spawn,
		admin_reload_fish,
		admin_reload_abuse,
		admin_reload_translit,
		admin_reload_shops,
		admin_reload_static,
		admin_reload_pets,
		admin_reload_locale,
		admin_reload_nobles,
		admin_reload_im
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanReload)
			return false;

		switch(command)
		{
			case admin_reload:
				break;
			case admin_reload_config:
			{
				try
				{
					Config.load();
				}
				catch(Exception e)
				{
					activeChar.sendMessage("Error: " + e.getMessage() + "!");
					return false;
				}
				activeChar.sendMessage("Config reloaded!");
				break;
			}
			case admin_reload_multisell:
			{
				MultiSellParser.getInstance().reload();
				activeChar.sendMessage("Multisell list reloaded!");
				break;
			}
			case admin_reload_gmaccess:
			{
				try
				{
					Config.loadGMAccess();
					for(Player player : GameObjectsStorage.getAllPlayersForIterate())
						if(!Config.EVERYBODY_HAS_ADMIN_RIGHTS)
							player.setPlayerAccess(Config.gmlist.get(player.getObjectId()));
						else
							player.setPlayerAccess(Config.gmlist.get(new Integer(0)));
				}
				catch(Exception e)
				{
					return false;
				}
				activeChar.sendMessage("GMAccess reloaded!");
				break;
			}
			case admin_reload_htm:
			{
				HtmCache.getInstance().reload();
				activeChar.sendMessage("HTML cache clearned.");
				break;
			}
			case admin_reload_qs:
			{
				if(fullString.endsWith("all"))
					for(Player p : GameObjectsStorage.getAllPlayersForIterate())
						reloadQuestStates(p);
				else
				{
					GameObject t = activeChar.getTarget();

					if(t != null && t.isPlayer())
					{
						Player p = (Player) t;
						reloadQuestStates(p);
					}
					else
						reloadQuestStates(activeChar);
				}
				break;
			}
			case admin_reload_qs_help:
			{
				activeChar.sendMessage("");
				activeChar.sendMessage("Quest Help:");
				activeChar.sendMessage("reload_qs_help - This Message.");
				activeChar.sendMessage("reload_qs <selected target> - reload all quest states for target.");
				activeChar.sendMessage("reload_qs <no target or target is not player> - reload quests for self.");
				activeChar.sendMessage("reload_qs all - reload quests for all players in world.");
				activeChar.sendMessage("");
				break;
			}
			case admin_reload_skills:
			{
				SkillTable.getInstance().reload();
				break;
			}
			case admin_reload_npc:
			{
				NpcParser.getInstance().reload();
				break;
			}
			case admin_reload_spawn:
			{
				ThreadPoolManager.getInstance().execute(new RunnableImpl(){
					@Override
					public void runImpl() throws Exception
					{
						SpawnManager.getInstance().reloadAll();
					}
				});
				break;
			}
			case admin_reload_fish:
			{
				FishTable.getInstance().reload();
				break;
			}
			case admin_reload_abuse:
			{
				Config.abuseLoad();
				break;
			}
			case admin_reload_translit:
			{
				Strings.reload();
				break;
			}
			case admin_reload_shops:
			{
				BuyListHolder.reload();
				break;
			}
			case admin_reload_static:
			{
				//StaticObjectsTable.getInstance().reloadStaticObjects();
				break;
			}
			case admin_reload_pets:
			{
				PetDataParser.getInstance().reload();
				break;
			}
			case admin_reload_locale:
			{
				StringHolder.getInstance().reload();
				break;
			}
			case admin_reload_nobles:
			{
				OlympiadNobleDAO.getInstance().select();
				OlympiadDatabase.loadNoblesRank();
				break;
			}
			case admin_reload_im:
			{
				ProductDataParser.getInstance().reload();
				break;
			}
		}
		activeChar.sendPacket(new NpcHtmlMessagePacket(5).setFile("admin/reload.htm"));
		return true;
	}

	private void reloadQuestStates(Player p)
	{
		for(QuestState qs : p.getAllQuestsStates())
			p.removeQuestState(qs.getQuest().getName());
		Quest.restoreQuestStates(p);
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}