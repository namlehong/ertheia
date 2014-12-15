package quests;

import l2s.gameserver.Config;
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
import l2s.gameserver.utils.Language;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.NpcUtils;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author Hien Son
 * 
 */
public class _10742_AFurryFriend extends Quest implements ScriptFile
{

	private static final int LEIRA = 33952;
	private final static int KIKU_CAVE = 33995;
	
	private final static int RICKY = 19552;
	
	private static final int minLevel = 11;
	private static final int maxLevel = 20;
	
	public static NpcInstance foxInstance = null;
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10742_AFurryFriend()
	{
		super(false);
		addStartNpc(LEIRA);
		addTalkId(KIKU_CAVE);
		
		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33952-4.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			st.set("spawn_cave", (int)Math.round(Math.random()*3));
			st.set("cave_check", 0);
			
		}
		
		if(event.equalsIgnoreCase("check_fox"))
		{
			int caveCheckCount = st.getInt("cave_check");
			int spawnCave = st.getInt("spawn_cave");
			
			if(caveCheckCount == spawnCave && st.getInt("fox_spawn") != 1)
			{
				st.set("fox_spawn", 1);
				
				foxInstance = st.addSpawn(RICKY, 2*60000); //despawn after 2 mins
				
				st.startQuestTimer("fox_move", 2000);
				if(Config.DEFAULT_LANG == Language.VIETNAMESE)
				{
					player.sendPacket(new ExShowScreenMessage("Dắt Ricky về lại với Leira! Nhanh lên nhé.", 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else
				{
					player.sendPacket(new ExShowScreenMessage("Take Ricky back to Leira! Hurry up.", 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
			}
			else
			{
				if(Config.DEFAULT_LANG == Language.VIETNAMESE)
				{
					player.sendPacket(new ExShowScreenMessage("Có lẽ Ricky đã chui vào hang khác.", 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				else
				{
					player.sendPacket(new ExShowScreenMessage("May be Ricky is in another cave.", 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
				}
				
			}
			caveCheckCount++;
			st.set("cave_check", caveCheckCount);
			
			return null;
		}
		
		if(event.equalsIgnoreCase("fox_move"))
		{
			if(foxInstance == null) return null;
			
			foxInstance.setRunning();
			
			foxInstance.moveToLocation(player.getLoc(), 20, true);
			
			if(foxInstance.getLoc().distance(new Location(-78080, 237343, -3536)) > 100)
			{
				st.startQuestTimer("fox_move", 2000);
			}
			else
			{
				st.setCond(2);
				foxInstance.deleteMe();
			}
			return null;
		}
		
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{	
		String htmltext = "noquest";
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		
		if(npcId == LEIRA)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				htmltext = "33952-1.htm";
			}
			else
				htmltext = "noquest";
			if(cond == 1)
			{
				htmltext = "33952-5.htm";
			}
			else if(cond == 2)
			{
				st.giveItems(ADENA_ID, 2000, true);
				st.addExpAndSp(22973, 2);
				
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
				
				htmltext = "33952-6.htm";
			}
			
		}
		else if(npcId == KIKU_CAVE)
		{
			if(cond == 1)
			{
				htmltext = "33995.htm";
			}
		}
		
		return htmltext;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		return (player.getLevel() >= minLevel && player.getLevel() <= maxLevel && player.getRace() == Race.ERTHEIA);
	}
}