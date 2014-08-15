package l2s.gameserver.model.instances;

import l2s.gameserver.ai.CharacterAI;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.s2c.DiePacket;
import l2s.gameserver.templates.npc.NpcTemplate;

public class DeadManInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public DeadManInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setAI(new CharacterAI(this));
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();
		setCurrentHp(0, false);
		broadcastPacket(new DiePacket(this));
		setWalking();
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage)
	{}

	@Override
	public boolean isBlocked()
	{
		return true;
	}
}