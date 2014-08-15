package l2s.gameserver.model.actor.instances.player;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.lang.reference.HardReference;
import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.tables.SkillTable;

public class DeathPenalty
{
	private static final int PENALTY_SKILL_ID = 14571;
	private static final int MAX_PENALTY_LVL = 5;
	private static final int FORTUNE_OF_NOBLESSE_SKILL_ID = 1325;
	private static final int CHARM_OF_LUCK_SKILL_ID = 2168;
	private static final int[] RAID_BOSSES_IDs = { 25779, // Спасия
			25867, // Спасия
			25868, // Спасия
			//TODO: [Bonux] Проверить - 25869, // Всепоглощающая Спасия
			//TODO: [Bonux] Найти ИД, // Траджан
			25532, // Кечи
			25714, // Кечи
			25797, // Кечи
			25799, // Михаэль
			25800, // Михаэль
			25837, // Рыдающая Юи
			25840, // Разъяренный Мастер Киннен
			25843, // Магический Воин Коняр
			25844, // Магический Воин Коняр
			25845, // Магический Воин Коняр
			25841, // Сэр Тьмы Ресинда
			25838, // Трусливый Мукшу
			25839, // Слепой Хорнапи
			25846, // Йоентумак Ожидания
			25824, // Фрон
			25825, // Фрон
			25855, // Меллиса
			25876, // Меллиса
			25856, // Исадора
			25877, // Исадора
			29191, // Октавис
			29193, // Октавис
			29194, // Октавис
			29209, // Октавис
			29211, // Октавис
			29212, // Октавис
			29195, // Истхина
			29196, // Истхина
			29218, // Валлок
			29099, // Байлор
			29103, // Байлор
			29186, // Байлор
			29213, // Байлор
			29068, // Антарас
	};

	private HardReference<Player> _playerRef;
	private boolean _hasCharmOfLuck;

	public DeathPenalty(Player player)
	{
		_playerRef = player.getRef();
	}

	public Player getPlayer()
	{
		return _playerRef.get();
	}

	public int getLevel()
	{
		if(!Config.ALLOW_DEATH_PENALTY)
			return 0;

		Player player = getPlayer();
		if(player == null)
			return 0;

		for(Effect e : player.getEffectList().getEffects())
		{
			if(e.getSkill().getId() == PENALTY_SKILL_ID)
				return e.getSkill().getLevel();
		}

		return 0;
	}

	public void notifyDead(Creature killer)
	{
		if(!Config.ALLOW_DEATH_PENALTY)
			return;

		if(_hasCharmOfLuck)
		{
			_hasCharmOfLuck = false;
			return;
		}

		if(killer == null || !killer.isNpc())
			return;

		Player player = getPlayer();
		if(player == null || player.getLevel() <= 9)
			return;

		double chance = 0;
		NpcInstance npc = (NpcInstance) killer;
		if(ArrayUtils.contains(RAID_BOSSES_IDs, npc.getNpcId()))
			chance = 100.;
		else
		{
			int karmaBonus = player.getKarma() / Config.ALT_DEATH_PENALTY_KARMA_PENALTY;
			if(karmaBonus < 0)
				karmaBonus = 0;

			chance = Config.ALT_DEATH_PENALTY_CHANCE + karmaBonus;
		}

		if(Rnd.chance(chance))
			addLevel();
	}

	public void addLevel()
	{
		Player player = getPlayer();
		if(player == null || player.isGM())
			return;

		int level = getLevel();
		if(level >= MAX_PENALTY_LVL)
			return;

		if(level != 0)
			player.getEffectList().stopEffects(PENALTY_SKILL_ID);

		level++;

		Skill skill = SkillTable.getInstance().getInfo(PENALTY_SKILL_ID, level);
		if(skill != null)
		{
			skill.getEffects(player, player, false);
			player.sendPacket(new SystemMessage(SystemMessage.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addNumber(level));
		}
		else
			player.sendPacket(SystemMsg.YOUR_DEATH_PENALTY_HAS_BEEN_LIFTED);
	}

	public void reduceLevel()
	{
		Player player = getPlayer();
		if(player == null)
			return;

		int level = getLevel();
		if(level <= 0)
			return;

		player.getEffectList().stopEffects(PENALTY_SKILL_ID);

		level--;

		Skill skill = SkillTable.getInstance().getInfo(PENALTY_SKILL_ID, level);
		if(skill != null && level > 0)
		{
			skill.getEffects(player, player, false);
			player.sendPacket(new SystemMessage(SystemMessage.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addNumber(level));
		}
		else
			player.sendPacket(SystemMsg.YOUR_DEATH_PENALTY_HAS_BEEN_LIFTED);
	}

	public void checkCharmOfLuck()
	{
		Player player = getPlayer();
		if(player != null)
		{
			for(Effect e : player.getEffectList().getEffects())
			{
				if(e.getSkill().getId() == CHARM_OF_LUCK_SKILL_ID || e.getSkill().getId() == FORTUNE_OF_NOBLESSE_SKILL_ID)
				{
					_hasCharmOfLuck = true;
					return;
				}
			}
		}

		_hasCharmOfLuck = false;
	}
}