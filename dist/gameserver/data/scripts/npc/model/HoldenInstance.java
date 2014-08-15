package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.templates.npc.NpcTemplate;
import quests._10323_GoingIntoARealWar;

/**
 * @author Bonux
 */
public final class HoldenInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public HoldenInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("enter_to_underground"))
		{
			QuestState qs = player.getQuestState(_10323_GoingIntoARealWar.class);
			if(qs == null || qs.getCond() != 1)
			{
				showChatWindow(player, "default/" + getNpcId() + "-no.htm");
				return;
			}

			Quest quest = qs.getQuest();
			if(quest == null)
			{
				showChatWindow(player, "default/" + getNpcId() + "-no.htm");
				return;
			}

			quest.onTalk(this, qs);
			return;
		}
		else
			super.onBypassFeedback(player, command);
	}
}
