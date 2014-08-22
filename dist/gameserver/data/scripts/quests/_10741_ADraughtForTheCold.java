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
 */
public class _10741_ADraughtForTheCold extends Quest implements ScriptFile
{

	private static final int SIVANTHE = 33951;
	private static final int LEIRA = 33952;
	
	private final static int HONEYBEE = 23452;
	private final static int ROBUST_HONEYBEE = 23484;
	private final static int KIKU = 23453;
	
	private final static int EMPTY_JAR  = 39527;
	private final static int SWEET_HONEY  = 39528;
	private final static int NUTRITIOUS_MEAT  = 39529;
	
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

	public _10741_ADraughtForTheCold()
	{
		super(false);
		addStartNpc(SIVANTHE);
		addTalkId(LEIRA);
		
		addKillId(HONEYBEE, ROBUST_HONEYBEE, KIKU);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33951-2.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33952-2.htm"))
		{
			st.takeItems(SWEET_HONEY, 10);
			st.takeItems(NUTRITIOUS_MEAT, 10);
			
			st.giveItems(ADENA_ID, 2000);
			st.addExpAndSp(22973, 2);
			
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
		
		if(npcId == SIVANTHE)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				htmltext = "33951-1.htm";
			}
			else
				htmltext = "noquest";
			if(cond == 1)
			{
				htmltext = "33951-2.htm";
			}
			
		}
		else if(npcId == LEIRA)
		{
			if(cond == 2)
			{
				htmltext = "33952-1.htm";
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
			if(npc.getNpcId() == HONEYBEE || npc.getNpcId() == ROBUST_HONEYBEE)
			{
				st.playSound(SOUND_MIDDLE);
				st.takeItems(EMPTY_JAR, 1);
				st.giveItems(SWEET_HONEY, 1);
			}
			
			if(npc.getNpcId() == KIKU)
			{
				st.playSound(SOUND_MIDDLE);
				st.giveItems(NUTRITIOUS_MEAT, 1);
			}
		}
		
		if(getItemCountById(st.getPlayer(), SWEET_HONEY) >= 10 && getItemCountById(st.getPlayer(), NUTRITIOUS_MEAT) >= 10 )
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