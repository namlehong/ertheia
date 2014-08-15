package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _490_DutyOfTheSurvivor extends Quest implements ScriptFile
{
	//npc
	public static final int VOLODOS = 30137;
	
	//mobs
	public static final int[] mobs = {23162, 23163, 23164, 23165, 23166, 23167, 23168, 23169, 23170, 23171, 23172, 23173};
	private static int Zhelch = 34059;
	private static int Blood = 34060;
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _490_DutyOfTheSurvivor()
	{
		super(true);
		addStartNpc(VOLODOS);
		addTalkId(VOLODOS);
		addKillId(mobs);
		addLevelCheck(85, 89);
		addQuestItem(Zhelch);
		addQuestItem(Blood);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("30137-6.htm"))
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
		if(npcId == VOLODOS)
		{
			if(state == 1)
			{
				if(player.getLevel() < 85 || player.getLevel() > 89)
					return "30137-lvl.htm";
				if(!st.isNowAvailable())
					return "30137-comp.htm";
				return "30137.htm";
			}
			if(state == 2)
			{
				if(cond == 1)
					return "30137-7.htm";
				if(cond == 2)
				{
					st.giveItems(57, 505062);
					st.addExpAndSp(145557000, 58119840);
					st.unset("cond");
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(this);
					return "30137-9.htm";
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
			
		if(ArrayUtils.contains(mobs, npc.getNpcId()))
			if(Rnd.chance(10))
			{
				if(Rnd.chance(50))
				{
					if(st.getQuestItemsCount(Zhelch) < 20)
						st.giveItems(Zhelch, 1);
				}
				else
					if(st.getQuestItemsCount(Blood) < 20)
						st.giveItems(Blood, 1);
				if(st.getQuestItemsCount(Zhelch) >= 20 && st.getQuestItemsCount(Blood) >= 20)
					st.setCond(2);
			}
		return null;
	}	
}