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
public class _10763_TerryfyingChetuba extends Quest implements ScriptFile
{

	private static final int VORBOS = 33966;
	
	private final static int CHERTUBA_MIRAGE = 23421;
	private final static int CHERTUBA_ILLUSION = 23422;

	private static final int MAGIC_CHAIN_KEY_2 = 39489;
	private static final int STEEL_DOOR_COIN = 37045;
	
	private static final int minLevel = 34;
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

	public _10763_TerryfyingChetuba()
	{
		super(false);
		addStartNpc(VORBOS);
		
		addKillId(CHERTUBA_MIRAGE, CHERTUBA_ILLUSION);
		
		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}
	

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33966-3.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33966-5.htm"))
		{
			st.takeItems(MAGIC_CHAIN_KEY_2, 1);
			st.giveItems(STEEL_DOOR_COIN, 20);
			st.addExpAndSp(354546, 85);
			
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
		
		if(npcId == VORBOS)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				if(cond == 0)
				{
					htmltext = "33966-1.htm";
				}
				else if(cond == 1)
				{
					htmltext = "33966-3.htm";
				}
				else if(cond == 2)
				{
					htmltext = "33966-4.htm";
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
		
		if((npcId == CHERTUBA_MIRAGE || npcId == CHERTUBA_ILLUSION) && getItemCountById(st.getPlayer(), MAGIC_CHAIN_KEY_2) == 0 && Math.random()<0.4)
		{
			st.giveItems(MAGIC_CHAIN_KEY_2, 1);
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
		QuestState qs = player.getQuestState(_10762_MarionetteSpirit.class);
		
		return (player.getLevel() >= minLevel && 
				player.getLevel() <= maxLevel && 
				qs != null && 
				qs.getState() == COMPLETED);
	}
}