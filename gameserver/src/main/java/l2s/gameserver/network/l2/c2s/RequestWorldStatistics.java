package l2s.gameserver.network.l2.c2s;

import java.util.List;

import l2s.gameserver.instancemanager.WorldStatisticsManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.worldstatistics.CategoryType;
import l2s.gameserver.model.worldstatistics.CharacterStatistic;
import l2s.gameserver.network.l2.s2c.ExLoadStatWorldRank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestWorldStatistics extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestWorldStatistics.class);

	private int _section;
	private int _subSection;

	@Override
	protected void readImpl()
	{
		_section = readD();
		_subSection = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		CategoryType cat = CategoryType.getCategoryById(_section, _subSection);
		if(cat == null)
		{
			_log.warn("RequestWorldStatistics: Not found category for section: " + _section + " subsection: " + _subSection);
			return;
		}

		List<CharacterStatistic> generalStatisticList = WorldStatisticsManager.getInstance().getStatisticTop(cat, true, WorldStatisticsManager.STATISTIC_TOP_PLAYER_LIMIT);
		List<CharacterStatistic> monthlyStatisticList = WorldStatisticsManager.getInstance().getStatisticTop(cat, false, WorldStatisticsManager.STATISTIC_TOP_PLAYER_LIMIT);

		activeChar.sendPacket(new ExLoadStatWorldRank(_section, _subSection, generalStatisticList, monthlyStatisticList));
	}
}