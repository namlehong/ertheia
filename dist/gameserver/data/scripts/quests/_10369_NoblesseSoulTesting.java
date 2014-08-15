package quests;

import l2s.commons.util.Rnd;
import l2s.gameserver.listener.actor.OnMagicHitListener;
import l2s.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.components.SceneMovie;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.SkillListPacket;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.ReflectionUtils;

/**
 * @author Bonux
 http://www.youtube.com/watch?v=zTE8prhbb-w
 http://l2central.info/wiki/Дворянин,_испытание_духа
 TODO: Написано по информации с источников. Переписать с оффа.
**/
public class _10369_NoblesseSoulTesting extends Quest implements ScriptFile, OnMagicHitListener
{
	public static class ZoneListener implements OnZoneEnterLeaveListener
	{
		private static final ExShowScreenMessage ZONE_MESSAGE = new ExShowScreenMessage(NpcString.IF_YOU_DOUBLECLICK_THE_EMPTY_BOTTLE_IT_WILL_BECOME_FULL_OF_WATER, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER);

		@Override
		public void onZoneEnter(Zone zone, Creature character)
		{
			if(zone == null)
				return;

			if(character == null)
				return;

			if(!character.isPlayer())
				return;

			Player player = character.getPlayer();
			QuestState qs = player.getQuestState(_10369_NoblesseSoulTesting.class);
			if(qs == null)
				return;

			if(qs.getCond() == 6 && qs.getQuestItemsCount(HOT_SPRING_WATER_BOTTLE) < 1)
				player.sendPacket(ZONE_MESSAGE);
		}

		@Override
		public void onZoneLeave(Zone zone, Creature character)
		{}
	}

	// NPC's
	private final static int CERENAS = 31281;
	private final static int EVAS_AVATAR = 33686;
	private final static int LANYA = 33696;
	private final static int FLAME_FLOWER = 33735;

	// MONSTER's
	private final static int ONE_WHO_EATS_PROPHECIES = 27482;
	private final static int HOT_SPRINGS_YETI = 21320;
	private final static int HOT_SPRINGS_BANDERSNATCH = 21322;
	private final static int HOT_SPRINGS_GRENDEL = 21323;
	private final static int CASTALIA = 22264;
	private final static int SEYCHELLES = 22261;
	private final static int NAIAD = 22262;
	private final static int SONNERATIA = 22263;
	private final static int CHRYSOCOLLA = 22265;
	private final static int PYTHIA = 22266;

	// ITEM's
	private final static int NOVELLA_PROPHECY = 34886;
	private final static int EMPTY_HOT_SPRING_WATER_BOTTLE = 34887;
	private final static int HOT_SPRING_WATER_BOTTLE = 34888;
	private final static int SUMMONING_STONE = 34912;
	private final static int SOE_HOT_SPRINGS = 34978;
	private final static int SOE_FOG = 34979;
	private final static int SOE_ISLE_OF_PRAYER = 34980;
	private final static int SOE_ADEN_CASTLE = 34981;
	private final static int SOE_RUNE_CASTLE = 34982;
	private final static int HARD_LEATHER = 34889;
	private final static int TROWEL = 34890;
	private final static int FLAME_POWER = 34891;
	private final static int WATER_ENERGY = 34892;
	private final static int SACK_CONTAINING_INGREDIENTS = 34913;
	private final static int SEED_OF_HELPING = 34961;
	private final static int ASHES_OF_REMNANTS = 34962;
	private final static int NOBLESS_TIARA = 7694;
	private final static int DIMENSIONAL_DIAMOND = 7562;

	// OTHER
	public final static int INSTANCE_ZONE_ID = 217; // Eva's Hidden Space

	private final static int BOTTLE_SKILL_ID = 9443;
	private final static int TROWEL_SKILL_ID = 9442;
	private final static int SUMMON_TREE_SKILL_ID = 9444;

	private static final ExShowScreenMessage NPC_MESSAGE = new ExShowScreenMessage(NpcString.CLICK_THE_FLAME_FLOWER_THEN_DOUBLE_CLICK_THE_TROWEL, 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER);

	public static final String[] ZONES = { "[spa_31]", "[spa_51]", "[spa_81]" };

	private static ZoneListener _zoneListener;

