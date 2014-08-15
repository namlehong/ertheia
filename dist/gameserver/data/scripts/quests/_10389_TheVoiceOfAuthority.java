package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10389_TheVoiceOfAuthority extends Quest implements ScriptFile
{
	//reward items
	private static final int SIGN = 36229;

	private static final int RADZEN = 33803;
	
	private static final String KILL = "kill";

	public _10389_TheVoiceOfAuthority()
	{
		super(false);
		addStartNpc(RADZEN);
		addTalkId(RADZEN);
		
		addKillNpcWithLog(1, KILL, 30, 22139, 22140, 22141, 22147, 22154, 22144, 22145, 22148, 22142, 22155);
		
		addLevelCheck(97);
		addQuestCompletedCheck(_10388_ConspiracyBehindDoor.class);
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
		
		if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.getPlayer().addExpAndSp(592767000, 59276700);
			st.giveItems(SIGN, 1);
			st.giveItems(57, 1302720);
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
		QuestState Cb = st.getPlayer().getQuestState(_10388_ConspiracyBehindDoor.class);
		if(Cb == null || !Cb.isCompleted())
			return "you cannot procceed with this quest until you have completed the Conspiracy Behind Door quest";		
		if(npcId == RADZEN)
		{
			if(cond == 0)
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "notcollected.htm";
			else if(cond == 2)
				htmltext = "collected.htm";
		}
			
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs == null)
			return null;

		if(qs.getState() != STARTED)
			return null;
		if(qs.getCond() != 1)
			return null;
			
		boolean doneKill = updateKill(npc, qs);
		if(doneKill) 
		{
			qs.unset(KILL);
			qs.setCond(2);			
		}
		return null;
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