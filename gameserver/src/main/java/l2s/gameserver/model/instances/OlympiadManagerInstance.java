package l2s.gameserver.model.instances;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.olympiad.CompType;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.entity.olympiad.OlympiadDatabase;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExHeroListPacket;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExReceiveOlympiadPacket;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlympiadManagerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final Logger _log = LoggerFactory.getLogger(OlympiadManagerInstance.class);

	public OlympiadManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		if(Config.ENABLE_OLYMPIAD && getNpcId() == 31688)
			Olympiad.addOlympiadNpc(this);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(!Config.ENABLE_OLYMPIAD)
			return;

		StringTokenizer st = new StringTokenizer(command);
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("noble_manage"))
		{
			if(!Config.ENABLE_OLYMPIAD)
				return;

			if(!st.hasMoreTokens())
				return;

			int val = Integer.parseInt(st.nextToken());
			switch(val)
			{
				case 1:
					Olympiad.unRegisterNoble(player);
					showChatWindow(player, 0);
					break;
				case 2:
					if(Olympiad.isRegistered(player, false))
						showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "manager_noregister.htm");
					else if(Olympiad.isClassedBattlesAllowed())
						showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "manager_register_classed.htm", "<?members_count?>", Olympiad.getNoblesCount());
					else
						showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "manager_register_no_classed.htm", "<?members_count?>", Olympiad.getNoblesCount());
					break;
				case 4:
					Olympiad.registerNoble(player);
					break;
				case 6:
					int passes = Olympiad.getNoblessePasses(player);
					if(passes > 0)
					{
						player.getInventory().addItem(Config.ALT_OLY_COMP_RITEM, passes);
						player.sendPacket(SystemMessagePacket.obtainItems(Config.ALT_OLY_COMP_RITEM, passes, 0));
					}
					else
						showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "manager_nopoints.htm");
					break;
				case 7:
					MultiSellHolder.getInstance().SeparateAndSend(102, player, 0);
					break;
				case 9:
					MultiSellHolder.getInstance().SeparateAndSend(103, player, 0);
					break;
				case 11:
					if(!player.isHero())
						showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_weapon_no_hero.htm");
					else
					{
						for(int itemId : Olympiad.HERO_WEAPONS)
						{
							if(player.getInventory().getItemByItemId(itemId) != null)
							{
								showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_weapon_have.htm");
								return;
							}
						}
						showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_weapon_list.htm");
					}
					break;
				case 12:
					if(!player.isHero())
						showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_weapon_no_hero.htm");
					else
					{
						for(int itemId : Olympiad.HERO_WEAPONS)
						{
							if(player.getInventory().getItemByItemId(itemId) != null)
							{
								showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_weapon_have.htm");
								return;
							}
						}

						if(!st.hasMoreTokens())
							return;

						int weaponId = Integer.parseInt(st.nextToken());
						if(!ArrayUtils.contains(Olympiad.HERO_WEAPONS, weaponId))
							return; //Cheater!

						ItemFunctions.addItem(player, weaponId, 1, true);
						showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_weapon_give.htm");
					}
					break;
				case 13:
					if(player.isHero())
					{
						if(player.getInventory().getItemByItemId(Olympiad.HERO_WING_ID) != null)
							showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_circlet_have.htm");
						else
						{
							ItemFunctions.addItem(player, Olympiad.HERO_WING_ID, 1, true); //Wings of Destiny Circlet
							showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_circlet_give.htm");
						}
					}
					else
						showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_circlet_no_hero.htm");
					break;
				case 14:
					if(player.isHero())
					{
						if(player.getInventory().getItemByItemId(Olympiad.HERO_CLOAK_ID) != null)
							showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_cloak_have.htm");
						else
						{
							ItemFunctions.addItem(player, Olympiad.HERO_CLOAK_ID, 1, true); //Hero's Cloak
							showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_cloak_hero_give.htm");
						}
					}
					else if(player.isNoble())
					{
						if(!Olympiad.isEnableCloak())
						{
							showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_cloak_not_yet.htm");
							return;
						}
						int rank = Olympiad.getRank(player);
						if(rank == 2 || rank == 3)
						{
							if(player.getInventory().getItemByItemId(Olympiad.FAME_CLOAK_ID) != null)
								showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_cloak_have.htm");
							else
							{
								ItemFunctions.addItem(player, Olympiad.FAME_CLOAK_ID, 1, true); //Glorious Cloak
								showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_cloak_fame_give.htm");
							}
						}
						else
							showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_cloak_no_hero.htm");
					}
					else
						showChatWindow(player, Olympiad.OLYMPIAD_HTML_PATH + "monument_cloak_no_hero.htm");
					break;
				default:
					_log.warn("Olympiad System: Couldnt send packet for request " + val);
					break;
			}
		}
		else if(cmd.equalsIgnoreCase("manage"))
		{
			if(!Config.ENABLE_OLYMPIAD)
				return;

			if(!st.hasMoreTokens())
				return;

			int val = Integer.parseInt(st.nextToken());
			NpcHtmlMessagePacket reply = new NpcHtmlMessagePacket(player, this);
			switch(val)
			{
				case 1:
					if(!Olympiad.inCompPeriod() || Olympiad.isOlympiadEnd())
					{
						player.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
						return;
					}
					player.sendPacket(new ExReceiveOlympiadPacket.MatchList());
					break;
				case 2:
					if(!st.hasMoreTokens())
						return;

					// for example >> Olympiad 2 88
					int classId = Integer.parseInt(st.nextToken());
					if(classId >= 88)
					{
						reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_ranking.htm");

						List<String> names = OlympiadDatabase.getClassLeaderBoard(classId);

						int index = 1;
						for(String name : names)
						{
							reply.replace("%place" + index + "%", String.valueOf(index));
							reply.replace("%rank" + index + "%", name);
							index++;
							if(index > 10)
								break;
						}
						for(; index <= 10; index++)
						{
							reply.replace("%place" + index + "%", "");
							reply.replace("%rank" + index + "%", "");
						}

						player.sendPacket(reply);
					}
					// TODO Send player each class rank
					break;
				case 3:
					if(!Config.ENABLE_OLYMPIAD_SPECTATING)
						break;

					Olympiad.addSpectator(Integer.parseInt(command.substring(11)), player);
					break;
				case 4:
					player.sendPacket(new ExHeroListPacket());
					break;
				case 5:
					if(Hero.getInstance().isInactiveHero(player.getObjectId()))
					{
						Hero.getInstance().activateHero(player);
						reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "monument_give_hero.htm");
						switch(ClassId.VALUES[player.getBaseClassId()])
						{
							case SIGEL_PHOENIX_KNIGHT:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_SIGEL_PHOENIX_KNIGHT_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case SIGEL_HELL_KNIGHT:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_SIGEL_HELL_KNIGHT_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case SIGEL_EVAS_TEMPLAR:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_SIGEL_EVAS_TEMPLAR_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case SIGEL_SHILLIEN_TEMPLAR:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_SIGEL_SHILLIEN_TEMPLAR_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case TYR_DUELIST:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_TYRR_DUELIST_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case TYR_DREADNOUGHT:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_TYRR_DREADNOUGHT_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case TYR_TITAN:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_TYRR_TITAN_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case TYR_GRAND_KHAVATARI:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_TYRR_GRAND_KHAVATARI_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case TYR_MAESTRO:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_TYRR_MAESTRO_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case TYR_DOOMBRINGER:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_TYRR_DOOMBRINGER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case OTHELL_ADVENTURER:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_OTHELL_ADVENTURER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case OTHELL_WIND_RIDER:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_OTHELL_WIND_RIDER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case OTHELL_GHOST_HUNTER:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_OTHELL_GHOST_HUNTER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case OTHELL_FORTUNE_SEEKER:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_OTHELL_FORTUNE_SEEKER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case YR_SAGITTARIUS:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_YUL_SAGITTARIUS_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case YR_MOONLIGHT_SENTINEL:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_YUL_MOONLIGHT_SENTINEL_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case YR_GHOST_SENTINEL:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_YUL_GHOST_SENTINEL_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case YR_TRICKSTER:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_YUL_TRICKSTER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case FEOH_ARCHMAGE:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_FEOH_ARCHMAGE_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case FEOH_SOULTAKER:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_FEOH_SOULTAKER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case FEOH_MYSTIC_MUSE:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_FEOH_MYSTIC_MUSE_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case FEOH_STORM_SCREAMER:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_FEOH_STORM_SCREAMER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case FEOH_SOUL_HOUND:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_FEOH_SOUL_HOUND_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case ISS_HIEROPHANT:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_ISS_HIEROPHANT_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case ISS_SWORD_MUSE:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_ISS_SWORD_MUSE_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case ISS_SPECTRAL_DANCER:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_ISS_SPECTRAL_DANCER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case ISS_DOMINATOR:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_ISS_DOMINATOR_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case ISS_DOOMCRYER:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_ISS_DOOMCRYER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case WYNN_ARCANA_LORD:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_WYNN_ARCANA_LORD_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case WYNN_ELEMENTAL_MASTER:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_WYNN_ELEMENTAL_MASTER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case WYNN_SPECTRAL_MASTER:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_WYNN_SPECTRAL_MASTER_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case AEORE_CARDINAL:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_AEORE_CARDINAL_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case AEORE_EVAS_SAINT:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_AEORE_EVAS_SAINT_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
							case AEORE_SHILLIEN_SAINT:
								player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_BECOME_THE_HERO_OF_THE_AEORE_SHILLIEN_SAINT_CLASS_CONGRATULATIONS, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, player.getName()));
								break;
						}
					}
					else
						reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "monument_dont_hero.htm");
					player.sendPacket(reply);
					break;
				default:
					_log.warn("Olympiad System: Couldnt send packet for request " + val);
					break;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		String fileName = Olympiad.OLYMPIAD_HTML_PATH;
		int npcId = getNpcId();
		switch(npcId)
		{
			case 31688: // Grand Olympiad Manager
				fileName += "manager";
				break;
			default: // Monument of Heroes
				fileName += "monument";
				break;
		}
		if(player.isNoble())
			fileName += "_n";
		if(val > 0)
			fileName += "-" + val;
		fileName += ".htm";
		player.sendPacket(new NpcHtmlMessagePacket(player, this, fileName, val));
	}
}