package l2s.gameserver.stats.conditions;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.stats.Env;

/**
 * @author Bonux
**/
public class ConditionPlayerQuestState extends Condition
{
	private final String _questName;
	private final int _cond;

	public ConditionPlayerQuestState(String questName, int cond)
	{
		_questName = questName;
		_cond = cond;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		if(!env.character.isPlayer())
			return false;

		Player player = env.character.getPlayer();
		QuestState qs = player.getQuestState(_questName);
		if(qs == null)
			return false;

		return qs.getCond() == _cond;
	}
}
