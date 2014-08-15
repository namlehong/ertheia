package npc.model;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.model.World;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.entity.Reflection;

import java.util.concurrent.ScheduledFuture;
import l2s.gameserver.model.instances.NpcInstance;

import instances.LindviorBoss;

public class GeneratorLindviorInstance extends MonsterInstance
{
	private static final long serialVersionUID = 1L;

	public GeneratorLindviorInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	private static boolean blockedChargeBlocked = true;
	private ScheduledFuture<?> _checkState;
	public static final Skill SKILL = SkillTable.getInstance().getInfo(15605, 1);
	
	@Override
	protected void onSpawn()
	{
		_checkState = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckState(this), 3000, 3000);
	}	
	
	private class CheckState implements Runnable
	{
		NpcInstance _npc;
		public CheckState(NpcInstance npc)
		{
			_npc = npc;
		}
		@Override
		public void run()
		{
			/*for(NpcInstance mob : World.getAroundNpc(_npc, 700, 700))
			{
				if(mob != null && mob.isMonster() && !mob.isDead())
				{
					blockedChargeBlocked = true;
					setNpcState(2);
					return;
				}	
			}//if not found we presume that no need to continue
			*/
			setNpcState(1);
			//blockedChargeBlocked = false;
			if(!getEffectList().containsEffects(SKILL) && !isCastingNow() && getNpcState() != 0) //to avoid flood
				doCast(SKILL, _npc, false);
		}
	}		

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		Reflection r = getReflection();	
		if (r != null)
		{
			if(r instanceof LindviorBoss)
			{
				LindviorBoss lInst = (LindviorBoss) r;	
				lInst.endInstance();
			}	
		}		
	}
	
	@Override
	public boolean isChargeBlocked()
	{
		if(getNpcState() == 0)
			return true;
		return !getEffectList().containsEffects(SKILL);
	}
	
	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp)
	{
		if(attacker.getPlayer() != null)
			return;
		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);	
	}	
}