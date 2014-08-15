package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10386_PathOfMystery extends Quest implements ScriptFile
{

	private static final int TOPOI = 30499;
	private static final int HESET = 33780;
	private static final int BERNA = 33796;
	

	public _10386_PathOfMystery()
	{
		super(false);
		addStartNpc(TOPOI);
		addTalkId(TOPOI);
		addTalkId(HESET);
		addTalkId(BERNA);
		
		
		addLevelCheck(93);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accepted.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equalsIgnoreCase("acceptedHeset.htm"))
		{
			st.setCond(3);
		}	
		if(event.equalsIgnoreCase("acceptedBerma.htm"))
		{
			st.setCond(4);
		}			
		if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.getPlayer().addExpAndSp(27244350, 2724435);
			st.giveItems(57, 58707);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		if(npcId == TOPOI)
		{
			if(cond == 0)
				htmltext = "start.htm";
		}
		else if(npcId == HESET)
		{
			if(cond == 1)
				htmltext = "hesetCond1.htm";
			if(cond == 4)
				htmltext = "collected.htm";
		}
		else if(npcId == BERNA)
		{
			if(cond == 3)
				htmltext = "berna.htm";
				
		}	
		return htmltext;
	}
	
	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}