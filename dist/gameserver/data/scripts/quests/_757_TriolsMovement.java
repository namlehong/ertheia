package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Iqman
 */
public class _757_TriolsMovement extends Quest implements ScriptFile
{
	//q items
	private static final int TOTEM = 36230;
	private static final int SPIRIT = 36231;
	//reward items
	private static final int T_CHEST = 36232;
	private static final int BLOOD = 36278;

	private static final int RAZDEN = 33803;

	public _757_TriolsMovement()
	{
		super(false);
		addStartNpc(RAZDEN);
		addTalkId(RAZDEN);
		addQuestItem(TOTEM);
		addQuestItem(SPIRIT);
		
		addKillId(22140, 22147, 23278, 23283, 22146, 22155, 22141, 22144, 22139, 22152, 22153, 22154);
		
		addLevelCheck(97);
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
			int vit_points_add = (int)st.getQuestItemsCount(SPIRIT);
			if(vit_points_add != 0)
				st.getPlayer().setVitality(st.getPlayer().getVitality() + vit_points_add, true);
			st.takeAllItems(TOTEM);
			st.takeAllItems(SPIRIT);
			st.giveItems(T_CHEST, 1);
			st.giveItems(BLOOD, 10);
			st.exitCurrentQuest(this);
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
		if(st.isNowAvailable())
		{
			if(npcId == RAZDEN)
			{
				if(cond == 0)
					htmltext = "start.htm";
				else if(cond == 1)
					htmltext = "notcollected.htm";
				else if(cond == 2 || cond == 3)
					htmltext = "collected.htm";
			}
		}
		else
			htmltext = "You have completed this quest today, come back tomorow at 6:30!";
			
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if(qs == null)
			return null;

		if(qs.getState() != STARTED)
			return null;
			
		if(qs.getCond() != 1 && qs.getCond() != 2)
			return null;
			
		if(qs.getQuestItemsCount(TOTEM) < 50 && Rnd.chance(40))
			qs.giveItems(TOTEM, 1);
		if(qs.getQuestItemsCount(SPIRIT) < 1200 && Rnd.chance(25))
			qs.giveItems(SPIRIT, Rnd.get(1,4));
		if(qs.getQuestItemsCount(TOTEM) >= 50)
			qs.setCond(2);
		if(qs.getQuestItemsCount(TOTEM) >= 50 && qs.getQuestItemsCount(SPIRIT) >= 1200)
			qs.setCond(3);			
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