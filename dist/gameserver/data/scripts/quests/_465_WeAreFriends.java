package quests;


import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.utils.Location;

public class _465_WeAreFriends extends Quest implements ScriptFile
{
	
	private static final int Feya_Gorozhanin = 32922;
	
	public static final int FEYA_STARTER = 32921;
	
	public static final int COCON = 33147;
	public static final int HUGE_COCON = 33148;
	
	public static final int SIGN_OF_GRATITUDE = 17377;
	
	
	private static NpcInstance npcFeya = null;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _465_WeAreFriends()
	{
		super(true);
		addStartNpc(FEYA_STARTER);
		addTalkId(Feya_Gorozhanin);
		addFirstTalkId(Feya_Gorozhanin);
		addKillId(COCON);
		addKillId(HUGE_COCON);
		addQuestItem(SIGN_OF_GRATITUDE);
		addLevelCheck(90);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32921-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equalsIgnoreCase("32922-4.htm"))
		{
			st.setCond(2);
			st.giveItems(SIGN_OF_GRATITUDE, 2);
			return "despawn_task";
		}
		
		if(event.equalsIgnoreCase("despawn_task"))
		{
			if(npcFeya == null)
				return null;
			st.unset("q465feya");	
			npcFeya.deleteMe();
			npcFeya = null;
			return null;	
		}	
		
		if(event.equalsIgnoreCase("32921-8.htm"))
		{
			st.takeItems(SIGN_OF_GRATITUDE, 2);
			return "reward";
		}

		if(event.equalsIgnoreCase("32921-10.htm"))
		{
			return "reward";
		}
		
		if(event.equalsIgnoreCase("reward"))
		{
				st.giveItems(17378, 1);
				st.unset("cond");
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(this);
				//30384 2-4
				if(st.getQuestItemsCount(SIGN_OF_GRATITUDE) > 0)
				{
					st.giveItems(30384, 2);
					return "32921-10.htm";	
				}
				else
				{
					st.giveItems(30384, 4);
					return "32921-8.htm";					
				}
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
		if(npcId == FEYA_STARTER)
		{
			if(state == 1)
			{
				if(player.getLevel() < 90)
					return "32921-lvl.htm";
				if(!st.isNowAvailable())
					return "32921-comp.htm";
				if(st.getPlayer().getLevel() < 90)
					return "32921-lvl.htm";
				return "32921.htm";
			}
			if(state == 2)
			{
				if(cond == 1)
					return "32921-5.htm";
					
				if(cond == 2)
				{
					return "32921-6.htm";
				}
			}
		}
		return "noquest";
	}
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(Rnd.chance(5))
		{
			npcFeya = Functions.spawn(Location.findPointToStay(st.getPlayer(), 50, 100), Feya_Gorozhanin);
			st.set("q465feya", ""+npcFeya.getObjectId()+"");
			st.startQuestTimer("despawn_task", 180000);
		}
		return null;
	}	
	
	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState st = player.getQuestState(getClass());
		if(st == null)
			return "32922.htm";
		if(st.get("q465feya") != null && Integer.parseInt(st.get("q465feya")) != npc.getObjectId())
			return "32922-1.htm";
		if(st.get("q465feya") == null)
			return "32922-1.htm";				
		return "32922-3.htm";
	}	
}