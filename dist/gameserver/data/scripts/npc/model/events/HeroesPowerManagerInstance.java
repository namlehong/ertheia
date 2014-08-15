package npc.model.events;

import java.util.StringTokenizer;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
**/
public class HeroesPowerManagerInstance extends NpcInstance
{
	private static final int SCROLL_IMBUED_WITH_HEROES_POWER = 36221; // Свиток Силы Героя
	private static final int TAKE_SCROLL_REUSE_DELAY = 24 * 60 * 60 * 1000; // Сутки
	private static final String EVENT_VARIABLE = "HeroesPowerEvent";

	public HeroesPowerManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("givescroll"))
		{
			long currTime = System.currentTimeMillis();
			String lastUseTime = player.getVar(EVENT_VARIABLE);
			long remainingTime;
			if(lastUseTime != null)
				remainingTime = currTime - Long.parseLong(lastUseTime);
			else
				remainingTime = TAKE_SCROLL_REUSE_DELAY;

			if(remainingTime >= TAKE_SCROLL_REUSE_DELAY)
			{
				showChatWindow(player, "events/heroes_power/" + getNpcId() + "-give_scroll.htm");
				ItemFunctions.addItem(player, SCROLL_IMBUED_WITH_HEROES_POWER, 1, true);
				player.setVar(EVENT_VARIABLE, String.valueOf(currTime), -1);
			}
			else
				showChatWindow(player, "events/heroes_power/" + getNpcId() + "-have_scroll.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}
}