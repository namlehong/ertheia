package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;

//By Evil_dnk dev.fairytale-world.ru

public class _10734_DoOrDie extends Quest implements ScriptFile
{
	private static final int KATALIN = 33943;
	private static final int ADV_GUIDE = 33950;
	private static final int DUMMY_TRAINING = 19546;


	public _10734_DoOrDie()
	{
		super(false);
		addStartNpc(KATALIN);
		addTalkId(KATALIN, ADV_GUIDE);
		addKillId(DUMMY_TRAINING);

		addLevelCheck(4, 20);
		addQuestCompletedCheck(_10733_TheTestForSurvivor.class);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33943-3.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			htmltext = "33943-3.htm";
		}
		
		if(event.equalsIgnoreCase("33950-3.htm"))
		{
			
			if(player.isMageClass())
			{
				htmltext = "33950-3.htm";
				
			}
			else
			{
				htmltext = "33950-4.htm";
			}
		}
		
		if(event.equalsIgnoreCase("33950-5.htm"))
		{
			buffPlayer(st.getPlayer());
		}
		
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";

		if(npcId == KATALIN)
		{
			if(st.isCompleted())
				htmltext = "33943-5.htm";
			else if(cond == 0)
				htmltext = "33943-1.htm";
			else if(cond == 2)
			{
				st.setCond(3);
				htmltext = "33943-4.htm";
			}
			else if(cond == 3)
				htmltext = "33943-4.htm";
			else if(cond == 5)
			{
				st.giveItems(ADENA_ID, 7000);
				st.addExpAndSp(805, 2);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
				htmltext = "33943-5.htm";
			}
		}
		else if(npcId == ADV_GUIDE)
		{
			if(cond == 3)
			{
				htmltext = "33950-1.htm";
				st.getPlayer().sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_002_Guide_01.htm"));	
				//st.showTutorialHTML(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_002_Guide_01.htm");
			}
			else if(cond == 4 || cond == 5)
			{
				buffPlayer(st.getPlayer());
				htmltext = "33950-5.htm";
			}
		}

		return htmltext;
	}

	private void buffPlayer(Player player)
	{
		SkillTable.getInstance().getInfo(4322, 1).getEffects(player, player, false, false);
		SkillTable.getInstance().getInfo(4323, 1).getEffects(player, player, false, false);
		SkillTable.getInstance().getInfo(5637, 1).getEffects(player, player, false, false);
		if(player.isMageClass())
		{
			SkillTable.getInstance().getInfo(4324, 1).getEffects(player, player, false, false);
			SkillTable.getInstance().getInfo(4327, 1).getEffects(player, player, false, false);
			SkillTable.getInstance().getInfo(4325, 1).getEffects(player, player, false, false);
			SkillTable.getInstance().getInfo(4326, 1).getEffects(player, player, false, false);
			
		}
		else
		{
			SkillTable.getInstance().getInfo(4328, 1).getEffects(player, player, false, false);
			SkillTable.getInstance().getInfo(4329, 1).getEffects(player, player, false, false);
			SkillTable.getInstance().getInfo(4330, 1).getEffects(player, player, false, false);
			SkillTable.getInstance().getInfo(4331, 1).getEffects(player, player, false, false);
		}
		
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		System.out.println("npcId " + npcId);
		if(npcId != DUMMY_TRAINING) return null;
		System.out.println("npc is dummy training st.getCond() " + st.getCond());
		if(st.getCond() == 1)
		{
			st.playSound(SOUND_MIDDLE);
			st.setCond(2);
		}
		if(st.getCond() == 4)
		{
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
		}
		return null;
	}

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}