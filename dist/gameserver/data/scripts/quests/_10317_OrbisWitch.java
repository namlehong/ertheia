package quests;

import java.util.Calendar;

import org.apache.commons.lang3.ArrayUtils;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10317_OrbisWitch extends Quest implements ScriptFile
{
	//npc
	public static final int OPERA = 32946;
	public static final int TIPIA = 32892;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10317_OrbisWitch()
	{
		super(true);
		addStartNpc(OPERA);
		addTalkId(TIPIA);
		
		addQuestCompletedCheck(_10316_UndecayingMemoryOfThePast.class);

		addLevelCheck(95);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("32946-7.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if(event.equalsIgnoreCase("32892-1.htm"))
		{
			st.giveItems(57, 506760);
			st.addExpAndSp(7412805, 3319695);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);			
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
		if(player.getLevel() < 95)
			return "32946-lvl.htm";

		QuestState qs = st.getPlayer().getQuestState(_10316_UndecayingMemoryOfThePast.class);
		if(qs == null || !qs.isCompleted())
			return "32946-lvl.htm";

		if(npcId == OPERA)
		{
			if(cond == 0)
			{
				return "32946.htm";
			}
			if(cond == 1)
				return "32946-8.htm";		
		}
		if(npcId == TIPIA)
		{
			if(cond == 1)
				return "32892.htm";
		}
		return "noquest";
	}
}