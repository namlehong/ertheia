package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;
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
import l2s.gameserver.model.entity.events.impl.FightBattleEvent;
import l2s.gameserver.model.entity.events.impl.DuelEvent;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.ExAutoSoulShot;
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
public final class FightBattlePlayerObject implements Serializable, Comparable<FightBattlePlayerObject>
{
	private static final long serialVersionUID = 1L;

	private Player _player;

	private double _damage = 0.;
	private int _winCount = 0;
	private boolean _killed = false;

	private Location _returnLoc = null;

	public FightBattlePlayerObject(Player player)
	{
		_player = player;
	}

	public String getName()
	{
		if(_player == null)
			return "";

		return _player.getName();
	}

	public int getObjectId()
	{
		if(_player == null)
			return 0;

		return _player.getObjectId();
	}

	public Player getPlayer()
	{
		return _player;
	}

	public void setDamage(double val)
	{
		_damage = val;
	}

	public double getDamage()
	{
		return _damage;
	}

	public void setWinCount(int val)
	{
		_winCount = val;
	}

	public int getWinCount()
	{
		return _winCount;
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
	public int compareTo(FightBattlePlayerObject o)
	{
		return _player.getLevel() - o.getPlayer().getLevel();
	}

	public void teleportPlayer(FightBattleArenaObject arena)
	{
		Player player = _player;
		if(player == null)
			return;

		if(player.isTeleporting())
		{
			_player = null;
			return;
		}

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

		// Remove Hero Skills
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

		player.broadcastUserInfo(true);

		DuelEvent duel = player.getEvent(DuelEvent.class);
		if(duel != null)
			duel.abortDuel(player);

		_returnLoc = player.getStablePoint() == null ? player.getLoc() : player.getStablePoint();

		if(player.isSitting())
			player.standUp();

		player.setTarget(null);

		player.leaveParty();

		player.setStablePoint(_returnLoc);

		Location loc = arena.getMember1() == this ? arena.getInfo().getTeleportLoc1() : arena.getInfo().getTeleportLoc2();
		player.teleToLocation(Location.findPointToStay(loc, 0, arena.getReflection().getGeoIndex()), arena.getReflection());

		setDamage(0.);
		setKilled(false);
	}

	public void onStopEvent(FightBattleEvent event)
	{
		Player player = _player;
		if(player == null)
			return;

		if(_returnLoc == null) // игрока не портнуло на стадион
			return;

		player.removeEvent(event);

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

		// Add Hero Skills
		player.activateHeroSkills(true);

		// Обновляем скилл лист, после добавления скилов
		player.sendPacket(new SkillListPacket(player));

		player.setStablePoint(null);
		player.teleToLocation(_returnLoc, ReflectionManager.DEFAULT);
	}

	public void onDamage(double damage)
	{
		setDamage(getDamage() + damage);
	}

	public void onKill(FightBattleEvent event, FightBattlePlayerObject killer)
	{
		setKilled(true);
	}

	public void onTeleport(FightBattleEvent event, Player player, int x, int y, int z, Reflection reflection)
	{
		onExit(event);
	}

	public void onExit(FightBattleEvent event)
	{
		Player player = _player;
		if(player == null)
			return;

		player.removeEvent(event);

		if(player.isDead())
			player.setCurrentHp(player.getMaxHp(), true);
		else
			player.setCurrentHp(player.getMaxHp(), false);

		player.setCurrentCp(player.getMaxCp());
		player.setCurrentMp(player.getMaxMp());
	}
}
