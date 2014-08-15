package l2s.gameserver.model.quest.startcondition.impl;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.startcondition.ICheckStartCondition;

/**
 * @author : Ragnarok
 * @date : 07.02.12  3:29
 */
public final class QuestCompletedCondition implements ICheckStartCondition
{
	private final String _questName;

	public QuestCompletedCondition(String questName)
	{
		_questName = questName;
	}

	@Override
	public final boolean checkCondition(Player player)
	{
		return player.isQuestCompleted(_questName);
	}
}
