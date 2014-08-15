package l2s.gameserver.skills.effects;

import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.World;
import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.model.base.InvisibleType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

public final class EffectInvisible extends Effect
{
	private InvisibleType _invisibleType = InvisibleType.NONE;

	public EffectInvisible(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public boolean checkCondition()
	{
		if(_effected.isInvisible())
			return false;

		if(_effected.isPlayer())
		{
			Player player = _effected.getPlayer();
			if(player.getActiveWeaponFlagAttachment() != null)
				return false;
		}
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if(!_effected.isPlayer())
			return;

		Player player = _effected.getPlayer();

		_invisibleType = player.getInvisibleType();

		player.setInvisibleType(InvisibleType.EFFECT);

		World.removeObjectFromPlayersByInvisible(player);

		Servitor[] servitors = player.getServitors();
		if(servitors.length > 0)
		{
			for(Servitor servitor : servitors)
				World.removeObjectFromPlayersByInvisible(servitor);
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();

		if(_effected.isPlayer())
		{
			Player player = _effected.getPlayer();
			if(!player.isInvisible())
				return;

			player.setInvisibleType(_invisibleType);

			player.sendUserInfo(true);

			List<Player> players = World.getAroundPlayers(player);
			for(Player p : players)
				p.sendPacket(p.addVisibleObject(player, null));

			Servitor[] servitors = player.getServitors();
			if(servitors.length > 0)
			{
				for(Servitor servitor : servitors)
				{
					servitor.getEffectList().stopEffects(getSkill());
					for(Player p : players)
						p.sendPacket(p.addVisibleObject(servitor, null));
				}
			}
		}
		else if(_effected.isServitor())
		{
			_effected.getPlayer().getEffectList().stopEffects(getSkill());
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}