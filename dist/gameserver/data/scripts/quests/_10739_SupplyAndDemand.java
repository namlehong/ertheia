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
public class _10739_SupplyAndDemand  extends Quest implements ScriptFile
{
	//NPC's
	private static final int EVNA = 33935;
	private static final int DEYNA = 33934;
	private static final int PELU = 33936;
	private static final int CERI = 33937;
	private static final int SIVANTHE = 33951;
	
	private static final int WEAPON_BOX = 39522;
	private static final int ARMOR_BOX = 39523;
	private static final int GROCERY_BOX = 39524;
	private static final int JEWEL_BOX = 39525;
	

	private static final int APPRENTICE_EARING = 112;
	private static final int KNOWLEDGE_NECKLACE = 906;
	private static final int LEATHER_PANTS = 29;
	private static final int LEATHER_SHIRT = 709;
	
	private static final int minLevel = 6;
	private static final int maxLevel = 20;
	
	public _10739_SupplyAndDemand ()
	{
		super(false);
		addStartNpc(EVNA);
		addTalkId(DEYNA, PELU, CERI, SIVANTHE);
		
		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
		
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33935-3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(WEAPON_BOX, 1);
		}
		if(event.equalsIgnoreCase("33934-2.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(ARMOR_BOX, 1);
		}
		if(event.equalsIgnoreCase("33936-2.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(GROCERY_BOX, 1);
		}
		if(event.equalsIgnoreCase("33937-2.htm"))
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(JEWEL_BOX, 1);
		}
		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == EVNA)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "33935-1.htm";
				else
					htmltext = "noquest";
			}
			else if(cond == 1)
				htmltext = "33935-4.htm";
		}
		else if(npcId == DEYNA)
		{
			if(st.getCond() == 1)
				htmltext = "33934-1.htm";
			else
				htmltext = "33934-3.htm";
		}
		else if(npcId == PELU)
		{
			if(st.getCond() == 2)
				htmltext = "33936-1.htm";
			else
				htmltext = "33936-3.htm";
		}
		else if(npcId == CERI)
		{
			if(st.getCond() == 3)
				htmltext = "33937-1.htm";
			else
				htmltext = "33937-3.htm";
		}
		else if(npcId == SIVANTHE && st.getCond() == 4)
		{
			st.takeItems(WEAPON_BOX, 1);
			st.takeItems(ARMOR_BOX, 1);
			st.takeItems(GROCERY_BOX, 1);
			st.takeItems(JEWEL_BOX, 1);
			
			
			st.giveItems(ADENA_ID, 1400);
			st.giveItems(APPRENTICE_EARING, 1);
			st.giveItems(KNOWLEDGE_NECKLACE, 1);
			st.giveItems(LEATHER_PANTS, 1);
			st.giveItems(LEATHER_SHIRT, 1);
			
			st.addExpAndSp(8136,1);
			
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			
			htmltext = "33951-1.htm";
		}
		return htmltext;
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState qs = player.getQuestState(_10738_AnInnerBeauty.class);
		
		boolean result = false;
		
		result = (qs != null && qs.getState() == COMPLETED);
	
		return (player.getLevel() >= minLevel && player.getLevel() <= maxLevel && result);
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