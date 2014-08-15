package ai.feyas;

import l2s.commons.collections.LazyArrayList;
import l2s.commons.threading.RunnableImpl;

import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;

public class SwampSpirit extends DefaultAI
{

	public SwampSpirit(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		
		if(actor == null)
			return false;

		for(Player player : World.getAroundPlayers(actor, 300, 300))
		{
			if(player != null && !player.isDead() && !player.isAlikeDead() && player.getCurrentHp() != player.getMaxHp())
			{
				Skill skill;
				if(actor.getNpcId() == 32915)
				{
					skill = SkillTable.getInstance().getInfo(14064, 1);
					actor.broadcastPacket(new MagicSkillUse(actor, player, 14064, 1, 0, 0, false));
				}
				else
				{
					skill = SkillTable.getInstance().getInfo(14065, 1);
					actor.broadcastPacket(new MagicSkillUse(actor, player, 14065, 1, 0, 0, false));
				}
				LazyArrayList<Creature> target = new LazyArrayList<Creature>(); //if more than one
				target.add(player);
				actor.callSkill(skill, target, true);
				//Thread.sleep(1500); //to let them finish the casting! to do thread.
				actor.decayMe();
				target.clear();
				target = null;
				ThreadPoolManager.getInstance().schedule(new SpawnTask(actor), 180000L);
				return true; //one player only
			}
		}
		return false;
	}

	private class SpawnTask extends RunnableImpl
	{
		private NpcInstance _npc;

		public SpawnTask(NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl()
		{
			_npc.spawnMe();
		}
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{}
}