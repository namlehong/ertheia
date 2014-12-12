package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author Hien Son
 * 
 */
public class _10757_QuietingTheStorm extends Quest implements ScriptFile
{

	private static final int PIO = 33963;
	
	private final static int WIND_VORTEX = 23417;
	private final static int GIANT_WINDIMA = 23419;
	private final static int IMMENSE_WINDIMA = 23420;

	private static final int STEEL_DOOR_COIN = 37045;
	
	private static final int minLevel = 24;
	private static final int maxLevel = 99;
	
	private static final String VORTEX_KILL_LIST = "vortex_kill_list";
	private static final String WINDIMA_KILL_LIST = "windima_kill_list";
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10757_QuietingTheStorm()
	{
		super(false);
		addStartNpc(PIO);
		
		
		addKillNpcWithLog(1, WINDIMA_KILL_LIST, 1, GIANT_WINDIMA, IMMENSE_WINDIMA);
		addKillNpcWithLog(1, VORTEX_KILL_LIST, 5, WIND_VORTEX);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33963-5.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33963-7.htm"))
		{
			st.giveItems(STEEL_DOOR_COIN, 7, true);
			st.addExpAndSp(632051, 151);
			
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{	
		String htmltext = "noquest";
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		
		if(npcId == PIO)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				if(cond == 0)
				{
					htmltext = "33963-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33963-5.htm";
				}
				else if(cond == 2)
				{
					htmltext = "33963-6.htm";
				}
			}
			else
				htmltext = "noquest";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		/*TODO
		 * Sniff packet after kill in this quest, make another packet for notify the counter in client
		 * the current packet ExQuestNpcLogList doesn't work
		 */
		if(updateKill(npc, st))
		{
			st.playSound(SOUND_MIDDLE);
			st.setCond(cond+1);
		}
		
		return null;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState qs = player.getQuestState(_10756_AnInterdimensionalDraft.class);
		
		return (player.getLevel() >= minLevel && 
				player.getLevel() <= maxLevel && 
				player.getRace() == Race.ERTHEIA && 
				qs != null && 
				qs.getState() == COMPLETED);
	}
}