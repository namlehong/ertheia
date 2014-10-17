package l2s.gameserver.network.l2.c2s;

import l2s.commons.lang.ArrayUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.AcquireSkillListPacket;
import l2s.gameserver.network.l2.s2c.SkillListPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.tables.SkillTable;

public class RequestAquireSkill extends L2GameClientPacket
{
	private AcquireType _type;
	private int _id, _level, _subUnit;

	@Override
	protected void readImpl()
	{
		_id = readD();
		_level = readD();
		_type = ArrayUtils.valid(AcquireType.VALUES, readD());
		if(_type == AcquireType.SUB_UNIT)
			_subUnit = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null || player.isTransformed() || _type == null)
			return;

		NpcInstance trainer = player.getLastNpc();
		if((trainer != null && player.getDistance(trainer.getX(), trainer.getY()) > Creature.INTERACTION_DISTANCE) && !player.isGM())
			trainer = null;

		Skill skill = SkillTable.getInstance().getInfo(_id, _level);
		if(skill == null)
			return;

		if(!SkillAcquireHolder.getInstance().isSkillPossible(player, skill, _type))
			return;

		SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, _id, _level, _type);
		if(skillLearn == null)
			return;

		if(skillLearn.getMinLevel() > player.getLevel())
			return;

		if(skillLearn.getDualClassMinLvl() > player.getDualClassLevel())
			return;

		if(!checkSpellbook(player, skillLearn))
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
			return;
		}

		switch(_type)
		{
			case NORMAL:
				learnSimpleNextLevel(player, skillLearn, skill);
				player.sendPacket(new AcquireSkillListPacket(player));
				break;
			case TRANSFORMATION:
				if(trainer != null)
				{
					learnSimpleNextLevel(player, skillLearn, skill);
					trainer.showTransformationSkillList(player, AcquireType.TRANSFORMATION);
				}
				break;
			case COLLECTION:
				if(trainer != null)
				{
					learnSimpleNextLevel(player, skillLearn, skill);
					NpcInstance.showCollectionSkillList(player);
				}
				break;
			case TRANSFER_CARDINAL:
			case TRANSFER_EVA_SAINTS:
			case TRANSFER_SHILLIEN_SAINTS:
				if(trainer != null)
				{
					learnSimple(player, skillLearn, skill);
					trainer.showTransferSkillList(player);
				}
				break;
			case FISHING:
				if(trainer != null)
				{
					learnSimpleNextLevel(player, skillLearn, skill);
					NpcInstance.showFishingSkillList(player);
				}
				break;
			case CLAN:
				if(trainer != null)
					learnClanSkill(player, skillLearn, trainer, skill);
				break;
			case SUB_UNIT:
				if(trainer != null)
					learnSubUnitSkill(player, skillLearn, trainer, skill, _subUnit);
				break;
			case CERTIFICATION:
			case DUAL_CERTIFICATION:
				if(trainer != null)
				{
					if(!player.getActiveSubClass().isBase())
					{
						player.sendPacket(SystemMsg.THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUBCLASS_STATE);
						return;
					}
					learnSimpleNextLevel(player, skillLearn, skill);
					trainer.showTransformationSkillList(player, _type);
				}
				break;
			case CHAOS:
			case DUAL_CHAOS:
				if(trainer != null)
				{
					learnSimpleNextLevel(player, skillLearn, skill);
					NpcInstance.showChaosSkillList(player);
				}
				break;
			case ALCHEMY:
				if(trainer != null)
				{
					System.out.println("skillLearn id " + skillLearn.getId() + " cost " + skillLearn.getCost() + " item " + skillLearn.getItemCount() + " level " + skillLearn.getLevel() + " minlevel " + skillLearn.getMinLevel());
					System.out.println("skill ud " + skill.getName() + " level " + skill.getLevel());
					learnSimpleNextLevel(player, skillLearn, skill);
					NpcInstance.showAlchemyAcquireList(player);
					player.checkAlchemySkills();
				}
				break;
		default:
			break;
		}
	}

	/**
	 * Изучение следующего возможного уровня скилла
	 */
	private static void learnSimpleNextLevel(Player player, SkillLearn skillLearn, Skill skill)
	{
		final int skillLevel = player.getSkillLevel(skillLearn.getId(), 0);
		System.out.println("skillLevel != skillLearn.getLevel() - 1 " + (skillLevel != skillLearn.getLevel() - 1));
		if(skillLevel != skillLearn.getLevel() - 1)
			return;

		learnSimple(player, skillLearn, skill);
	}

	private static void learnSimple(Player player, SkillLearn skillLearn, Skill skill)
	{
		if(player.getSp() < skillLearn.getCost())
		{
			System.out.println("no sp");
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
			return;
		}

		if(skillLearn.getItemId() > 0)
			if(!player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount()))
				return;

		System.out.println("get skill");
		player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(), skill.getLevel()));

		player.setSp(player.getSp() - skillLearn.getCost());
		player.addSkill(skill, true);
		player.sendUserInfo();
		player.updateStats();

		player.sendPacket(new SkillListPacket(player, skill.getId()));

		RequestExEnchantSkill.updateSkillShortcuts(player, skill.getId(), skill.getLevel());
	}

	private static void learnClanSkill(Player player, SkillLearn skillLearn, NpcInstance trainer, Skill skill)
	{
		if(!player.isClanLeader())
		{
			player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return;
		}

		Clan clan = player.getClan();
		final int skillLevel = clan.getSkillLevel(skillLearn.getId(), 0);
		if(skillLevel != skillLearn.getLevel() - 1) // можно выучить только следующий уровень
			return;
		if(clan.getReputationScore() < skillLearn.getCost())
		{
			player.sendPacket(SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
			return;
		}

		if(skillLearn.getItemId() > 0)
			if(!player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount()))
				return;

		clan.incReputation(-skillLearn.getCost(), false, "AquireSkill: " + skillLearn.getId() + ", lvl " + skillLearn.getLevel());
		clan.addSkill(skill, true);
		clan.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill));

		NpcInstance.showClanSkillList(player);
	}

	private static void learnSubUnitSkill(Player player, SkillLearn skillLearn, NpcInstance trainer, Skill skill, int id)
	{
		Clan clan = player.getClan();
		if(clan == null)
			return;
		SubUnit sub = clan.getSubUnit(id);
		if(sub == null)
			return;

		if((player.getClanPrivileges() & Clan.CP_CL_TROOPS_FAME) != Clan.CP_CL_TROOPS_FAME)
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}

		int lvl = sub.getSkillLevel(skillLearn.getId(), 0);
		if(lvl >= skillLearn.getLevel())
		{
			player.sendPacket(SystemMsg.THIS_SQUAD_SKILL_HAS_ALREADY_BEEN_ACQUIRED);
			return;
		}

		if(lvl != (skillLearn.getLevel() - 1))
		{
			player.sendPacket(SystemMsg.THE_PREVIOUS_LEVEL_SKILL_HAS_NOT_BEEN_LEARNED);
			return;
		}

		if(clan.getReputationScore() < skillLearn.getCost())
		{
			player.sendPacket(SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
			return;
		}

		if(skillLearn.getItemId() > 0)
			if(!player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount()))
				return;

		clan.incReputation(-skillLearn.getCost(), false, "AquireSkill2: " + skillLearn.getId() + ", lvl " + skillLearn.getLevel());
		sub.addSkill(skill, true);
		player.sendPacket(new SystemMessagePacket(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill));

		if(trainer != null)
			NpcInstance.showSubUnitSkillList(player);
	}

	private static boolean checkSpellbook(Player player, SkillLearn skillLearn)
	{
		if(Config.ALT_DISABLE_SPELLBOOKS)
			return true;

		if(skillLearn.getItemId() == 0)
			return true;

		return player.getInventory().getCountOf(skillLearn.getItemId()) >= skillLearn.getItemCount();
	}
}