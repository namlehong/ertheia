package ai;

import l2s.gameserver.GameTimeController;
import l2s.gameserver.ai.NpcAI;
import l2s.gameserver.listener.game.OnDayNightChangeListener;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.Functions;

/**
 * @author Bonux
**/
public class BloodySwampland extends NpcAI
{
	public BloodySwampland(NpcInstance actor)
	{
		super(actor);
		GameTimeController.getInstance().addListener(new BSDayNightListener());
	}

	private class BSDayNightListener implements OnDayNightChangeListener
	{
		private BSDayNightListener()
		{
			if(GameTimeController.getInstance().isNowNight())
				onNight();
			else
				onDay();
		}

		/**
		 * Вызывается, когда на сервере наступает день
		 */
		@Override
		public void onDay()
		{
			Functions.npcSay(getActor(), NpcString.LOVELY_PLAGUEWORMS_CONTAMINATE_THE_SWAMP_EVEN_MORE);
		}

		/**
		 * Вызывается, когда на сервере наступает ночь
		 */
		@Override
		public void onNight()
		{
			Functions.npcSay(getActor(), NpcString.LOVELY_PLAGUEWORMS_CONTAMINATE_THE_SWAMP_EVEN_MORE);
		}
	}
}