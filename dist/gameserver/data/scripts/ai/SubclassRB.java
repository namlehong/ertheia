package ai;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.NpcInstance;

public class SubclassRB extends Fighter
{
	private final static int SHILLEN_MESSAGER = 27470; //GoD Update
	private final static int DEATH_LORD = 27477; //GoD Update
	private final static int KERNON = 27473;	// GOD Update
	private final static int LONGHORN = 27476;  // GoD Update

	private final static int CABRIOCOFFER = 31027;
	private final static int CHEST_KERNON = 31028;
	private final static int CHEST_GOLKONDA = 31029;
	private final static int CHEST_HALLATE = 31030;

	public SubclassRB(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		switch(actor.getNpcId())
		{
			case SHILLEN_MESSAGER:
				chestSelect(actor,CABRIOCOFFER);
				break;
			case DEATH_LORD:
				chestSelect(actor,CHEST_HALLATE);
				break;
			case KERNON:
				chestSelect(actor,CHEST_KERNON);
				break;
			case LONGHORN:
				chestSelect(actor,CHEST_GOLKONDA);
				break;
		}
		super.onEvtDead(killer);
	}

	private void chestSelect(NpcInstance actor, int npcId)
	{
		NpcInstance chest = NpcHolder.getInstance().getTemplate(npcId).getNewInstance();
		chest.spawnMe(actor.getLoc());
		ThreadPoolManager.getInstance().schedule(new ChestDespawnTask(chest), 120*1000); // Удаляем через 2 минуты
	}

	class ChestDespawnTask extends RunnableImpl
	{
		final NpcInstance _chest;

		public ChestDespawnTask(NpcInstance chest)
		{
			_chest = chest;
		}

		@Override
		public void runImpl()
		{
			if(_chest != null)
				_chest.deleteMe();
		}
	}
}
