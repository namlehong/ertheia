package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.actor.instances.player.Mentee;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _245_ComeToMe extends Quest implements ScriptFile
{
	private static final int FERRIS = 30847;
	
	private static final int SNAKE = 21111;
	private static final int SWAMP = 21110;
	
	private static final int ORCHAMES = 21112;
	private static final int ORC_SNIPER = 21113;
	private static final int SHAMAN_ORC = 21115;
	private static final int VLADUK_ORC = 21116;

	private static final int ASHES = 30322;
	private static final int CRYSTAL_EXP = 30323;

	public _245_ComeToMe()
	{
		super(false);

		addStartNpc(FERRIS);
		addTalkId(FERRIS);
		
		addKillId(SNAKE);
		addKillId(SWAMP);

		addKillId(ORCHAMES);
		addKillId(ORC_SNIPER);
		addKillId(SHAMAN_ORC);		
		addKillId(VLADUK_ORC);	
		
		addQuestItem(ASHES);
		addQuestItem(CRYSTAL_EXP);
		addLevelCheck(40, 50);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();
		
		if(event.equalsIgnoreCase("30847-3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);			
		}
		
		if(event.equalsIgnoreCase("30847-7.htm"))
		{
			st.takeItems(ASHES, -1);
			st.setCond(3);	
		}		
		
		if(event.equalsIgnoreCase("crystals"))
		{
			if(player.getInventory().getCountOf(1461) >= 100 && trySetCondMentee(player, 4))
			{
				st.takeItems(1461, 100);
				return "30847-12.htm";
			}
			else
				return "30847-13.htm";	
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		int id = st.getState();
		Player player = st.getPlayer();
		if(player.getLevel() < 40 || player.getLevel() > 50)
			return "30847-lvl.htm";

		if(player.getMenteeList().size() > 0 && checkMenteesQuest(player))
		{
			switch(getMenteeCond(player))
			{
				case 3:
					return "30847-10.htm";
				default:
					return "30847-15.htm";
			}
			
		}	
		if(id == COMPLETED)
			return "30847-comp.htm";
		
		if(npcId == FERRIS)
		{
			if(cond == 0)
			{
				return "30847.htm";
			}
			
			if(cond == 1)
			{
				return "30847-4.htm";
			}
			
			if(cond == 2)
			{
				return "30847-5.htm";
			}
			
			if(cond == 3 && player.getVar("_245") == null) 	
			{
				if(player.getMenteeList().getMentor() == 0)
					return "30847-8.htm";
				else
				{
					int mentorId = player.getMenteeList().getMentor();
					Player mentorPlayer = World.getPlayer(mentorId);
					if(mentorPlayer == null)
						return "30847-6.htm";
					else
						return "30847-9.htm";
				}	
			}
			if(cond == 3 && player.getVar("_245") != null)
			{
				player.unsetVar("_245");
				st.setCond(4);
				return "30847-16.htm";
			}
			if(cond == 4)
			{
				return "30847-17.htm";
			}
			if(cond == 5)
			{
				st.takeItems(CRYSTAL_EXP, -1);
				if(player.getClan() != null && player.getClan().getLevel() >= 5)
					player.getClan().incReputation(1000, false, "_245_ComeToMe");
				st.giveItems(30383, 1); //ring
				st.addExpAndSp(2018733, 200158);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);				
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int _id = npc.getNpcId();
		if(st.getCond() == 1)
		{
			if(_id == SNAKE || _id == SWAMP)
				st.rollAndGive(ASHES, 1, 10);
			if(st.getQuestItemsCount(ASHES) >= 15)	
				st.setCond(2);
		}	

		if(st.getCond() == 4)
		{
			if(_id == ORCHAMES || _id == ORC_SNIPER || _id == SHAMAN_ORC || _id == VLADUK_ORC)
				st.rollAndGive(CRYSTAL_EXP, 1, 5);
			if(st.getQuestItemsCount(CRYSTAL_EXP) >= 12)	
				st.setCond(5);
		}		
		return null;	
			
	}	

	private static boolean checkMenteesQuest(Player player)
	{
		boolean somebodyhasquest = false;
		
		for(Mentee mentee : player.getMenteeList().values())
		{
			Player menteePlayer = World.getPlayer(mentee.getObjectId());
			if(menteePlayer == null || !menteePlayer.isOnline())
				continue;
			QuestState questState = menteePlayer.getQuestState(_245_ComeToMe.class);
			if(questState == null)
				continue;
			int cond = questState.getCond();
			if(cond == 3) //todo maybe more conds?
			{
				somebodyhasquest = true;
				break; //important to break in case there's more than one mentee with this quest, we'll take only the first one!
			}
		}
		return somebodyhasquest;
	}
	
	private static int getMenteeCond(Player player)
	{
		int qs = 0;
		
		for(Mentee mentee : player.getMenteeList().values())
		{
			Player menteePlayer = World.getPlayer(mentee.getObjectId());
			if(menteePlayer == null || !menteePlayer.isOnline())
				continue;
			QuestState questState = menteePlayer.getQuestState(_245_ComeToMe.class);
			if(questState == null)
				continue;
			int cond = questState.getCond();
			if(cond == 3) //todo maybe more conds?
			{
				qs = cond;
				break; //important to break in case there's more than one mentee with this quest, we'll take only the first one!
			}
		}
		return qs;	
	}
	
	private static boolean trySetCondMentee(Player player, int cond)
	{
		boolean isSet = false;
		for(Mentee mentee : player.getMenteeList().values())
		{
			Player menteePlayer = World.getPlayer(mentee.getObjectId());
			if(menteePlayer == null || !menteePlayer.isOnline())
				continue;
			QuestState questState = menteePlayer.getQuestState(_245_ComeToMe.class);
			if(questState == null)
				continue;
			int cond1 = questState.getCond();
			if(cond1 == cond - 1) //todo maybe more conds?
			{
				menteePlayer.setVar("_245","1",-1);
				isSet = true;
				break; //important to break in case there's more than one mentee with this quest, we'll take only the first one!
			}
		}	
		return isSet;
	}
	
	@Override
	public boolean checkStartNpc(NpcInstance npc, Player player)
	{
		if(player.getMenteeList().size() > 0 && checkMenteesQuest(player))
			return true;
		
		if(player.getClan() == null)
			return false;
			
		if(player.getLvlJoinedAcademy() == 0)
			return false;
			
		//if(player.getMenteeList().getMentor() == 0)
			//return false;
		
		if(player.getLevel() > 75 || player.getLevel() < 70)
			return false;
		return true;	
	}		
	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}
