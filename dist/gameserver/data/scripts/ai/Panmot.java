package ai;


import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.scripts.Functions;


public class Panmot extends DefaultAI
{
	public Panmot(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();

		if(Math.random() < 0.001)
		{
			if(Math.random()<0.5)
				Functions.npcShout(actor, "Ê bạn ơi, bạn có thích vũ khí Apocalypse không?");
			else
				Functions.npcShout(actor, "Hà hà, bạn có gan thử trình độ enchant của mình không?");
			
			return true;
		}
		
		if(randomAnimation())
			return true;

		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{}
}