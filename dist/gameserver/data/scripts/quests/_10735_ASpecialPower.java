package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.ReflectionUtils;

//By Hien Son
public class _10735_ASpecialPower extends Quest implements ScriptFile
{

	private static final int AYANTHE1 = 33942;
	private static final int AYANTHE2 = 33944;
	
	private final static int FLOATO = 27526;
	private final static int RATEL = 23113;
	
	private final static int SPIRITSHOT = 5790;

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10735_ASpecialPower()
	{
		super(false);
		addStartNpc(AYANTHE1);
		addTalkId(AYANTHE2);
		addKillId(FLOATO, RATEL);

		addLevelCheck(4, 20);
		addQuestCompletedCheck(_10733_TheTestForSurvivor.class);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33942-2.htm"))
		{
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("enter_camp"))
		{
			st.setCond(1);
			enterInstance(st, 400);	
		}
		
		if(event.equalsIgnoreCase("33944-5.htm"))
		{
			st.setCond(6);
			//spawn ratel
			//kill to get to cond 7
			st.getPlayer().getReflection().addSpawnWithoutRespawn(RATEL, new Location(-75160, 240328, -3628, 0), 0);
			st.getPlayer().getReflection().addSpawnWithoutRespawn(RATEL, new Location(-74744, 240440, -3630, 0), 0);
			
		}
		
		if(event.equalsIgnoreCase("leave_camp"))
		{
			st.setCond(8);
			player.getReflection().collapse();
		}
		
		if(event.equalsIgnoreCase("spirit_timer"))
		{
			player.sendPacket(new ExShowScreenMessage(NpcString.AUTOMATE_SPIRITSHOT_AS_SHOWN_IN_THE_TUTORIAL, 4500, ScreenMessageAlign.TOP_CENTER));
		}
		
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{	
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		Player player = st.getPlayer();

		if(npcId == AYANTHE1)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
				{
					htmltext = "33942-1.htm";
				}
				else
					htmltext = "noquest";
			}
			else if(cond == 8)
			{
				htmltext = "33942-3.htm";
			}
		}
		else if(npcId == AYANTHE2)
		{
			if(cond == 1)
			{
				htmltext = "33944-1.htm";
				//spawn floato
				st.setCond(2);
				//kill to get to cond 3
				st.playSound(SOUND_MIDDLE);
				st.getPlayer().getReflection().addSpawnWithoutRespawn(FLOATO, new Location(-75160, 240328, -3628, 0), 0);
				st.getPlayer().getReflection().addSpawnWithoutRespawn(FLOATO, new Location(-74744, 240440, -3630, 0), 0);
				
			}
			else if(cond == 3)
			{
				st.setCond(4);
				htmltext = "33944-2.htm";
				//give ss
				st.playSound(SOUND_MIDDLE);
				player.sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_003_bullet_01.htm"));
				player.sendPacket(new ExShowScreenMessage(NpcString.SPIRITSHOT_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, 4500, ScreenMessageAlign.TOP_CENTER));
				st.startQuestTimer("spirit_timer", 4000);
				st.giveItems(5790, 150);
				
			}
			else if(cond == 4)
			{
				htmltext = "33944-3.htm";
				//spawn floato
				//kill to get to cond 5
				st.getPlayer().getReflection().addSpawnWithoutRespawn(FLOATO, new Location(-75160, 240328, -3628, 0), 0);
				st.getPlayer().getReflection().addSpawnWithoutRespawn(FLOATO, new Location(-74744, 240440, -3630, 0), 0);
				
			}
			else if(cond == 5)
			{
				htmltext = "33944-4.htm";
			}
			else if(cond == 7)
			{
				htmltext = "33944-6.htm";
			}
		}
		
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int killedFloato = st.getInt("killedFloato");
		int killedRatel = st.getInt("killedRatel");
		
		if(npcId == FLOATO && (st.getCond() == 2 || st.getCond() == 4))
		{
			if(killedFloato >= 2)
			{
				st.setCond(st.getCond() + 1);
				st.unset("killedFloato");
				st.playSound(SOUND_MIDDLE);
			}
			else
				st.set("killedFloato", ++killedFloato);
		}
		
		if(npcId == RATEL && st.getCond() == 6)
		{
			if(killedRatel >= 2)
			{
				st.setCond(st.getCond() + 1);
				st.unset("killedRatel");
				st.playSound(SOUND_MIDDLE);
			}
			else
				st.set("killedRatel", ++killedRatel);
		}
		
		return null;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState qs = player.getQuestState(_10733_TheTestForSurvivor.class);
		return (player.getLevel() >= 4 && player.getLevel() <= 20 && qs != null && qs.getState() == COMPLETED);
	}
}