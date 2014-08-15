package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.Config;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.StatsSet;

public class Transformation extends Skill
{
	public final boolean isDispell;

	public Transformation(StatsSet set)
	{
		super(set);
		isDispell = set.getBool("is_dispel", false);
	}

	@Override
	public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		Player player = activeChar.getPlayer();
		if(player == null || player.getActiveWeaponFlagAttachment() != null)
			return false;

		if(player.isTransformed() && !isDispell)
		{
			// Для всех скилов кроме Transform Dispel
			activeChar.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
			return false;
		}

		// Нельзя использовать летающую трансформу на территории Aden, или слишком высоко/низко, или при вызванном пете/саммоне, или в инстансе
		if((getId() == SKILL_FINAL_FLYING_FORM || getId() == SKILL_AURA_BIRD_FALCON || getId() == SKILL_AURA_BIRD_OWL) && (player.getX() > -166168 || player.getZ() <= 0 || player.getZ() >= 6000 || player.getServitors().length > 0 || player.getReflection() != ReflectionManager.DEFAULT))
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(_id, _level));
			return false;
		}

		// Нельзя отменять летающую трансформу слишком высоко над землей
		if(player.isInFlyingTransform() && isDispell && Math.abs(player.getZ() - player.getLoc().correctGeoZ().z) > 333)
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(_id, _level));
			return false;
		}

		if(player.isInWater())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER);
			return false;
		}

		if(player.isMounted())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_PET);
			return false;
		}

		// Для трансформации у игрока не должно быть активировано умение Mystic Immunity.
		if(player.getEffectList().containsEffects(Skill.SKILL_MYSTIC_IMMUNITY))
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_UNDER_THE_EFFECT_OF_A_SPECIAL_SKILL);
			return false;
		}

		if(player.isInBoat())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT);
			return false;
		}

		if(player.getPet() != null && !isDispell/* && !isBaseTransformation()*/)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_POLYMORPH_WHEN_YOU_HAVE_SUMMONED_A_SERVITORPET);
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}
}