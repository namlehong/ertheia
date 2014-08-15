package ai.lindvior;

import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.model.World;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.ExSendUIEventPacket;
import l2s.gameserver.model.entity.Reflection;

import instances.LindviorBoss;

/**
generator by iqman
 */

public class Generator extends DefaultAI
{
	private static final int SAY_RAFF = 12000;

	private int charges = 120;


	public Generator(NpcInstance actor)
	{
		super(actor);
		charges = 120;
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		NpcInstance actor = getActor();
		if(actor == null || caster == null || caster.getPlayer() == null || skill.getId() != 15606)
		{
			super.onEvtSeeSpell(skill, caster);
			return;
		}
		charges--;
		for(Player player : World.getAroundPlayers(actor, 700, 500))
		{
			player.sendPacket(new ExShowScreenMessage(NpcString.S1_HAS_CHARGED_THE_CANNON, 5000, ScreenMessageAlign.TOP_CENTER, caster.getPlayer().getName()));
			player.sendPacket(new ExSendUIEventPacket(player, 5, 5000, charges > 0 ? charges : 0, 0, "Remaining Charges"));
		}
		if(charges == 0)
		{
			Reflection r = getActor().getReflection();	
			if (r != null)
			{
				if(r instanceof LindviorBoss)
				{
					LindviorBoss lInst = (LindviorBoss) r;	
					lInst.increaseCharges();
					getActor().setNpcState(0); //innactive
				}	
			}			
		}
		super.onEvtSeeSpell(skill, caster);
	}
}