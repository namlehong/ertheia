package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _10378_WeedingWork extends Quest implements ScriptFile
{
	//q items
	private static final int STEBEL = 34974;
	private static final int KOREN = 34975;
	//reward items
	private static final int SCROLL = 35292;

	private static final int DADFENA = 33697;

	public _10378_WeedingWork()
	{
		super(false);
		addTalkId(DADFENA);
		addQuestItem(STEBEL);
		addQuestItem(KOREN);
		
		addKillId(23210, 23211);
		
		addLevelCheck(95);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;

		if(event.equalsIgnoreCase("accepted.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("endquest.htm"))
		{
			st.takeAllItems(STEBEL);
			st.takeAllItems(KOREN);
			st.getPlayer().addExpAndSp(845059770, 378445230);
			st.giveItems(SCROLL, 1);
			st.giveItems(57, 3000000);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		if(npcId == DADFENA)
		{
			if(cond == 0)
				htmltext = "start.htm";
			else if(cond == 1)
				htmltext = "notcollected.htm";
			else if(cond == 2)
				htmltext = "collected.htm";
		}
			
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs == null)
			return null;

		if(qs.getState() != STARTED)
			return null;
		if(qs.getCond() != 1)
			return null;
		if(qs.getQuestItemsCount(STEBEL) < 5 && Rnd.chance(7))
			qs.giveItems(STEBEL, 1);
		if(qs.getQuestItemsCount(KOREN) < 5 && Rnd.chance(7))
			qs.giveItems(KOREN, 1);
		if(qs.getQuestItemsCount(KOREN) >= 5 && qs.getQuestItemsCount(STEBEL) >= 5)
			qs.setCond(2);
		return null;
	}
	
	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}