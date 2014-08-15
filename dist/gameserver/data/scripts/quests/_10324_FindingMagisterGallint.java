package quests;

import l2s.gameserver.model.Player;
//import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;

public class _10324_FindingMagisterGallint extends Quest implements ScriptFile
{
	private static final int SHENON = 32974;
	private static final int GALLINT = 32980;

	public _10324_FindingMagisterGallint()
	{
		super(false);
		addStartNpc(SHENON);
		addTalkId(GALLINT);
		addLevelCheck(1,20);
		addQuestCompletedCheck(_10323_GoingIntoARealWar.class);
		addRaceCheck(true, true, true, true, true, true, false);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("0-3.htm"))
		{
			st.set("cond", "1");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("1-2.htm"))
		{
			st.getPlayer().sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_004_skill_01.htm"));
			
			st.giveItems(57, 11000);
			st.addExpAndSp(1700, 2000);
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
		int cond = st.getInt("cond");
		Player player = st.getPlayer();
		//Reflection r = player.getReflection();
		if(npcId == SHENON)
		{
			if(cond == 0)
			{
				if(checkStartCondition(player))
					htmltext = "start.htm";
				else
				{
					if(player.getLevel() > 20)
						return "This quest is avaliable to character who is 20 level or lower";
					return "noqu.htm";
				}	
			}
			else if(cond == 1)
				htmltext = "0-4.htm";
		}
		else if(npcId == GALLINT)
		{
			if(cond == 1)
				htmltext = "1-1.htm";
		}
		return htmltext;
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
