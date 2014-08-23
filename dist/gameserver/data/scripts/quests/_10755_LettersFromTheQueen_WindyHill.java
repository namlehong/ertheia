package quests;

import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.listener.actor.player.OnLevelChangeListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.s2c.ExCallToChangeClass;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author Hien Son
 */

public class _10755_LettersFromTheQueen_WindyHill extends Quest implements ScriptFile, OnPlayerEnterListener, OnLevelChangeListener
{

	private static final int LEVIAN = 30037;
	
	private static final int SOE_GLUDIN = 39491;
	
	private static final int minLevel = 20;
	private static final int maxLevel = 99;
	
	private static final String LETTER_ALERT_STRING = "Bạn vừa nhận được thư từ Nữ Hoàng Navari\n Nhấn vào dấu hỏi dưới góc màn hình để đọc thư";

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
	
	public _10755_LettersFromTheQueen_WindyHill()
	{
		super(false);

		CharListenerList.addGlobal(this);

		addLevelCheck(minLevel, maxLevel);
		addRaceCheck(false, false, false, false, false, false, true);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		if(player == null)
			return null;

		String html = "";

		int classId = player.getClassId().getId();
		if(event.startsWith("UC"))
		{
			if(checkStartCondition(player))
			{
				alertLetterReceived();
			}
		}
		
		// Question mark clicked
		else if(event.startsWith("QM"))
		{
			int MarkId = Integer.valueOf(event.substring(2));
			System.out.println("Mark id " + MarkId);
			if(MarkId == 10755)
			{
				if(player.getRace() == Race.ERTHEIA)
					html = "tutorial_01a.htm";
				else
					return null;
			}
		}

		if(html.isEmpty())
			return null;
		st.showTutorialHTML(html);
		return null;
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		if(checkStartCondition(player))
		{
			alertLetterReceived();
		}
	}

	@Override
	public void onLevelChange(Player player, int oldLvl, int newLvl)
	{
		
		if(player.isBaseClassActive())
		{
			if(oldLvl < 20 && newLvl >= 20 && checkStartCondition(player))
			{
				if(player.getVarBoolean("@received_navari_letter_1st"))
					return;

				alertLetterReceived();
			}

		}
	}
	
	private void alertLetterReceived(QuestState st)
	{
		st.getPlayer().sendPacket(new ExShowScreenMessage(LETTER_ALERT_STRING, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		
		st.showQuestionMark(10755);
		
		st.playSound(SOUND_TUTORIAL);
		
		st.setCond(1);
		st.setState(STARTED);
		st.giveItems(SOE_GLUDIN, 1);
		
		st.getPlayer().setVar("@received_navari_letter_1st", true);
	}
	

	@Override
	public boolean checkStartCondition(Player player)
	{
		return (player.getLevel() >= minLevel && player.getLevel() <= maxLevel && player.getRace() == Race.ERTHEIA || player.getQuestState("_10755_LettersFromTheQueen_WindyHill") == null);
	}

}