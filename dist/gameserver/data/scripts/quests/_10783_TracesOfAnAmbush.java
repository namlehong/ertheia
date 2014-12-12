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
public class _10783_TracesOfAnAmbush extends Quest implements ScriptFile
{

	private static final int NOVAIN = 33866;
	
	private final static int KRANROT = 20650;
	private final static int YINTZU = 20647;
	private final static int HAMRUT = 20649;
	private final static int PALLIOTE = 20648;
	private final static int FALLEN_ORC_SHAMAN = 21258;
	private final static int SHARP_TALON_TIGER = 21021;
	private final static int FALLEN_ORC_CAPTAIN = 21022;
	private final static int EMBRYO_PREDATOR = 27539;

	private static final int MISSIVE_SCRAP = 39722;
	private static final int STEEL_DOOR_COIN = 37045;
	private static final int SCROLL_EAB = 948;
	
	private static final int minLevel = 58;
	private static final int maxLevel = 61;
	
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _10783_TracesOfAnAmbush()
	{
		super(false);
		addStartNpc(NOVAIN);
		
		addKillId(KRANROT, YINTZU, HAMRUT, PALLIOTE, PALLIOTE, SHARP_TALON_TIGER, FALLEN_ORC_CAPTAIN, EMBRYO_PREDATOR);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33866-4.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33866-6.htm"))
		{
			st.takeAllItems(MISSIVE_SCRAP);
			st.giveItems(STEEL_DOOR_COIN, 34, true);
			st.giveItems(SCROLL_EAB, 5, true);
			st.addExpAndSp(5482574, 1315);
			
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
		
		if(npcId == NOVAIN)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				if(cond == 0)
				{
					htmltext = "33866-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33866-4.htm";
				}
				else if(cond == 2)
				{
					htmltext = "33866-5.htm";
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
		Player player = st.getPlayer();
		
		if(player == null) return null;
		
		if(	npcId == KRANROT || 
			npcId == YINTZU || 
			npcId == HAMRUT || 
			npcId == PALLIOTE || 
			npcId == FALLEN_ORC_SHAMAN || 
			npcId == SHARP_TALON_TIGER )
		{
			if(Math.random() < 0.6 && cond == 1)
			{
				//spawn Embryo Predator and attack player and despawn after 3 minutes
				NpcInstance embryo_predator = st.addSpawn(EMBRYO_PREDATOR, npc.getLoc().getX(), npc.getLoc().getY(), npc.getLoc().getZ(), 180000);
				embryo_predator.getAggroList().addDamageHate(player, 10000, 10000);
				embryo_predator.setAggressionTarget(player);
				embryo_predator.getAI().Attack(player, false, false);
			}
		}
		
		if(	npcId == EMBRYO_PREDATOR)
		{
			st.giveItems(MISSIVE_SCRAP, 1);
		}
		
		if(getItemCountById(st.getPlayer(), MISSIVE_SCRAP) >= 10)
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
		
		return (player.getLevel() >= minLevel && 
				player.getLevel() <= maxLevel &&
				player.getRace() == Race.ERTHEIA);
	}
}