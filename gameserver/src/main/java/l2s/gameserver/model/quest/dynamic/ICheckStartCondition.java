package l2s.gameserver.model.quest.dynamic;

import l2s.gameserver.model.Player;

public interface ICheckStartCondition
{
	public boolean checkCondition(Player player);
}