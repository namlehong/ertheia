package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.instancemanager.DailyQuestsManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _474_WaitingForTheSummer extends Quest implements ScriptFile
{
	//npc
	public static final int GUIDE = 33463;
	public static final int VUSOTSKII = 31981;
	
	//mobs
	private final int[] Byval = {22093, 22094};
	private final int[] Yryna = {22095, 22096};
	private final int[] Yeti = {22097, 22097};
	
	//q items
	public static final int MEAT_BYVAL = 19490;
	public static final int MEAT_YRYS = 19491;
	public static final int MEAT_YETI = 19492;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _474_WaitingForTheSummer()
	{
		super(true);
		addStartNpc(GUIDE);
		addTalkId(VUSOTSKII);
		addKillId(Byval);
		addKillId(Yryna);
		addKillId(Yeti);
		addQuestItem(MEAT_BYVAL);
		addQuestItem(MEAT_YRYS);
		addQuestItem(MEAT_YETI);
		addLevelCheck(60, 64);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33463-3.htm"))
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
		if(npcId == GUIDE)
		{
			if(state == 1)
			{
				if(!checkStartCondition(st.getPlayer()))
					return "33463-lvl.htm";
				if(!st.isNowAvailable())
					return "33463-comp.htm";
				return "33463.htm";
			}
			if(state == 2)
			{
				if(cond == 1)
					return "33463-4.htm";
				if(cond == 2)
					return "33463-5.htm";
			}
			
		}
		if(npcId == VUSOTSKII && state == 2)
		{
			if(cond == 1)
				return "31981-1.htm";
			if(cond == 2)
			{
				st.giveItems(57,194000);
				st.addExpAndSp(1879400, 1782000);
				st.takeItems(MEAT_BYVAL, -1);
				st.takeItems(MEAT_YRYS, -1);
				st.takeItems(MEAT_YETI, -1);
				st.unset("cond");
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(this);		
				return "31981.htm"; //no further html do here
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
		if(ArrayUtils.contains(Byval, npc.getNpcId()) && Rnd.chance(10))
		{
			st.giveItems(MEAT_BYVAL, 1);
		}
		if(ArrayUtils.contains(Yryna, npc.getNpcId()) && Rnd.chance(10))
		{
			st.giveItems(MEAT_YRYS, 1);
		}
		if(ArrayUtils.contains(Yeti, npc.getNpcId()) && Rnd.chance(10))
		{
			st.giveItems(MEAT_YETI, 1);
		}		
		if(st.getQuestItemsCount(MEAT_BYVAL) >= 30 && st.getQuestItemsCount(MEAT_YRYS) >= 30 && st.getQuestItemsCount(MEAT_YETI) >= 30)
			st.setCond(2);
			
		return null;
	}

	@Override
	public boolean isVisible(Player player)
	{
		if(DailyQuestsManager.isQuestDisabled(getQuestIntId()))
			return false;
		return checkStartCondition(player);
	}
}