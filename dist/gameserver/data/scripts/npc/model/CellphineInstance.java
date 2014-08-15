package npc.model;

import instances.MemoryOfDisaster;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author : Ragnarok
 * @date : 02.04.12  2:59
 */
public class CellphineInstance extends NpcInstance {
	private static final int INSTANCE_ID = 200;

	public CellphineInstance(int objectId, NpcTemplate template) {
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command) {
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("Call_Hermuncus")) {
			Reflection r = player.getActiveReflection();
			if(r != null) {
				if(player.canReenterInstance(INSTANCE_ID))
					player.teleToLocation(r.getTeleportLoc(), r);
			} else if(player.canEnterInstance(INSTANCE_ID)) {
				ReflectionUtils.enterReflection(player, new MemoryOfDisaster(player), INSTANCE_ID);
			} else {
				showChatWindow(player, "default/33477-no.htm");
			}
		} else
			super.onBypassFeedback(player, command);
	}
}
