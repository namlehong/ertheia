package l2s.gameserver.model.entity.olympiad;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.Announcements;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.network.l2.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CompEndTask extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(CompEndTask.class);

	@Override
	public void runImpl() throws Exception
	{
		if(Olympiad.isOlympiadEnd())
			return;

		Olympiad._inCompPeriod = false;

		try
		{
			OlympiadManager manager = Olympiad._manager;

			// Если остались игры, ждем их завершения еще одну минуту
			if(manager != null && !manager.getOlympiadGames().isEmpty())
			{
				ThreadPoolManager.getInstance().schedule(new CompEndTask(), 60000);
				return;
			}

			Announcements.getInstance().announceToAll(SystemMsg.MUCH_CARNAGE_HAS_BEEN_LEFT_FOR_THE_CLEANUP_CREW_OF_THE_OLYMPIAD_STADIUM);
			_log.info("Olympiad System: Olympiad Game Ended");
			OlympiadDatabase.save();
		}
		catch(Exception e)
		{
			_log.warn("Olympiad System: Failed to save Olympiad configuration:");
			_log.error("", e);
		}
		Olympiad.init();
	}
}