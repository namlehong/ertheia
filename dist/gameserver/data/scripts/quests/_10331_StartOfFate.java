package quests;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.util.Rnd;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
 */
public class _10331_StartOfFate extends Quest implements ScriptFile
{
	// NPC.
	private static final int FRANCO = 32153; // Верховный Жрец - Прана.
	private static final int RIVIAN = 32147; // Великий Мастер - Ривиан.
	private static final int DEVON = 32160; // Великий Магистр - Девон.
	private static final int TOOK = 32150; // Понтифик - Тук.
	private static final int MOKA = 32157; // Главный Кузнец - Мока.
	private static final int VALFAR = 32146; // Великий Мастер - Валпор.
	private static final int LAKCIS = 32977; // Раксис.
	private static final int SEBION = 32978; // Себион.
	private static final int INFILTRATION_OFFICER = 19155; // Наставник Боя - Офицер Атаки.
	private static final int PANTHEON = 32972; // Пантеон.
	private static final int BELIS_VERIFICATION_SYSTEM = 33215; // Устройство Проверки Знака.
	private static final int ELECTRICITY_GENERATOR = 33216; // Устройство Электрического Импульса.

	// Монстры.
	private static final int HANDYMAN = 22997; // Эмбрио - Мастер.
	private static final int OPERATIVE = 22998; // Эмбрио - Механик.
	private static final int NEMERTESS = 22984; // Преторианец - Немеритес.

	// Итемы.
	private static final int BELIS_MARK = 17615; // Знак Беллис.
	private static final int SARILS_NECKLACE = 17580; // Ожерелье Сарила.
	private static final int PROOF_OF_COURAGE = 17821; // Доказательство Отваги.

	// Точки телепортации.
	private static final Location ESAGIRA_5_AREA = new Location(-111774, 231933, -3160); // 5-я зона исследований Руины Эсагира.

	// Остальное.
	private static final int INSTANCED_ZONE_ID = 178; // Лабиринт Велиса.
	private static final int NEED_BELIS_MARKS_COUNT = 3; // Необходимое количество Знаков Беллис.
	private static final int NEED_OPERATIVES_KILLS_COUNT = 6; // Необходимое количество убийств Махеников.
	private static final int NEED_DEFENDERS_KILLS_COUNT = 6; // Необходимое количество убийств Махеников.
	private static final int BELISE_MARK_DROP_CHANCE = 45; // Шанс дропа Знака Беллис (в %).
	private static final int PROOF_OF_COURAGE_MULTISELL_ID = 717; // ID мультиселла для Доказательств Отваги.

