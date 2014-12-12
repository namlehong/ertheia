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
public class _10743_StrangeFungus extends Quest implements ScriptFile
{

	private static final int LEIRA = 33952;
	private static final int MILONE = 33953;
	
	private final static int GROWLER = 23455;
	private final static int ROBUST_GROWLER = 23486;
	private final static int EVOLVE_GROWLER = 23456;
	
	private final static int MUSHROOM_SPORE  = 39530;
	private final static int LEATHER_SHOES  = 37;
	
	private static final int minLevel = 13;
	private static final int maxLevel = 20;
	
	public static final String GROWLER_LIST = "GROWLER_LIST";
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10743_StrangeFungus()
	{
		super(false);
		addStartNpc(LEIRA);
		addTalkId(MILONE);
		
		addKillId(GROWLER, ROBUST_GROWLER, EVOLVE_GROWLER);
		
		addKillNpcWithLog(1, GROWLER_LIST, 0, GROWLER, ROBUST_GROWLER);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33952-3.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33953-3.htm"))
		{
			st.takeAllItems(MUSHROOM_SPORE);
			
			st.giveItems(ADENA_ID, 62000, true);
			st.giveItems(LEATHER_SHOES, 1);
			st.addExpAndSp(62876, 2);
			
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			player.sendPacket(new ExShowScreenMessage("Bạn nhận được Leather Shoes, kiểm tra thùng đồ nhé", 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			
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
				htmltext = "33952-3.htm";
			}
			
		}
		else if(npcId == MILONE)
		{
			if(cond == 2)
			{
				htmltext = "33953-1.htm";
			}
		}
		
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(st.getCond() == 1)
		{
			if(npc.getNpcId() == GROWLER || npc.getNpcId() == ROBUST_GROWLER)
			{
				updateKill(npc, st);
				
				if(Math.random()<0.33)
				{
					st.addSpawn(EVOLVE_GROWLER, npc.getX(), npc.getY(), npc.getZ(), 0, 0, 120000);
				}
			}
			
			if(npc.getNpcId() == EVOLVE_GROWLER)
			{
				st.playSound(SOUND_MIDDLE);
				st.giveItems(MUSHROOM_SPORE, 1);
			}
		}
		
		if(getItemCountById(st.getPlayer(), MUSHROOM_SPORE) >= 10)
		{
			st.setCond(2);
		}
		
		return null;
	}
	
	private long getItemCountById(Player player, int itemId)
	{
		long itemCount = 0;
		
		PcInventory inventory = player.getInventory();
		
		if(inventory!= null)
		{
			ItemInstance itemInstance = inventory.getItemByItemId(itemId);

			if(itemInstance!= null)
				itemCount = itemInstance.getCount();
		}
		
		return itemCount;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		return (player.getLevel() >= minLevel && player.getLevel() <= maxLevel && player.getRace() == Race.ERTHEIA);
	}
}