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
public class _10770_InSearchOfTheGrail extends Quest implements ScriptFile
{

	private static final int LORAIN = 30673;
	private static final int JANSSEN = 30484;
	
	private final static int PORTA = 20213;
	private final static int EXCURO = 20214;
	private final static int RICENSEO = 20216;
	private final static int KRATO = 20217;
	private final static int SHINDEBARN = 21036;

	private static final int SHINING_MYSTERIOUS_FRAGMENT = 39711;
	private static final int STEEL_DOOR_COIN = 37045;
	private static final int SCROLL_EWC = 951;
	private static final int SCROLL_EAC = 952;
	
	private static final int minLevel = 30;
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

	public _10770_InSearchOfTheGrail()
	{
		super(false);
		addStartNpc(LORAIN, JANSSEN);
		
		addKillId(PORTA, EXCURO, RICENSEO, KRATO, SHINDEBARN);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("30673-5.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("30484-2.htm"))
		{
			st.takeAllItems(SHINING_MYSTERIOUS_FRAGMENT);
			st.giveItems(STEEL_DOOR_COIN, 30, true);
			st.giveItems(SCROLL_EWC, 2, true);
			st.giveItems(SCROLL_EAC, 5, true);
			st.addExpAndSp(2342300, 562);
			
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
		
		if(npcId == LORAIN)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				if(cond == 0)
				{
					htmltext = "30673-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "30673-5.htm";
				}
				else if(cond == 2)
				{
					htmltext = "30673-6.htm";
				}
			}
			
		}
		else if(npcId == JANSSEN)
		{
			if(cond == 2)
			{
				htmltext = "30484-1.htm";
			}
		}
		
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(	npcId == PORTA || 
			npcId == EXCURO || 
			npcId == RICENSEO ||
			npcId == KRATO || 
			npcId == SHINDEBARN)
		{
			if(Math.random() < 0.6)
			{
				st.giveItems(SHINING_MYSTERIOUS_FRAGMENT, 1);
			}
		}
		
		if(getItemCountById(st.getPlayer(), SHINING_MYSTERIOUS_FRAGMENT) >= 30)
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