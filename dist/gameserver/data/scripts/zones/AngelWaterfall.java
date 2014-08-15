package zones;

import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

import quests._10301_ShadowOfTerrorBlackishRedFog;

public class AngelWaterfall implements ScriptFile
{
	private static final String TELEPORT_ZONE_NAME = "[25_20_telzone_to_magmeld]";
	private static final Location TELEPORT_LOC = new Location(207559, 86429, -1000);

	private static ZoneListener _zoneListener;

	private void init()
	{
		_zoneListener = new ZoneListener();
		Zone zone = ReflectionUtils.getZone(TELEPORT_ZONE_NAME);
		zone.addListener(_zoneListener);
	}

	@Override
	public void onLoad()
	{
		init();
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			if(zone == null)
				return;

			if(cha == null)
				return;
			Player player = cha.getPlayer();
			if(player == null)
				return;

			QuestState qs = player.getQuestState(_10301_ShadowOfTerrorBlackishRedFog.class);
			if(qs != null && qs.getCond() == 3 && player.getVar("instance10301") == null)
			{
				Quest q = QuestManager.getQuest(10301);
				player.processQuestEvent(q.getName(), "enterInstance", null);
				//player.setVar("instance10301", "true", -1);
			}
			else
				cha.teleToLocation(TELEPORT_LOC);
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{}
	}
}