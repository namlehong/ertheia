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
public class _10740_NeverForget extends Quest implements ScriptFile
{

	private static final int SIVANTHE = 33951;
	private static final int REMEMBRANCE_TOWER = 33989;
	
	private final static int KEEN_FLOATO = 23449;
	private final static int RATEL = 23450;
	private final static int ROBUS_RATEL = 23451;
	
	private final static int UNNAMED_RELIC  = 39526;
	
	private final static int HEALING_POTION  = 1060;
	private final static int KNOWLEDGE_RING  = 875;
	
	private static final int minLevel = 6;
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

	public _10740_NeverForget()
	{
		super(false);
		addStartNpc(SIVANTHE);
		addTalkId(REMEMBRANCE_TOWER);
		
		addKillId(KEEN_FLOATO, RATEL, ROBUS_RATEL);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33951-3.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33989-2.htm"))
		{
			st.setCond(3);
			st.takeItems(UNNAMED_RELIC, 20);
			st.playSound(SOUND_MIDDLE);
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
				htmltext = "33951-4.htm";
			}
			
			if(cond == 3)
			{
				st.giveItems(ADENA_ID, 1600, true);
				st.giveItems(HEALING_POTION, 100, true);
				st.giveItems(KNOWLEDGE_RING, 2);
				st.addExpAndSp(16851, 0);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
				
				htmltext = "33951-5.htm";
			}
		}
		else if(npcId == REMEMBRANCE_TOWER)
		{
			if(cond == 2)
			{
				htmltext = "33989-1.htm";
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
			if(npc.getNpcId() == KEEN_FLOATO || npc.getNpcId() == RATEL || npc.getNpcId() == ROBUS_RATEL)
			{
				st.playSound(SOUND_MIDDLE);
				st.giveItems(UNNAMED_RELIC, 1);
			}
		}
		
		if(getItemCountById(st.getPlayer(), UNNAMED_RELIC) >= 20)
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