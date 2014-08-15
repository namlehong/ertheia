package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author GodWorld & Bonux
**/
public class _943_FillingtheEnergyofDestruction extends Quest implements ScriptFile 
{
	// NPC's
	private static final int SEED_TALISMAN_SUPERVISOR = 33715;

	// Monster's
	private static final int[] RAID_BOSSES = { 29195, 29196, 29212, 29194, 25779, 25867, 29213, 29218, 25825, 29236, 29238 };

	// Item's
	private static final int CORE_OF_TWISTED_MAGIC = 35668;
	private static final int ENERGY_OF_DESTRUCTION = 35562;

	public _943_FillingtheEnergyofDestruction()
	{
		super(false);
		addStartNpc(SEED_TALISMAN_SUPERVISOR);
		addTalkId(SEED_TALISMAN_SUPERVISOR);
		addKillId(RAID_BOSSES);
		addQuestItem(CORE_OF_TWISTED_MAGIC);
		addLevelCheck(90);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc) 
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("seed_talisman_manager_q0943_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("seed_talisman_manager_q0943_08.htm"))
		{
			st.giveItems(ENERGY_OF_DESTRUCTION, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(this);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = "noquest";
		if(npcId == SEED_TALISMAN_SUPERVISOR)
		{
			if(st.isStarted())
			{
				if(cond == 1)
					htmltext = "seed_talisman_manager_q0943_06.htm";
				else if(cond == 2)
					htmltext = "seed_talisman_manager_q0943_07.htm";
			}
			else
			{
				if(checkStartCondition(st.getPlayer()))
				{
					if(st.isNowAvailable())
						htmltext = "seed_talisman_manager_q0943_01.htm";
					else
						htmltext = "seed_talisman_manager_q0943_05.htm";
				}
				else
					htmltext = "seed_talisman_manager_q0943_04.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(ArrayUtils.contains(RAID_BOSSES, npcId))
		{
			if(cond== 1)
			{
				st.giveItems(CORE_OF_TWISTED_MAGIC, 1);
				st.playSound(SOUND_MIDDLE);
				st.setCond(2);
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