/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.reward.RewardItem;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.utils.ItemFunctions;

/**
 */
public class Plunder extends Skill
{
	/**
	 * Constructor for Sweep.
	 * @param set StatsSet
	 */
	public Plunder(StatsSet set)
	{
		super(set);
	}
	
	/**
	 * Method checkCondition.
	 * @param activeChar Creature
	 * @param target Creature
	 * @param forceUse boolean
	 * @param dontMove boolean
	 * @param first boolean
	 * @return boolean
	 */
	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if (isNotTargetAoE())
		{
			return super.checkCondition(activeChar, target, forceUse, dontMove, first);
		}
		if (target == null)
		{
			return false;
		}
		if (!target.isMonster())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}
	
	/**
	 * Method useSkill.
	 * @param activeChar Creature
	 * @param targets List<Creature>
	 */
	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if (!activeChar.isPlayer())
		{
			return;
		}
		int ss = isSSPossible() ? (isMagic() ? activeChar.getChargedSpiritShot() : activeChar.getChargedSoulShot() ? 2 : 0) : 0;
		if ((ss > 0) && (getPower() > 0))
		{
			activeChar.unChargeShots(false);
		}
		for (Creature target : targets)
		{
			
			if ((target != null) && !target.isDead())
			{
				if (target.isMonster())
				{
					//System.out.println("Target is monster. Skill power " + getPower() );
					//CAUSE DAMAGE
					if (getPower() > 0)
					{
						//System.out.println("isMagic() " + isMagic());
						double damage = 0;
						if (isMagic())
						{
							AttackInfo info = Formulas.calcMagicDam(activeChar, target, this, ss);
							damage = info.damage;
						}
						else
						{
							AttackInfo info = Formulas.calcPhysDam(activeChar, target, this, false, false, ss > 0, false);
							damage = info.damage;
						}
						//System.out.println("Skill damage" + damage);
						target.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);

					}
					
					getEffects(activeChar, target, false, false);
					target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, Math.max(_effectPoint, 1));
					
					// SPOIL PART
					MonsterInstance monster = (MonsterInstance) target;
					
					//Check if target has been plundered or not
					if(monster.isRobbed() > 1)
					{
						activeChar.sendPacket(SystemMsg.PLUNDER_SKILL_HAS_BEEN_ALREADY_USED_ON_THIS_TARGET);
						return;
					}
					
					boolean success;
					success = Formulas.calcSkillSuccess(activeChar, target, this, getActivateRate());
					
					if (success)
					{
						//System.out.println("Plunder success");
						monster.setRobbed(2);
					}
					else
					{
						//System.out.println("Plunder fails");
						monster.setRobbed(1);
						activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_HAS_FAILED).addSkillName(_id, getDisplayLevel()));
						return;
					}
					
					// SWEEP PART
					Player player = (Player) activeChar;
					if ((target == null) || !target.isMonster())
					{
						
						continue;
					}
					MonsterInstance targetMonster = (MonsterInstance) target;
					
					List<RewardItem> items = targetMonster.takePlunder(player);
					if (items == null)
					{
						//System.out.println("No sweep found");
						continue;
					}
					
					for (RewardItem item : items)
					{
						ItemInstance sweep = ItemFunctions.createItem(item.itemId);
						sweep.setCount(item.count);
						if (player.isInParty() && player.getParty().isDistributeSpoilLoot())
						{
							player.getParty().distributeItem(player, sweep, null);
							continue;
						}
						if (!player.getInventory().validateCapacity(sweep) || !player.getInventory().validateWeight(sweep))
						{
							sweep.dropToTheGround(player, targetMonster);
							continue;
						}
						
						player.getInventory().addItem(sweep);

                        SystemMessagePacket smsg;
						if(item.count == 1)
						{
							smsg = new SystemMessagePacket(SystemMsg.YOU_HAVE_OBTAINED_S1);
							smsg.addItemName(item.itemId);
							player.sendPacket(smsg);
						}
						else
						{
							smsg = new SystemMessagePacket(SystemMsg.YOU_HAVE_OBTAINED_S2_S1);
							smsg.addItemName(item.itemId);
							smsg.addLong(item.count);
							player.sendPacket(smsg);
						}

					}
				}
				
			}
			
		}
	}
}
