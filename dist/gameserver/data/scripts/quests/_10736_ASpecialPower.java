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

/**
 * @author Hien Son
 */
public class _10736_ASpecialPower extends Quest implements ScriptFile
{

	private static final int KATALIN1 = 33943;
	private static final int KATALIN2 = 33945;
	
	private final static int FLOATO = 27526;
	private final static int RATEL = 27527;
	
	private final static int SOULSHOT = 5789;
	
	public static final String FLOATO_LIST1 = "FLOATO_LIST1";
	
	public static final String FLOATO_LIST2 = "FLOATO_LIST2";
	
	public static final String RATEL_LIST = "RATEL_LIST";

	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10736_ASpecialPower()
	{
		super(false);
		addStartNpc(KATALIN1);
		addTalkId(KATALIN2);
		
		addKillNpcWithLog(2, FLOATO_LIST1, 2, FLOATO);
		addKillNpcWithLog(4, FLOATO_LIST2, 2, FLOATO);
		addKillNpcWithLog(6, RATEL_LIST, 2, RATEL);

		addLevelCheck(4, 20);
		addQuestCompletedCheck(_10733_TheTestForSurvivor.class);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33943-2.htm"))
		{
			st.setState(STARTED);
			st.setCond(0);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("enter_camp"))
		{
			st.setCond(1);
			enterInstance(st, 252);	
			return null;
		}
		
		
		if(event.equalsIgnoreCase("33945-5.htm"))
		{
			st.setCond(6);
			//spawn ratel
			//kill to get to cond 7
			st.getPlayer().getReflection().addSpawnWithoutRespawn(RATEL, new Location(-75160, 240328, -3628, 0), 0);
			st.getPlayer().getReflection().addSpawnWithoutRespawn(RATEL, new Location(-74744, 240440, -3630, 0), 0);
			
		}
		
		if(event.equalsIgnoreCase("leave_camp"))
		{
			player.getReflection().collapse();
			return null;
		}
		
		if(event.equalsIgnoreCase("spirit_timer"))
		{
			player.sendPacket(new ExShowScreenMessage(NpcString.AUTOMATE_SOULSHOT_AS_SHOWN_IN_THE_TUTORIAL, 4500, ScreenMessageAlign.TOP_CENTER));
			st.setCond(4);
			htmltext = "33945-3.htm";
			//spawn floato
			//kill to get to cond 5
			st.getPlayer().getReflection().addSpawnWithoutRespawn(FLOATO, new Location(-75160, 240328, -3628, 0), 0);
			st.getPlayer().getReflection().addSpawnWithoutRespawn(FLOATO, new Location(-74744, 240440, -3630, 0), 0);
			
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

		if(npcId == KATALIN1)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				htmltext = "33943-1.htm";
			}
			else
				htmltext = "noquest";
			
			if(cond == 7)
			{
				st.giveItems(ADENA_ID, 900);
				st.giveItems(SOULSHOT, 500);
				st.addExpAndSp(3154, 0);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
				st.unset("bonusXP");
				
				htmltext = "33943-3.htm";
			}
		}
		else if(npcId == KATALIN2)
		{
			if(cond == 1)
			{
				htmltext = "33945-1.htm";
				//spawn floato
				st.setCond(2);
				//kill to get to cond 3
				st.playSound(SOUND_MIDDLE);
				st.getPlayer().getReflection().addSpawnWithoutRespawn(FLOATO, new Location(-75160, 240328, -3628, 0), 0);
				st.getPlayer().getReflection().addSpawnWithoutRespawn(FLOATO, new Location(-74744, 240440, -3630, 0), 0);
				
			}
			else if(cond == 3)
			{
				htmltext = "33945-2.htm";
				//give ss
				st.playSound(SOUND_MIDDLE);
				player.sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2Text\\QT_003_bullet_01.htm"));
				player.sendPacket(new ExShowScreenMessage(NpcString.SOULSHOT_HAVE_BEEN_ADDED_TO_YOUR_INVENTORY, 4500, ScreenMessageAlign.TOP_CENTER));
				st.startQuestTimer("spirit_timer", 4000);
				st.giveItems(SOULSHOT, 150);
				
			}
			else if(cond == 5)
			{
				htmltext = "33945-4.htm";
				if(st.getInt("bonusXP") != 1)
				{
					st.addExpAndSp(1716, 0);
					st.set("bonusXP", 1);
				}
			}
			else if(cond == 7)
			{
				htmltext = "33945-6.htm";
			}
		}
		
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(updateKill(npc, st))
		{
			st.playSound(SOUND_MIDDLE);
			st.setCond(cond+1);
		}
		
		return null;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState qs = player.getQuestState(_10734_DoOrDie.class);
		return (player.getLevel() >= 4 && player.getLevel() <= 20 && qs != null && qs.getState() == COMPLETED && !player.isMageClass());
	}
}