package ai;

import l2s.commons.collections.LazyArrayList;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.tables.SkillTable;

public class BufferNoobs extends DefaultAI {
	private static final int[] BUFFS = {4322, 4323, 4324, 4325, 4326, 4327, 4328};

	public BufferNoobs(NpcInstance actor) {
		super(actor);
		this.AI_TASK_ACTIVE_DELAY = 1000;
	}

	@Override
	protected boolean thinkActive() {
		NpcInstance actor = getActor();
		if(actor == null)
			return true;
		Skill skill;

		for(Player player : World.getAroundPlayers(actor, 200, 200)) {
			if(checkBuff(player)) {
				for(int skillId : BUFFS) {
					skill = SkillTable.getInstance().getInfo(skillId, 1);
					LazyArrayList<Creature> target = new LazyArrayList<Creature>();
					target.add(player);
					actor.broadcastPacket(new MagicSkillUse(actor, player, skillId, 1, 0, 0, false));
					actor.callSkill(skill, target, true);
				}
				player.sendPacket(new ExShowScreenMessage(NpcString.NEWBIE_HELPER_HAS_CASTED_BUFFS_ON_S1, 800, ScreenMessageAlign.TOP_CENTER, player.getName()));
			}
		}
		return true;
	}

	private boolean checkBuff(Player player) {
		for(int skillId : BUFFS)
		{
			if(player.getEffectList().containsEffects(skillId))
				return false;
		}
		return true;
	}

	@Override
	public boolean isGlobalAI() {
		return true;
	}
}
