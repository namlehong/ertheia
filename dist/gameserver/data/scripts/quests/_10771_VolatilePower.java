package quests;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
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
public class _10771_VolatilePower extends Quest implements ScriptFile
{

	private static final int JANSSEN = 30484;
	private static final int HIDDEN_CRUSHER = 33990;
	
	private final static int FRAGMENT_EATER = 27533;

	private static final int SHINING_MYSTERIOUS_FRAGMENT = 39713;
	private static final int NORMAL_FRAGMENT_DUST = 39714;
	private static final int STEEL_DOOR_COIN = 37045;
	private static final int SCROLL_EWC = 951;
	private static final int SCROLL_EAC = 952;
	
	private static final int minLevel = 44;
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

	public _10771_VolatilePower()
	{
		super(false);
		addStartNpc(JANSSEN);

		addTalkId(HIDDEN_CRUSHER);
		//addKillId(FRAGMENT_EATER);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("30484-5.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(SHINING_MYSTERIOUS_FRAGMENT, 20);
		}
		
		if(event.equalsIgnoreCase("30484-7.htm"))
		{
			st.takeAllItems(NORMAL_FRAGMENT_DUST);
			st.giveItems(STEEL_DOOR_COIN, 20, true);
			st.giveItems(SCROLL_EAC, 5, true);
			st.addExpAndSp(2708350, 650);
			
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		
		if(event.equalsIgnoreCase("inser_fragment"))
		{
			if(st.getCond() == 1)
			{
				npc.toggleVisible();
				
				ThreadPoolManager.getInstance().schedule(new VisibilityScheduleTimerTask(npc), 180000);
				
				st.takeItems(SHINING_MYSTERIOUS_FRAGMENT, 1);
				st.giveItems(NORMAL_FRAGMENT_DUST, 1);
				
				if(getItemCountById(player, NORMAL_FRAGMENT_DUST) >= 20)
					st.setCond(3);
				
				//spawn Fragment Eaters and attack player and despawn after 3 minutes
				NpcInstance eater1 = st.addSpawn(FRAGMENT_EATER, npc.getLoc().getX() - 40, npc.getLoc().getY() - 40, npc.getLoc().getZ(), 180000);
				NpcInstance eater2 = st.addSpawn(FRAGMENT_EATER, npc.getLoc().getX(), npc.getLoc().getY(), npc.getLoc().getZ(), 180000);
				NpcInstance eater3 = st.addSpawn(FRAGMENT_EATER, npc.getLoc().getX() + 40, npc.getLoc().getY() + 40, npc.getLoc().getZ(), 180000);
				
				eater1.getAggroList().addDamageHate(player, 10000, 10000);
				eater2.getAggroList().addDamageHate(player, 10000, 10000);
				eater3.getAggroList().addDamageHate(player, 10000, 10000);
				
				eater1.setAggressionTarget(player);
				eater2.setAggressionTarget(player);
				eater3.setAggressionTarget(player);
				
				eater1.getAI().Attack(player, false, false);
				eater2.getAI().Attack(player, false, false);
				eater3.getAI().Attack(player, false, false);
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
		
		if(npcId == JANSSEN)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				if(cond == 0)
				{
					htmltext = "30484-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "30484-5.htm";
				}
				else if(cond == 3)
				{
					htmltext = "30484-6.htm";
				}
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
	public boolean checkStartCondition(Player player)
	{
		QuestState qs = player.getQuestState(_10770_InSearchOfTheGrail.class);
		
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