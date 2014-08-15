package npc.model;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.scripts.Events;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
**/
public class BloodyHornInstance extends NpcInstance
{
	private static final Skill BLOOD_SACRIFICE_DECREASE_SPEED_1 = SkillTable.getInstance().getInfo(15537, 1);	// Blood Sacrifice: Decrease Speed (1 lvl.)
	private static final Skill BLOOD_SACRIFICE_DECREASE_SPEED_2 = SkillTable.getInstance().getInfo(15537, 2);	// Blood Sacrifice: Decrease Speed	(2 lvl.)

	private Player _killer = null;

    public BloodyHornInstance(int objectId, NpcTemplate template)
    {
        super(objectId, template);
    }

	@Override
	public void onAction(Player player, boolean shift)
	{
		if(!isTargetable(player))
		{
			player.sendActionFailed();
			return;
		}

		if(player.getTarget() != this)
		{
			player.setNpcTarget(this);
			return;
		}

		if(Events.onAction(player, this, shift))
		{
			player.sendActionFailed();
			return;
		}

		if(!isInRange(player, INTERACTION_DISTANCE))
		{
			if(player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
			return;
		}

		// С NPC нельзя разговаривать мертвым и сидя
		if(!Config.ALLOW_TALK_WHILE_SITTING && player.isSitting() || player.isAlikeDead())
			return;

		if(hasRandomAnimation())
			onRandomAnimation();

		player.sendActionFailed();
		player.stopMove(false);

		if(Rnd.chance(30))
		{
			player.sendPacket(new ExShowScreenMessage(NpcString.HP_IS_FULLY_RESTORED, 5000, ScreenMessageAlign.TOP_CENTER));
			doCast(BLOOD_SACRIFICE_DECREASE_SPEED_2, player, false);
		}
		else
		{
			player.sendPacket(new ExShowScreenMessage(NpcString.HP_IS_HALFWAY_RESTORED, 5000, ScreenMessageAlign.TOP_CENTER));
			doCast(BLOOD_SACRIFICE_DECREASE_SPEED_1, player, false);
		}

		_killer = player;
	}

	@Override
	public void onCastEndTime(boolean dual, boolean success)
	{
		super.onCastEndTime(dual, success);

		if(success && _killer != null)
			doDie(_killer);
	}
}
