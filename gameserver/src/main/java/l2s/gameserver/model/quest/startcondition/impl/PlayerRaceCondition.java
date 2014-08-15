package l2s.gameserver.model.quest.startcondition.impl;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.quest.startcondition.ICheckStartCondition;

/**
 * @author : Ragnarok
 * @date : 02.04.12  21:50
 */
public class PlayerRaceCondition implements ICheckStartCondition
{
	private final boolean _human;
	private final boolean _elf;
	private final boolean _delf;
	private final boolean _orc;
	private final boolean _dwarf;
	private final boolean _kamael;
	private final boolean _ertheia;

	public PlayerRaceCondition(boolean human, boolean elf, boolean delf, boolean orc, boolean dwarf, boolean kamael, boolean ertheia)
	{
		_human = human;
		_elf = elf;
		_delf = delf;
		_orc = orc;
		_dwarf = dwarf;
		_kamael = kamael;
		_ertheia = ertheia;
	}

	@Override
	public boolean checkCondition(Player player)
	{
		if(_human && player.getRace() == Race.HUMAN)
			return true;

		if(_elf && player.getRace() == Race.ELF)
			return true;

		if(_delf && player.getRace() == Race.DARKELF)
			return true;

		if(_orc && player.getRace() == Race.ORC)
			return true;

		if(_dwarf && player.getRace() == Race.DWARF)
			return true;

		if(_kamael && player.getRace() == Race.KAMAEL)
			return true;

		if(_ertheia && player.getRace() == Race.ERTHEIA)
			return true;

		return false;
	}
}