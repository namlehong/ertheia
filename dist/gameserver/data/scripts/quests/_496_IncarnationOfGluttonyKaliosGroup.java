package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _496_IncarnationOfGluttonyKaliosGroup extends Quest implements ScriptFile
{
	//npc
	public static final int KARTIA_RESEARCH = 33647;
	
	//mobs
	public static final int KALIOS = 25884;
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _496_IncarnationOfGluttonyKaliosGroup()
	{
		super(true);
		addStartNpc(KARTIA_RESEARCH);
		addTalkId(KARTIA_RESEARCH);
		addKillId(KALIOS);
		addLevelCheck(95);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33647-2.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int state = st.getState();
		int cond = st.getCond();
		if(npcId == KARTIA_RESEARCH)
		{
			if(state == 1)
			{
				if(!st.isNowAvailable())
					return "33647-3.htm";			
				return "33647.htm";
			}
			if(state == 2)
			{
				if(cond == 1)
					return "33647-2.htm";
				if(cond == 2)
				{
					st.giveItems(34929, 1);
					st.unset("cond");
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(this);			
					return "33647-4.htm";
				}					
			}
		}
		return "noquest";
	}
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond != 1 || npc == null)
			return null;
		st.setCond(2);
		return null;
	}	

    @Override
    public boolean isVisible(Player player) 
	{
		return checkStartCondition(player);
    }		
}