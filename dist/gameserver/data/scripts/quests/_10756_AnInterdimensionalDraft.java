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
public class _10756_AnInterdimensionalDraft extends Quest implements ScriptFile
{

	private static final int PIO = 33963;
	
	private final static int WINDIMA = 23414;
	private final static int WINDIMA_FERI = 23415;
	private final static int WINDIMA_RESH = 23416;
	private final static int WHISPERING_WIND = 23457;
	private final static int SOBBING_WIND = 21023;
	private final static int BABBLING_WIND = 21024;
	private final static int GIGGLING_WIND = 21025;
	private final static int SINGING_WIND = 21026;

	private static final int STEEL_DOOR_COIN = 37045;
	private static final int UNWORLDLY_WIND = 39493;
	
	private static final int minLevel = 20;
	private static final int maxLevel = 99;
	
	private static final String WIND_KILL_LIST = "wind_kill_list";
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10756_AnInterdimensionalDraft()
	{
		super(false);
		addStartNpc(PIO);
		
		addKillId(WINDIMA, WINDIMA_FERI, WINDIMA_RESH, WHISPERING_WIND, SOBBING_WIND, BABBLING_WIND, GIGGLING_WIND, SINGING_WIND);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33963-5.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33963-7.htm"))
		{
			st.giveItems(STEEL_DOOR_COIN, 8);
			st.addExpAndSp(174222, 41);
			
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
					htmltext = "33963-5.htm";
				}
				else if(cond == 2)
				{
					htmltext = "33963-6.htm";
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
		
		if( npcId == WINDIMA || 
			npcId == WINDIMA_FERI || 
			npcId == WINDIMA_RESH || 
			npcId == WHISPERING_WIND || 
			npcId == SOBBING_WIND || 
			npcId == BABBLING_WIND || 
			npcId == GIGGLING_WIND ||
			npcId == SINGING_WIND)
		{
			st.giveItems(UNWORLDLY_WIND, 1);
		}
		
		if(getItemCountById(st.getPlayer(), UNWORLDLY_WIND) >= 30)
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
		QuestState qs = player.getQuestState(_10755_LettersFromTheQueen_WindyHill.class);
		
		return (player.getLevel() >= minLevel && 
				player.getLevel() <= maxLevel && 
				player.getRace() == Race.ERTHEIA && 
				qs != null && 
				qs.getState() == COMPLETED);
	}
}