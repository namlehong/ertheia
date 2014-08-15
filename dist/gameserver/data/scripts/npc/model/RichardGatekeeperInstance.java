package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.MerchantInstance;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class RichardGatekeeperInstance extends MerchantInstance
{
	private static final long serialVersionUID = 1L;
	private static final String SCENE_VAR = "@" + SceneMovie.SINEMA_ILLUSION_03_QUE.toString().toLowerCase();

	public RichardGatekeeperInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if(val == 0)
		{
			if(player == null)
				return;

			if(!player.getVarBoolean(SCENE_VAR))
			{
				showChatWindow(player, "teleporter/" + getNpcId() + "-no.htm");
				return;
			}
		}
		super.showChatWindow(player, val, arg);
	}
}