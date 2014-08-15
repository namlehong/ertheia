package npc.model;

import java.util.StringTokenizer;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.AwakeningManagerInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.ExChangeToAwakenedClass;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
 */
public class AgentOfChaosInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public AgentOfChaosInstance(int objectId, NpcTemplate template)
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

			if(!checkCond(player))
				return;
		}
		super.showChatWindow(player, val, arg);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("reawake"))
		{
			if(!checkCond(player))
				return;

			if(!st.hasMoreTokens())
			{
				showChatWindow(player, "default/" + getNpcId() + "-reawake.htm");
				return;
			}

			ClassId classId = player.getClassId();
			String cmd2 = st.nextToken();
			if(cmd2.equals("list"))
			{
				StringBuilder classes = new StringBuilder();
				for(ClassId c : ClassId.VALUES)
				{
					if(!c.isOfLevel(ClassLevel.AWAKED))
						continue;

					if(c.isOutdated())
						continue;

					if(!c.isOfType2(classId.getType2()))
						continue;

					if(c == classId)
						continue;

					classes.append("<button value=\"");
					classes.append(c.getName(player));
					classes.append("\" action=\"bypass -h npc_%objectId%_reawake_try_");
					classes.append(String.valueOf(c.getId()));
					classes.append("\" width=\"200\" height=\"31\" back=\"L2UI_CT1.HtmlWnd_DF_Awake_Down\" fore=\"L2UI_CT1.HtmlWnd_DF_Awake\"><br>");
				}

				if(classId.isOutdated())
					showChatWindow(player, "default/" + getNpcId() + "-reawake_list.htm", "<?CLASS_LIST?>", classes.toString());
				else
					showChatWindow(player, "default/" + getNpcId() + "-reawake_list_essense.htm", "<?CLASS_LIST?>", classes.toString());
			}
			else if(cmd2.equals("try"))
			{
				if(!st.hasMoreTokens())
					return;

				ClassId awakedClassId = ClassId.VALUES[Integer.parseInt(st.nextToken())];
				if(awakedClassId == classId)
					return;

				if(!awakedClassId.isOfType2(classId.getType2()))
					return;

				player.setVar(AwakeningManagerInstance.getAwakeningRequestVar(classId), String.valueOf(awakedClassId.getId()), -1);
				player.sendPacket(new ExChangeToAwakenedClass(player, this, awakedClassId.getId()));
				return;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	private boolean checkCond(Player player)
	{
		ClassId classId = player.getClassId();
		if(!classId.isOfLevel(ClassLevel.AWAKED))
		{
			showChatWindow(player, "default/" + getNpcId() + "-no.htm");
			return false;
		}

		if(player.isHero())
		{
			showChatWindow(player, "default/" + getNpcId() + "-no_hero.htm");
			return false;
		}

		if(!classId.isOutdated())
		{
			if(player.isBaseClassActive())
			{
				if(ItemFunctions.getItemCount(player, AwakeningManagerInstance.CHAOS_ESSENCE) == 0)
				{
					showChatWindow(player, "default/" + getNpcId() + "-no_already_reawakened.htm");
					return false;
				}
			}
			else if(player.isDualClassActive())
			{
				if(ItemFunctions.getItemCount(player, AwakeningManagerInstance.CHAOS_ESSENCE) > 0 || ItemFunctions.getItemCount(player, AwakeningManagerInstance.CHAOS_ESSENCE_DUAL_CLASS) == 0)
				{
					showChatWindow(player, "default/" + getNpcId() + "-no_already_reawakened.htm");
					return false;
				}
			}
			else
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_already_reawakened.htm");
				return false;
			}
		}

		if(player.getServitors().length > 0)
		{
			showChatWindow(player, "default/" + getNpcId() + "-no_summon.htm");
			return false;
		}

		return true;
	}
}
