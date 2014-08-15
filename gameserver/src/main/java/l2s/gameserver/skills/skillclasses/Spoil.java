package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.reward.RewardItem;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.model.reward.RewardType;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.stats.Formulas.AttackInfo;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.utils.ItemFunctions;

public class Spoil extends Skill
{
	private final boolean _isPermament;

	public Spoil(StatsSet set)
	{
		super(set);
		_isPermament = set.getBool("isPermament", false);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if(!activeChar.isPlayer())
			return;

		int ss = isSSPossible() ? (isMagic() ? activeChar.getChargedSpiritShot() : activeChar.getChargedSoulShot() ? 2 : 0) : 0;
		if(ss > 0 && getPower() > 0)
			activeChar.unChargeShots(false);

		for(Creature target : targets)
		{
			if(target != null && !target.isDead())
			{
				if(target.isMonster())
				{
					MonsterInstance monster = (MonsterInstance) target;
					if(monster.isSpoiled())
						activeChar.sendPacket(SystemMsg.IT_HAS_ALREADY_BEEN_SPOILED);
					else if(monster.isRobbed() > 0)
						activeChar.sendPacket(SystemMsg.PLUNDER_SKILL_HAS_BEEN_ALREADY_USED_ON_THIS_TARGET);
					else
					{
						boolean success;
						if(!Config.ALT_SPOIL_FORMULA)
						{
							int monsterLevel = monster.getLevel();
							int modifier = Math.abs(monsterLevel - activeChar.getLevel());
							double rateOfSpoil = Config.BASE_SPOIL_RATE;

							if(modifier > 8)
								rateOfSpoil = rateOfSpoil - rateOfSpoil * (modifier - 8) * 9 / 100;

							rateOfSpoil = rateOfSpoil * getMagicLevel() / monsterLevel;

							if(rateOfSpoil < Config.MINIMUM_SPOIL_RATE)
								rateOfSpoil = Config.MINIMUM_SPOIL_RATE;
							else if(rateOfSpoil > 99.)
								rateOfSpoil = 99.;

							if(activeChar.getPlayer().isGM())
								activeChar.sendMessage(new CustomMessage("l2s.gameserver.skills.skillclasses.Spoil.Chance", (Player) activeChar).addNumber((long) rateOfSpoil));
							success = Rnd.chance(rateOfSpoil);
						}
						else
							success = Formulas.calcSkillSuccess(activeChar, target, this, getActivateRate());

						if(success)
							monster.setSpoiled(activeChar.getPlayer());

						if(!_isPermament)
						{
							if(success)
								activeChar.sendPacket(SystemMsg.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
							else
								activeChar.sendPacket(new SystemMessagePacket(SystemMsg.S1_HAS_FAILED).addSkillName(_id, getDisplayLevel()));
						}
					}
				}

				if(getPower() > 0)
				{
					if(isMagic())
					{
						AttackInfo info = Formulas.calcMagicDam(activeChar, target, this, ss);
						target.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true, true, info.crit, false, false, true);
						if(info.damage >= 1)
						{
							double lethalDmg = Formulas.calcLethalDamage(activeChar, target, this);
							if(lethalDmg > 0)
								target.reduceCurrentHp(lethalDmg, activeChar, this, true, true, false, false, false, false, false);
						}
					}
					else
					{
						AttackInfo info = Formulas.calcPhysDam(activeChar, target, this, false, false, ss > 0, false);
						target.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true, true, info.crit || info.blow, false, false, false);
						if(!info.miss || info.damage >= 1)
						{
							double lethalDmg = Formulas.calcLethalDamage(activeChar, target, this);
							if(lethalDmg > 0)
								target.reduceCurrentHp(lethalDmg, activeChar, this, true, true, false, false, false, false, false);
							else
								target.doCounterAttack(this, activeChar, false);
						}
					}
				}

				getEffects(activeChar, target, false);

				target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, Math.max(_effectPoint, 1));

				if(_isPermament && target.isMonster())
				{
					MonsterInstance monster = (MonsterInstance) target;
					if(monster.isRobbed() > 0)
						continue;

					if(monster.isSpoiled())
					{
						RewardList spoilReward = monster.getTemplate().getRewards().get(RewardType.SWEEP);
						if(spoilReward != null)
						{
							monster.rollRewards(RewardType.SWEEP, spoilReward, activeChar, activeChar);

							Player player = activeChar.getPlayer();
							List<RewardItem> items = monster.takeSweep();
							if(items != null && !items.isEmpty())
							{
								for(RewardItem item : items)
								{
									ItemInstance sweep = ItemFunctions.createItem(item.itemId);
									sweep.setCount(item.count);

									if(player.isInParty() && player.getParty().isDistributeSpoilLoot())
									{
										player.getParty().distributeItem(player, sweep, null);
										continue;
									}

									if(!player.getInventory().validateCapacity(sweep) || !player.getInventory().validateWeight(sweep))
									{
										sweep.dropToTheGround(player, monster);
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

									if(player.isInParty())
									{
										if(item.count == 1)
										{
											smsg = new SystemMessagePacket(SystemMsg.C1_HAS_OBTAINED_S2_BY_USING_SWEEPER);
											smsg.addName(player);
											smsg.addItemName(item.itemId);
											player.getParty().broadcastToPartyMembers(player, smsg);
										}
										else
										{
											smsg = new SystemMessagePacket(SystemMsg.C1_HAS_OBTAINED_S3_S2_BY_USING_SWEEPER);
											smsg.addName(player);
											smsg.addItemName(item.itemId);
											smsg.addLong(item.count);
											player.getParty().broadcastToPartyMembers(player, smsg);
										}
									}
								}
								monster.clearSweep();
								monster.setRobbed(2);
								continue;
							}
						}
					}
					monster.clearSweep();
					monster.setRobbed(1);
				}
			}
		}

		super.useSkill(activeChar, targets);
	}
}