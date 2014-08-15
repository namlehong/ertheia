package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _468_FollowingAnUnknownSmell extends Quest implements ScriptFile
{
	private static final String Npc_kill1 = "mob_kill1";
	private static final String Npc_kill2 = "mob_kill2";
	private static final String Npc_kill3 = "mob_kill3";
	private static final String Npc_kill4 = "mob_kill4";
	
	public _468_FollowingAnUnknownSmell()
	{
		super(false);

		addStartNpc(33032);
		addTalkId(33032);
		
		addKillNpcWithLog(1, Npc_kill1, 10, 22962);
		addKillNpcWithLog(1, Npc_kill2, 10, 22958);
		addKillNpcWithLog(1, Npc_kill3, 10, 22960);
		addKillNpcWithLog(1, Npc_kill4, 10, 22959);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33032-04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33032-07.htm"))
		{
			st.giveItems(30385, 2, false);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(this);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == 33032)
		{
			if(!st.isNowAvailable())
				return "33032-08.htm";
			if(st.getPlayer().getLevel() < 90)
				return "33032-02.htm"; 
			else if(cond == 0)
				return "33032-01.htm";
			else if(cond == 1)
				return "33032-05.htm";
			else if(cond == 2)
				return "33032-06.htm";
		}	
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() != 1)
			return null;
		boolean doneKill = updateKill(npc, st);
		if(doneKill)
		{
			st.unset(Npc_kill1);
			st.unset(Npc_kill2);
			st.unset(Npc_kill3);
			st.unset(Npc_kill4);
			st.setCond(2);
		}
		return null;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		return player.getLevel() >= 90;
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