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
public class _10780_AWeakenedBarrier extends Quest implements ScriptFile
{

	private static final int ANDY = 33845;
	private static final int BACON = 33846;
	
	private final static int GIANT_FUNGUS = 20555;
	private final static int ROTTING_TREE = 20558;
	private final static int CORRODED_SKELETON = 23305;
	private final static int ROTTEN_CORPSE = 23306;
	private final static int CORPSE_SPIDER = 23307;
	private final static int EXPLOSIVE_SPIDER = 23308;
	
	private static final int STEEL_DOOR_COIN = 37045;
	private static final int SCROLL_EWB = 947;
	
	private static final int minLevel = 30;
	private static final int maxLevel = 99;
	
	private static final String SPORES_KILL_LIST = "SPORES_KILL_LIST";
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10780_AWeakenedBarrier()
	{
		super(false);
		addStartNpc(ANDY);
		addTalkId(BACON);
		
		addKillNpcWithLog(1, SPORES_KILL_LIST, 20, 	GIANT_FUNGUS, 
													ROTTING_TREE, 
													CORRODED_SKELETON, 
													ROTTEN_CORPSE, 
													CORPSE_SPIDER, 
													EXPLOSIVE_SPIDER);

		addLevelCheck(minLevel, maxLevel);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33845-4.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33846-2.htm"))
		{
			st.giveItems(STEEL_DOOR_COIN, 37);
			st.giveItems(SCROLL_EWB, 5);
			st.addExpAndSp(3811500, 914);
			
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
		
		if(npcId == ANDY)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				if(cond == 0)
				{
					htmltext = "33845-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33845-4.htm";
				}
			}
		
		}
		
		if(npcId == BACON)
		{
			if(cond == 2)
			{
				htmltext = "33846-1.htm";
			}
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
		
		if(	npcId == GIANT_FUNGUS || 
			npcId == ROTTING_TREE || 
			npcId == CORRODED_SKELETON ||
			npcId == ROTTEN_CORPSE || 
			npcId == CORPSE_SPIDER || 
			npcId == EXPLOSIVE_SPIDER)
		{
			int count = st.getInt(SPORES_KILL_LIST)+1;
			st.getPlayer().sendPacket(new ExShowScreenMessage("Bạn giết được " + count + " quái vật yêu cầu", 2000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, false));
			
		}
		
		if(updateKill(npc, st))
		{
			st.playSound(SOUND_MIDDLE);
			st.setCond(2);
		}
		
		return null;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		return (player.getLevel() >= minLevel && player.getLevel() <= maxLevel);
	}
}