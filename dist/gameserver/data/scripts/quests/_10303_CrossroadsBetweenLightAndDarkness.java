package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.data.xml.holder.EventHolder;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.listener.event.OnStartStopListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.PlayerListenerList;
import l2s.gameserver.model.entity.events.EventType;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.commons.util.Rnd;

/**
 * @author pchayka
 */
public abstract class _10303_CrossroadsBetweenLightAndDarkness extends Quest
{
	private class OnPlayerEnterListenerImpl implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			QuestState questState = player.getQuestState(_10303_CrossroadsBetweenLightAndDarkness.this.getClass());
			if(player.getLevel() >= 90 && questState == null && questState.getState() != COMPLETED && questState.getState() != STARTED)
				questState = newQuestState(player, Quest.CREATED);
		}
	}
	
	private static final int YONA = 32909;
	private static final int SECRET_ZHREC = 33343;
	private static final int DARKSTONE = 17747;
	private final int[] locMobs = {22895, 22887, 22879, 22871, 22863};

	public _10303_CrossroadsBetweenLightAndDarkness()
	{
		super(false);
		addKillId(locMobs);
		addQuestItem(DARKSTONE);
		addTalkId(YONA);
		addTalkId(SECRET_ZHREC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("32909-5.htm"))
		{
			st.takeItems(57, 465855);
			st.addExpAndSp(6730155, 2847330);
			st.takeItems(DARKSTONE, -1);
			st.giveItems(getRndRewardYona(), 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		
		if(event.equalsIgnoreCase("33343-5.htm"))
		{
			st.takeItems(57, 465855);
			st.addExpAndSp(6730155, 2847330);
			st.giveItems(getRndRewardZhrec(), 1);
			st.playSound(SOUND_FINISH);
			st.setState(COMPLETED);
		}		
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(st.getPlayer().getLevel() < 90)
			return null;	
		if(st.getState() == COMPLETED)
			return null;
			
		if(st.getCond() == 0 && st.getState() == CREATED && Rnd.get(1000) == 3)
		{
			st.setState(STARTED);
			return null;
		}
		else if(cond == 1 && Rnd.chance(5))
		{	
			if(st.getQuestItemsCount(DARKSTONE) == 0)
				st.giveItems(DARKSTONE, 1);
			return null;
		}

		return null;
	}
	
	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == YONA)
		{
			if(st.getState() == COMPLETED)
				return "32909-comp.htm";
			if(st.getPlayer().getLevel() < 90)
				return "32909-lvl.htm";
				
			if(cond == 1 && st.getQuestItemsCount(DARKSTONE) >= 1)
				return "32909.htm";
		}
		else if(npcId == SECRET_ZHREC)
		{
			if(st.getState() == COMPLETED)
				return "33343-comp.htm";
			if(st.getPlayer().getLevel() < 90)
				return "33343-lvl.htm";
				
			if(cond == 1 && st.getQuestItemsCount(DARKSTONE) >= 1)
				return "33343.htm";
		}
		return htmltext;
	}	
	
	@Override
	public boolean isVisible(Player player)
	{
		if(player.getLevel() < 90)
			return false;
		QuestState questState = player.getQuestState(_10303_CrossroadsBetweenLightAndDarkness.this.getClass());	
		if(questState == null || questState.getState() != STARTED)
			return false;
		return true;	
	}	
	
	private static int getRndRewardYona()
	{
		switch(Rnd.get(12))
		{
			case 1:
			case 2:
			case 3:
				return 13505;
			case 4:
			case 5:
			case 6:	
				return 16108;
			case 7:
			case 8:
			case 9:	
				return 16102;
			case 10:
			case 11:
			case 12:	
				return 16105;
		}
		return 57;
	}

	private static int getRndRewardZhrec()
	{
		switch(Rnd.get(12))
		{
			case 1:
			case 2:
			case 3:
				return 16101;
			case 4:
			case 5:
			case 6:	
				return 16100;
			case 7:
			case 8:
			case 9:	
				return 16099;
			case 10:
			case 11:
			case 12:	
				return 16098;
		}
		return 57;
	}	
}
