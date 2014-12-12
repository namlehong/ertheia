package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.PcInventory;
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
 * 
 */
public class _10758_TheOathOfTheWind extends Quest implements ScriptFile
{

	private static final int PIO = 33963;
	
	private final static int WINDIMA_CLONE = 27522;

	private static final int STEEL_DOOR_COIN = 37045;
	
	private static final int minLevel = 28;
	private static final int maxLevel = 99;
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10758_TheOathOfTheWind()
	{
		super(false);
		addStartNpc(PIO);
		
		addKillId(WINDIMA_CLONE);
		
		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33963-2.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("spawn_windima"))
		{
			NpcInstance windima = st.addSpawn(WINDIMA_CLONE, -93592, 89912, -3236, 53988, 0, 180000);
			
			windima.setRunning();
			windima.setAggressionTarget(player);
			windima.getAggroList().addDamageHate(player, 10000, 10000);
			windima.getAI().Attack(player, false, false);
			
			return null;
		}
		
		if(event.equalsIgnoreCase("33963-5.htm"))
		{
			st.giveItems(STEEL_DOOR_COIN, 3, true);
			st.addExpAndSp(561645, 134);
			
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{	
		String htmltext = "noquest";
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		
		if(npcId == PIO)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				if(cond == 0)
				{
					htmltext = "33963-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33963-3.htm";
				}
				else if(cond == 2)
				{
					htmltext = "33963-4.htm";
				}
			}
			else
				htmltext = "noquest";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(npcId == WINDIMA_CLONE)
		{
			st.setCond(2);
		}
		
		return null;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState qs = player.getQuestState(_10757_QuietingTheStorm.class);
		
		return (player.getLevel() >= minLevel && 
				player.getLevel() <= maxLevel && 
				player.getRace() == Race.ERTHEIA && 
				qs != null && 
				qs.getState() == COMPLETED);
	}
}