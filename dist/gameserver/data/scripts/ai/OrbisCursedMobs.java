package ai;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.Location;

public class OrbisCursedMobs extends Fighter
{
	private static final int[] Orbis_Zhertva = { 22911, 22912, 22913, 25833 };
	private static final int[] Orbis_Strazh = { 22914, 22915, 22916, 25834 };
	private static final int[] Orbis_Metatel = { 22917, 22918, 22919, 25835, 22920 };
	private static final int[] Orbis_ychennui = { 22921, 22922, 22923, 25836 };
	private static final int[] Orbis_drevnui = { 22924, 22925 };
	private static final int[] Orbis_starwii = { 22926, 22927 };
	
	public OrbisCursedMobs(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
			
		if (actor.isDead())
			return;
			
		if(Rnd.chance(1))
		{
			spawnDamn(actor.getNpcId(), actor.getLoc(), attacker);
			actor.doDie(attacker);
			actor.decayMe();
		}	
			
		super.onEvtAttacked(attacker, damage);
	}	
	
	public void spawnDamn(int npcId, Location loc, Creature attacker)
	{
		int cursedId = 18978;

		if (ArrayUtils.contains(Orbis_Zhertva, npcId))
			cursedId = 18978;
		else if (ArrayUtils.contains(Orbis_Strazh, npcId))
			cursedId = 18979;
		else if (ArrayUtils.contains(Orbis_Metatel, npcId))
			cursedId = 18980;
		else if (ArrayUtils.contains(Orbis_ychennui, npcId))
			cursedId = 18981;
		else if (ArrayUtils.contains(Orbis_drevnui, npcId))
			cursedId = 18982;
		else if (ArrayUtils.contains(Orbis_starwii, npcId))
			cursedId = 18983;
			
		NpcInstance cursed = NpcUtils.spawnSingle(cursedId, loc);
		cursed.getAggroList().addDamageHate(attacker, 10000, 0);			
  }	
}
