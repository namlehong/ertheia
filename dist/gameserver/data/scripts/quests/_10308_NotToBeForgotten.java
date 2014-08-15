package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _10308_NotToBeForgotten extends Quest implements ScriptFile
{
	//npc
	private static final int GUIDE = 33463;
	private static final int KERTIS = 30870;
	private static final int[] MOBS = {20679, 20680, 21017, 21018, 21019, 21020, 21021, 21022};
	private static final int YADRO = 19487;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10308_NotToBeForgotten()
	{
		super(true);
		addStartNpc(GUIDE);
		addTalkId(KERTIS);
		addKillId(MOBS);
		addQuestItem(YADRO);
		addLevelCheck(55, 59);
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
			if(state == COMPLETED)
				return "30452-comp.htm";
			if(cond == 0)
				return "33463.htm";
			if(state == 2)
			{
				if(cond == 1)
					return "33463-4.htm";
				if(cond == 2)
					return "33463-5.htm";
			}
			
		}
		if(npcId == KERTIS && state == 2)
		{
			if(state == COMPLETED)
				return "30452-comp.htm";	
				
			if(cond == 1)
				return "30452-1.htm";
			if(cond == 2)
			{
				st.takeItems(YADRO, -1);
				st.giveItems(57, 376704);
				st.addExpAndSp(2322445, 1968325);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
				return "30452.htm"; //no further html do here
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
		if(Rnd.chance(20))
			st.giveItems(YADRO, 1);
		if(st.getQuestItemsCount(YADRO) >= 40)
			st.setCond(2);
		return null;
	}

	@Override
	public boolean isVisible(Player player)
	{
		return player.getLevel() >= 55 && player.getLevel() <= 59;
	}	
}