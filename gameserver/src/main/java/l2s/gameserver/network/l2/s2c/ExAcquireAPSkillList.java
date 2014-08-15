package l2s.gameserver.network.l2.s2c;

import java.util.Collection;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;

/**
 * @author Bonux
**/
public class ExAcquireAPSkillList extends L2GameServerPacket
{
	private final boolean _avaiable;
	private final long _abilitiesRefreshPrice;
	private final long _abilityPointSPCost;
	private final int _maxAbilitiesPoints;
	private final int _allowAbilitiesPoints;
	private final int _usedPoints;
	private final Collection<Skill> _learnedSkills;

	public ExAcquireAPSkillList(Player player)
	{
		_avaiable = player.isAllowAbilities();
		_abilitiesRefreshPrice = Player.getAbilitiesRefreshPrice();
		_abilityPointSPCost = player.getAbilityPointSPCost();
		_maxAbilitiesPoints = Player.getMaxAbilitiesPoints();
		_allowAbilitiesPoints = player.getAllowAbilitiesPoints();
		_usedPoints = player.getUsedAbilitiesPoints();
		_learnedSkills = player.getLearnedAbilitiesSkills();
	}

	@Override
	protected void writeImpl()
	{
		writeD(_avaiable ? 0x01 : 0x00);	// Разрешено ли использовать.
		writeQ(_abilitiesRefreshPrice);	// Цена сброса способностей.
		writeQ(_abilityPointSPCost);	// Цена SP покупки одного очка способностей.
		writeD(_maxAbilitiesPoints);	// Максимальное количество очков.
		writeD(_allowAbilitiesPoints);	// Полученые очки.
		writeD(_usedPoints);	// Использованные очки.
		writeD(_learnedSkills.size());	// Количество изученных умений.
		for(Skill skill : _learnedSkills)
		{
			writeD(skill.getId()); // ID Изученного умения.
			writeD(skill.getLevel()); // Уровень Изученного умения.
		}
	}
}
