package l2s.gameserver.skills.effects;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import l2s.gameserver.model.actor.instances.creature.Effect;
import l2s.gameserver.skills.AbnormalType;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;

/**
 * @author Bonux
**/
public class p_block_buff_slot extends Effect
{
	private final TIntSet _blockedAbnormalTypes;

	public p_block_buff_slot(Env env, EffectTemplate template)
	{
		super(env, template);

		_blockedAbnormalTypes = new TIntHashSet();

		String[] types = template.getParam().getString("abnormal_types", "").split(";");
		for(String type : types)
			_blockedAbnormalTypes.add(AbnormalType.valueOf(type).ordinal());
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onExit()
	{
		super.onExit();
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	@Override
	public boolean checkBlockedAbnormalType(AbnormalType abnormal)
	{
		if(_blockedAbnormalTypes.isEmpty())
			return false;

		return _blockedAbnormalTypes.contains(abnormal.ordinal());
	}
}