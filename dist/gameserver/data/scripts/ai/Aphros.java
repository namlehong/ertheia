package ai;

import java.util.List;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.Location;

import services.AphrosManager;

public class Aphros extends Fighter
{
	
	private int RaidHpState = 0;
	
	public Aphros(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
			
		if (actor.isDead())
			return;

		if (actor.getNpcId() == 25775)
		{
			if(actor.getCurrentHpPercents() <= 80.0D && actor.getCurrentHpPercents() > 60.0D && RaidHpState == 0)
			{
				spawnMinions(actor, 2);
				AphrosManager._effectZone.setActive(true);
				Skill skill = SkillTable.getInstance().getInfo(14464, 1);
				AphrosManager._effectZone.getTemplate().setZoneSkill(skill);
				RaidHpState += 1;
			}
			else if(actor.getCurrentHpPercents() <= 60.0D && actor.getCurrentHpPercents() > 40.0D && RaidHpState == 1)
			{
				spawnMinions(actor, 3);
				RaidHpState += 1;
			}
			else if(actor.getCurrentHpPercents() <= 40.0D && actor.getCurrentHpPercents() > 20.0D && RaidHpState == 2)
			{
				spawnMinions(actor, 4);
				RaidHpState += 1;
			}
			else if(actor.getCurrentHpPercents() <= 20.0D && actor.getCurrentHpPercents() > 10.0D && RaidHpState == 3)
			{
				Location oldLoc = AphrosManager.AphrosNpc.getLoc();
				AphrosManager.AphrosNpc.deleteMe();
				AphrosManager.AphrosNpc = NpcUtils.spawnSingle(25866, oldLoc);
				AphrosManager.AphrosNpc.setCurrentHp(AphrosManager.AphrosNpc.getCurrentHp() / 10.0D, true);
				Skill skill = SkillTable.getInstance().getInfo(14624, 1);
				AphrosManager._effectZone.getTemplate().setZoneSkill(skill);
			}
		}

		super.onEvtAttacked(attacker, damage);
	}	
	@Override
	public void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if(actor.getNpcId() == 25866)
			AphrosManager.stopRaid();
		super.onEvtDead(killer);
	}
	
	private void spawnMinions(NpcInstance actor, int count)
	{
		List<Player> attackers = World.getAroundPlayers(actor, 900, 200);
		for (int i = 0; i < count; i++)
		{
			NpcInstance minion = NpcUtils.spawnSingle(25865, actor.getLoc());
			minion.getAggroList().addDamageHate(attackers.get(Rnd.get(attackers.size())), 10000, 0);
		}
	}	
}
