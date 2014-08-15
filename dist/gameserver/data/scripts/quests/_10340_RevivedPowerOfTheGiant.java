package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10340_RevivedPowerOfTheGiant extends Quest implements ScriptFile
{
	private static final int SEBION = 32978;
	private static final int PANTEON = 32972;
	
	private static final int HARNAK_GHOST = 25772;

	public _10340_RevivedPowerOfTheGiant()
	{
		super(false);

		addStartNpc(SEBION);

		addTalkId(SEBION);
		addTalkId(PANTEON);
		
		addKillId(HARNAK_GHOST);
		
		addLevelCheck(85);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32978-5.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);			
		}
		
		if(event.startsWith("give"))
		{
			int i = 0;
			if(event.equalsIgnoreCase("giveyellow"))
			{
				st.giveItems(19508, 1);
				i = 1;
			}
			else if(event.equalsIgnoreCase("giveakva"))
			{
				st.giveItems(19509, 1);
				i = 2;
			}
			else if(event.equalsIgnoreCase("givepurple"))
			{
				st.giveItems(19510, 1);
				i = 3;
			}		
			
			st.addExpAndSp(235645257, 97634343);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);					
			
			switch(i)
			{
				case 1:
					return "32972-1.htm";
				case 2:
					return "32972-2.htm";
				case 3:
					return "32972-3.htm";					
			}
			
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		int id = st.getState();

		if(id == COMPLETED)
			return "32978-comp.htm";

		if(st.getPlayer().getLevel() < 85)
			return "32978-lvl.htm";

		if(npcId == SEBION)
		{
			if(cond == 0)
			{
				return "32978.htm";
			}
			if(cond == 1)
				return "32978-6.htm";
			if(cond == 2)
			{
				st.setCond(3);
				return "32978-7.htm";
			}	
			if(cond == 3)
				return "32978-8.htm";
		}
		else if(npcId == PANTEON)
		{
			if(cond == 3)	
				return "32972.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() == 1)
			st.setCond(2);
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
