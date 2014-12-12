package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.UsmVideo;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Hien Son
 */
public class _10737_GarkonsWarehouse  extends Quest implements ScriptFile
{
	//NPC's
	private static final int AYANTHE = 33942;
	private static final int KATALIN = 33943;
	private static final int GRANKON = 33947;
	private static final int APPRENTICE_STAFF = 7816;
	private static final int APPRENTICE_CESTUS = 7819;
	private static final int APPRENTICE_BOX = 39520;
	
	public _10737_GarkonsWarehouse ()
	{
		super(false);
		addStartNpc(AYANTHE, KATALIN);
		addTalkId(GRANKON);
		
		addLevelCheck(5, 20);
		addRaceCheck(false, false, false, false, false, false, true);
		
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33942-3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(APPRENTICE_BOX, 1);
		}
		if(event.equalsIgnoreCase("33943-3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(APPRENTICE_BOX, 1);
		}
		else if(event.equalsIgnoreCase("33947-4.htm"))
		{
			st.takeItems(APPRENTICE_BOX, 1);
			
			st.giveItems(ADENA_ID, 11000, true);
			
			if(st.getPlayer().isMageClass())
				st.giveItems(APPRENTICE_STAFF, 1);
			else
				st.giveItems(APPRENTICE_CESTUS, 1);
			
			st.addExpAndSp(2625,0);
			
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == AYANTHE)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()) && st.getPlayer().isMageClass())
					htmltext = "33942-1.htm";
				else
					htmltext = "noquest";
			}
			else if(cond == 1)
				htmltext = "33942-4.htm";
		}
		if(npcId == KATALIN)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()) && !st.getPlayer().isMageClass())
					htmltext = "33943-1.htm";
				else
					htmltext = "noquest";
			}
			else if(cond == 1)
				htmltext = "33943-4.htm";
		}
		else if(npcId == GRANKON && st.getCond() == 1)
		{
			if(st.getPlayer().isMageClass())
				htmltext = "33947-1a.htm";
			else
				htmltext = "33947-1b.htm";
		}
		return htmltext;
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState qs1 = player.getQuestState(_10735_ASpecialPower.class);
		QuestState qs2 = player.getQuestState(_10736_ASpecialPower.class);
		boolean result = false;
		
		if(player.isMageClass())
			result = (qs1 != null && qs1.getState() == COMPLETED);
		else 
			result = (qs2 != null &&  qs2.getState() == COMPLETED);
		
		return (player.getLevel() >= 5 && player.getLevel() <= 20 && result);
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