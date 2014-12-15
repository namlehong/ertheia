package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Hien Son
 * 
 */
public class _10775_InSearchOfGiants extends Quest implements ScriptFile
{

	private static final int ROMBEL = 30487;
	private static final int BElKADHI = 30485;
	
	private final static int DARK_LORD = 20753;
	private final static int DARK_KNIGHT = 20754;
	private final static int SOLDIER_DARKNESS = 21040;
	private final static int OSSIUD = 21037;
	private final static int PERUM = 20221;
	private final static int LIANGMA = 21038;
	private final static int ACHELANDO = 23153;
	private final static int STYRINDO = 23154;
	private final static int ASHENDE = 23155;

	private static final int ENERGY_OF_REGENERATION = 39715;
	private static final int STEEL_DOOR_COIN = 37045;
	private static final int SCROLL_EWC = 951;
	private static final int SCROLL_EAC = 952;
	
	private static final int minLevel = 46;
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

	public _10775_InSearchOfGiants()
	{
		super(false);
		addStartNpc(ROMBEL);
		addTalkId(BElKADHI);
		
		addKillId(DARK_LORD, DARK_KNIGHT, SOLDIER_DARKNESS, OSSIUD, PERUM, LIANGMA, ACHELANDO, STYRINDO, ASHENDE);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("30487-5.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("30485-4.htm"))
		{
			st.takeAllItems(ENERGY_OF_REGENERATION);
			st.giveItems(STEEL_DOOR_COIN, 46, true);
			st.giveItems(SCROLL_EAC, 9, true);
			st.addExpAndSp(4443600, 1066);
			
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
		
		if(npcId == ROMBEL)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				if(cond == 0)
				{
					htmltext = "30487-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "30487-5.htm";
				}
			}
			
		}
		else if(npcId == BElKADHI)
		{
			if(cond == 2)
			{
				htmltext = "30485-1.htm";
			}
		}
		
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(	npcId == DARK_LORD || 
			npcId == DARK_KNIGHT || 
			npcId == SOLDIER_DARKNESS ||
			npcId == OSSIUD || 
			npcId == PERUM || 
			npcId == LIANGMA || 
			npcId == ACHELANDO || 
			npcId == STYRINDO || 
			npcId == ASHENDE)
		{
			if(Math.random() < 0.6)
			{
				st.giveItems(ENERGY_OF_REGENERATION, 1);
			}
		}
		
		if(getItemCountById(st.getPlayer(), ENERGY_OF_REGENERATION) >= 20)
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
		return (player.getLevel() >= minLevel && player.getLevel() <= maxLevel);
	}
}