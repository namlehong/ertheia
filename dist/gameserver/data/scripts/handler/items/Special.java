package handler.items;

import org.apache.commons.lang3.ArrayUtils;

import l2s.gameserver.data.xml.holder.InstantZoneHolder;
import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.InstantZone;
import l2s.gameserver.utils.Location;
import bosses.AntharasManager;
import bosses.ValakasManager;
import quests._464_Oath;
import quests._466_PlacingMySmallPower;
import quests._480_AnotherLegacyOfCrumaTower;
import quests._10301_ShadowOfTerrorBlackishRedFog;
import quests._10304_ForTheForgottenHeroes;
import quests._10352_LegacyofCrumaTower;

public class Special extends SimpleItemHandler
{
	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl)
	{
		int itemId = item.getItemId();

		switch(itemId)
		{
			//Key of Enigma
			case 8060:
				return use8060(player, ctrl);
			//Dewdrop of Destruction
			case 8556:
				return use8556(player, ctrl);
			//DestroyedDarknessFragmentPowder -> DestroyedLightFragmentPowder
			case 13853:
				return use13853(player, ctrl);
			//Holy Water for SSQ 2nd quest
			case 13808:
				return use13808(player, ctrl);
			//Court Mag Staff for SSQ 2nd quest
			case 13809:
				return use13809(player, ctrl);
			case 14835:
				return use14835(player, ctrl);
			//Strongbox of Promise
			case 15537:
				return use15537(player, ctrl);
			case 21899:
				return use21899(player, ctrl);
			case 21900:
				return use21900(player, ctrl);
			case 21901:
				return use21901(player, ctrl);
			case 21902:
				return use21902(player, ctrl);
			case 21903:
				return use21903(player, ctrl);
			case 21904:
				return use21904(player, ctrl);
			//Antharas Blood Crystal
			case 17268:
				return use17268(player, ctrl);
			case 17619: //cruma quest
				return use17619(player, ctrl);
			case 17604: //megameld quest
				return use17604(player, ctrl);
			case 34033:
				return use34033(player, ctrl);
			case 17603:
				return use17603(player, ctrl);
			case 35556:
				return useKartiaTicket(player, ctrl, item, 205);
			case 35557:
				return useKartiaTicket(player, ctrl, item, 206);
			case 35558:
				return useKartiaTicket(player, ctrl, item, 207);
			case 35559:
				return useKartiaTicket(player, ctrl, item, 208);
			case 35560:
				return useKartiaTicket(player, ctrl, item, 209);
			case 35561:
				return useKartiaTicket(player, ctrl, item, 210);
			default:
				return false;
		}
	}

	//Key of Enigma
	private boolean use8060(Player player, boolean ctrl)
	{
		if(Functions.removeItem(player, 8058, 1) == 1)
		{
			Functions.addItem(player, 8059, 1);
			return true;
		}
		return false;
	}

	//Dewdrop of Destruction
	private boolean use8556(Player player, boolean ctrl)
	{
		int[] npcs = {29048, 29049};

		GameObject t = player.getTarget();
		if(t == null || !t.isNpc() || !ArrayUtils.contains(npcs, ((NpcInstance) t).getNpcId()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(8556));
			return false;
		}
		if(player.getDistance(t) > 200)
		{
			player.sendPacket(new SystemMessage(SystemMessage.YOUR_TARGET_IS_OUT_OF_RANGE));
			return false;
		}

		useItem(player, 8556, 1);
		((NpcInstance) t).doDie(player);
		return true;
	}

	//DestroyedDarknessFragmentPowder -> DestroyedLightFragmentPowde
	private boolean use13853(Player player, boolean ctrl)
	{
		if(!player.isInZone(ZoneType.mother_tree))
		{
			player.sendPacket(SystemMsg.THERE_WAS_NOTHING_FOUND_INSIDE);
			return false;
		}
		useItem(player, 13853, 1);
		Functions.addItem(player, 13854, 1);
		return true;
	}

	//Holy Water for SSQ 2nd quest
	private boolean use13808(Player player, boolean ctrl)
	{
		int[] allowedDoors = {17240101, 17240105, 17240109};

		GameObject target = player.getTarget();
		if(player.getDistance(target) > 150)
			return false;
		if(target != null && target.isDoor())
		{
			int _door = ((DoorInstance) target).getDoorId();
			if(ArrayUtils.contains(allowedDoors, _door))
				player.getReflection().openDoor(_door);
			else
			{
				player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
		}
		else
		{
			player.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}
		return true;
	}

	//Court Mag Staff for SSQ 2nd quest
	private boolean use13809(Player player, boolean ctrl)
	{
		int[] allowedDoors = {17240103, 17240107};

		GameObject target = player.getTarget();
		if(target != null && target.isDoor())
		{
			int _door = ((DoorInstance) target).getDoorId();
			if(ArrayUtils.contains(allowedDoors, _door))
			{
				useItem(player, 13809, 1);
				player.getReflection().openDoor(_door);
				return false;
			}
			else
			{
				player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
				return false;
			}
		}
		else
		{
			player.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}
	}

	private boolean use14835(Player player, boolean ctrl)
	{
		//TODO [G1ta0] добавить кучу других проверок на возможность телепорта
		if(player.isActionsDisabled() || player.isInOlympiadMode() || player.isInZone(ZoneType.no_escape))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(14835));
			return false;
		}

		useItem(player, 14835, 1);
		//Stakato nest entrance
		player.teleToLocation(89464, -44712, -2167, ReflectionManager.DEFAULT);
		return true;
	}

	//Strongbox of Promise
	private boolean use15537(Player player, boolean ctrl)
	{
		QuestState qs = player.getQuestState(_464_Oath.class);
		if(player.getLevel() >= 82 && qs == null)
		{
			useItem(player, 15537, 1);
			Functions.addItem(player, 15538, 1);
			Quest q = QuestManager.getQuest(464);
			QuestState st = player.getQuestState(q.getClass());
			st = q.newQuestState(player, Quest.CREATED);
			st.setState(Quest.STARTED);
			st.setCond(1);
		}
		else
		{
			player.sendMessage(new CustomMessage("Quest._464_Oath.QuestCannotBeTaken", player));
			return false;
		}
		return true;
	}

	//Totem
	private boolean use21899(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21899));
			return false;
		}
		Functions.spawn(Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 143);
		return true;
	}

	//Totem
	private boolean use21900(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21900));
			return false;
		}
		Functions.spawn(Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 144);
		return true;
	}

	//Totem
	private boolean use21901(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21901));
			return false;
		}
		Functions.spawn(Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 145);
		return true;
	}

	//Totem
	private boolean use21902(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21902));
			return false;
		}
		Functions.spawn(Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 146);
		return true;
	}

	// Refined Red Dragon Blood
	private boolean use21903(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21903));
			return false;
		}
		player.doCast(SkillTable.getInstance().getInfo(22298, 1), player, false);
		Functions.removeItem(player, 21903, 1);
		return true;
	}

	// Refined Blue Dragon Blood
	private boolean use21904(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21904));
			return false;
		}
		player.doCast(SkillTable.getInstance().getInfo(22299, 1), player, false);
		Functions.removeItem(player, 21904, 1);
		return true;
	}

	// Antharas Blood Crystal
	private boolean use17268(Player player, boolean ctrl)
	{
		if(!player.isInZone(AntharasManager.getZone()))
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(17268));
			return false;
		}
		player.doCast(SkillTable.getInstance().getInfo(9179, 1), player, false);
		Functions.removeItem(player, 17268, 1);
		return true;
	}

	private boolean use17619(Player player, boolean ctrl)	
	{
		//TODO[Iqman+Nosferatu] Define zone in cruma tower we can use this scroll only there!!
		QuestState qs = player.getQuestState(_10352_LegacyofCrumaTower.class);
		QuestState qs2 = player.getQuestState(_480_AnotherLegacyOfCrumaTower.class);
		if(player.getVar("MechanismSpawn") != null || qs == null || qs.getCond() > 4)
		{
			if(qs2 == null || qs2.getCond() > 4 || player.getVar("MechanismSpawn") != null)
			{
				player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(17619));
				return false;
			}	
		}	
		
		Functions.removeItem(player, 17619, 1);
		NpcInstance npc = Functions.spawn(Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 17619);
		player.setVar("MechanismSpawn", "1", 120000);
		if(qs != null && !qs.isCompleted())
		{
			Quest q = QuestManager.getQuest(10352);
			player.processQuestEvent(q.getName(), "advanceCond3", null);
		}
		if(qs2 != null && !qs2.isCompleted())
		{
			Quest q2 = QuestManager.getQuest(480);
			player.processQuestEvent(q2.getName(), "advanceCond3", null);
		}		
		Functions.executeTask("handler.items.Special", "despawnNpc", new Object[] {npc, player}, 120000);
		return true;
	}

	private boolean use17604(Player player, boolean ctrl)
	{
		//TODO[Iqman+Nosferatu] Define zone in cruma tower we can use this scroll only there!!
		QuestState qs = player.getQuestState(_10301_ShadowOfTerrorBlackishRedFog.class);
		GameObject target = player.getTarget();
		if(target == null || !target.isNpc())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(17604));
			return false;
		}

		NpcInstance _target = (NpcInstance) target;
		if(_target.getNpcId() != 33489)
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(17604));
			return false;
		}

		if(qs == null || qs.getCond() != 2 || player.getVar("CrystalsSpawn") != null)
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(17604));
			return false;
		}

		Functions.removeItem(player, 17604, 1);
		NpcInstance npc = Functions.spawn(Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 32938);
		NpcInstance npc2 = Functions.spawn(Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()), 32938);
		player.setVar("CrystalsSpawn", "1", 120000);
		Functions.executeTask("handler.items.Special", "despawnNpc", new Object[] {npc, player}, 120000);
		Functions.executeTask("handler.items.Special", "despawnNpc", new Object[] {npc2, player}, 120000);
		return true;
	}

	public static void despawnNpc(NpcInstance npc, Player player)
	{
		if(npc != null)
			npc.deleteMe();
		if(player != null)
			player.unsetVar("CrystalsSpawn");
	}

	private boolean use34033(Player player, boolean ctrl)
	{
		QuestState qs = player.getQuestState(_10304_ForTheForgottenHeroes.class);
		NpcHtmlMessagePacket msg = new NpcHtmlMessagePacket(5);
		if(player.getLevel() >= 90 && qs == null)
		{
			Quest q = QuestManager.getQuest(10304);
			QuestState st = player.getQuestState(q.getClass());
			st = q.newQuestState(player, Quest.CREATED);
			st.setState(Quest.STARTED);
			st.setCond(1);
			useItem(player, 34033, 1);
			Functions.addItem(player, 17618, 1);
			msg.setFile("quests/_10304_ForTheForgottenHeroes/2.htm");
			player.sendPacket(msg);
		}
		else
		{
			msg.setFile("quests/_10304_ForTheForgottenHeroes/4.htm");
			player.sendPacket(msg);
			return false;
		}
		return true;
	}

	private boolean use17603(Player player, boolean ctrl)
	{
		QuestState qs = player.getQuestState(_466_PlacingMySmallPower.class);
		if(qs != null && qs.getCond() == 4)
		{
			if(qs.getQuestItemsCount(_466_PlacingMySmallPower.WingI) >= 5 && qs.getQuestItemsCount(_466_PlacingMySmallPower.CoconI) >= 5 && qs.getQuestItemsCount(_466_PlacingMySmallPower.BreathI) >= 5)
			{
				useItem(player, 17603, 1);

				qs.setCond(5);
				qs.takeItems(_466_PlacingMySmallPower.WingI, -1);
				qs.takeItems(_466_PlacingMySmallPower.CoconI, -1);
				qs.takeItems(_466_PlacingMySmallPower.BreathI, -1);
				qs.giveItems(_466_PlacingMySmallPower.NavozItem, 1);
				return true;
			}
		}
		return false;
	}

	// Kartia Extra Ticket
	private boolean useKartiaTicket(Player player, boolean ctrl, ItemInstance item, int instanceID)
	{
		int itemId = item.getItemId();
		
		String itemName = "item" + itemId; //the real name is too long
		
		long instanceReuse = player.getInstanceReuse(instanceID);
		
		int itemReuse = player.getVarInt(itemName);
		
		InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instanceID);
		
		if(InstantZoneHolder.getInstance().getMinutesToNextEntrance(instanceID, player) > 0)
		{
			if(itemReuse == 0)
			{
				player.removeInstanceReuse(205);
				Functions.removeItem(player, itemId, 1);
				player.setVar(itemName, 1, instanceReuse);
				player.sendPacket(new ExShowScreenMessage("Bạn có thể tham gia "+ iz.getName() + " thêm 1 lần nữa.", 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			}
			else
			{
				player.sendPacket(new ExShowScreenMessage("Chỉ có thể sử dụng loại vé này 1 lần trong 24h.", 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			}
		}
		else
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
			
		}
		
		return true;
	}
	
	private static long useItem(Player player, int itemId, long count)
	{
		player.sendPacket(new SystemMessage(SystemMessage.YOU_USE_S1).addItemName(itemId));
		return Functions.removeItem(player, itemId, count);
	}
}