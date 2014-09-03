package quests;

import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

/**
 * @author By Evil_dnk dev.fairytale-world.ru
 * reworked by Bonux
**/
public class _10365_SeekerEscort extends Quest implements ScriptFile
{
	// NPC's
	private static final int DEP = 33453;
	private static final int SEBION = 32978;
	private static final int BLOODHOUND = 32988;

	// Other
	private static final Location SEEKER_START_POINT = new Location(-110616, 238376, -2950);

	private NpcInstance _bloodhound = null;

	public _10365_SeekerEscort()
	{
		super(false);

		addStartNpc(DEP);
		addTalkId(DEP);
		addTalkId(SEBION);
		addFirstTalkId(BLOODHOUND);

		addLevelCheck(16, 25);
		addQuestCompletedCheck(_10364_ObligationsOfSeeker.class);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("dep_q10365_3.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);

			spawnBloodhound(st);
		}
		else if(event.equalsIgnoreCase("spawn_king"))
		{
			spawnBloodhound(st);
			return null;
		}
		else if(event.equalsIgnoreCase("sebion_q10365_2.htm"))
		{
			st.getPlayer().addExpAndSp(120000, 28);
			st.giveItems(57, 650);
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
		if(npcId == DEP)
		{
			if(st.isCompleted())
				htmltext = "dep_q10365_6.htm";
			else if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "dep_q10365_1.htm";
				else
					htmltext = "dep_q10365_0.htm";
			}
			else if(cond == 1 && st.getInt("seeksp") == 0)
				htmltext = "dep_q10365_5.htm";
			else if(cond == 1)
				htmltext = "dep_q10365_4.htm";

		}
		else if(npcId == SEBION)
		{
			if(st.isCompleted())
				htmltext = "sebion_q10365_3.htm";
			else if(cond == 0)
				htmltext = "sebion_q10365_0.htm";
			else if(cond == 2)
				htmltext = "sebion_q10365_1.htm";
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		return null;
	}

	private void spawnBloodhound(QuestState st)
	{
		_bloodhound = NpcUtils.spawnSingle(BLOODHOUND, Location.findPointToStay(SEEKER_START_POINT, 50, 100, st.getPlayer().getGeoIndex()));
		_bloodhound.setFollowTarget(st.getPlayer());
		st.set("seeksp", 1, false);
		st.set("zone", 1, false);
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