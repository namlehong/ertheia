package quests;


import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;
import l2s.gameserver.instancemanager.DailyQuestsManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _477_Awakened extends Quest implements ScriptFile
{
	//npc
	public static final int GUIDE = 33463;
	public static final int JASTIN = 31282;
	
	//mobs
	private final int[] Mobs = {21294, 21295, 21296, 21297, 21298, 21299, 21300, 21301, 21302, 21303, 21304, 21305, 21307, 21312, 21313};
	
	//q items
	public static final int BLOOD_TEARS = 19496;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _477_Awakened()
	{
		super(true);
		addStartNpc(GUIDE);
		addTalkId(JASTIN);
		addKillId(Mobs);
		addQuestItem(BLOOD_TEARS);
		addLevelCheck(70, 74);
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
		if(npcId == JASTIN && state == 2)
		{
			if(cond == 1)
				return "31282-1.htm";
			if(cond == 2)
			{
				st.giveItems(57, 334560);
				st.addExpAndSp(8534700, 8523390);
				st.takeItems(BLOOD_TEARS, -1);
				st.unset("cond");
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(this);			
				return "31282.htm"; //no further html do here
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
		if(ArrayUtils.contains(Mobs, npc.getNpcId()) && Rnd.chance(10))
		{
			st.giveItems(BLOOD_TEARS, 1);
		}	
		if(st.getQuestItemsCount(BLOOD_TEARS) >= 45)
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