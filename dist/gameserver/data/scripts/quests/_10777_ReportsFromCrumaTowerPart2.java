package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.UsmVideo;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Hien Son
 */
public class _10777_ReportsFromCrumaTowerPart2 extends Quest implements ScriptFile
{
	//NPC's
	private static final int BElKADHI = 30485;
	private static final int MAGIC_OWL = 33991;

	private static final int STEEL_DOOR_COIN = 37045;
	private static final int SCROLL_EAC = 952;
	
	private static final int minLevel = 49;
	private static final int maxLevel = 99;
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
	
	public _10777_ReportsFromCrumaTowerPart2()
	{
		super(false);
		addStartNpc(BElKADHI);
		addTalkId(MAGIC_OWL);
		
		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		
		if(event.equalsIgnoreCase("summon_owl"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			
			st.addSpawn(MAGIC_OWL, 17720, 108424, -9083, 180000);
		}
		
		if(event.equalsIgnoreCase("dismiss_owl"))
		{
			st.setCond(2);
			
			npc.deleteMe();
		}
		
		if(event.equalsIgnoreCase("30485-8.htm"))
		{
			st.giveItems(STEEL_DOOR_COIN, 4);
			st.giveItems(SCROLL_EAC, 2);
			st.addExpAndSp(151263, 36);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == BElKADHI)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "30485-1.htm";
			}
			else if(cond == 1)
			{
				htmltext = "30485-6.htm";
			}
			else if(cond == 2)
			{
				htmltext = "30485-7.htm";
			}
		}
		else if(npcId == MAGIC_OWL && st.getCond() == 1)
		{
			htmltext = "33991-1.htm";
		}
		
		return htmltext;
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState qs = player.getQuestState(_10776_TheWrathOfTheGiants.class);
		
		return (player.getLevel() >= minLevel && 
				player.getLevel() <= maxLevel && 
				qs != null && 
				qs.getState() == COMPLETED);
	}
	
}