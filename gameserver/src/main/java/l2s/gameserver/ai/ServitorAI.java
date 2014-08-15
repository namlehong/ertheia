package l2s.gameserver.ai;

import l2s.gameserver.Config;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Servitor.AttackMode;

public class ServitorAI extends PlayableAI
{
	public ServitorAI(Servitor actor)
	{
		super(actor);
	}

	@Override
	protected void thinkActive()
	{
		Servitor actor = getActor();

		clearNextAction();
		if(actor.isDepressed())
		{
			setAttackTarget(actor.getPlayer());
			changeIntention(CtrlIntention.AI_INTENTION_ATTACK, actor.getPlayer(), null);
			thinkAttack();
		}
		else if(actor.isFollowMode())
		{
			changeIntention(CtrlIntention.AI_INTENTION_FOLLOW, actor.getPlayer(), Config.FOLLOW_RANGE);
			thinkFollow();
		}

		super.thinkActive();
	}

	@Override
	protected void thinkAttack()
	{
		Servitor actor = getActor();

		if(actor.isDepressed())
			setAttackTarget(actor.getPlayer());

		super.thinkAttack();
	}

	@Override
	protected boolean thinkCast()
	{
		if(super.thinkCast())
		{
			setNextAction(AINextAction.ATTACK, getAttackTarget(), null, _forceUse, false);
			return true;
		}
		return false;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		Servitor actor = getActor();
		if(attacker != null && actor.getPlayer().isDead() && !actor.isDepressed())
			Attack(attacker, false, false);
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	public Servitor getActor()
	{
		return (Servitor) super.getActor();
	}

	public void notifyAttackModeChange(AttackMode mode)
	{
		getActor().setAttackMode(mode);
	}
}