	public _10369_NoblesseSoulTesting()
	{
		super(false);

		CharListenerList.addGlobal(this);

		addStartNpc(CERENAS);
		addTalkId(CERENAS, EVAS_AVATAR, LANYA);
		addFirstTalkId(EVAS_AVATAR, LANYA, FLAME_FLOWER);
		addKillId(ONE_WHO_EATS_PROPHECIES, HOT_SPRINGS_YETI, HOT_SPRINGS_BANDERSNATCH, HOT_SPRINGS_GRENDEL, CASTALIA, SEYCHELLES, NAIAD, SONNERATIA, CHRYSOCOLLA, PYTHIA);
		addSkillUseId(FLAME_FLOWER);
		addQuestItem(NOVELLA_PROPHECY, EMPTY_HOT_SPRING_WATER_BOTTLE, HOT_SPRING_WATER_BOTTLE, SUMMONING_STONE, TROWEL, HARD_LEATHER, FLAME_POWER, WATER_ENERGY, SACK_CONTAINING_INGREDIENTS, SEED_OF_HELPING, ASHES_OF_REMNANTS);
		addQuestItem(SOE_HOT_SPRINGS); // TODO: [Bonux] Check.
		addQuestItem(SOE_FOG); // TODO: [Bonux] Check.
		addQuestItem(SOE_ISLE_OF_PRAYER); // TODO: [Bonux] Check.
		addQuestItem(SOE_ADEN_CASTLE); // TODO: [Bonux] Check.
		addQuestItem(SOE_RUNE_CASTLE); // TODO: [Bonux] Check.

		addLevelCheck(75);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if(npc == null)
			return null;

		if(event.equalsIgnoreCase("first_part_prophecy"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.getPlayer().startScenePlayer(SceneMovie.SCENE_NOBLE_OPENING);
			return null;
		}
		else if(event.equalsIgnoreCase("second_part_prophecy"))
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
			st.takeItems(NOVELLA_PROPHECY, -1);
			st.getPlayer().startScenePlayer(SceneMovie.SCENE_NOBLE_ENDING);
			return null;
		}
		else if(event.equalsIgnoreCase("enter_instance"))
		{
			if(st.getCond() == 14)
			{
				st.setCond(15);
				st.playSound(SOUND_MIDDLE);
			}
			else if(st.getCond() == 17)
			{
				st.setCond(18);
				st.playSound(SOUND_MIDDLE);
			}

			enterInstance(st, INSTANCE_ZONE_ID);
			return null;
		}
		else if(event.equalsIgnoreCase("33686_02.htm"))
		{
			st.setCond(6);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(EMPTY_HOT_SPRING_WATER_BOTTLE, 1);
			st.giveItems(SUMMONING_STONE, 1);
			st.giveItems(SOE_HOT_SPRINGS, 1);
		}
		else if(event.equalsIgnoreCase("33696_01.htm"))
		{
			st.setCond(8);
			st.playSound(SOUND_MIDDLE);
			st.takeItems(HOT_SPRING_WATER_BOTTLE, -1);
		}
		else if(event.equalsIgnoreCase("33696_04.htm"))
		{
			st.setCond(12);
			st.playSound(SOUND_MIDDLE);
			st.takeItems(TROWEL, -1);
			st.takeItems(FLAME_POWER, -1);
			st.giveItems(SOE_ISLE_OF_PRAYER, 1);
		}
		else if(event.equalsIgnoreCase("33686_05.htm"))
		{
			st.setCond(16);
			st.playSound(SOUND_MIDDLE);
			st.takeItems(SUMMONING_STONE, -1);
			st.takeItems(SACK_CONTAINING_INGREDIENTS, -1);
			st.giveItems(SEED_OF_HELPING, 1);
			if(Rnd.chance(50))
				st.giveItems(SOE_ADEN_CASTLE, 1);
			else
				st.giveItems(SOE_RUNE_CASTLE, 1);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(npcId)
		{
			case CERENAS:
			{
				if(st.isCompleted())
					htmltext = "31281-completed.htm";
				else if(cond == 0)
				{
					if(checkStartCondition(st.getPlayer()))
						htmltext = "31281_01.htm";
					else
						htmltext = "31281_00.htm";
				}
				else if(cond == 1)
				{
					htmltext = "31281_02.htm";
					st.setCond(2);
					st.playSound(SOUND_MIDDLE);
				}
				else if(cond == 2)
					htmltext = "31281_02.htm";
				else if(cond == 3)
					htmltext = "31281_03.htm";
				else if(cond == 4)
				{
					htmltext = "31281_04.htm";
					st.setCond(5);
					st.playSound(SOUND_MIDDLE);
				}
				else if(cond == 5)
					htmltext = "31281_04.htm";
				else if(cond == 17)
					htmltext = "31281_05.htm";
				break;
			}
			case EVAS_AVATAR:
			{
				if(cond == 5)
					htmltext = "33686_00.htm";
				else if(cond == 6)
					htmltext = "33686_03.htm";
				else if(cond == 15)
					htmltext = "33686_04.htm";
				else if(cond == 16)
					htmltext = "33686_05.htm";
				else if(cond == 18)
				{
					htmltext = "33686_06.htm";
					st.giveItems(DIMENSIONAL_DIAMOND, 10);
					st.giveItems(NOBLESS_TIARA, 1);
					st.takeItems(ASHES_OF_REMNANTS, -1);
					st.addExpAndSp(12625440, 0);
					st.playSound(SOUND_FINISH);
					st.unset("cond");
					st.exitCurrentQuest(false);
					Olympiad.addNoble(st.getPlayer());
					st.getPlayer().setNoble(true);
					st.getPlayer().updatePledgeRank();
					st.getPlayer().checkNobleSkills();
					st.getPlayer().sendPacket(new SkillListPacket(st.getPlayer()));
					st.getPlayer().broadcastUserInfo(true);
				}
				break;
			}
			case LANYA:
			{
				if(cond == 7)
					htmltext = "33696_00.htm";
				else if(cond == 8)
					htmltext = "33696_01.htm";
				else if(cond == 9)
				{
					htmltext = "33696_02.htm";
					st.setCond(10);
					st.playSound(SOUND_MIDDLE);
					st.takeItems(HARD_LEATHER, -1);
					st.giveItems(TROWEL, 1);
					st.giveItems(SOE_FOG, 1);
				}
				else if(cond == 10)
					htmltext = "33696_02.htm";
				else if(cond == 11)
					htmltext = "33696_03.htm";
				else if(cond == 12)
					htmltext = "33696_04.htm";
				else if(cond == 13)
				{
					htmltext = "33696_05.htm";
					st.setCond(14);
					st.playSound(SOUND_MIDDLE);
					st.takeItems(WATER_ENERGY, -1);
					st.giveItems(SACK_CONTAINING_INGREDIENTS, 1);
				}
				else if(cond == 14)
					htmltext = "33696_06.htm";
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState st = player.getQuestState(getClass());
		if(st == null)
			return "";

		String htmltext = "";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(npcId)
		{
			case FLAME_FLOWER:
			{
				if(cond == 10 && st.getQuestItemsCount(FLAME_POWER) < 1)
				{
					player.sendPacket(NPC_MESSAGE);
					return "";
				}
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(npcId)
		{
			case ONE_WHO_EATS_PROPHECIES:
			{
				if(cond == 2)
				{
					if(Rnd.chance(40))
					{
						st.giveItems(NOVELLA_PROPHECY, 1);
						st.setCond(3);
						st.playSound(SOUND_MIDDLE);
					}
				}
				break;
			}
			case HOT_SPRINGS_YETI:
			case HOT_SPRINGS_BANDERSNATCH:
			case HOT_SPRINGS_GRENDEL:
			{
				if(cond == 8)
				{
					if(Rnd.chance(35))
					{
						st.giveItems(HARD_LEATHER, 1);
						if(st.getQuestItemsCount(HARD_LEATHER) >= 10)
						{
							st.setCond(9);
							st.playSound(SOUND_MIDDLE);
						}
						else
							st.playSound(SOUND_ITEMGET);
					}
				}
				break;
			}
			case CASTALIA:
			case SEYCHELLES:
			case NAIAD:
			case SONNERATIA:
			case CHRYSOCOLLA:
			case PYTHIA:
			{
				if(cond == 12)
				{
					st.giveItems(WATER_ENERGY, 1);
					if(st.getQuestItemsCount(WATER_ENERGY) >= 10)
					{
						st.setCond(13);
						st.playSound(SOUND_MIDDLE);
					}
					else
						st.playSound(SOUND_ITEMGET);
				}
				break;
			}
		}
		return null;
	}

	@Override
	public String onSkillUse(NpcInstance npc, Skill skill, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch(npcId)
		{
			case FLAME_FLOWER:
			{
				if(cond == 10 && skill.getId() == TROWEL_SKILL_ID)
				{
					st.giveItems(FLAME_POWER, 1);
					if(st.getQuestItemsCount(FLAME_POWER) >= 5)
					{
						st.setCond(11);
						st.playSound(SOUND_MIDDLE);
					}
					else
						st.playSound(SOUND_ITEMGET);
					npc.doDie(st.getPlayer());
				}
				break;
			}
		}
		return null;
	}

	@Override
	public boolean checkStartCondition(Player player)
	{
		if(player.isBaseClassActive() || player.isNoble())
			return false;

		return super.checkStartCondition(player);
	}

	@Override
	public void onMagicHit(Creature actor, Skill skill, Creature caster)
	{
		int skillId = skill.getId();
		if(skillId != BOTTLE_SKILL_ID && skillId != SUMMON_TREE_SKILL_ID)
			return;

		if(caster == null)
			return;

		if(!caster.isPlayer())
			return;

		QuestState qs = caster.getPlayer().getQuestState(getClass());
		if(qs == null)
			return;

		if(qs.getCond() == 6)
		{
			if(skillId == BOTTLE_SKILL_ID)
			{
				qs.giveItems(HOT_SPRING_WATER_BOTTLE, 1);
				qs.setCond(7);
				qs.playSound(SOUND_MIDDLE);
			}
		}
		else if(qs.getCond() == 16)
		{
			if(skillId == SUMMON_TREE_SKILL_ID)
			{
				qs.giveItems(ASHES_OF_REMNANTS, 1);
				qs.setCond(17);
				qs.playSound(SOUND_MIDDLE);
			}
		}
	}

	private void init()
	{
		_zoneListener = new ZoneListener();
		Zone zone;
		for(String zoneName : ZONES)
		{
			zone = ReflectionUtils.getZone(zoneName);
			if(zone == null)
				continue;

			zone.addListener(_zoneListener);
		}
	}

	@Override
	public void onLoad()
	{
		init();
	}

	@Override
	public void onReload()
	{}

	@Override
	public void onShutdown()
	{}
}