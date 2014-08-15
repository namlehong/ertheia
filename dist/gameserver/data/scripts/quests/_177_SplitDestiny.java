package quests;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.SubClass;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.SubClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExSubjobInfo;
import l2s.gameserver.network.l2.s2c.SocialActionPacket;
import l2s.gameserver.network.l2.s2c.SkillListPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.scripts.ScriptFile;

/**
 * @author GodWorld & Bonux
**/
public class _177_SplitDestiny extends Quest implements ScriptFile
{
	// NPC'S
	private static final int HADEL = 33344;
	private static final int ISHUMA = 32615;

	// Item's
	private static final int PETRIFIED_GIANTS_HAND = 17718;
	private static final int PETRIFIED_GIANTS_FOOT = 17719;
	private static final int PETRIFIED_GIANTS_HAND_PIECE = 17720;
	private static final int PETRIFIED_GIANTS_FOOT_PIECE = 17721;

	// Other's
	private static final int[] HAND_DROP_MONSTERS = { 21549, 21547, 21548, 21582 };
	private static final int[] FOOT_DROP_MONSTERS = { 22257, 22258, 22259, 22260 };
	private static final int UNIDENTIFIED_TWILIGHT_NECKLACE = 18168;

	public _177_SplitDestiny()
	{
		super(false);
		addStartNpc(HADEL);
		addTalkId(HADEL, ISHUMA);
		addKillId(HAND_DROP_MONSTERS);
		addKillId(FOOT_DROP_MONSTERS);
		addQuestItem(PETRIFIED_GIANTS_HAND, PETRIFIED_GIANTS_FOOT, PETRIFIED_GIANTS_HAND_PIECE, PETRIFIED_GIANTS_FOOT_PIECE);
		addLevelCheck(80);
		addClassLevelCheck(3);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();

		String htmltext = event;
		if(event.equalsIgnoreCase("33344-14.htm"))
		{
			if(!checkStartCondition(player)) // Для того чтобы не возникла подмена.
				return "";

			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.set("accepted_sub_id", st.getPlayer().getActiveClassId());
		}
		else if(event.equalsIgnoreCase("33344-19.htm"))
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("33344-22.htm"))
		{
			st.setCond(7);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("32615-03.htm"))
		{
			st.takeItems(PETRIFIED_GIANTS_HAND_PIECE, -1);
			st.takeItems(PETRIFIED_GIANTS_FOOT_PIECE, -1);
			st.setCond(8);
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("33344-25.htm"))
		{
			st.takeItems(PETRIFIED_GIANTS_HAND, -1);
			st.takeItems(PETRIFIED_GIANTS_FOOT, -1);
			st.set("talk", "1");
		}
		else if(event.equalsIgnoreCase("red") || event.equalsIgnoreCase("blue") || event.equalsIgnoreCase("green"))
		{
			if(player.getActiveClassId() != st.getInt("accepted_sub_id"))
				return "33344-16.htm";

			htmltext = "33344-29.htm";

			SubClass sub = player.getActiveSubClass();
			if(sub == null)
				return "Error! Active Subclass is null!";

			if(sub.isDual())
				return "Error! You already have double-class!";

			sub.setType(SubClassType.DUAL_SUBCLASS);

			// Для добавления дуал-класс скиллов.
			player.restoreSkills(true);
			player.sendPacket(new SkillListPacket(player));

			int classId = sub.getClassId();
			player.sendPacket(new SystemMessagePacket(SystemMsg.SUBCLASS_S1_HAS_BEEN_UPGRADED_TO_DUEL_CLASS_S2_CONGRATULATIONS).addClassName(classId).addClassName(classId));
			player.sendPacket(new ExSubjobInfo(player, true));
			player.broadcastPacket(new SocialActionPacket(player.getObjectId(), SocialActionPacket.LEVEL_UP));

			if(event.equalsIgnoreCase("red"))
				st.giveItems(10480, 1);
			else if(event.equalsIgnoreCase("blue"))
				st.giveItems(10481, 1);
			else if(event.equalsIgnoreCase("green"))
				st.giveItems(10482, 1);

			st.giveItems(UNIDENTIFIED_TWILIGHT_NECKLACE, 1);
			st.addExpAndSp(175739575, 2886300);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st) 
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		String htmltext = "noquest";
		if(npcId == HADEL)
		{
			Player player = st.getPlayer();
			if(st.isCompleted())
			{
				QuestState qs = player.getQuestState(_10338_SeizeYourDestiny.class);
				if(qs == null || player.getVar(qs.getQuest().getName()) == null)
					htmltext = "33344-12.htm";
				else
					htmltext = startQuest(st, player);
			}	
			else if(st.isStarted())
			{
				if(player.getActiveClassId() != st.getInt("accepted_sub_id"))
					return "33344-16.htm";

				switch(cond)
				{
					case 1:
					case 2:
						htmltext = "33344-15.htm";
						break;
					case 3:
						htmltext = "33344-17.htm";
						break;
					case 4:
					case 5:
						htmltext = "33344-20.htm";
						break;
					case 6:
						htmltext = "33344-21.htm";
						break;
					case 7:
					case 8:
						htmltext = "33344-23.htm";
						break;
					case 9:
						if(st.getInt("talk") == 1)
							htmltext = "33344-28.htm";
						else
							htmltext = "33344-24.htm";
						break;
				}
			}
			else
				htmltext = startQuest(st, player);
		}
		else if(npcId == ISHUMA)
		{
			if(st.isStarted())
			{
				if(cond == 7)
					htmltext = "32615-01.htm";
				else if(cond == 8)
				{
					st.setCond(9);
					st.playSound(SOUND_MIDDLE);
					st.giveItems(PETRIFIED_GIANTS_HAND, 2);
					st.giveItems(PETRIFIED_GIANTS_FOOT, 2);
					htmltext = "32615-04.htm";
				}
				else
					htmltext = "32615-05.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st) 
	{
		if(st.getPlayer().getActiveClassId() != st.getInt("accepted_sub_id")) // TODO: [Bonux] Проверить, нужна ли данная заглушка.
			return null;

		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(st.getCond())
		{
			case 1:
				if(ArrayUtils.contains(HAND_DROP_MONSTERS, npcId))
				{
					st.giveItems(PETRIFIED_GIANTS_HAND_PIECE, 1);
					st.setCond(2);
					st.playSound(SOUND_MIDDLE);
				}
				break;
			case 2:
				if(ArrayUtils.contains(HAND_DROP_MONSTERS, npcId) || Rnd.chance(60))
				{
					st.giveItems(PETRIFIED_GIANTS_HAND_PIECE, 1);
					if(st.getQuestItemsCount(PETRIFIED_GIANTS_HAND_PIECE) >= 10)
					{
						st.setCond(3);
						st.playSound(SOUND_MIDDLE);
					}
					else
						st.playSound(SOUND_ITEMGET);
				}
				break;
			case 4:
				if(ArrayUtils.contains(FOOT_DROP_MONSTERS, npcId))
				{
					st.giveItems(PETRIFIED_GIANTS_FOOT_PIECE, 1);
					st.setCond(5);
					st.playSound(SOUND_MIDDLE);
				}
				break;
			case 5:
				if(ArrayUtils.contains(FOOT_DROP_MONSTERS, npcId) || Rnd.chance(60))
				{
					st.giveItems(PETRIFIED_GIANTS_FOOT_PIECE, 1);
					if(st.getQuestItemsCount(PETRIFIED_GIANTS_FOOT_PIECE) >= 10)
					{
						st.setCond(6);
						st.playSound(SOUND_MIDDLE);
					}
					else
						st.playSound(SOUND_ITEMGET);
				}
				break;
		}
		return null;
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		if(player.isBaseClassActive())
			return false;

		if(player.getClassId().isOfRace(Race.ERTHEIA))
			return false;

		for(SubClass sub : player.getSubClassList().values())
		{
			if(sub.isDual())
				return false;
		}

		if(!ClassId.VALUES[player.getBaseClassId()].isOfLevel(ClassLevel.AWAKED))
			return false;

		return super.checkStartCondition(player);
	}
	
	public String startQuest(QuestState st, Player player)
	{
		if(!player.isBaseClassActive() && player.getClassId().isOfLevel(ClassLevel.THIRD) && player.getLevel() >= 80)
		{
			ClassId baseClassId = ClassId.VALUES[player.getBaseClassId()];
			if(baseClassId.isOfLevel(ClassLevel.AWAKED))
			{
				if(baseClassId.childOf(player.getClassId()))
					return "33344-noid-" + baseClassId.getId() + ".htm";

				boolean haveDualClass = false;
				for(SubClass sub : player.getSubClassList().values())
				{
					if(sub.isDual())
						haveDualClass = true;
				}

				if(haveDualClass)
					return "33344-12.htm";
				else
					return "33344-13.htm";
			}
			else
				return "33344-03.htm";
		}
		else
			return "33344-02.htm";	
	}
	
	@Override
	public void onLoad()
	{
		//
	}

	@Override
	public void onReload()
	{
		//
	}

	@Override
	public void onShutdown()
	{
		//
	}
}