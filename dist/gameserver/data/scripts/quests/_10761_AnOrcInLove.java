package quests;

import l2s.gameserver.Config;
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
import l2s.gameserver.utils.Language;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author Hien Son
 * 
 */
public class _10761_AnOrcInLove extends Quest implements ScriptFile
{

	private static final int VORBOS = 33966;
	
	private final static int TUREK_WAR_HOUND = 20494;
	private final static int TUREK_ORC_PREFECT = 20495;
	private final static int TUREK_ORC_ARCHER = 20496;
	private final static int TUREK_ORC_SKIRMISHER = 20497;
	private final static int TUREK_ORC_SUPPLIER = 20498;
	private final static int TUREK_ORC_FOOTMAN = 20499;
	private final static int TUREK_ORC_SENTINEL = 20500;
	private final static int TUREK_ORC_PRIEST = 20501;
	private final static int TUREK_ORC_ELDER = 20546;

	private static final int STEEL_DOOR_COIN = 37045;
	
	private static final int minLevel = 30;
	private static final int maxLevel = 99;
	
	private static final String TUREK_KILL_LIST = "TUREK_KILL_LIST";
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10761_AnOrcInLove()
	{
		super(false);
		addStartNpc(VORBOS);
		
		addKillNpcWithLog(1, TUREK_KILL_LIST, 30, 	TUREK_WAR_HOUND, 
													TUREK_ORC_PREFECT, 
													TUREK_ORC_ARCHER, 
													TUREK_ORC_SKIRMISHER, 
													TUREK_ORC_SUPPLIER, 
													TUREK_ORC_FOOTMAN, 
													TUREK_ORC_SENTINEL, 
													TUREK_ORC_PRIEST, 
													TUREK_ORC_ELDER);

		addLevelCheck(minLevel, maxLevel);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33966-4.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33966-6.htm"))
		{
			st.giveItems(STEEL_DOOR_COIN, 20, true);
			st.addExpAndSp(354546, 85);
			
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
		
		if(npcId == VORBOS)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				if(cond == 0)
				{
					htmltext = "33966-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33966-4.htm";
				}
				else if(cond == 2)
				{
					htmltext = "33966-5.htm";
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
		
		if(	npcId == TUREK_WAR_HOUND || 
			npcId == TUREK_ORC_PREFECT || 
			npcId == TUREK_ORC_ARCHER ||
			npcId == TUREK_ORC_SKIRMISHER || 
			npcId == TUREK_ORC_SUPPLIER || 
			npcId == TUREK_ORC_FOOTMAN || 
			npcId == TUREK_ORC_SENTINEL || 
			npcId == TUREK_ORC_PRIEST || 
			npcId == TUREK_ORC_ELDER)
		{
			int count = st.getInt(TUREK_KILL_LIST);
			if(Config.DEFAULT_LANG != Language.VIETNAMESE)
			{
				st.getPlayer().sendPacket(new ExShowScreenMessage("Bạn giết được " + count + " Turek Orc", 2000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, false));
			}
			else
			{	
				st.getPlayer().sendPacket(new ExShowScreenMessage("You killed " + count + " Turek Orc", 2000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, false));
			}
			
		}
		
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
		return (player.getLevel() >= minLevel && player.getLevel() <= maxLevel);
	}
}