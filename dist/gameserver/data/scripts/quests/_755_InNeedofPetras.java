package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author GodWorld & Bonux
**/
public class _755_InNeedofPetras extends Quest implements ScriptFile 
{
	// NPC's
	private static final int AKU = 33671;

	// Monster's
	private static final int[] MONSTERS = { 23213, 23214, 23227, 23228, 23229, 23230, 23215, 23216, 23217, 23218, 23231, 23232, 23233, 23234, 23237, 23219 };

	// Item's
	private static final int AKUS_SUPPLY_BOX = 35550;
	private static final int ENERGY_OF_DESTRUCTION = 35562;
	private static final int PETRA = 34959;

	// Other
	private static final double PETRA_DROP_CHANCE = 75.0;

	public _755_InNeedofPetras()
	{
		super(true);
		addStartNpc(AKU);
		addTalkId(AKU);
		addKillId(MONSTERS);
		addQuestItem(PETRA);
		addLevelCheck(97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("sofa_aku_q0755_04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = "noquest";
		if(npcId == AKU)
		{
			if(st.isStarted())
			{
				if(cond == 1)
					htmltext = "sofa_aku_q0755_07.htm";
				else if(cond == 2)
				{
					st.takeItems(PETRA, -1L);
					st.addExpAndSp(570676680, 136962);
					st.giveItems(AKUS_SUPPLY_BOX, 1);
					st.giveItems(ENERGY_OF_DESTRUCTION, 1);
					st.exitCurrentQuest(this);
					htmltext = "sofa_aku_q0755_08.htm";
				}
			}
			else
			{
				if(checkStartCondition(st.getPlayer()))
				{
					if(st.isNowAvailable())
						htmltext = "sofa_aku_q0755_01.htm";
					else
						htmltext = "sofa_aku_q0755_06.htm";
				}
				else
					htmltext = "sofa_aku_q0755_05.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(ArrayUtils.contains(MONSTERS, npcId))
		{
			if(cond == 1)
			{
				if(Rnd.chance(PETRA_DROP_CHANCE))
				{
					st.giveItems(PETRA, 1);
					if(st.getQuestItemsCount(PETRA) >= 50)
					{
						st.setCond(2);
						st.playSound(SOUND_MIDDLE);
					}
					else
						st.playSound(SOUND_ITEMGET);
				}
			}
		}
		return null;
	}

	@Override
	public void onLoad()
	{
		//
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}
}