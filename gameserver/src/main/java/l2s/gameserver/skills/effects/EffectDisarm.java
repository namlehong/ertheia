package l2s.gameserver.skills.effects;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectDisarm extends Effect
{
	public EffectDisarm(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(!_effected.isPlayer())
			return false;
		Player player = _effected.getPlayer();
		// Нельзя снимать/одевать проклятое оружие и флаги
		if(player.isCursedWeaponEquipped() || player.getActiveWeaponFlagAttachment() != null)
			return false;
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		Player player = (Player) _effected;

		ItemInstance wpn = player.getActiveWeaponInstance();
		if(wpn != null)
		{
			player.getInventory().unEquipItem(wpn);
			player.sendDisarmMessage(wpn);
		}
		player.startWeaponEquipBlocked();
	}

	@Override
	public void onExit()
	{
		super.onExit();
		_effected.stopWeaponEquipBlocked();
	}

	@Override
	public boolean onActionTime()
	{
		// just stop this effect
		return false;
	}
}