package npc.model;

import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.listener.actor.ai.OnAiIntentionListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public final class OrbisMonsterInstance extends MonsterInstance
{
	private static class IntentionListener implements OnAiIntentionListener
	{
		@Override
		public void onAiIntention(Creature actor, CtrlIntention intention, Object arg0, Object arg1)
		{
			if(actor == null)
				return;

			if(!(actor instanceof OrbisMonsterInstance))
				return;

			OrbisMonsterInstance monster = (OrbisMonsterInstance) actor;
			switch(intention)
			{
				case AI_INTENTION_IDLE:
				case AI_INTENTION_ACTIVE:
					monster.unequipWeapon();
					break;
				default:
					monster.equipWeapon();
					break;
			}
		}
	}

	private final static IntentionListener INTENTION_LISTENER = new IntentionListener();

	private final int _weaponId;

	public OrbisMonsterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		_weaponId = getParameter("weapon_id", 0);

		addListener(INTENTION_LISTENER);
	}

	public void equipWeapon()
	{
		setRHandId(_weaponId);
		broadcastCharInfoImpl();
	}

	public void unequipWeapon()
	{
		if(isAlikeDead())
			return;

		setRHandId(0);
		broadcastCharInfoImpl();
	}
	
	@Override
	public boolean isOrbisMonster()
	{
		return true;
	}
}