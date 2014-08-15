package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.base.InvisibleType;
import l2s.gameserver.model.entity.Hero;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.entity.events.impl.ChaosFestivalEvent;
import l2s.gameserver.model.entity.events.impl.DuelEvent;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseEnter;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseLeave;
import l2s.gameserver.network.l2.s2c.ExCuriousHouseState;
import l2s.gameserver.network.l2.s2c.RevivePacket;
import l2s.gameserver.network.l2.s2c.SkillCoolTimePacket;
import l2s.gameserver.network.l2.s2c.SkillListPacket;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.skills.EffectType;
import l2s.gameserver.skills.TimeStamp;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Location;

/**
 * @author Bonux
**/
public final class ChaosFestivalPlayerObject implements Serializable, Comparable<ChaosFestivalPlayerObject>
{
	public static class WinnerComparator implements Comparator<ChaosFestivalPlayerObject>
	{
		@Override
		public int compare(ChaosFestivalPlayerObject o1, ChaosFestivalPlayerObject o2)
		{
			if(o1.getKills() == o2.getKills())
				return (int) (o2.getDamage() - o1.getDamage());
			return o2.getKills() - o1.getKills();
		}
	}

	public static class LevelComparator implements Comparator<ChaosFestivalPlayerObject>
	{
		@Override
		public int compare(ChaosFestivalPlayerObject o1, ChaosFestivalPlayerObject o2)
		{
			return o2.getPlayer().getLevel() - o1.getPlayer().getLevel();
		}
	}

	private static final long serialVersionUID = 1L;

	private Player _player;

	private int _id = 0;

	private int _kills = 0;
	private int _lifeTime = 0;
	private double _damage = 0;

	private boolean _killed = false;

	private Location _returnLoc = null;

	public ChaosFestivalPlayerObject(Player player)
	{
		_player = player;
	}

	public int getObjectId()
	{
		return _player.getObjectId();
	}

	public int getActiveClassId()
	{
		return _player.getActiveClassId();
	}

	public int getMaxHp()
	{
		return _player.getMaxHp();
	}

	public int getMaxCp()
	{
		return _player.getMaxCp();
	}

	public int getCurrentHp()
	{
		return (int) _player.getCurrentHp();
	}

	public int getCurrentCp()
	{
		return (int) _player.getCurrentCp();
	}

	public Player getPlayer()
	{
		return _player;
	}

	public void setId(int val)
	{
		_id = val;
	}

	public int getId()
	{
		return _id;
	}

	public void setKills(int val)
	{
		_kills = val;
	}

	public int getKills()
	{
		return _kills;
	}

	public void setLifeTime(int val)
	{
		_lifeTime = val;
	}

	public int getLifeTime()
	{
		return _lifeTime;
	}

	public void setDamage(double val)
	{
		_damage = val;
	}

	public double getDamage()
	{
		return _damage;
	}

	public void setKilled(boolean val)
	{
		_killed = val;
	}

	public boolean isKilled()
	{
		return _killed;
	}

	@Override
	public int compareTo(ChaosFestivalPlayerObject o)
	{
		return getId() - o.getId();
	}

	public void teleportPlayer(ChaosFestivalEvent event, Reflection reflection)
	{
		Player player = _player;
		if(player == null)
			return;

		if(player.isTeleporting())
		{
			_player = null;
			return;
		}

		player.addEvent(event);

		if(player.isInObserverMode())
		{
			if(player.getOlympiadObserveGame() != null)
				player.leaveOlympiadObserverMode(true);
			else
				player.leaveObserverMode();
		}

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
		{
			if(!player.isSpecialEffect(e.getSkill()) && (e.getEffectType() != EffectType.Cubic || player.getSkillLevel(e.getSkill().getId()) <= 0))
				e.exit();
		}

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

		if(player.isDead())
		{
			player.setCurrentHp(player.getMaxHp(), true);
			player.broadcastPacket(new RevivePacket(player));
			//player.broadcastStatusUpdate();
		}
		else
			player.setCurrentHp(player.getMaxHp(), false);

		player.setCurrentCp(player.getMaxCp());
		player.setCurrentMp(player.getMaxMp());

		player.broadcastUserInfo(true);

		DuelEvent duel = player.getEvent(DuelEvent.class);
		if(duel != null)
			duel.abortDuel(player);

		_returnLoc = player.getStablePoint() == null ? player.getReflection().getReturnLoc() == null ? player.getLoc() : player.getReflection().getReturnLoc() : player.getStablePoint();

		if(player.isSitting())
			player.standUp();

		player.setTarget(null);

		player.leaveParty();

		player.setInvisibleType(InvisibleType.NORMAL);
		player.sendUserInfo(true);
		World.removeObjectFromPlayersByInvisible(player);

		servitors = player.getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
				World.removeObjectFromPlayersByInvisible(servitor);
		}

