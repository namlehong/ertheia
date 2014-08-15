package ai.seedofinfinity;

import l2s.commons.lang.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.utils.Location;

public class YehanBrother extends Fighter
{
	private long _spawnTimer = 0;
	private static final int[] _minions = ArrayUtils.createAscendingArray(22509, 22512);

	public YehanBrother(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		_spawnTimer = System.currentTimeMillis();
	}

	private NpcInstance getBrother()
	{
		NpcInstance actor = getActor();
		int brotherId = 0;
		if(actor.getNpcId() == 25665)
			brotherId = 25666;
		else if(actor.getNpcId() == 25666)
			brotherId = 25665;
		for(NpcInstance npc : actor.getReflection().getNpcs())
			if(npc.getNpcId() == brotherId)
				return npc;
		return null;
	}

	@Override
	protected void thinkAttack()
	{
		NpcInstance actor = getActor();
		NpcInstance brother = getBrother();
		if(!brother.isDead() && !actor.isInRange(brother, 300))
			actor.altOnMagicUse(getActor(), SkillTable.getInstance().getInfo(6371, 1));
		else
			removeInvul(actor);
		if(_spawnTimer + 40000 < System.currentTimeMillis())
		{
			_spawnTimer = System.currentTimeMillis();
			NpcInstance mob = actor.getReflection().addSpawnWithoutRespawn(_minions[Rnd.get(_minions.length)], Location.findAroundPosition(actor, 300), 0);
			mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, actor.getAggressionTarget(), 1000);
		}
		super.thinkAttack();
	}

	private void removeInvul(NpcInstance npc)
	{
		npc.getEffectList().stopEffects(6371);
	}
}