package ai.GuillotineFortress;

import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.utils.NpcUtils;

/*
 * User: Iqman
 * Date: 21.05.13
 * Time: 19:39
 * Location: Guillotine Fortress raid stage 1
 */
//TODO find the manument ID and their skill when they ressurect the raid boss
public class Mob_25888 extends Fighter
{
    public Mob_25888(NpcInstance actor)
	{
        super(actor);
    }

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance npc = NpcUtils.spawnSingle(25885, getActor().getLoc(), getActor().getReflection());
		npc.getAggroList().addDamageHate(killer, 10000, 10000);	
		super.onEvtDead(killer);
	}	

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}