		player.setStablePoint(_returnLoc);

		List<Location> teleportCoords = reflection.getInstancedZone().getTeleportCoords();
		Location loc = teleportCoords.get(Math.min(getId() - 1, teleportCoords.size() - 1));
		player.teleToLocation(Location.findPointToStay(loc, 0, reflection.getGeoIndex()), reflection);

		player.startImmobilized();

		player.sendPacket(ExCuriousHouseState.IDLE);
		player.sendPacket(ExCuriousHouseEnter.STATIC);

		Functions.show("chaos_festival/rules.htm", player, null);
	}

	public void onStartBattle()
	{
		Player player = _player;
		if(player == null)
			return;

		player.stopImmobilized();

		player.setInvisibleType(InvisibleType.NONE);
		player.sendUserInfo(true);

		List<Player> players = World.getAroundPlayers(player);
		for(Player p : players)
			p.sendPacket(p.addVisibleObject(player, null));

		Servitor[] servitors = player.getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
			{
				for(Player p : players)
					p.sendPacket(p.addVisibleObject(servitor, null));
			}
		}
	}

	public void onStopBattle(ChaosFestivalEvent event)
	{
		if(!isKilled())
			setLifeTime((int) (System.currentTimeMillis() / 1000L) - event.getBattleStartTime());
	}

	public void onFinishBattle(ChaosFestivalArenaObject arena)
	{
		Player player = _player;
		if(player == null)
			return;

		if(_returnLoc == null) // игрока не портнуло на стадион
			return;

		if(arena.getWinner() != this)
			ItemFunctions.addItem(player, ChaosFestivalEvent.MYSTERIOUS_MARK_ITEM_ID, 2, true);

		player.removeEvent(arena.getEvent());

		if(player.isDead())
		{
			player.setCurrentHp(player.getMaxHp(), true);
			player.broadcastPacket(new RevivePacket(player));
			//player.broadcastStatusUpdate();
		}
		else
			player.setCurrentHp(player.getMaxHp(), false);

		player.setCurrentCp(player.getMaxCp());
		player.setCurrentMp(player.getMaxMp());

		// Возвращаем клановые скиллы если репутация положительная.
		if(player.getClan() != null && player.getClan().getReputationScore() >= 0)
			player.getClan().enableSkills(player);

		// Активируем геройские скиллы.
		player.activateHeroSkills(true);

		// Обновляем скилл лист, после добавления скилов
		player.sendPacket(new SkillListPacket(player));

		player.sendPacket(ExCuriousHouseLeave.STATIC);

		player.setStablePoint(null);
		player.teleToLocation(_returnLoc, ReflectionManager.DEFAULT);
	}

	public void onDamage(double damage)
	{
		setDamage(getDamage() + damage);
	}

	public void onKill(ChaosFestivalEvent event, ChaosFestivalPlayerObject killer)
	{
		if(killer != null)
			killer.setKills(killer.getKills() + 1);

		setKilled(true);
		setLifeTime((int) (System.currentTimeMillis() / 1000L) - event.getBattleStartTime());
	}

	public void onTeleport(ChaosFestivalEvent event, Player player, int x, int y, int z, Reflection reflection)
	{
		onExit(event);
	}

	public void onExit(ChaosFestivalEvent event)
	{
		Player player = _player;
		if(player == null)
			return;

		player.removeEvent(event);

		player.setCurrentCp(player.getMaxCp());
		player.setCurrentMp(player.getMaxMp());

		if(player.isDead())
			player.setCurrentHp(player.getMaxHp(), true);
		else
			player.setCurrentHp(player.getMaxHp(), false);
	}
}
