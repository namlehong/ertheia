package quests;

import java.util.HashMap;
import java.util.Map;

import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.listener.actor.player.OnLevelChangeListener;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExCallToChangeClass;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.TutorialCloseHtmlPacket;
import l2s.gameserver.network.l2.s2c.TutorialShowHtmlPacket;
import l2s.gameserver.scripts.ScriptFile;
//import l2s.gameserver.utils.ItemFunctions;

public class _255_Tutorial extends Quest implements ScriptFile, OnPlayerEnterListener, OnLevelChangeListener
{
	// table for Question Mark Clicked (35) 1st class transfer [raceId, html]
	public final Map<Integer, String> QMCc = new HashMap<Integer, String>();

	private static TutorialShowListener _tutorialShowListener;
	
	private static final String QUEEN_CALL = "Nữ Hoàng Navari đang gọi bạn";
	
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

	public _255_Tutorial()
	{
		super(false);

		CharListenerList.addGlobal(this);

		_tutorialShowListener = new TutorialShowListener();

		QMCc.put(0, "tutorial_1st_ct_human.htm");
		QMCc.put(1, "tutorial_1st_ct_elf.htm");
		QMCc.put(2, "tutorial_1st_ct_dark_elf.htm");
		QMCc.put(3, "tutorial_1st_ct_orc.htm");
		QMCc.put(4, "tutorial_1st_ct_dwarf.htm");
		QMCc.put(5, "tutorial_1st_ct_kamael.htm");
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		if(player == null)
			return null;

		String html = "";

		int classId = player.getClassId().getId();
		int Ex = st.getInt("Ex");

		// Вход в мир
		if(event.startsWith("UC"))
		{
			int level = player.getLevel();
			if(level < 6)
			{
				int uc = st.getInt("ucMemo");
				if(uc == 0)
				{
					st.set("ucMemo", "0");
					st.startQuestTimer("QT", 10000);
					st.set("Ex", "-2");
				}
				else if(uc == 1)
				{
					st.showQuestionMark(1);
					st.playTutorialVoice("tutorial_voice_006");
					st.playSound(SOUND_TUTORIAL);
				}
				else if(uc == 3)
				{
					st.showQuestionMark(12);
					st.playSound(SOUND_TUTORIAL);
					st.onTutorialClientEvent(0);
				}
			}
			else if(level >= 18 && player.getClassLevel() == 0)
			{
				st.showQuestionMark(35);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 20 && player.getQuestState("_10276_MutatedKaneusGludio") == null)
			{
				st.showQuestionMark(36);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 28 && player.getQuestState("_10277_MutatedKaneusDion") == null)
			{
				st.showQuestionMark(36);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 28 && player.getQuestState("_10278_MutatedKaneusHeine") == null)
			{
				st.showQuestionMark(36);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 28 && player.getQuestState("_10279_MutatedKaneusOren") == null)
			{
				st.showQuestionMark(36);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 28 && player.getQuestState("_10280_MutatedKaneusSchuttgart") == null)
			{
				st.showQuestionMark(36);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 28 && player.getQuestState("_10281_MutatedKaneusRune") == null)
			{
				st.showQuestionMark(36);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level == 79 && player.getQuestState("_192_SevenSignSeriesOfDoubt") == null)
			{
				st.showQuestionMark(36);
				st.playSound(SOUND_TUTORIAL);
			}
			else if(level <= 85 && player.isBaseClassActive() && player.getMenteeList().getMentor() == 0 && player.getRace() != Race.ERTHEIA)
			{
				st.showQuestionMark(37);
				st.playSound(SOUND_TUTORIAL);
			}
		}

		// Обработка таймера QT
		else if(event.startsWith("QT"))
		{
			if(Ex == -2)
			{
				if(player.getClassId().isOfRace(Race.ERTHEIA))
				{
					if(!player.getVarBoolean("@queen_called"))
					{
						for(NpcInstance tempNpc : player.getAroundNpc(2000, 500))
						{
							if(tempNpc.getNpcId() == 33931)
							{
								player.setVar("@queen_called", true);
								//player.sendPacket(new ExShowScreenMessage(NpcString.QUEEN_SERENITY_IS_CAUSING_YOU, 30000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
								
								player.sendPacket(new ExShowScreenMessage(QUEEN_CALL, 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
								
							}
						}
					}
					html = "tutorial_00a.htm";
				}
				else
					html = "tutorial_00.htm";
				st.set("Ex", "-3");
				st.cancelQuestTimer("QT");
				st.startQuestTimer("QT", 30000);
			}
			else if(Ex == -3)
			{
				st.playTutorialVoice("tutorial_voice_002");
				st.set("Ex", "0");
			}
			else if(Ex == -4)
			{
				st.playTutorialVoice("tutorial_voice_008");
				st.set("Ex", "-5");
			}
		}

		// Tutorial close
		else if(event.startsWith("TE"))
		{
			st.cancelQuestTimer("TE");
			int event_id = 0;
			if(!event.equalsIgnoreCase("TE"))
				event_id = Integer.valueOf(event.substring(2));
			if(event_id == 0)
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
			else if(event_id == 1)
			{
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
				st.playTutorialVoice("tutorial_voice_006");
				st.showQuestionMark(1);
				st.playSound(SOUND_TUTORIAL);
				st.startQuestTimer("QT", 30000);
				st.set("Ex", "-4");
			}
			else if(event_id == 2)
			{
				st.playTutorialVoice("tutorial_voice_003");
				html = "tutorial_02.htm";
				st.onTutorialClientEvent(1);
				st.set("Ex", "-5");
			}
			else if(event_id == 3)
			{
				html = "tutorial_03.htm";
				st.onTutorialClientEvent(2);
			}
			else if(event_id == 5)
			{
				html = "tutorial_05.htm";
				st.onTutorialClientEvent(8);
			}
			else if(event_id == 7)
			{
				html = "tutorial_100.htm";
				st.onTutorialClientEvent(0);
			}
			else if(event_id == 8)
			{
				html = "tutorial_101.htm";
				st.onTutorialClientEvent(0);
			}
			else if(event_id == 10)
			{
				html = "tutorial_103.htm";
				st.onTutorialClientEvent(0);
			}
			else if(event_id == 12)
				player.sendPacket(TutorialCloseHtmlPacket.STATIC);
		}

		// Client Event
		else if(event.startsWith("CE"))
		{
			int event_id = Integer.valueOf(event.substring(2));
			if(event_id == 1 && player.getLevel() < 6)
			{
				st.playTutorialVoice("tutorial_voice_004");
				html = "tutorial_03.htm";
				st.playSound(SOUND_TUTORIAL);
				st.onTutorialClientEvent(2);
			}
			else if(event_id == 2 && player.getLevel() < 6)
			{
				st.playTutorialVoice("tutorial_voice_005");
				html = "tutorial_05.htm";
				st.playSound(SOUND_TUTORIAL);
				st.onTutorialClientEvent(8);
			}
			else if(event_id == 8 && player.getLevel() < 6)
			{
				if(player.getClassId().isOfRace(Race.ERTHEIA))
					html = "tutorial_01a.htm";
				else
					html = "tutorial_01.htm";
				st.playSound(SOUND_TUTORIAL);
				st.playTutorialVoice("ItemSound.quest_tutorial");
				st.set("ucMemo", "1");
				st.set("Ex", "-5");
			}
			else if(event_id == 30 && player.getLevel() < 10 && st.getInt("Die") == 0)
			{
				st.playTutorialVoice("tutorial_voice_016");
				st.playSound(SOUND_TUTORIAL);
				st.set("Die", "1");
				st.showQuestionMark(8);
				st.onTutorialClientEvent(0);
			}
			else if(event_id == 800000 && player.getLevel() < 6 && st.getInt("sit") == 0)
			{
				st.playTutorialVoice("tutorial_voice_018");
				st.playSound(SOUND_TUTORIAL);
				st.set("sit", "1");
				st.onTutorialClientEvent(0);
				html = "tutorial_21z.htm";
			}
			else if(event_id == 40) // Повышение уровня.
			{
				if(player.getLevel() == 15)
				{
					if(st.getInt("lvl") < 15)
					{
						// st.playTutorialVoice("tutorial_voice_???");
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "15");
						st.showQuestionMark(33);
					}
				}
				else if(player.getLevel() >= 18)
				{
					if(st.getInt("lvl") < 18 && player.getClassLevel() == 0)
					{
						// st.playTutorialVoice("tutorial_voice_???");
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "18");
						st.showQuestionMark(35);
					}
				}
				else if(player.getLevel() == 20)
				{
					if(st.getInt("lvl") < 20)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "20");
						st.showQuestionMark(36);
					}
				}
				else if(player.getLevel() == 28)
				{
					if(st.getInt("lvl") < 28)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "28");
						st.showQuestionMark(36);
					}
				}
				else if(player.getLevel() == 38)
				{
					if(st.getInt("lvl") < 38)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "38");
						st.showQuestionMark(36);
					}
				}
				else if(player.getLevel() == 48)
				{
					if(st.getInt("lvl") < 48)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "48");
						st.showQuestionMark(36);
					}
				}
				else if(player.getLevel() == 58)
				{
					if(st.getInt("lvl") < 58)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "58");
						st.showQuestionMark(36);
					}
				}
				else if(player.getLevel() == 68)
				{
					if(st.getInt("lvl") < 68)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "68");
						st.showQuestionMark(36);
					}
				}
				else if(player.getLevel() == 79)
				{
					if(st.getInt("lvl") < 79)
					{
						st.playSound(SOUND_TUTORIAL);
						st.set("lvl", "79");
						st.showQuestionMark(79);
					}
				}
			}
			else if(event_id == 45 && player.getLevel() < 10 && st.getInt("HP") == 0)
			{
				st.playTutorialVoice("tutorial_voice_017");
				st.playSound(SOUND_TUTORIAL);
				st.set("HP", "1");
				st.showQuestionMark(10);
				st.onTutorialClientEvent(800000);
			}
			else if(event_id == 57 && player.getLevel() < 6 && st.getInt("Adena") == 0)
			{
				st.playTutorialVoice("tutorial_voice_012");
				st.playSound(SOUND_TUTORIAL);
				st.set("Adena", "1");
				st.showQuestionMark(23);
			}
		}

		// Question mark clicked
		else if(event.startsWith("QM"))
		{
			int MarkId = Integer.valueOf(event.substring(2));
			if(MarkId == 1)
			{
				st.set("Ex", "-5");
				if(player.getClassId().isOfRace(Race.ERTHEIA))
					html = "tutorial_01a.htm";
				else
					html = "tutorial_01.htm";
			}
			else if(MarkId == 7)
			{
				html = "tutorial_15.htm";
				st.set("ucMemo", "3");
			}
			else if(MarkId == 8)
				html = "tutorial_18.htm";
			else if(MarkId == 10)
				html = "tutorial_19.htm";
			else if(MarkId == 12)
			{
				html = "tutorial_15.htm";
				st.set("ucMemo", "4");
			}
			else if(MarkId == 12)
				html = "tutorial_30.htm";
			else if(MarkId == 23)
				html = "tutorial_24.htm";
			else if(MarkId == 33)
				html = "tutorial_27.htm";
			else if(MarkId == 35 && QMCc.containsKey(player.getRace().ordinal()))
				html = QMCc.get(player.getRace().ordinal());
			else if(MarkId == 36)
			{
				int lvl = player.getLevel();
				if(lvl == 20)
					html = "tutorial_kama_20.htm";
				else if(lvl == 28)
					html = "tutorial_kama_28.htm";
				else if(lvl == 38)
					html = "tutorial_kama_38.htm";
				else if(lvl == 48)
					html = "tutorial_kama_48.htm";
				else if(lvl == 58)
					html = "tutorial_kama_58.htm";
				else if(lvl == 68)
					html = "tutorial_kama_68.htm";
				else if(lvl == 79)
					html = "tutorial_epic_quest.htm";
			}
			else if(MarkId == 37)
				html = "tutorial_mentoring.htm";
		}

		if(html.isEmpty())
			return null;
		st.showTutorialHTML(html);
		return null;
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		if(player.getLevel() < 6)
			player.addListener(_tutorialShowListener);
		/*else if(player.getLevel() >= 40)
		{
		}*/
		else
			checkHermenkusMsg(player);
	}

	@Override
	public void onLevelChange(Player player, int oldLvl, int newLvl)
	{
		checkHermenkusMsg(player);

		/* TODO: [Bonux] Доделать.
		if(player.isBaseClassActive())
		{
			if(oldLvl < 40 && newLvl >= 40)
			{
				if(player.getVarBoolean("@received_advent_book"))
					return;

				ItemFunctions.addItem(player, 32777, 1, true);
				player.sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2text\\Guide_Ad_4050_01_ivorytower.htm"));
			}

			if(oldLvl < 85 && newLvl >= 85)
			{
				if(player.getVarBoolean("@received_awake_book"))
					return;

				ItemFunctions.addItem(player, 32778, 1, true);
				player.sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.LARGE_WINDOW, "..\\L2text\\Guide_Aw_8590_00_main.htm")); // TODO: [Bonux] Проверить на оффе.
			}
		}*/
	}

	private static void checkHermenkusMsg(Player player)
	{
		// Сообщение от гермункуса.
		if(!player.getClassId().isOfRace(Race.ERTHEIA) && player.getLevel() >= 85 && player.isBaseClassActive() && player.getClassId().isOfLevel(ClassLevel.THIRD))
		{
			int classId = 0;
			for(ClassId c : ClassId.VALUES)
			{
				if(c.isOfLevel(ClassLevel.AWAKED) && c.childOf(player.getClassId()))
				{
					classId = c.getId();
					break;
				}
			}

			if(!player.getVarBoolean("GermunkusUSM"))
			{
				player.sendPacket(new ExCallToChangeClass(classId, false));
				player.sendPacket(new ExShowScreenMessage(NpcString.FREE_THE_GIANT_FROM_HIS_IMPRISONMENT_AND_AWAKEN_YOUR_TRUE_POWER, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			}
		}
	}

	public class TutorialShowListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{
			Player player = actor.getPlayer();
			if(player.getCurrentHpPercents() < 25)
			{
				player.removeListener(_tutorialShowListener);
				Quest q = QuestManager.getQuest(255);
				if(q != null)
					player.processQuestEvent(q.getName(), "CE45", null);
			}
			else if(player.getLevel() > 5)
				player.removeListener(_tutorialShowListener);
		}
	}

	@Override
	public boolean isVisible(Player player)
	{
		return false;
	}
}