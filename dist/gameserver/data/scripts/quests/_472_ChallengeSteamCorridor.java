package quests;


import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _472_ChallengeSteamCorridor extends Quest implements ScriptFile
{
	//npc
	public static final int FIOREN = 33044;
	
	//mobs
	public static final int KECHI = 25532;
	
	@Override
	public void onLoad()
	{}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}

	public _472_ChallengeSteamCorridor()
	{
		super(true);
		addStartNpc(FIOREN);
		addKillId(KECHI);
		addLevelCheck(97);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if(event.equalsIgnoreCase("33044-3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		
		if(event.equalsIgnoreCase("33044-6.htm"))
		{
			st.giveItems(30387,10); // hell proof
			st.unset("cond");
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(this);		
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		int npcId = npc.getNpcId();
		int state = st.getState();
		int cond = st.getCond();
		if(npcId == FIOREN)
		{
			if(state == 1)
			{
				if(player.getLevel() < 97)
					return "33044-lvl.htm";
				if(!st.isNowAvailable())
					return "33044-comp.htm";
					
				if(player.getLevel() < 97)
					return "33044-lvl.htm";
					
				return "33044.htm";
			}
			if(state == 2)
			{
				if(cond == 1)
					return "33044-4.htm";
				if(cond == 2)
				{
					return "33044-5.htm";
				}					
			}
		}
		return "noquest";
	}
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if(cond != 1 || npc == null)
			return null;
		st.setCond(2);
		return null;
	}	
}