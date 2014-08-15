package l2s.gameserver.skills.skillclasses;

import static l2s.gameserver.model.Zone.ZoneType.no_restart;
import static l2s.gameserver.model.Zone.ZoneType.no_summon;

import java.util.List;

import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.utils.Location;

public class Call extends Skill
{
	final boolean _party;

	public Call(StatsSet set)
	{
		super(set);
		_party = set.getBool("party", false);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(activeChar.isPlayer())
		{
			if(_party && ((Player) activeChar).getParty() == null)
				return false;

			IStaticPacket msg = canSummonHere((Player) activeChar);
			if(msg != null)
			{
				activeChar.sendPacket(msg);
				return false;
			}

			// Эта проверка только для одиночной цели
			if(!_party)
			{
				if(activeChar == target)
					return false;

				msg = canBeSummoned(target);
				if(msg != null)
				{
					activeChar.sendPacket(msg);
					return false;
				}
			}
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if(!activeChar.isPlayer())
			return;

		IStaticPacket msg = canSummonHere((Player) activeChar);
		if(msg != null)
		{
			activeChar.sendPacket(msg);
			return;
		}
		boolean success = false;
		if(_party)
		{
			if(((Player) activeChar).getParty() != null)
				for(Player target : ((Player) activeChar).getParty().getPartyMembers())
					if(!target.equals(activeChar) && canBeSummoned(target) == null && !target.isTerritoryFlagEquipped())
					{
						target.stopMove();
						target.teleToLocation(Location.findPointToStay(activeChar, 100, 150), activeChar.getGeoIndex());
						getEffects(activeChar, target, false);
						success = true; //if one succeed
					}

			if(isSSPossible())
				activeChar.unChargeShots(isMagic());
			return;
		}

		for(Creature target : targets)
			if(target != null)
			{
				if(canBeSummoned(target) != null)
					continue;

				((Player) target).summonCharacterRequest(activeChar, Location.findAroundPosition(activeChar, 100, 150), getId() == 1403 || getId() == 1404 ? 1 : 0);

				getEffects(activeChar, target, false);
				success = true; //if one succeed
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());
		if(success) //spec for 19007
		{
			//must be player for usage with a clan.
			int clanRepConsume = getClanRepConsume();
			if(clanRepConsume > 0)
				activeChar.getPlayer().getClan().incReputation(-clanRepConsume, false, "clan skills");

			int fameConsume = getFameConsume();
			if(fameConsume > 0)
				activeChar.getPlayer().setFame(activeChar.getPlayer().getFame() - fameConsume, "clan skills");	
		}		
		super.useSkill(activeChar, targets);
	}

	/**
	 * Может ли призывающий в данный момент использовать призыв
	 */
	public static IStaticPacket canSummonHere(Player activeChar)
	{
		if(activeChar.isAlikeDead() || activeChar.isInOlympiadMode() || activeChar.isInObserverMode() || activeChar.isFlying())
			return SystemMsg.NOTHING_HAPPENED;

		// "Нельзя вызывать персонажей в/из зоны свободного PvP"
		// "в зоны осад"
		// "на Олимпийский стадион"
		// "в зоны определенных рейд-боссов и эпик-боссов"
		if(activeChar.isInZoneBattle() || activeChar.isInZone(Zone.ZoneType.SIEGE) || activeChar.isInZone(no_restart) || activeChar.isInZone(no_summon) || activeChar.isInBoat() || activeChar.getReflection() != ReflectionManager.DEFAULT)
			return SystemMsg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION;

		//if(activeChar.isInCombat())
		//return SystemMsg.YOU_CANNOT_SUMMON_DURING_COMBAT;

		if(activeChar.isInStoreMode() || activeChar.isProcessingRequest())
			return SystemMsg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE;

		return null;
	}

	/**
	 * Может ли цель ответить на призыв
	 */
	public static IStaticPacket canBeSummoned(Creature target)
	{
		if(target == null || !target.isPlayer() || target.getPlayer().isTerritoryFlagEquipped() || target.isFlying() || target.isInObserverMode() || !target.getPlayer().getPlayerAccess().UseTeleport)
			return SystemMsg.INVALID_TARGET;

		if(target.isInOlympiadMode())
			return SystemMsg.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD;

		if(target.isInZoneBattle() || target.isInZone(Zone.ZoneType.SIEGE) || target.isInZone(no_restart) || target.isInZone(no_summon) || target.getReflection() != ReflectionManager.DEFAULT || target.isInBoat())
			return SystemMsg.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING;

		// Нельзя призывать мертвых персонажей
		if(target.isAlikeDead())
			return new SystemMessagePacket(SystemMsg.C1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED).addName(target);

		// Нельзя призывать персонажей, которые находятся в режиме PvP или Combat Mode
		if(target.getPvpFlag() != 0 || target.isInCombat())
			return new SystemMessagePacket(SystemMsg.C1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED).addName(target);

		Player pTarget = (Player) target;

		// Нельзя призывать торгующих персонажей
		if(pTarget.getPrivateStoreType() != Player.STORE_PRIVATE_NONE || pTarget.isProcessingRequest())
			return new SystemMessagePacket(SystemMsg.C1_IS_CURRENTLY_TRADING_OR_OPERATING_A_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED).addName(target);

		return null;
	}
}