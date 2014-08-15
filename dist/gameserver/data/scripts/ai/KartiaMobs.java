package ai;

import java.util.Calendar;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.GuardInstance;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

import instances.Kartia;

public class KartiaMobs extends Fighter
{
	public KartiaMobs(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();

		Reflection reflection = actor.getReflection();
		if(reflection != null)
		{
			if(reflection instanceof Kartia)
			{
				final Kartia kartiaInstance = (Kartia) reflection;
				if(kartiaInstance.getMonsterSet().get("ruler").intValue() == actor.getNpcId())
					Functions.npcSay(actor, NpcString.HOW_ITS_IMPOSSIBLE_RETURNING_TO_ABYSS_AGAIN);
			}
		}
		super.onEvtDead(killer);
	}

	@Override
	protected void onEvtAttacked(final Creature attacker, int damage)
	{
		final NpcInstance actor = getActor();

		Reflection reflection = actor.getReflection();
		if(reflection != null)
		{
			if(reflection instanceof Kartia)
			{
				final Kartia kartiaInstance = (Kartia) reflection;	
				if(kartiaInstance.getStatus() == 1 && kartiaInstance.getMonsterSet().get("overseer").intValue() == actor.getNpcId() && (actor.getCurrentHp() / actor.getMaxHp() <= 0.4D) && !actor.isMoving)
				{
					Functions.npcSay(actor, NpcString.YOU_VERY_STRONG_FOR_MORTAL_I_RETREAT);
					Location loc = kartiaInstance.isPartyInstance() ? new Location(-120865, -13904, -11440) : new Location(-111297, -13904, -11440);
					DefaultAI ai = (DefaultAI) actor.getAI();
					ai.addTaskMove(Location.findPointToStay(loc, 250, actor.getGeoIndex()), true);

					ThreadPoolManager.getInstance().schedule(new RunnableImpl()
					{
						@Override
						public void runImpl()
						{
							actor.doDie(attacker);
							actor.deleteMe();
							kartiaInstance.openRaidDoor();
						}
					}
					, 10000L);
				}				
			}
		}
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		final NpcInstance actor = getActor();
		
		actor.setRunning();

		if(actor instanceof GuardInstance)
		{
			actor.setBusy(true);
			actor.setHaveRandomAnim(true);
		}
		else if(actor instanceof MonsterInstance)
		{
			actor.setRandomWalk(false);

			Reflection reflection = actor.getReflection();	
			if(reflection != null)
			{
				if(reflection instanceof Kartia)
				{
					Kartia kartiaInstance = (Kartia) reflection;	
					if(kartiaInstance.getMonsterSet().get("captivated").intValue() != actor.getNpcId())
					{
						if(kartiaInstance.getStatus() == 0)
						{
							actor.setRunning();
							Location loc = kartiaInstance.isPartyInstance() ? new Location(-120921 + Rnd.get(-150, 150), -10452 + Rnd.get(-150, 150), -11680) : new Location(-111353 + Rnd.get(-150, 150), -10452 + Rnd.get(-150, 150), -11680);
							DefaultAI ai = (DefaultAI) actor.getAI();
							ai.addTaskMove(Location.findPointToStay(loc, 250, actor.getGeoIndex()), true);	
						}
					}
					if(kartiaInstance.getMonsterSet().get("overseer").intValue() == actor.getNpcId())
					{
						if(kartiaInstance.getStatus() == 0)
							Functions.npcSay(actor, NpcString.INTRUDERS_CANNOT_LEAVE_ALIVE);
					}
					/*if(kartiaInstance.getMonsterSet().get("ruler").intValue() == actor.getNpcId())
					{
						actor.setIsInvul(true);
						actor.startAbnormalEffect(AbnormalEffect.FLESH_STONE);
						actor.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, kartiaInstance.ruler);
						actor.startParalyzed();
					} */
				}
			}
		}
	}
}
