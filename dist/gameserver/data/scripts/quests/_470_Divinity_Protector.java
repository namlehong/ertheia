package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.instancemanager.DailyQuestsManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _470_Divinity_Protector extends Quest implements ScriptFile
{
	//npc
	public static final int GUIDE = 33463;
	public static final int APRIGEL = 31348;
	
	//mobs
	private final int[] Mobs = {21520, 21521, 21522, 21523, 21524, 21525, 21526, 21542, 21543, 21527, 21528, 21529, 21541, 21530, 21531, 21532, 21533, 21534, 21535, 21536, 21545, 21546, 21537, 21538, 21539, 21540, 21544};
	
	//q items
	public static final int COLORLESS_SOUL = 19489;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _470_Divinity_Protector()
	{
		super(true);
		addStartNpc(GUIDE);
		addTalkId(APRIGEL);
		addKillId(Mobs);
		addQuestItem(COLORLESS_SOUL);
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
					
			}
		}
		if(npcId == APRIGEL && state == 2)
		{
			if(cond == 1)
				return "31348-1.htm";
			if(cond == 2)
			{
				st.giveItems(57,194000);
				st.addExpAndSp(1879400, 1782000);
				st.takeItems(COLORLESS_SOUL, -1);
				st.unset("cond");
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(this);			
				return "31348.htm"; //no further html do here
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
		if(ArrayUtils.contains(Mobs, npc.getNpcId()) && Rnd.chance(8))
		{
			st.giveItems(COLORLESS_SOUL, 1);
		}
		if(st.getQuestItemsCount(COLORLESS_SOUL) >= 20)
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