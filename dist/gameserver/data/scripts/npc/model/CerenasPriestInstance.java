package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

import quests._10369_NoblesseSoulTesting;

/**
 * @author Bonux
 */
public final class CerenasPriestInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public CerenasPriestInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("enter_to_eva"))
		{
			QuestState qs = player.getQuestState(_10369_NoblesseSoulTesting.class);
			if(qs == null || qs.getState() != Quest.STARTED)
				showChatWindow(player, "default/" + getNpcId() + "-no_enter.htm");
			else
				qs.getQuest().onEvent("enter_instance", qs, this);
		}
		else
			super.onBypassFeedback(player, command);
	}
}