	public _10331_StartOfFate()
	{
		super(false);
		addStartNpc(FRANCO, RIVIAN, DEVON, TOOK, MOKA, VALFAR);
		addTalkId(FRANCO, RIVIAN, DEVON, TOOK, MOKA, VALFAR, LAKCIS, SEBION, INFILTRATION_OFFICER, PANTHEON);
		addFirstTalkId(INFILTRATION_OFFICER, BELIS_VERIFICATION_SYSTEM, ELECTRICITY_GENERATOR);
		addKillId(HANDYMAN, OPERATIVE, NEMERTESS);
		addQuestItem(BELIS_MARK, SARILS_NECKLACE);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(npc != null && event.equalsIgnoreCase(getStartNpcPrefix(npc.getNpcId()) + "_q10331_3.htm"))
		{
			st.setCond(1); // К Раксису
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if(event.equalsIgnoreCase("lakcis_q10331_2.htm"))
		{
			st.setCond(2); // К Себиону
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("teleport_to_5_area"))
		{
			st.getPlayer().teleToLocation(ESAGIRA_5_AREA);
			return null;
		}
		else if(event.equalsIgnoreCase("sebion_q10331_3.htm"))
		{
			st.setCond(3); // Ликвидация Немеритеса
			st.playSound(SOUND_MIDDLE);
		}
		else if(event.equalsIgnoreCase("enter_to_labyrinth"))
		{
			enterInstance(st, INSTANCED_ZONE_ID);
			return null;
		}
		else if(event.equalsIgnoreCase("start_stage_1"))
		{
			Reflection reflect = npc.getReflection();
			if(reflect.getInstancedZoneId() == INSTANCED_ZONE_ID)
			{
				st.set("stage", 1);
				reflect.openDoor(16240002);

				npc.setRunning();
				npc.setFollowTarget(st.getPlayer());
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, st.getPlayer(), 150);
			}
			return null;
		}
		else if(event.equalsIgnoreCase("start_stage_3"))
		{
			Reflection reflect = npc.getReflection();
			if(reflect.getInstancedZoneId() == INSTANCED_ZONE_ID)
			{
				st.set("stage", 3);
				reflect.openDoor(16240004);

				npc.setRunning();
				npc.setFollowTarget(st.getPlayer());
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, st.getPlayer(), 150);
				st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.MARK_OF_BELIS_CAN_BE_ACQUIRED_FROM_ENEMIESNUSE_THEM_IN_THE_BELIS_VERIFICATION_SYSTEM, 7000, ScreenMessageAlign.TOP_CENTER));
				st.startQuestTimer("belise_mark_msg_timer", 10000);
			}
			return null;
		}
		else if(event.equalsIgnoreCase("drop_belise_mark"))
		{
			npc.dropItem(st.getPlayer(), BELIS_MARK, 1);
			return null;
		}
		else if(event.equalsIgnoreCase("belise_mark_msg_timer"))
		{
			Player player = st.getPlayer();
			Reflection reflection = player.getActiveReflection();
			if(reflection!= null && reflection.getInstancedZoneId() == INSTANCED_ZONE_ID && st.getInt("stage") == 3)
			{
				player.sendPacket(new ExShowScreenMessage(NpcString.MARK_OF_BELIS_CAN_BE_ACQUIRED_FROM_ENEMIESNUSE_THEM_IN_THE_BELIS_VERIFICATION_SYSTEM, 7000, ScreenMessageAlign.TOP_CENTER));
				st.startQuestTimer("belise_mark_msg_timer", 10000);
			}
			return null;
		}
		else if(event.equalsIgnoreCase("use_belise_mark"))
		{
			if(st.getInt("stage") == 3)
			{
				if(st.takeItems(BELIS_MARK, 1) == 1)
				{
					int marksLeft = NEED_BELIS_MARKS_COUNT - st.getInt("belise_marks_left") - 1;
					if(marksLeft > 0)
					{
						htmltext = HtmCache.getInstance().getNotNull("quests/_10331_StartOfFate/belis_verification_system_q10331_2.htm", st.getPlayer());
						htmltext = htmltext.replace("<?BELISE_MARKS_LEFT?>", String.valueOf(marksLeft));
					}
					else
					{
						Reflection reflect = npc.getReflection();
						if(reflect.getInstancedZoneId() == INSTANCED_ZONE_ID)
						{
							st.set("stage", 4);
							reflect.openDoor(16240005);
							NpcInstance officer = getNpcFromReflection(INFILTRATION_OFFICER, reflect);
							if(officer != null)
								officerMoveToLocation(officer, new Location(-117896, 214248, -8617, 49151));
						}
						htmltext = "belis_verification_system_q10331_3.htm";
					}
					st.set("belise_marks_left", NEED_BELIS_MARKS_COUNT - marksLeft);
				}
				else
					htmltext = "belis_verification_system_q10331_no.htm";
			}
			else
				htmltext = "belis_verification_system_q10331_4.htm";
		}
		else if(event.equalsIgnoreCase("start_stage_5"))
		{
			Reflection reflect = npc.getReflection();
			if(reflect.getInstancedZoneId() == INSTANCED_ZONE_ID)
			{
				st.set("stage", 5);
				reflect.openDoor(16240006);

				npc.setRunning();
				NpcInstance generator = getNpcFromReflection(ELECTRICITY_GENERATOR, reflect);
				if(generator != null)
				{
					generator.setNpcState(1);
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, generator, 1000);
					Functions.npcSay(npc, NpcString.DONT_COME_BACK_HERE);
					st.startQuestTimer("stage_5_phrases_timer", 5000, npc);
					st.startQuestTimer("stage_5_spawn_timer", 5000, npc);
				}
			}
			return null;
		}
		else if(event.equalsIgnoreCase("stage_5_phrases_timer"))
		{
			if(st.getInt("stage") == 5)
			{
				Functions.npcSay(npc, NpcString.DONT_COME_BACK_HERE);

				Player player = st.getPlayer();
				Reflection reflection = player.getActiveReflection();
				if(reflection!= null && reflection.getInstancedZoneId() == INSTANCED_ZONE_ID)
				{
					NpcString screenMsg = NpcString.BEHIND_YOU_THE_ENEMY_IS_AMBUSHING_YOU;
					if(Rnd.chance(50))
						screenMsg = NpcString.IF_TERAIN_DIES_THE_MISSION_WILL_FAIL;
					player.sendPacket(new ExShowScreenMessage(screenMsg, 7000, ScreenMessageAlign.TOP_CENTER, true, true));
				}
				st.startQuestTimer("stage_5_phrases_timer", 10000, npc);
			}
			return null;
		}
		else if(event.equalsIgnoreCase("stage_5_spawn_timer"))
		{
			if(st.getInt("stage") == 5)
			{
				int defendersCount = st.getInt("spawned_defenders");
				if(defendersCount < NEED_DEFENDERS_KILLS_COUNT)
				{
					int defenderNpcId = (defendersCount == 0 || defendersCount % 2 == 0) ? HANDYMAN : OPERATIVE;
					Reflection reflect = npc.getReflection();
					NpcInstance defender = addSpawnToInstance(defenderNpcId, new Location(-116600, 213080, -8615, 21220), 0, reflect.getId());

					NpcString defenderPhrase = NpcString.FOCUS_ON_ATTACKING_THE_GUY_IN_THE_ROOM;
					if(Rnd.chance(50))
						defenderPhrase = NpcString.KILL_THE_GUY_MESSING_WITH_THE_ELECTRIC_DEVICE;
					Functions.npcSay(defender, defenderPhrase);

					NpcInstance officer = getNpcFromReflection(INFILTRATION_OFFICER, reflect);
					if(officer != null)
					{
						defender.setSpawnedLoc(officer.getLoc());
						defender.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, officer, 10);
					}
					st.set("spawned_defenders", defendersCount + 1);
					String defendersIds = st.get("defenders_ids");
					if(defendersIds == null)
						defendersIds = "";
					st.set("defenders_ids", st.get("defenders_ids") + "-" + defender.getObjectId() + "-");
				}

				st.startQuestTimer("stage_5_spawn_timer", 20000, npc);
			}
			return null;
		}
		else if(event.equalsIgnoreCase("process_stage_5"))
		{
			Reflection reflect = npc.getReflection();
			if(reflect.getInstancedZoneId() == INSTANCED_ZONE_ID)
			{
				int defenderKills = st.getInt("defender_kills");
				if(defenderKills >= NEED_DEFENDERS_KILLS_COUNT)
				{
					st.set("stage", 6);
					st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ELECTRONIC_DEVICE_HAS_BEEN_DESTROYED, 5000, ScreenMessageAlign.TOP_CENTER));
					reflect.openDoor(16240007);
					reflect.getZone("[belise_labyrinth_03_1]").setActive(true);
					reflect.getZone("[belise_labyrinth_03_2]").setActive(false);

					NpcInstance officer = getNpcFromReflection(INFILTRATION_OFFICER, reflect);
					if(officer != null)
						officerMoveToLocation(officer, new Location(-119112, 213672, -8617, 8191));

					NpcInstance generator = getNpcFromReflection(ELECTRICITY_GENERATOR, reflect);
					if(generator != null)
						generator.deleteMe();
				}
			}
			return null;
		}
		else if(event.equalsIgnoreCase("start_stage_7"))
		{
			Reflection reflect = npc.getReflection();
			if(reflect.getInstancedZoneId() == INSTANCED_ZONE_ID)
			{
				st.set("stage", 7);
				reflect.openDoor(16240008);
				SceneMovie scene = SceneMovie.SCENE_TALKING_ISLAND_BOSS_OPENING;
				st.getPlayer().startScenePlayer(scene);
				st.startQuestTimer("spawn_nemertess", scene.getDuration(), npc);
			}
			return null;
		}
		else if(event.equalsIgnoreCase("spawn_nemertess"))
		{
			if(st.getInt("stage") == 7)
			{
				Reflection reflect = npc.getReflection();
				if(reflect.getInstancedZoneId() == INSTANCED_ZONE_ID)
				{
					addSpawnToInstance(NEMERTESS, new Location(-118328, 212968, -8705, 24575), 0, reflect.getId());
				}
			}
			return null;
		}
		else if(event.equalsIgnoreCase("kill_nemertess"))
		{
			if(st.getInt("stage") == 7)
			{
				Reflection reflect = npc.getReflection();
				if(reflect.getInstancedZoneId() == INSTANCED_ZONE_ID)
				{
					clearInstanceVariables(st);
					st.setCond(4); // Ожерелье Сарила
					st.giveItems(SARILS_NECKLACE, 1); 
					NpcInstance officer = getNpcFromReflection(INFILTRATION_OFFICER, reflect);
					if(officer != null)
						officerMoveToLocation(officer, new Location(-118328, 212968, -8705, 24575));
				}
			}
			return null;
		}
		else if(event.equalsIgnoreCase("pantheon_q10331_2.htm"))
		{
			int cond = st.getPlayer().getRace().ordinal() + 6;
			st.setCond(cond); // Подтверждение квалификации рейнджера
		}
		else if(event.startsWith("class_transfer"))
		{
			String[] params = event.split(" ");
			if(params.length < 3)
				return null;

			int classId = Integer.parseInt(params[1]);
			String html = params[2];
			htmltext = HtmCache.getInstance().getNotNull("quests/_10331_StartOfFate/" + html, st.getPlayer());
			htmltext = htmltext.replace("<?CLASS_NAME?>", HtmlUtils.htmlClassName(classId));

			Player player = st.getPlayer();
			player.setClassId(classId, false);
			player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER);
			player.broadcastPacket(new MagicSkillUse(player, player, 5103, 1, 1000, 0));

			MultiSellHolder.getInstance().SeparateAndSend(PROOF_OF_COURAGE_MULTISELL_ID, st.getPlayer(), 0);
			st.showTutorialClientHTML("QT_009_enchant_01");
			st.giveItems(ADENA_ID, 80000);
			st.giveItems(PROOF_OF_COURAGE, 40);
			st.addExpAndSp(200000, 48);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FANFARE2);
			//st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if(npcId == FRANCO || npcId == RIVIAN || npcId == DEVON || npcId == TOOK || npcId == MOKA || npcId == VALFAR)
		{
			String prefix = getStartNpcPrefix(npcId);
			if(cond == 0)
			{
				if(checkStartCondition(st.getPlayer()))
					htmltext = prefix + "_q10331_1.htm";
				else
					htmltext = prefix + "_q10331_0.htm";
			}
			else if(cond == 1)
				htmltext = prefix + "_q10331_4.htm";
			else if(cond >= 6)
				htmltext = getAvailableClassList(st.getPlayer(), prefix + "_q10331_5.htm", prefix + "_q10331_6.htm");
		}
		else if(npcId == LAKCIS)
		{
			if(cond == 1)
				htmltext = "lakcis_q10331_1.htm";
			else if(cond == 2)
				htmltext = "lakcis_q10331_3.htm";
		}
		else if(npcId == SEBION)
		{
			if(cond == 2)
				htmltext = "sebion_q10331_1.htm";
			else if(cond == 3)
				htmltext = "sebion_q10331_4.htm";
			else if(cond == 4)
			{
				htmltext = "sebion_q10331_5.htm";
				st.setCond(5); // Передача ожерелья Сарила
			}
			else if(cond == 5)
				htmltext = "sebion_q10331_6.htm";
		}
		else if(npcId == PANTHEON)
		{
			if(cond == 5)
				htmltext = "pantheon_q10331_1.htm";
			else if(cond >= 6)
				htmltext = "pantheon_q10331_3.htm";
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState st = player.getQuestState(getClass());
		if(st == null)
			return "";

		Reflection reflect = npc.getReflection();
		if(reflect.getInstancedZoneId() == INSTANCED_ZONE_ID)
		{
			int npcId = npc.getNpcId();
			if(npcId == INFILTRATION_OFFICER)
			{
				int cond = st.getCond();
				if(cond == 3)
				{
					if(npc.isMoving || npc.isFollow || npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_ACTIVE)
						return "infiltration_officer_q10331_no.htm";

					int stage = st.getInt("stage");
					if(stage == 0)
						return "infiltration_officer_q10331_1.htm";
					else if(stage == 2)
						return "infiltration_officer_q10331_2.htm";
					else if(stage == 4)
						return "infiltration_officer_q10331_3.htm";
					else if(stage == 6)
						return "infiltration_officer_q10331_4.htm";

					return "infiltration_officer_q10331_no.htm";
				}
				else if(cond == 4)
				{
					if(npc.isMoving || npc.isFollow || npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_ACTIVE)
						return "infiltration_officer_q10331_no.htm";

					return "infiltration_officer_q10331_5.htm";
				}
			}
			else if(npcId == BELIS_VERIFICATION_SYSTEM)
			{
				int cond = st.getCond();
				if(cond == 3 || cond == 4)
				{
					String htmltext = HtmCache.getInstance().getNotNull("quests/_10331_StartOfFate/belis_verification_system_q10331_1.htm", st.getPlayer());
					htmltext = htmltext.replace("<?BELISE_MARK_COUNT?>", String.valueOf(NEED_BELIS_MARKS_COUNT));
					return htmltext;
				}
			}
			else if(npcId == ELECTRICITY_GENERATOR)
			{
				int cond = st.getCond();
				if(cond == 3 || cond == 4)
					return "electricity_generator_q10331_1.htm";
			}
		}
		return null;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		Reflection reflect = npc.getReflection();
		if(reflect.getInstancedZoneId() == INSTANCED_ZONE_ID)
		{
			int npcId = npc.getNpcId();
			if(npcId == OPERATIVE)
			{
				if(st.getCond() == 3)
				{
					if(st.getInt("stage") == 1)
					{
						int killsCount = st.getInt("operative_kills") + 1;
						if(killsCount >= NEED_OPERATIVES_KILLS_COUNT)
						{
							st.set("stage", 2);
							reflect.openDoor(16240003);
							NpcInstance officer = getNpcFromReflection(INFILTRATION_OFFICER, reflect);
							if(officer != null)
								officerMoveToLocation(officer, new Location(-117037, 212504, -8592, 39479));
						}
						else
							st.set("operative_kills", killsCount);
					}
					else if(st.getInt("stage") == 5)
					{
						st.set("defender_kills", st.getInt("defender_kills") + 1);
						st.startQuestTimer("process_stage_5", 3000, npc);
					}
				}
			}
			else if(npcId == HANDYMAN)
			{
				if(st.getCond() == 3)
				{
					if(st.getInt("stage") == 3)
					{
						if(Rnd.chance(BELISE_MARK_DROP_CHANCE))
							st.startQuestTimer("drop_belise_mark", 3000, npc);
					}
					else if(st.getInt("stage") == 5)
					{
						String defendersIds = st.get("defenders_ids");
						if(defendersIds != null && defendersIds.contains("-" + npc.getObjectId() + "-"))
						{
							st.set("defender_kills", st.getInt("defender_kills") + 1);
							st.startQuestTimer("process_stage_5", 3000, npc);
						}
					}
				}
			}
			else if(npcId == NEMERTESS)
			{
				if(st.getCond() == 3)
				{
					if(st.getInt("stage") == 7)
					{
						npc.deleteMe();
						SceneMovie scene = SceneMovie.SCENE_TALKING_ISLAND_BOSS_ENDING;
						st.getPlayer().startScenePlayer(scene);
						st.startQuestTimer("kill_nemertess", scene.getDuration(), npc);
					}
				}
			}
		}
		return null;
	}

	@Override
	public void onEnterInstance(QuestState st, Reflection reflection, Object[] args)
	{
		clearInstanceVariables(st);
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		return player.getLevel() >= 18 && player.getClassId().isOfLevel(ClassLevel.NONE);
	}

	@Override
	public boolean checkStartNpc(NpcInstance npc, Player player)
	{
		int npcId = npc.getNpcId();
		Race race = player.getRace();
		switch(npcId)
		{
			case FRANCO:
				if(race == Race.HUMAN)
					return true;
				return false;
			case RIVIAN:
				if(race == Race.ELF)
					return true;
				return false;
			case DEVON:
				if(race == Race.DARKELF)
					return true;
				return false;
			case TOOK:
				if(race == Race.ORC)
					return true;
				return false;
			case MOKA:
				if(race == Race.DWARF)
					return true;
				return false;
			case VALFAR:
				if(race == Race.KAMAEL)
					return true;
				return false;
		}
		return true;
	}

	@Override
	public boolean checkTalkNpc(NpcInstance npc, QuestState st)
	{
		return checkStartNpc(npc, st.getPlayer());
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

	private static String getStartNpcPrefix(int npcId)
	{
		String prefix = "high_priest_franco";
		if(npcId == RIVIAN)
			prefix = "grand_master_rivian";
		else if(npcId == DEVON)
			prefix = "grand_magister_devon";
		else if(npcId == TOOK)
			prefix = "high_prefect_took";
		else if(npcId == MOKA)
			prefix = "head_blacksmith_moka";
		else if(npcId == VALFAR)
			prefix = "grand_master_valfar";
		return prefix;
	}

	private static void clearInstanceVariables(QuestState st)
	{
		st.unset("belise_marks_left");
		st.unset("operative_kills");
		st.unset("stage");
		st.unset("defender_kills");
		st.unset("spawned_defenders");
		st.unset("defenders_ids");
	}

	private static NpcInstance getNpcFromReflection(int npcId, Reflection reflect)
	{
		List<NpcInstance> npc = reflect.getAllByNpcId(npcId, true);
		if(!npc.isEmpty())
			return npc.get(0);
		return null;
	}

	private static void officerMoveToLocation(NpcInstance officer, Location loc)
	{
		officer.abortAttack(true, true);
		officer.abortCast(true, true);
		officer.setTarget(null);
		officer.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		officer.setRunning(); // На всякий пожарный.
		officer.setSpawnedLoc(loc);
		if(!officer.moveToLocation(loc, 0, true))
		{
			officer.getAI().clientStopMoving();
			officer.teleToLocation(loc);
		}
		officer.setHeading(loc.h);
	}

	private static String getAvailableClassList(Player player, String html, String html2)
	{
		String htmltext = HtmCache.getInstance().getNotNull("quests/_10331_StartOfFate/" + html, player);
		ClassId classId = player.getClassId();
		StringBuilder classList = new StringBuilder();
		for(ClassId firstClassId : ClassId.VALUES)
		{
			if(!firstClassId.isOfLevel(ClassLevel.FIRST))
				continue;

			if(!firstClassId.childOf(classId))
				continue;

			int firstClsId = firstClassId.getId();
			classList.append("<a action=\"bypass -h Quest _10331_StartOfFate class_transfer " + firstClsId + " " + html2 + "\">" + HtmlUtils.htmlClassName(firstClsId) + "</a><br>");
		}
		htmltext = htmltext.replace("<?AVAILABLE_CLASS_LIST?>", classList.toString());
		return htmltext;
	}
}