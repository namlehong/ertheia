package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Hien Son
 */
public class _10746_SeeTheWorld extends Quest implements ScriptFile
{
	//NPC's
	private static final int KARLA = 33933;
	private static final int ASTIEL = 33948;
	private static final int LEVIAN = 30037;

	private final static int EMISSARY_BOX_WIZARD = 40265;
	private final static int EMISSARY_BOX_FIGHTER = 40264;
	
	private static final int minLevel = 17;
	private static final int maxLevel = 40;
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
	
	public _10746_SeeTheWorld()
	{
		super(false);
		addStartNpc(KARLA);
		addTalkId(ASTIEL, LEVIAN);
		
		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("33933-2.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("33948-2.htm"))
		{
			//tele to gludin village
			st.getPlayer().teleToLocation(-80728, 149848, -3069);
			st.setCond(2);
		}
		
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == KARLA)
		{
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = "33933-1.htm";
			}
			else if(cond == 1)
				htmltext = "33933-2.htm";
		}
		else if(npcId == ASTIEL && st.getCond() == 1)
		{
			htmltext = "33948-1.htm";
		}
		else if(npcId == LEVIAN && st.getCond() == 2)
		{
			htmltext = "33948-1.htm";
			
			if(st.getPlayer().isMageClass())
				st.giveItems(EMISSARY_BOX_WIZARD, 1);
			else
				st.giveItems(EMISSARY_BOX_FIGHTER, 1);
			
			st.giveItems(ADENA_ID, 43000, true);
			st.addExpAndSp(53422, 5);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		return (player.getLevel() >= minLevel && player.getLevel() <= maxLevel && player.getRace() == Race.ERTHEIA);
	}
}