package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
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
public class _10745_TheSecretIngredient extends Quest implements ScriptFile
{

	private static final int DOLKIN1 = 33954;
	private static final int DOLKIN2 = 33002;
	private static final int KARLA = 33933;
	
	private final static int KERAPHON = 23459;
	
	private final static int DOLKIN_REPORT = 39534;
	private final static int SECRET_INGREDIENT = 39533;
	private final static int FAERON_BOX1 = 40262;
	private final static int FAERON_BOX2 = 40263;
	
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

	public _10745_TheSecretIngredient()
	{
		super(false);
		addStartNpc(DOLKIN1);
		addTalkId(DOLKIN2, KARLA);
		
		addKillId(KERAPHON);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();

		if(event.equalsIgnoreCase("33954-2.htm"))
		{
			st.setState(STARTED);
			st.setCond(0);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33954-4.htm"))
		{
			st.setCond(3);
			st.giveItems(DOLKIN_REPORT, 1);
			st.playSound(SOUND_MIDDLE);
		}
		
		if(event.equalsIgnoreCase("33933-2.htm"))
		{
			st.giveItems(ADENA_ID, 48000);
			
			if(player.isMageClass())
				st.giveItems(FAERON_BOX1, 1);
			else
				st.giveItems(FAERON_BOX2, 1);
			
			st.addExpAndSp(241076, 5);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
			
		}
		
		if(event.equalsIgnoreCase("enter_instance"))
		{
			st.setCond(1);
			enterInstance(st, 402);
			return null;
		}
		
		if(event.equalsIgnoreCase("leave_instance"))
		{
			player.getReflection().collapse();
			return null;
		}
		
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{	
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		Player player = st.getPlayer();

		if(npcId == DOLKIN1)
		{
			if(checkStartCondition(st.getPlayer()))
			{
				if(cond == 0)
					htmltext = "33954-1.htm";
				else if(cond == 1)
					htmltext = "33954-2.htm";
				else if(cond == 2)
					htmltext = "33954-3.htm";
			}
			else
				htmltext = "noquest";
			
		}
		else if(npcId == KARLA)
		{
			if(cond == 3)
				htmltext = "33933-1.htm";
		}
		
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		
		if(npc.getNpcId() == KERAPHON)
		{
			st.giveItems(SECRET_INGREDIENT, 1);
			st.setCond(2);
		}
		
		return null;
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		return (player.getLevel() >= minLevel && player.getLevel() <= maxLevel && player.getRace() == Race.ERTHEIA);
	}
}