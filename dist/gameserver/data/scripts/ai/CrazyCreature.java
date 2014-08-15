package ai;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.tables.SkillTable;

import instances.Sansililion;

public class CrazyCreature extends Fighter
{
	public CrazyCreature(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		Reflection r = actor.getReflection();
		Player player = null;
		if(killer.isPlayer() || killer.isServitor())
			player = killer.getPlayer();

		if(player != null && r != null)
		{
			if(r instanceof Sansililion)
			{
				Sansililion sInst = (Sansililion) r;
				sInst._points += Rnd.get(8, 15);
				if(Rnd.chance(10) && sInst._lastBuff < 3)
				{
					Skill skill = SkillTable.getInstance().getInfo(14227 + Math.min(3, sInst._lastBuff), 1);
					sInst._lastBuff++;
					if(skill != null)
					{
						skill.getEffects(player, player, false, false);
						player.sendPacket(new ExShowScreenMessage(NpcString.RECEIVED_REGENERATION_ENERGY, 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER));
					}
				}
			}
		}
		super.onEvtDead(killer);
	}
}
