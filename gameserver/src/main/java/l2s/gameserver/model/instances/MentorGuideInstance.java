package l2s.gameserver.model.instances;

import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.templates.npc.NpcTemplate;

public class MentorGuideInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public MentorGuideInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("changediplom"))
		{
			if(player.getInventory().getCountOf(33800) > 0)
			{
				player.getInventory().destroyItemByItemId(33800, 1); //take cert of mentee
				Functions.addItem(player, 33805, 40); //give cert of graduation 40 units
				showChatWindow(player, 0);
				return;
			}
			else
			{
				showChatWindow(player, "mentoring/menthelper-no.htm");
				return;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		if(val == 0)
			return "mentoring/menthelper.htm";
		return "mentoring/menthelper-" + val + ".htm";
	}
}