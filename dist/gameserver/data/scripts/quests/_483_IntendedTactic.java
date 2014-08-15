package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _483_IntendedTactic extends Quest implements ScriptFile
{
	//npc
	public static final int ENDE = 33357;
	//mobs
	private static final int[] mobs = {23069, 23070, 23073, 23071, 23072, 23074, 23075};
	private static final int[] bosses = {25811, 25812, 25815, 25809};
	
	private static final int BLOOD_V = 17736;
	private static final int BLOOD_I = 17737;
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _483_IntendedTactic()
	{
		super(true);
		addStartNpc(ENDE);
		addTalkId(ENDE);
		addKillId(mobs);
		addKillId(bosses);
		addQuestItem(BLOOD_V);
		addQuestItem(BLOOD_I);

		addLevelCheck(48);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33357-6.htm"))
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
		if(npcId == ENDE)
		{
			if(state == 1)
			{
				if(player.getLevel() < 48)
					return "33357-lvl.htm";
				if(!st.isNowAvailable())
					return "33357-comp.htm";
				return "33357.htm";
			}
			if(state == 2)
			{
				if(cond == 1)
					return "33357-8.htm";
					
				if(cond == 2)
				{
					st.unset("cond");
					st.takeItems(BLOOD_V, -1);
					st.takeItems(BLOOD_I, -1);
					st.addExpAndSp(1500000, 1250000);
					st.giveItems(17624, 1);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(this);			
					return "33357-10.htm";		
				}		
			}
		}
		return "noquest";
	}
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond != 1)
			return null;
		if(ArrayUtils.contains(mobs, npc.getNpcId()) && Rnd.chance(10))
		{
			if(st.getQuestItemsCount(BLOOD_V) > 10)
				return null;
			else
				st.giveItems(BLOOD_V, 1);	
			checkItems(st);	
		}	
		if(ArrayUtils.contains(bosses, npc.getNpcId()))
		{
			if(st.getQuestItemsCount(BLOOD_I) > 0)
				return null;
			st.giveItems(BLOOD_I, 1);
			checkItems(st);
		}
		return null;	
	}	
	private static void checkItems(QuestState st)
	{
		if(st.getQuestItemsCount(BLOOD_V) >= 10 && st.getQuestItemsCount(BLOOD_I) > 0)
			st.setCond(2);
	}
}