package l2s.gameserver.skills.skillclasses;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.residences.SiegeFlagInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.templates.StatsSet;

import java.util.List;

/**
 * Created by Archer on 8/5/2014.
 */
public class Sacrifice extends Skill
{
    /**
     * Внимание!!! У наследников вручную надо поменять тип на public
     *
     * @param set парамерты скилла
     */
    public Sacrifice(StatsSet set)
    {
        super(set);
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
    {
        if(target == null || target.isDoor() || target instanceof SiegeFlagInstance || activeChar.getObjectId() == target.getObjectId())
        {
            return false;
        }
        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets)
    {
        if(targets != null && activeChar != null)
        {
            for(Creature target : targets)
            {
                if(target.isHealBlocked())
                {
                    continue;
                }
                else if(target.isCursedWeaponEquipped())
                {
                    continue;
                }

                double addToHp = _power;
                addToHp = Math.max(0, Math.min(addToHp, target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp() / 100. - target.getCurrentHp()));
                if(addToHp > 0)
                {
                    target.setCurrentHp(addToHp + target.getCurrentHp(), false);
                }
                if(getId() == 4051)
                {
                    target.sendPacket(SystemMsg.REJUVENATING_HP);
                }
                else if(target.isPlayer())
                {
                    if (activeChar == target)
                    {
                        activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(addToHp)));
                    } else
                    {
                        target.sendPacket(new SystemMessagePacket(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addName(activeChar).addInteger(Math.round(addToHp)));

                    }
                }
                getEffects(activeChar, target, false);
            }
        }
        super.useSkill(activeChar, targets);
    }
}
