package ai.hermunkus_message;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.network.l2.components.NpcString;

import instances.MemoryOfDisaster;

/**
 * @author : Ragnarok
 * @date : 02.04.12  16:41
 */
public class DarkElves extends DefaultAI
{
	private static NpcString[] TEXT = {
			NpcString.GAHSHILEN_WHY_MUST_YOU_MAKE_US_SUFFER,
			NpcString.SHILEN_ABANDONED_US_IT_IS_OUR_TIME_TO_DIE,
			NpcString.WITH_OUR_SACRIFICE_WILL_WE_FULFILL_THE_PROPHECY,
			NpcString.BLOODY_RAIN_PLAGUE_DEATH_SHE_IS_NEAR,
			NpcString.ARHHHH,
			NpcString.WE_OFFER_OUR_BLOOD_AS_A_SACRIFICE_SHILEN_SEE_US,
			NpcString.WILL_DARK_ELVES_BE_FORGOTTEN_AFTER_WHAT_WE_HAVE_DONE,
			NpcString.UNBELIEVERS_RUN_DEATH_WILL_FOLLOW_YOU,
			NpcString.I_CURSE_OUR_BLOOD_I_DESPISE_WHAT_WE_ARE_SHILEN
	};

	public DarkElves(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtScriptEvent(String event, Object arg1, Object arg2)
	{
		if(event.equalsIgnoreCase("START_DIE"))
		{
			addTimer(1, 1000);
		}
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		if(timerId == 1)
		{
			if(Rnd.chance(50))
				Functions.npcSayInRange(getActor(), 1000, TEXT[Rnd.get(TEXT.length)]);

			Reflection r = getActor().getReflection();
			if(r instanceof MemoryOfDisaster)
				((MemoryOfDisaster) r).dieNextElf();

			getActor().doDie(getActor());
		}
	}
}