package l2s.gameserver.model.instances;

import java.util.Collection;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2s.gameserver.model.entity.events.impl.SiegeEvent;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.entity.residence.Residence;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.model.pledge.UnitMember;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.PledgeReceiveSubPledgeCreated;
import l2s.gameserver.network.l2.s2c.PledgeShowInfoUpdatePacket;
import l2s.gameserver.network.l2.s2c.PledgeShowMemberListUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.tables.ClanTable;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.SiegeUtils;
import l2s.gameserver.utils.Util;

public final class VillageMasterInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public VillageMasterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("try_create_clan"))
		{
			String htmltext = "clan-02.htm";
			// Игроки меньше 10го уровня не могут создать клан.
			if(player.getLevel() <= 9)
				htmltext = "clan-06.htm";
			// Игроки, которые уже имеют клан, не могут создать новый клан.
			else if(player.isClanLeader())
				htmltext = "clan-07.htm";
			// Игрок, который уже состоит в клане, не может создать клан.
			else if(player.getClan() != null)
				htmltext = "clan-09.htm";
			super.onBypassFeedback(player, "Link villagemaster/clan/" + htmltext);
		}
		else if(command.startsWith("try_dissolve_clan"))
		{
			String htmltext = "clan-01.htm";
			if(player.isClanLeader())
				htmltext = "clan-04.htm";
			// Игрок состоящий в клане, клан лидером которого не является, не может удалить клан.
			else if(player.getClan() != null)
				htmltext = "clan-08.htm";
			// Игрок, который не состоит в клане, не может удалить клан.
			else
				htmltext = "clan-11.htm";
			super.onBypassFeedback(player, "Link villagemaster/clan/" + htmltext);
		}
		else if(command.startsWith("create_clan") && command.length() > 12)
		{
			String val = command.substring(12);
			createClan(player, val);
		}
		else if(command.startsWith("create_academy") && command.length() > 15)
		{
			String sub = command.substring(15, command.length());
			createSubPledge(player, sub, Clan.SUBUNIT_ACADEMY, 5, "");
		}
		else if(command.startsWith("create_royal") && command.length() > 15)
		{
			String[] sub = command.substring(13, command.length()).split(" ", 2);
			if(sub.length == 2)
				createSubPledge(player, sub[1], Clan.SUBUNIT_ROYAL1, 6, sub[0]);
		}
		else if(command.startsWith("create_knight") && command.length() > 16)
		{
			String[] sub = command.substring(14, command.length()).split(" ", 2);
			if(sub.length == 2)
				createSubPledge(player, sub[1], Clan.SUBUNIT_KNIGHT1, 7, sub[0]);
		}
		else if(command.startsWith("assign_subpl_leader") && command.length() > 22)
		{
			String[] sub = command.substring(20, command.length()).split(" ", 2);
			if(sub.length == 2)
				assignSubPledgeLeader(player, sub[1], sub[0]);
		}
		else if(command.startsWith("assign_new_clan_leader") && command.length() > 23)
		{
			String val = command.substring(23);
			setLeader(player, val);
		}
		else if(command.startsWith("try_create_ally"))
		{
			String htmltext = "ally-01.htm";
			if(player.isClanLeader())
				htmltext = "ally-02.htm";
			super.onBypassFeedback(player, "Link villagemaster/ally/" + htmltext);
		}
		else if(command.startsWith("try_dissolve_ally"))
		{
			String htmltext = "ally-01.htm";
			if(player.isAllyLeader())
				htmltext = "ally-03.htm";
			super.onBypassFeedback(player, "Link villagemaster/ally/" + htmltext);
		}
		if(command.startsWith("create_ally") && command.length() > 12)
		{
			String val = command.substring(12);
			createAlly(player, val);
		}
		else if(command.startsWith("dissolve_ally"))
			dissolveAlly(player);
		else if(command.startsWith("dissolve_clan"))
			dissolveClan(player);
		else if(command.startsWith("increase_clan_level"))
			levelUpClan(player);
		else if(command.startsWith("learn_clan_skills"))
			showClanSkillList(player);
		else if(command.startsWith("use_1st_proof"))
		{
			if(player.getClassId().isOfLevel(ClassLevel.FIRST))
			{
				if(getVillageMasterRace() != player.getRace())
					command = "Link villagemaster/" + getNpcId() + "-proof_no_race.htm";
				else
					command = "Multisell 717";
			}
			else if(player.getClassId().isOfLevel(ClassLevel.NONE))
				command = "Link villagemaster/" + getNpcId() + "-proof_no_low.htm";
			else
				command = "Link villagemaster/" + getNpcId() + "-proof_no_race.htm";
			super.onBypassFeedback(player, command);
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if(val == 0)
			pom = "" + npcId;
		else
			pom = npcId + "-" + val;

		return "villagemaster/" + pom + ".htm";
	}

	// Private stuff
	public void createClan(Player player, String clanName)
	{
		if(player.getLevel() < 10)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_MEET_THE_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN);
			return;
		}

		if(player.getClanId() != 0)
		{
			player.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_CREATE_A_CLAN);
			return;
		}

		if(!player.canCreateClan())
		{
			// you can't create a new clan within 10 days
			player.sendPacket(SystemMsg.YOU_MUST_WAIT_10_DAYS_BEFORE_CREATING_A_NEW_CLAN);
			return;
		}
		if(clanName.length() > 16)
		{
			player.sendPacket(SystemMsg.CLAN_NAMES_LENGTH_IS_INCORRECT);
			return;
		}
		if(!Util.isMatchingRegexp(clanName, Config.CLAN_NAME_TEMPLATE))
		{
			// clan name is not matching template
			player.sendPacket(SystemMsg.CLAN_NAME_IS_INVALID);
			return;
		}

		Clan clan = ClanTable.getInstance().createClan(player, clanName);
		if(clan == null)
		{
			// clan name is already taken
			player.sendPacket(SystemMsg.THIS_NAME_ALREADY_EXISTS);
			return;
		}

		// should be update packet only
		player.sendPacket(clan.listAll());
		player.sendPacket(new PledgeShowInfoUpdatePacket(clan), SystemMsg.YOUR_CLAN_HAS_BEEN_CREATED);
		player.updatePledgeRank();
		player.broadcastCharInfo();
	}

	public void setLeader(Player leader, String newLeader)
	{
		if(!leader.isClanLeader())
		{
			leader.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return;
		}

		if(leader.getEvent(SiegeEvent.class) != null)
		{
			leader.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow", leader));
			return;
		}

		Clan clan = leader.getClan();
		SubUnit mainUnit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
		UnitMember member = mainUnit.getUnitMember(newLeader);

		if(member == null)
		{
			//FIX ME зачем 2-ве мессаги(VISTALL)
			//leader.sendMessage(new CustomMessage("l2s.gameserver.model.instances.L2VillageMasterInstance.S1IsNotMemberOfTheClan", leader).addString(newLeader));
			showChatWindow(leader, "villagemaster/clan/clan-20.htm");
			return;
		}

		if(member.getLeaderOf() != Clan.SUBUNIT_NONE)
		{
			leader.sendMessage(new CustomMessage("l2s.gameserver.model.instances.L2VillageMasterInstance.CannotAssignUnitLeader", leader));
			return;
		}

		setLeader(leader, clan, mainUnit, member);
	}

	public static void setLeader(Player player, Clan clan, SubUnit unit, UnitMember newLeader)
	{
		player.sendMessage(new CustomMessage("l2s.gameserver.model.instances.L2VillageMasterInstance.ClanLeaderWillBeChangedFromS1ToS2", player).addString(clan.getLeaderName()).addString(newLeader.getName()));
		//TODO: В данной редакции смена лидера производится сразу же.
		// Надо подумать над реализацией смены кланлидера в запланированный день недели.

		/*if(clan.getLevel() >= CastleSiegeManager.getSiegeClanMinLevel())
		{
			if(clan.getLeader() != null)
			{
				L2Player oldLeaderPlayer = clan.getLeader().getPlayer();
				if(oldLeaderPlayer != null)
					SiegeUtils.removeSiegeSkills(oldLeaderPlayer);
			}
			L2Player newLeaderPlayer = newLeader.getPlayer();
			if(newLeaderPlayer != null)
				SiegeUtils.addSiegeSkills(newLeaderPlayer);
		}
		            */
		unit.setLeader(newLeader, true);

		clan.broadcastClanStatus(true, true, false);
	}

	public void createSubPledge(Player player, String clanName, int pledgeType, int minClanLvl, String leaderName)
	{
		UnitMember subLeader = null;

		Clan clan = player.getClan();

		if(clan == null || !player.isClanLeader())
		{
			player.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_CREATE_A_CLAN);
			return;
		}

		if(!Util.isMatchingRegexp(clanName, Config.CLAN_NAME_TEMPLATE))
		{
			player.sendPacket(SystemMsg.CLAN_NAME_IS_INVALID);
			return;
		}

		Collection<SubUnit> subPledge = clan.getAllSubUnits();
		for(SubUnit element : subPledge)
			if(element.getName().equals(clanName))
			{
				player.sendPacket(SystemMsg.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME);
				return;
			}

		if(ClanTable.getInstance().getClanByName(clanName) != null)
		{
			player.sendPacket(SystemMsg.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME);
			return;
		}

		if(clan.getLevel() < minClanLvl)
		{
			player.sendPacket(SystemMsg.THE_CONDITIONS_NECESSARY_TO_CREATE_A_MILITARY_UNIT_HAVE_NOT_BEEN_MET);
			return;
		}

		SubUnit unit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);

		if(pledgeType != Clan.SUBUNIT_ACADEMY)
		{
			subLeader = unit.getUnitMember(leaderName);
			if(subLeader == null)
			{
				player.sendMessage(new CustomMessage("l2s.gameserver.model.instances.L2VillageMasterInstance.PlayerCantBeAssignedAsSubUnitLeader", player));
				return;
			}
			else if(subLeader.getLeaderOf() != Clan.SUBUNIT_NONE)
			{
				player.sendMessage(new CustomMessage("l2s.gameserver.model.instances.L2VillageMasterInstance.ItCantBeSubUnitLeader", player));
				return;
			}
		}

		pledgeType = clan.createSubPledge(player, pledgeType, subLeader, clanName);
		if(pledgeType == Clan.SUBUNIT_NONE)
			return;

		clan.broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(clan.getSubUnit(pledgeType)));

		IStaticPacket sm;
		if(pledgeType == Clan.SUBUNIT_ACADEMY)
			sm = new SystemMessage(SystemMessage.CONGRATULATIONS_THE_S1S_CLAN_ACADEMY_HAS_BEEN_CREATED).addString(player.getClan().getName());
		else if(pledgeType >= Clan.SUBUNIT_KNIGHT1)
			sm = new SystemMessage(SystemMessage.THE_KNIGHTS_OF_S1_HAVE_BEEN_CREATED).addString(player.getClan().getName());
		else if(pledgeType >= Clan.SUBUNIT_ROYAL1)
			sm = new SystemMessage(SystemMessage.THE_ROYAL_GUARD_OF_S1_HAVE_BEEN_CREATED).addString(player.getClan().getName());
		else
			sm = SystemMsg.YOUR_CLAN_HAS_BEEN_CREATED;

		player.sendPacket(sm);

		if(subLeader != null)
		{
			clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdatePacket(subLeader));
			if(subLeader.isOnline())
			{
				subLeader.getPlayer().updatePledgeRank();
				subLeader.getPlayer().broadcastCharInfo();
			}
		}
	}

	public void assignSubPledgeLeader(Player player, String clanName, String leaderName)
	{
		Clan clan = player.getClan();

		if(clan == null)
		{
			player.sendMessage(new CustomMessage("l2s.gameserver.model.instances.L2VillageMasterInstance.ClanDoesntExist", player));
			return;
		}

		if(!player.isClanLeader())
		{
			player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return;
		}

		SubUnit targetUnit = null;
		for(SubUnit unit : clan.getAllSubUnits())
		{
			if(unit.getType() == Clan.SUBUNIT_MAIN_CLAN || unit.getType() == Clan.SUBUNIT_ACADEMY)
				continue;
			if(unit.getName().equalsIgnoreCase(clanName))
				targetUnit = unit;

		}
		if(targetUnit == null)
		{
			player.sendMessage(new CustomMessage("l2s.gameserver.model.instances.L2VillageMasterInstance.SubUnitNotFound", player));
			return;
		}
		SubUnit mainUnit = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
		UnitMember subLeader = mainUnit.getUnitMember(leaderName);
		if(subLeader == null)
		{
			player.sendMessage(new CustomMessage("l2s.gameserver.model.instances.L2VillageMasterInstance.PlayerCantBeAssignedAsSubUnitLeader", player));
			return;
		}

		if(subLeader.getLeaderOf() != Clan.SUBUNIT_NONE)
		{
			player.sendMessage(new CustomMessage("l2s.gameserver.model.instances.L2VillageMasterInstance.ItCantBeSubUnitLeader", player));
			return;
		}

		targetUnit.setLeader(subLeader, true);
		clan.broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(targetUnit));

		clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdatePacket(subLeader));
		if(subLeader.isOnline())
		{
			subLeader.getPlayer().updatePledgeRank();
			subLeader.getPlayer().broadcastCharInfo();
		}

		player.sendMessage(new CustomMessage("l2s.gameserver.model.instances.L2VillageMasterInstance.NewSubUnitLeaderHasBeenAssigned", player));
	}

	private void dissolveClan(Player player)
	{
		if(player == null || player.getClan() == null)
			return;
		Clan clan = player.getClan();

		if(!player.isClanLeader())
		{
			player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return;
		}
		if(clan.getAllyId() != 0)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_DISPERSE_THE_CLANS_IN_YOUR_ALLIANCE);
			return;
		}
		if(clan.isAtWar() > 0)
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_ENGAGED_IN_A_WAR);
			return;
		}
		if(clan.getCastle() != 0 || clan.getHasHideout() != 0 || clan.getHasFortress() != 0)
		{
			player.sendPacket(SystemMsg.UNABLE_TO_DISSOLVE_YOUR_CLAN_OWNS_ONE_OR_MORE_CASTLES_OR_HIDEOUTS);
			return;
		}

		for(Residence r : ResidenceHolder.getInstance().getResidences())
		{
			if(r.getSiegeEvent().getSiegeClan(SiegeEvent.ATTACKERS, clan) != null || r.getSiegeEvent().getSiegeClan(SiegeEvent.DEFENDERS, clan) != null || r.getSiegeEvent().getSiegeClan(CastleSiegeEvent.DEFENDERS_WAITING, clan) != null)
			{
				player.sendPacket(SystemMsg.UNABLE_TO_DISSOLVE_YOUR_CLAN_HAS_REQUESTED_TO_PARTICIPATE_IN_A_CASTLE_SIEGE);
				return;
			}
		}

		ClanTable.getInstance().dissolveClan(player);
	}

	public void levelUpClan(Player player)
	{
		Clan clan = player.getClan();
		if(clan == null)
			return;
		if(!player.isClanLeader())
		{
			player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return;
		}

		boolean increaseClanLevel = false;

		switch(clan.getLevel())
		{
			case 0:
				// Upgrade to 1
				if(player.getSp() >= 20000 && player.getAdena() >= 650000)
				{
					player.setSp(player.getSp() - 20000);
					player.reduceAdena(650000, true);
					increaseClanLevel = true;
				}
				break;
			case 1:
				// Upgrade to 2
				if(player.getSp() >= 100000 && player.getAdena() >= 2500000)
				{
					player.setSp(player.getSp() - 100000);
					player.reduceAdena(2500000, true);
					increaseClanLevel = true;
				}
				break;
			case 2:
				// Upgrade to 3
				// itemid 1419 == Blood Mark
				if(player.getSp() >= 350000 && player.getInventory().destroyItemByItemId(1419, 1))
				{
					player.setSp(player.getSp() - 350000);
					increaseClanLevel = true;
				}
				break;
			case 3:
				// Upgrade to 4
				// itemid 3874 == Alliance Manifesto
				if(player.getSp() >= 1000000 && player.getInventory().destroyItemByItemId(3874, 1))
				{
					player.setSp(player.getSp() - 1000000);
					increaseClanLevel = true;
				}
				break;
			case 4:
				// Upgrade to 5
				// itemid 3870 == Seal of Aspiration
				if(player.getSp() >= 2500000 && player.getInventory().destroyItemByItemId(3870, 1))
				{
					player.setSp(player.getSp() - 2500000);
					increaseClanLevel = true;
				}
				break;
			case 5:
				// Upgrade to 6
				if(clan.getReputationScore() >= 5000 && clan.getAllSize() >= 30)
				{
					clan.incReputation(-5000, false, "LvlUpClan");
					increaseClanLevel = true;
				}
				break;
			case 6:
				// Upgrade to 7
				if(clan.getReputationScore() >= 10000 && clan.getAllSize() >= 50)
				{
					clan.incReputation(-10000, false, "LvlUpClan");
					increaseClanLevel = true;
				}
				break;
			case 7:
				// Upgrade to 8
				if(clan.getReputationScore() >= 20000 && clan.getAllSize() >= 80)
				{
					clan.incReputation(-20000, false, "LvlUpClan");
					increaseClanLevel = true;
				}
				break;
			case 8:
				// Upgrade to 9
				// itemId 9910 == Blood Oath
				if(clan.getReputationScore() >= 40000 && clan.getAllSize() >= 120)
					if(player.getInventory().destroyItemByItemId(9910, 150L))
					{
						clan.incReputation(-40000, false, "LvlUpClan");
						increaseClanLevel = true;
					}
				break;
			case 9:
				// Upgrade to 10
				// itemId 9911 == Blood Alliance
				if(clan.getReputationScore() >= 40000 && clan.getAllSize() >= 140)
					if(player.getInventory().destroyItemByItemId(9911, 5))
					{
						clan.incReputation(-40000, false, "LvlUpClan");
						increaseClanLevel = true;
					}
				break;
			case 10:
				// Upgrade to 11
				if(clan.getReputationScore() >= 75000 && clan.getAllSize() >= 170 && clan.getCastle() > 0)
				{
					if(player.getInventory().destroyItemByItemId(34996, 1))
					{
						clan.incReputation(-75000, false, "LvlUpClan");
						increaseClanLevel = true;
					}
				}
				break;
		}

		if(increaseClanLevel)
		{
			int oldLevel = clan.getLevel();
			clan.setLevel(clan.getLevel() + 1);
			clan.updateClanInDB();

			doCast(SkillTable.getInstance().getInfo(5103, 1), player, true);

			clan.onLevelChange(oldLevel, clan.getLevel());
		}
		else
			player.sendPacket(SystemMsg.THE_CLAN_HAS_FAILED_TO_INCREASE_ITS_LEVEL);
	}

	public void createAlly(Player player, String allyName)
	{
		// D5 You may not ally with clan you are battle with.
		// D6 Only the clan leader may apply for withdraw from alliance.
		// DD No response. Invitation to join an
		// D7 Alliance leaders cannot withdraw.
		// D9 Different Alliance
		// EB alliance information
		// Ec alliance name $s1
		// ee alliance leader: $s2 of $s1
		// ef affilated clans: total $s1 clan(s)
		// f6 you have already joined an alliance
		// f9 you cannot new alliance 10 days
		// fd cannot accept. clan ally is register as enemy during siege battle.
		// fe you have invited someone to your alliance.
		// 100 do you wish to withdraw from the alliance
		// 102 enter the name of the clan you wish to expel.
		// 202 do you realy wish to dissolve the alliance
		// 502 you have accepted alliance
		// 602 you have failed to invite a clan into the alliance
		// 702 you have withdraw

		if(!player.isClanLeader())
		{
			player.sendPacket(SystemMsg.ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES);
			return;
		}
		if(player.getClan().getAllyId() != 0)
		{
			player.sendPacket(SystemMsg.YOU_ALREADY_BELONG_TO_ANOTHER_ALLIANCE);
			return;
		}
		if(allyName.length() > 16)
		{
			player.sendPacket(SystemMsg.INCORRECT_LENGTH_FOR_AN_ALLIANCE_NAME);
			return;
		}
		if(!Util.isMatchingRegexp(allyName, Config.ALLY_NAME_TEMPLATE))
		{
			player.sendPacket(SystemMsg.INCORRECT_ALLIANCE_NAME);
			return;
		}
		if(player.getClan().getLevel() < 5)
		{
			player.sendPacket(SystemMsg.TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER);
			return;
		}
		if(ClanTable.getInstance().getAllyByName(allyName) != null)
		{
			player.sendPacket(SystemMsg.THAT_ALLIANCE_NAME_ALREADY_EXISTS);
			return;
		}
		if(!player.getClan().canCreateAlly())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_CREATE_A_NEW_ALLIANCE_WITHIN_1_DAY_OF_DISSOLUTION);
			return;
		}

		Alliance alliance = ClanTable.getInstance().createAlliance(player, allyName);
		if(alliance == null)
			return;

		player.broadcastCharInfo();
		player.sendMessage("Alliance " + allyName + " has been created.");
	}

	private void dissolveAlly(Player player)
	{
		if(player == null || player.getAlliance() == null)
			return;

		if(!player.isAllyLeader())
		{
			player.sendPacket(SystemMsg.THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS);
			return;
		}

		if(player.getAlliance().getMembersCount() > 1)
		{
			player.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_DISSOLVE_THE_ALLIANCE);
			return;
		}

		ClanTable.getInstance().dissolveAlly(player);
	}

	private Race getVillageMasterRace()
	{
		switch(getTemplate().getRace())
		{
			case 14:
				return Race.HUMAN;
			case 15:
				return Race.ELF;
			case 16:
				return Race.DARKELF;
			case 17:
				return Race.ORC;
			case 18:
				return Race.DWARF;
			case 25:
				return Race.KAMAEL;
		}
		return null;
	}
}