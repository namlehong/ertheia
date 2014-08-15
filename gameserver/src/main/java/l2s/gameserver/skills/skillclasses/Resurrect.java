package l2s.gameserver.skills.skillclasses;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import l2s.gameserver.listener.actor.player.OnAnswerListener;
import l2s.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.base.BaseStats;
import l2s.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2s.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.templates.StatsSet;

public class Resurrect extends Skill
{
	private final boolean _canPet;

	public Resurrect(StatsSet set)
	{
		super(set);
		_canPet = set.getBool("canPet", false);
	}

	@Override
	public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!activeChar.isPlayer())
			return false;

		if(target == null || target != activeChar && !target.isDead())
		{
			activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		Player player = (Player) activeChar;
		Player pcTarget = target.getPlayer();

		if(pcTarget == null)
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		if(player.isInOlympiadMode() || pcTarget.isInOlympiadMode())
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		for(GlobalEvent e : player.getEvents())
			if(!e.canRessurect(player, target, forceUse))
			{
				player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
				return false;
			}

		if(target.getEffectList().containsEffects(EffectType.ResurrectBlock))
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		//this block for players who does not have anything to do with the siege but they're in siege zone, if both the sieges are null - cannot res.	
		boolean playerInZone = player.isInZone(Zone.ZoneType.SIEGE);
		List<CastleSiegeEvent> siegeEvents = player.getEvents(CastleSiegeEvent.class);
		FortressSiegeEvent fortEvent = target.getEvent(FortressSiegeEvent.class);
		if(playerInZone && siegeEvents.isEmpty() && fortEvent == null)
		{
			if(forceUse)
				target.sendPacket(SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
			player.sendPacket(forceUse ? SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE : SystemMsg.INVALID_TARGET);
			return false;		
		}
		if(oneTarget())
			if(target.isPet())
			{
				Pair<Integer, OnAnswerListener> ask = pcTarget.getAskListener(false);
				ReviveAnswerListener reviveAsk = ask != null && ask.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener) ask.getValue() : null;
				if(reviveAsk != null)
				{
					if(reviveAsk.isForPet())
						activeChar.sendPacket(SystemMsg.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
					else
						activeChar.sendPacket(SystemMsg.A_PET_CANNOT_BE_RESURRECTED_WHILE_ITS_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING);
					return false;
				}
				if(!(_canPet || _targetType == SkillTargetType.TARGET_ONE_SERVITOR))
				{
					player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
					return false;
				}
			}
			else if(target.isPlayer())
			{
				Pair<Integer, OnAnswerListener> ask = pcTarget.getAskListener(false);
				ReviveAnswerListener reviveAsk = ask != null && ask.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener) ask.getValue() : null;

				if(reviveAsk != null)
				{
					if(reviveAsk.isForPet())
						activeChar.sendPacket(SystemMsg.WHILE_A_PET_IS_BEING_RESURRECTED_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER); // While a pet is attempting to resurrect, it cannot help in resurrecting its master.
					else
						activeChar.sendPacket(SystemMsg.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED); // Resurrection is already been proposed.
					return false;
				}
				if(_targetType == SkillTargetType.TARGET_ONE_SERVITOR)
				{
					player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
					return false;
				}
			}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		double percent = _power;

		if(percent < 100 && !isHandler())
		{
			double wit_bonus = _power * (BaseStats.WIT.calcBonus(activeChar) - 1);
			percent += wit_bonus > 20 ? 20 : wit_bonus;
			if(percent > 90)
				percent = 90;
		}

		for(Creature target : targets)
			Loop: if(target != null)
			{
				if(target.getPlayer() == null)
					continue;

				for(GlobalEvent e : target.getEvents())
					if(!e.canRessurect((Player) activeChar, target, true))
						break Loop;

				if(target.isPet() && _canPet)
				{
					if(target.getPlayer() == activeChar)
						((PetInstance) target).doRevive(percent);
					else
						target.getPlayer().reviveRequest((Player) activeChar, percent, true);
				}
				else if(target.isPlayer())
				{
					if(_targetType == SkillTargetType.TARGET_ONE_SERVITOR)
						continue;

					Player targetPlayer = (Player) target;

					Pair<Integer, OnAnswerListener> ask = targetPlayer.getAskListener(false);
					ReviveAnswerListener reviveAsk = ask != null && ask.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener) ask.getValue() : null;
					if(reviveAsk != null)
						continue;

					targetPlayer.reviveRequest((Player) activeChar, percent, false);
				}
				else
					continue;

				getEffects(activeChar, target, false);
			}

		if(isSSPossible())
			activeChar.unChargeShots(isMagic());

		super.useSkill(activeChar, targets);
	}
}