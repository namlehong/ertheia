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
public class _10744_StrongerThanSteel extends Quest implements ScriptFile
{

	private static final int MILONE = 33953;
	private static final int DOLKIN = 33954;
	
	private final static int TREANT = 23457;
	private final static int LEAFIE = 23458;
	
	private final static int LEAFIE_LEAF  = 39531;
	private final static int TREANT_LEAF  = 39532;
	
	private static final int minLevel = 10;
	private static final int maxLevel = 20;
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10744_StrongerThanSteel()
	{
		super(false);
		addStartNpc(MILONE);
		addTalkId(DOLKIN);
		
		addKillId(TREANT, LEAFIE);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33953-3.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33954-3.htm"))
		{
			st.setCond(2);
			
		}
		
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{	
		String htmltext = "noquest";
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		
		if(npcId == MILONE)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				if(cond == 0)
				{
					htmltext = "33953-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33953-4.htm";
				}
			}
			else
				htmltext = "noquest";
			
			
		}
		else if(npcId == DOLKIN)
		{
			if(cond == 1)
			{
				htmltext = "33954-1.htm";
			}
			else if(cond == 2)
			{
				htmltext = "33954-5.htm";
			}
			else if(cond == 3)
			{
				st.takeAllItems(LEAFIE_LEAF);
				st.takeAllItems(TREANT_LEAF);
				
				st.giveItems(ADENA_ID, 34000, true);
				st.addExpAndSp(112001, 5);
				
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
				htmltext = "33954-4.htm";
			}
				
		}
		
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(st.getCond() == 2)
		{
			if(npc.getNpcId() == TREANT)
			{
				st.playSound(SOUND_MIDDLE);
				st.giveItems(TREANT_LEAF, 1);
			}
			
			if(npc.getNpcId() == LEAFIE)
			{
				st.playSound(SOUND_MIDDLE);
				st.giveItems(LEAFIE_LEAF, 1);
			}
		}
		
		if(getItemCountById(st.getPlayer(), LEAFIE_LEAF) >= 15 && getItemCountById(st.getPlayer(), TREANT_LEAF) >= 20 )
		{
			st.setCond(3);
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