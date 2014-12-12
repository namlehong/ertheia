package quests;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.MonsterInstance;
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
public class _10776_TheWrathOfTheGiants extends Quest implements ScriptFile
{

	private static final int BElKADHI = 30485;
	private static final int DESTROYED_DEVICE = 32366;
	private static final int GIANT_NARSIDES = 33992;
	
	private final static int ENRAGED_GIANT_NARSIDES = 27534;

	private static final int REGENERATION_DEVICE_CORE = 39716;
	private static final int STEEL_DOOR_COIN = 37045;
	private static final int SCROLL_EWC = 951;
	private static final int SCROLL_EAC = 952;
	
	private static final int minLevel = 48;
	private static final int maxLevel = 99;
	
	NpcInstance giant_narsides = null;
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10776_TheWrathOfTheGiants()
	{
		super(false);
		addStartNpc(BElKADHI);

		addTalkId(DESTROYED_DEVICE, GIANT_NARSIDES);
		addKillId(ENRAGED_GIANT_NARSIDES);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("30485-3.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(REGENERATION_DEVICE_CORE, 1);
		}
		
		if(event.equalsIgnoreCase("30485-6.htm"))
		{
			st.giveItems(STEEL_DOOR_COIN, 20, true);
			st.giveItems(SCROLL_EAC, 4, true);
			st.addExpAndSp(4838400, 1161);
			
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		
		if(event.equalsIgnoreCase("insert_core"))
		{
			if(st.getCond() == 1)
			{
				st.takeAllItems(REGENERATION_DEVICE_CORE);
				st.setCond(2);
				//spawn Giant Narsides and attack player and despawn after 3 minutes
				giant_narsides = st.addSpawn(GIANT_NARSIDES, 16376, 113336, -9094, 180000);
				
			}
			return null;
		}
		
		if(event.equalsIgnoreCase("start_fight"))
		{
			if(st.getCond() == 2)
			{
				st.setCond(3);
				
				if(giant_narsides != null) giant_narsides.deleteMe();
				
				//spawn Enraged Giant Narsides and attack player and despawn after 3 minutes
				NpcInstance enraged_giant_narsides = st.addSpawn(ENRAGED_GIANT_NARSIDES, npc.getLoc().getX(), npc.getLoc().getY(), npc.getLoc().getZ(), 180000);
				enraged_giant_narsides.getAggroList().addDamageHate(player, 10000, 10000);
				enraged_giant_narsides.setAggressionTarget(player);
				enraged_giant_narsides.getAI().Attack(player, false, false);
				
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
		
		if(npcId == BElKADHI)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				if(cond == 0)
				{
					htmltext = "30485-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "30485-3.htm";
				}
				else if(cond == 4)
				{
					htmltext = "30485-4.htm";
				}
			}
			
		}
		
		if(npcId == DESTROYED_DEVICE)
		{
			if(cond == 1 || cond == 2)
			{
				htmltext = "32366-1.htm";
			}
		}
		
		if(npcId == GIANT_NARSIDES)
		{
			if(cond == 2)
			{
				htmltext = "33992-1.htm";
			}
		}
		
		return htmltext;
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
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(	npcId == ENRAGED_GIANT_NARSIDES)
		{
			st.setCond(4);
		}
		
		return null;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		QuestState qs = player.getQuestState(_10775_InSearchOfGiants.class);
		
		return (player.getLevel() >= minLevel && 
				player.getLevel() <= maxLevel && 
				qs != null && 
				qs.getState() == COMPLETED);
	}
	

	public class VisibilityScheduleTimerTask extends RunnableImpl
	{
		NpcInstance _npc = null;

		public VisibilityScheduleTimerTask(NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl() throws Exception
		{
			if(_npc != null)
				_npc.toggleVisible();
		}
	}
}