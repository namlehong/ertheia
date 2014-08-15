package npc.model;

import java.util.StringTokenizer;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.EventTriggerPacket;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
 */
public class TowerOfJusticeInstance extends NpcInstance
{
	private class TriggerDeactivate extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			ACTIVE = false;
			broadcastPacket(new EventTriggerPacket(TRIGGER_ID, false));
		}
	}

	private static final long serialVersionUID = 1L;

	private static final int TRIGGER_ID = 17250700;

	private static boolean ACTIVE = false;

	public TowerOfJusticeInstance(int objectId, NpcTemplate template)
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
		if(cmd.equals("condolence"))
		{
			if(ACTIVE)
				return;

			ACTIVE = true;
			broadcastPacket(new EventTriggerPacket(TRIGGER_ID, true));
			ThreadPoolManager.getInstance().schedule(new TriggerDeactivate(), 3000L);
		}
		else
			super.onBypassFeedback(player, command);
	}
}
