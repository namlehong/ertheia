package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.actor.instances.player.Mentee;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _244_FollowMe extends Quest implements ScriptFile
{
	private static final int IZAEL = 30655;
	
	private static final int ORC_TAMLIN = 20601;
	private static final int ORC_ARCHER_TAMLIN = 20602;
	
	private static final int SPIDER_KROMBE = 20603;
	private static final int LAKIN = 20604;
	private static final int CRYSTAL_DRAKE = 20605;

	private static final int SIGN_OF_ORC_TAMLIN = 30320;
	private static final int MEMORIAL_CRYSTAL = 30321;

	public _244_FollowMe()
	{
		super(false);

		addStartNpc(IZAEL);
		addTalkId(IZAEL);
		
		addKillId(ORC_TAMLIN);
		addKillId(ORC_ARCHER_TAMLIN);

		addKillId(SPIDER_KROMBE);
		addKillId(LAKIN);
		addKillId(CRYSTAL_DRAKE);		
		
		addQuestItem(SIGN_OF_ORC_TAMLIN);
		addQuestItem(MEMORIAL_CRYSTAL);
		addLevelCheck(70, 75);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();
		
		if(event.equalsIgnoreCase("30655-3.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);			
		}
		
		if(event.equalsIgnoreCase("30655-7.htm"))
		{
			st.takeItems(SIGN_OF_ORC_TAMLIN, -1);
			st.setCond(3);	
		}		
		
		if(event.equalsIgnoreCase("crystals"))
		{
			if(player.getInventory().getCountOf(1459) >= 55 && trySetCondMentee(player, 4))
			{
				st.takeItems(1459, 55);
				return "30655-12.htm";
			}
			else
				return "30655-13.htm";	
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
		if(player.getLevel() < 70 || player.getLevel() > 75)
			return "30655-lvl.htm";

		if(player.getMenteeList().size() > 0 && checkMenteesQuest(player))
		{
			switch(getMenteeCond(player))
			{
				case 3:
					return "30655-10.htm";
				default:
					return "30655-15.htm";
			}
			
		}	
		if(id == COMPLETED)
			return "30655-comp.htm";
		
		if(npcId == IZAEL)
		{
			if(cond == 0)
			{
				return "30655.htm";
			}
			
			if(cond == 1)
			{
				return "30655-4.htm";
			}
			
			if(cond == 2)
			{
				return "30655-5.htm";
			}
			
			if(cond == 3 && player.getVar("_244") == null) 	
			{
				if(player.getMenteeList().getMentor() == 0)
					return "30655-8.htm";
				else
				{
					int mentorId = player.getMenteeList().getMentor();
					Player mentorPlayer = World.getPlayer(mentorId);
					if(mentorPlayer == null)
						return "30655-6.htm";
					else
						return "30655-9.htm";
				}	
			}
			if(cond == 3 && player.getVar("_244") != null)
			{
				player.unsetVar("_244");
				st.setCond(4);
				return "30655-16.htm";
			}
			if(cond == 4)
			{
				return "30655-17.htm";
			}
			if(cond == 5)
			{
				st.takeItems(MEMORIAL_CRYSTAL, -1);
				if(player.getClan() != null && player.getClan().getLevel() >= 5)
					player.getClan().incReputation(100, false, "_244_FollowMe");
				st.giveItems(8181, 1);
				st.addExpAndSp(606680, 39200);
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
			if(_id == ORC_TAMLIN || _id == ORC_ARCHER_TAMLIN)
				st.rollAndGive(SIGN_OF_ORC_TAMLIN, 1, 10);
			if(st.getQuestItemsCount(SIGN_OF_ORC_TAMLIN) >= 10)	
				st.setCond(2);
		}	

		if(st.getCond() == 4)
		{
			if(_id == SPIDER_KROMBE || _id == LAKIN || _id == CRYSTAL_DRAKE)
				st.rollAndGive(MEMORIAL_CRYSTAL, 1, 5);
			if(st.getQuestItemsCount(MEMORIAL_CRYSTAL) >= 8)	
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
			QuestState questState = menteePlayer.getQuestState(_244_FollowMe.class);
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
			QuestState questState = menteePlayer.getQuestState(_244_FollowMe.class);
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
			QuestState questState = menteePlayer.getQuestState(_244_FollowMe.class);
			if(questState == null)
				continue;
			int cond1 = questState.getCond();
			if(cond1 == cond - 1) //todo maybe more conds?
			{
				menteePlayer.setVar("_244","1",-1);
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
		
		if(player.getLevel() > 50 || player.getLevel() < 40)
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
