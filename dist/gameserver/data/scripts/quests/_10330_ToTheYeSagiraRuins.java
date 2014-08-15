package quests;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.scripts.ScriptFile;

public class _10330_ToTheYeSagiraRuins extends Quest implements ScriptFile
{
	private static final int ATRAN = 33448;
	private static final int RAXIS = 32977;

	public _10330_ToTheYeSagiraRuins()
	{
		super(false);
		addStartNpc(ATRAN);
		addTalkId(RAXIS);
		addLevelCheck(8, 25);
		addQuestCompletedCheck(_10329_BackupSeekers.class);
		addRaceCheck(true, true, true, true, true, true, false);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		Player player = st.getPlayer();
		Reflection r = player.getReflection();
		if(event.equalsIgnoreCase("3.htm"))
		{
			st.set("cond", "1", true);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		} 
		else if(event.equalsIgnoreCase("6.htm"))
		{
			player.sendPacket(new ExShowScreenMessage(NpcString.ARMOR_HAS_BEEN_ADDED_TO_YOUR_INVENTORY, 4500, ScreenMessageAlign.TOP_CENTER));
			st.giveItems(57, 62000);
			st.giveItems(29, 1);
			st.giveItems(22, 1);
			st.addExpAndSp(23000, 25000);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getInt("cond");
		Player player = st.getPlayer();
		if(npcId == ATRAN)
		{
			if(cond == 0)
			{
				if(checkStartCondition(player))
					htmltext = "1.htm";
				//else TODO
			}
			else if(cond == 1)
				htmltext = "3.htm";
		}
		else if(npcId == RAXIS)
		{
			if(cond == 1) 
				htmltext = "4.htm";
		}
		return htmltext;
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
