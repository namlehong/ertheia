package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2s.commons.util.Rnd;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.scripts.ScriptFile;

public class _750_SevenFlowers extends Quest implements ScriptFile
{
	private static final int Dadfena = 33697;
	private static final int[] Mandragoras = { 23210, 23211 };
	private static final int[] FlowersNPC = { 33720, 33721, 33722, 33723, 33724, 33725, 33726 }; 
	private static final int StrangeSeed = 34963;
  
	private static final int[] Flowers = { 34964, 34965, 34966, 34967, 34968, 34969, 34970 };
	private static final int DeadChest = 35546;
	
	public _750_SevenFlowers()
	{
		super(PARTY_ALL);

		addStartNpc(Dadfena);
		addTalkId(Dadfena);
		addKillId(Mandragoras);
		addKillId(FlowersNPC);
		addQuestItem(StrangeSeed);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_accept"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == Dadfena)
		{
			if(st.getPlayer().getLevel() < 95)
				return "np-level.htm"; 
			else if(cond == 0)
				return "001.htm";
		}	
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if(st.getCond() != 1)
			return null;
		if(ArrayUtils.contains(Mandragoras, npc.getNpcId()))
		{
			if(st.getCond() == 1)
				st.setCond(2);
			st.giveItems(StrangeSeed, 1);	
		}
		else if(Rnd.chance(50) && ArrayUtils.contains(FlowersNPC, npc.getNpcId()))
		{
			int flowerId = npc.getNpcId() + 244;
			if(st.getCond() >= 2)
			{
				st.setCond(3);
				st.giveItems(flowerId, 1);
			}
		}
		return null;
	}
	
	@Override
	public boolean checkStartCondition(Player player)
	{
		return player.getLevel() >= 95;
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