package l2s.gameserver.model.entity.olympiad;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.impl.DuelEvent;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2s.gameserver.network.l2.s2c.ExOlympiadMatchEndPacket;
import l2s.gameserver.network.l2.s2c.ExOlympiadModePacket;
import l2s.gameserver.network.l2.s2c.RevivePacket;
import l2s.gameserver.network.l2.s2c.SkillCoolTimePacket;
import l2s.gameserver.network.l2.s2c.SkillListPacket;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.TimeStamp;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.InstantZone;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlympiadMember
{
	private static final Logger _log = LoggerFactory.getLogger(OlympiadMember.class);

	private String _name = StringUtils.EMPTY;
	private String _clanName = StringUtils.EMPTY;
	private int _classId;
	private double _damage; // !!! ЭТО ПОЛУЧЕННЫЙ УРОН, А НЕ НАНЕСЕНННЫЙ!!! НЕ ПУТАТЬ!!!
	private boolean _isDead;

	private final int _objId;
	private final OlympiadGame _game;
	private final CompType _type;
	private final int _side;

	private Player _player;
	private Location _returnLoc = null;

	public boolean isDead()
	{
		return _isDead;
	}

	public void doDie()
	{
		_isDead = true;
	}

	public OlympiadMember(int obj_id, OlympiadGame game, int side)
	{
		String player_name = "";
		Player player = GameObjectsStorage.getPlayer(obj_id);
		if(player != null)
			player_name = player.getName();
		else
		{
			StatsSet noble = Olympiad._nobles.get(obj_id);
			if(noble != null)
				player_name = noble.getString(Olympiad.CHAR_NAME, "");
		}

		_objId = obj_id;
		_name = player_name;
		_game = game;
		_type = game.getType();
		_side = side;

		_player = player;
		if(_player == null)
			return;

		_clanName = player.getClan() == null ? StringUtils.EMPTY : player.getClan().getName();
		_classId = player.getActiveClassId();

		player.setOlympiadSide(side);
		player.setOlympiadGame(game);
	}

	public StatsSet getStat()
	{
		return Olympiad._nobles.get(_objId);
	}

	public void incGameCount()
	{
		StatsSet set = getStat();
		switch(_type)
		{
			case CLASSED:
				set.set(Olympiad.GAME_CLASSES_COUNT, set.getInteger(Olympiad.GAME_CLASSES_COUNT) + 1);
				break;
			case NON_CLASSED:
				set.set(Olympiad.GAME_NOCLASSES_COUNT, set.getInteger(Olympiad.GAME_NOCLASSES_COUNT) + 1);
				break;
		}
	}

	public void takePointsForCrash()
	{
		if(!checkPlayer())
		{
			StatsSet stat = getStat();
			int points = stat.getInteger(Olympiad.POINTS);
			int diff = Math.min(OlympiadGame.MAX_POINTS_LOOSE, points / _type.getLooseMult());
			stat.set(Olympiad.POINTS, points - diff);
			Log.add("Olympiad Result: " + _name + " lost " + diff + " points for crash", "olympiad");

			// TODO: Снести подробный лог после исправления беспричинного отъёма очков.
			Player player = _player;
			if(player == null)
				Log.add("Olympiad info: " + _name + " crashed coz player == null", "olympiad");
			else
			{
				if(player.isLogoutStarted())
					Log.add("Olympiad info: " + _name + " crashed coz player.isLogoutStarted()", "olympiad");
				if(!player.isConnected())
					Log.add("Olympiad info: " + _name + " crashed coz !player.isOnline()", "olympiad");
				if(player.getOlympiadGame() == null)
					Log.add("Olympiad info: " + _name + " crashed coz player.getOlympiadGame() == null", "olympiad");
				if(player.getOlympiadObserveGame() != null)
					Log.add("Olympiad info: " + _name + " crashed coz player.getOlympiadObserveGame() != null", "olympiad");
			}
		}
	}

	public boolean checkPlayer()
	{
		Player player = _player;
		if(player == null || player.isLogoutStarted() || player.getOlympiadGame() == null || player.isInObserverMode())
			return false;
		return true;
	}

	public void portPlayerToArena()
	{
		Player player = _player;
		if(!checkPlayer() || player.isTeleporting())
		{
			_player = null;
			return;
		}

		DuelEvent duel = player.getEvent(DuelEvent.class);
		if(duel != null)
			duel.abortDuel(player);

		_returnLoc = player.getStablePoint() == null ? player.getReflection().getReturnLoc() == null ? player.getLoc() : player.getReflection().getReturnLoc() : player.getStablePoint();

		if(player.isDead())
			player.setPendingRevive(true);
		if(player.isSitting())
			player.standUp();

		player.setTarget(null);
		player.setIsInOlympiadMode(true);

		player.leaveParty();

		Reflection ref = _game.getReflection();
		InstantZone instantZone = ref.getInstancedZone();

		Location tele = Location.findPointToStay(instantZone.getTeleportCoords().get(_side - 1), 50, 50, ref.getGeoIndex());

		player.setStablePoint(_returnLoc);
		player.teleToLocation(tele, ref);

		player.sendPacket(new ExOlympiadModePacket(_side));
	}

	public void portPlayerBack()
	{
		Player player = _player;
		if(player == null)
			return;

		if(_returnLoc == null) // игрока не портнуло на стадион
			return;

		player.setIsInOlympiadMode(false);
		player.setOlympiadSide(-1);
		player.setOlympiadGame(null);

		// Удаляем баффы и чужие кубики
		for(Effect e : player.getEffectList().getEffects())
			if(!player.isSpecialEffect(e.getSkill()) && (e.getEffectType() != EffectType.Cubic || player.getSkillLevel(e.getSkill().getId()) <= 0))
				e.exit();

		Servitor[] servitors = player.getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
				servitor.getEffectList().stopAllEffects();
		}

		player.setCurrentCp(player.getMaxCp());
		player.setCurrentMp(player.getMaxMp());

		if(player.isDead())
		{
			player.setCurrentHp(player.getMaxHp(), true);
			player.broadcastPacket(new RevivePacket(player));
			//player.broadcastStatusUpdate();
		}
		else
			player.setCurrentHp(player.getMaxHp(), false);

		// Возвращаем клановые скиллы если репутация положительная.
		if(player.getClan() != null && player.getClan().getReputationScore() >= 0)
			player.getClan().enableSkills(player);

		// Активируем геройские скиллы.
		player.activateHeroSkills(true);

		// Обновляем скилл лист, после добавления скилов
		player.sendPacket(new SkillListPacket(player));
		player.sendPacket(new ExOlympiadModePacket(0));
		player.sendPacket(new ExOlympiadMatchEndPacket());

		player.setStablePoint(null);
		player.teleToLocation(_returnLoc, ReflectionManager.DEFAULT);
	}

	public void preparePlayer()
	{
		Player player = _player;
		if(player == null)
			return;

		if(player.isInObserverMode())
			if(player.getOlympiadObserveGame() != null)
				player.leaveOlympiadObserverMode(true);
			else
				player.leaveObserverMode();

		// Un activate clan skills
		if(player.getClan() != null)
			player.getClan().disableSkills(player);

		// Деактивируем геройские скиллы.
		player.activateHeroSkills(false);

		// Abort casting if player casting
		if(player.isCastingNow())
			player.abortCast(true, true);

		// Abort attack if player attacking
		if(player.isAttackingNow())
			player.abortAttack(true, true);

		// Удаляем баффы и чужие кубики
		for(Effect e : player.getEffectList().getEffects())
			if(!player.isSpecialEffect(e.getSkill()) && (e.getEffectType() != EffectType.Cubic || player.getSkillLevel(e.getSkill().getId()) <= 0))
				e.exit();

		// Remove Servitor's Buffs
		Servitor[] servitors = player.getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
			{
				if(servitor.isPet())
					servitor.unSummon(false);
				else
				{
					servitor.getEffectList().stopAllEffects();
					servitor.transferOwnerBuffs();
				}
			}
		}

		// unsummon agathion
		if(player.getAgathionId() > 0)
			player.setAgathion(0);

		// Сброс кулдауна всех скилов, время отката которых меньше 15 минут
		for(TimeStamp sts : player.getSkillReuses())
		{
			if(sts == null)
				continue;
			Skill skill = SkillTable.getInstance().getInfo(sts.getId(), sts.getLevel());
			if(skill == null)
				continue;
			if(sts.getReuseBasic() <= 15 * 60001L)
				player.enableSkill(skill);
		}

		// Обновляем скилл лист, после удаления скилов
		player.sendPacket(new SkillListPacket(player));
		// Обновляем куллдаун, после сброса
		player.sendPacket(new SkillCoolTimePacket(player));

		// Проверяем одетые вещи на возможность ношения.
		player.getInventory().validateItems();

		// remove bsps/sps/ss automation
		Set<Integer> activeSoulShots = player.getAutoSoulShot();
		for(int itemId : activeSoulShots)
		{
			player.removeAutoSoulShot(itemId);
			player.sendPacket(new ExAutoSoulShot(itemId, false));
		}

		// Разряжаем заряженные соул и спирит шоты
		ItemInstance weapon = player.getActiveWeaponInstance();
		if(weapon != null)
		{
			weapon.setChargedSpiritshot(ItemInstance.CHARGED_NONE);
			weapon.setChargedSoulshot(ItemInstance.CHARGED_NONE);
		}

		player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
		player.setCurrentCp(player.getMaxCp());
		player.broadcastUserInfo(true);
	}

	public void saveNobleData()
	{
		OlympiadDatabase.saveNobleData(_objId);
	}

	public void logout()
	{
		_player = null;
	}

	public Player getPlayer()
	{
		return _player;
	}

	public String getName()
	{
		return _name;
	}

	public void addDamage(double d)
	{
		_damage += d;
	}

	public double getDamage()
	{
		return _damage;
	}

	public String getClanName()
	{
		return _clanName;
	}

	public int getClassId()
	{
		return _classId;
	}

	public int getObjectId()
	{
		return _objId;
	}
}