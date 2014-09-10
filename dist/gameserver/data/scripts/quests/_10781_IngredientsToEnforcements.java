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
public class _10781_IngredientsToEnforcements extends Quest implements ScriptFile
{

	private static final int BACON = 33846;
	
	private final static int CORPSE_LOOTER_STAKATO = 23309;
	private final static int LESSER_LAIKEL = 23310;

	private static final int WIND_SPIRIT_FRAGMENT = 39721;
	private static final int STEEL_DOOR_COIN = 37045;
	private static final int SCROLL_EWB = 947;
	
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

	public _10781_IngredientsToEnforcements()
	{
		super(false);
		addStartNpc(BACON);
		
		addKillId(CORPSE_LOOTER_STAKATO, LESSER_LAIKEL);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33846-4.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33846-6.htm"))
		{
			st.takeAllItems(WIND_SPIRIT_FRAGMENT);
			st.giveItems(STEEL_DOOR_COIN, 37);
			st.giveItems(SCROLL_EWB, 5);
			st.addExpAndSp(3811500, 914);
			
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
		
		if(npcId == BACON)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				if(cond == 0)
				{
					htmltext = "33846-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33846-4.htm";
				}
				else if(cond == 2)
				{
					htmltext = "33846-5.htm";
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(	npcId == CORPSE_LOOTER_STAKATO || 
			npcId == LESSER_LAIKEL )
		{
			if(Math.random() < 0.6)
			{
				st.giveItems(WIND_SPIRIT_FRAGMENT, 1);
			}
		}
		
		if(getItemCountById(st.getPlayer(), WIND_SPIRIT_FRAGMENT) >= 20)
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