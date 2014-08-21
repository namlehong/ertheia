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
	private static final int AYANTHE = 33942;
	private static final int KATALIN = 33943;
	private static final int ADV_GUIDE = 33950;
	private static final int DUMMY_TRAINING = 19546;

	/*
	 stage 
	0 start
	0 go to Katalin/Ayanthe 1st
	1 hit dummy
	2 go to Ayanthe 2nd
	3 go to katalin 2nd
	4 ask for buff
	5 hit dummy
	6 got to Ayanthe 3rd
	7 go to Katalin 3rd
	 */

	public _10734_DoOrDie()
	{
		super(false);
		addStartNpc(AYANTHE);
		addStartNpc(KATALIN);
		addTalkId(AYANTHE, KATALIN, ADV_GUIDE);
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
		//System.out.println("quest event " + event.toString());
		if(event.equalsIgnoreCase("33943-3.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			htmltext = "33943-3.htm";
		}
		
		if(event.equalsIgnoreCase("33942-3.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			htmltext = "33942-3.htm";
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
			st.setCond(6);
		}
		
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		Player player = st.getPlayer();
		String htmltext = "noquest";

		if(npcId == KATALIN)
		{
			if(!player.isMageClass())
			{
				if(cond == 0)
				{
					if(if(checkStartCondition(st.getPlayer())))
						htmltext = "33943-1.htm";
					else 
						htmltext = "noquest";
				}
				else if(cond == 3)
				{
					st.setCond(4);
					htmltext = "33943-4.htm";
				}
				else if(cond == 4)
					htmltext = "33943-4.htm";
				else if(cond == 8)
				{
					st.giveItems(ADENA_ID, 7000);
					st.addExpAndSp(805, 2);
					st.setState(COMPLETED);
					st.exitCurrentQuest(false);
					st.playSound(SOUND_FINISH);
					htmltext = "33943-5.htm";
				}
				else if(st.isCompleted())
					htmltext = "33943-5.htm";
			}
			else
				htmltext = "noquest";
		}
		else if(npcId == AYANTHE)
		{
			if(player.isMageClass())
			{
				if(cond == 0)
				{
					if(if(checkStartCondition(st.getPlayer())))
						htmltext = "33942-1.htm";
					else 
						htmltext = "noquest";
				}
				else if(cond == 2)
				{
					st.setCond(4);
					htmltext = "33942-4.htm";
				}
				else if(cond == 4)
					htmltext = "33942-4.htm";
				else if(cond == 7)
				{
					st.giveItems(ADENA_ID, 7000);
					st.addExpAndSp(805, 2);
					st.setState(COMPLETED);
					st.exitCurrentQuest(false);
					st.playSound(SOUND_FINISH);
					htmltext = "33942-5.htm";
				}
				else if(st.isCompleted())
					htmltext = "33942-5.htm";
			}
			else
				htmltext = "noquest";
		}
		else if(npcId == ADV_GUIDE)
		{
			if(cond == 4)
			{
				htmltext = "33950-1.htm";
				st.getPlayer().sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_002_Guide_01.htm"));	
				//st.showTutorialHTML(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_002_Guide_01.htm");
				
			}
			else if(cond == 6)
			{
				buffPlayer(st.getPlayer());
				htmltext = "33950-5.htm";
			}
		}

		return htmltext;
	}

	private void buffPlayer(Player player)
	{
		SkillTable.getInstance().getInfo(15642, 1).getEffects(player, player, false, false);
		SkillTable.getInstance().getInfo(15643, 1).getEffects(player, player, false, false);
		SkillTable.getInstance().getInfo(15644, 1).getEffects(player, player, false, false);
		SkillTable.getInstance().getInfo(15645, 1).getEffects(player, player, false, false);
		SkillTable.getInstance().getInfo(15646, 1).getEffects(player, player, false, false);
		SkillTable.getInstance().getInfo(15647, 1).getEffects(player, player, false, false);
		SkillTable.getInstance().getInfo(15651, 1).getEffects(player, player, false, false);
		SkillTable.getInstance().getInfo(15652, 1).getEffects(player, player, false, false);
		SkillTable.getInstance().getInfo(15653, 1).getEffects(player, player, false, false);
		if(player.isMageClass())
		{
			SkillTable.getInstance().getInfo(15650, 1).getEffects(player, player, false, false);
			
		}
		else
		{
			SkillTable.getInstance().getInfo(15649, 1).getEffects(player, player, false, false);
		}
		
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		//System.out.println("npcId " + npcId);
		if(npcId != DUMMY_TRAINING) return null;
		//System.out.println("npc is dummy training st.getCond() " + st.getCond());
		if(st.getCond() == 1)
		{
			st.playSound(SOUND_MIDDLE);
			if(st.getPlayer().isMageClass())
				st.setCond(2);
			else 
				st.setCond(3);
		}
		if(st.getCond() == 6)
		{
			st.playSound(SOUND_MIDDLE);
			if(st.getPlayer().isMageClass())
				st.setCond(7);
			else 
				st.setCond(8);
		}
		return null;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState qs = player.getQuestState(_10733_TheTestForSurvivor.class);
		return player.getLevel() <= 20 && qs != null && qs.getState() == COMPLETED;
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