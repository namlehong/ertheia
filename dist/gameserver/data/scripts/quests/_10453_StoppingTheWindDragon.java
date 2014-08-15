package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Bonux
**/
public class _10453_StoppingTheWindDragon extends Quest implements ScriptFile
{
	// NPC's
	private static final int JENNA = 33872;

	// Monster's
	private static final int LINDVIOR = 29240;

	// Item's
	private static final int LINDVIOR_SLAYERS_HELMET = 37497;

	public _10453_StoppingTheWindDragon()
	{
		super(COMMAND_CHANNEL);

		addStartNpc(JENNA);
		addTalkId(JENNA);

		addKillId(LINDVIOR);

		addLevelCheck(99);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("adens_wizard_jenna_q10453_2.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("adens_wizard_jenna_q10453_5.htm"))
		{
			st.giveItems(LINDVIOR_SLAYERS_HELMET, 1, true);
			st.addExpAndSp(2147483500, 37047780);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == JENNA)
		{
			if(st.isCompleted())
				htmltext = "adens_wizard_jenna_q10453_6.htm";
			else if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "adens_wizard_jenna_q10453_1.htm";
				else
					htmltext = "adens_wizard_jenna_q10453_0.htm";
			}
			else if(cond == 1)
				htmltext = "adens_wizard_jenna_q10453_3.htm";
			else if(cond == 2)
				htmltext = "adens_wizard_jenna_q10453_4.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(cond == 1)
		{
			if(npcId == LINDVIOR)
				st.setCond(2);
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