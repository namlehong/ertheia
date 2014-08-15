package l2s.gameserver.model.actor.basestats;

import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.stats.Stats;

/**
 * @author Bonux
**/
public class PlayerBaseStats extends PlayableBaseStats
{
	public PlayerBaseStats(Player owner)
	{
		super(owner);
	}

	@Override
	public Player getOwner()
	{
		return (Player) _owner;
	}

	@Override
	public double getHpMax()
	{
		if(getOwner().isMounted() && getOwner().getMount().getMaxHpOnRide() > 0)
			return getOwner().getMount().getMaxHpOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseHpMax(getOwner().getLevel()) > 0)
			return getOwner().getTransform().getBaseHpMax(getOwner().getLevel());

		return getOwner().getClassId().getBaseHp(getOwner().getLevel());
	}

	@Override
	public double getMpMax()
	{
		if(getOwner().isMounted() && getOwner().getMount().getMaxMpOnRide() > 0)
			return getOwner().getMount().getMaxMpOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseMpMax(getOwner().getLevel()) > 0)
			return getOwner().getTransform().getBaseMpMax(getOwner().getLevel());

		return getOwner().getClassId().getBaseMp(getOwner().getLevel());
	}

	@Override
	public double getCpMax()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseCpMax(getOwner().getLevel()) > 0)
			return getOwner().getTransform().getBaseCpMax(getOwner().getLevel());

		return getOwner().getClassId().getBaseCp(getOwner().getLevel());
	}

	@Override
	public double getHpReg()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseHpReg(getOwner().getLevel()) > 0)
			return getOwner().getTransform().getBaseHpReg(getOwner().getLevel());

		return super.getHpReg();
	}

	@Override
	public double getMpReg()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseMpReg(getOwner().getLevel()) > 0)
			return getOwner().getTransform().getBaseMpReg(getOwner().getLevel());

		return super.getMpReg();
	}

	@Override
	public double getCpReg()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseCpReg(getOwner().getLevel()) > 0)
			return getOwner().getTransform().getBaseCpReg(getOwner().getLevel());

		return super.getCpReg();
	}

	@Override
	public double getPAtk()
	{
		if(getOwner().getActiveWeaponInstance() == null)
		{
			if(getOwner().isMounted())
				return getOwner().getMount().getPAtkOnRide();

			if(getOwner().isTransformed() && getOwner().getTransform().getBasePAtk() > 0)
				return getOwner().getTransform().getBasePAtk();
		}

		return super.getPAtk();
	}

	@Override
	public double getMAtk()
	{
		if(getOwner().getActiveWeaponInstance() == null)
		{
			if(getOwner().isMounted())
				return getOwner().getMount().getMAtkOnRide();

			if(getOwner().isTransformed() && getOwner().getTransform().getBaseMAtk() > 0)
				return getOwner().getTransform().getBaseMAtk();
		}

		return super.getMAtk();
	}

	@Override
	public double getPDef()
	{
		double result = 0.;

		final ItemInstance chest = getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
		if(chest == null)
		{
			if(getOwner().isTransformed() && getOwner().getTransform().getBaseChestDef() > 0)
				result += getOwner().getTransform().getBaseChestDef();
			else
				result += getOwner().getTemplate().getBaseChestDef();
		}

		if(getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS) == null && (chest == null || chest.getBodyPart() != ItemTemplate.SLOT_FULL_ARMOR))
		{
			if(getOwner().isTransformed() && getOwner().getTransform().getBaseLegsDef() > 0)
				result += getOwner().getTransform().getBaseLegsDef();
			else
				result += getOwner().getTemplate().getBaseLegsDef();
		}

		if(getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD) == null)
		{
			if(getOwner().isTransformed() && getOwner().getTransform().getBaseHelmetDef() > 0)
				result += getOwner().getTransform().getBaseHelmetDef();
			else
				result += getOwner().getTemplate().getBaseHelmetDef();
		}

		if(getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES) == null)
		{
			if(getOwner().isTransformed() && getOwner().getTransform().getBaseGlovesDef() > 0)
				result += getOwner().getTransform().getBaseGlovesDef();
			else
				result += getOwner().getTemplate().getBaseGlovesDef();
		}

		if(getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET) == null)
		{
			if(getOwner().isTransformed() && getOwner().getTransform().getBaseBootsDef() > 0)
				result += getOwner().getTransform().getBaseBootsDef();
			else
				result += getOwner().getTemplate().getBaseBootsDef();
		}

		if(getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_UNDER) == null)
		{
			if(getOwner().isTransformed() && getOwner().getTransform().getBaseUnderwearDef() > 0)
				result += getOwner().getTransform().getBaseUnderwearDef();
			else
				result += getOwner().getTemplate().getBaseUnderwearDef();
		}

		if(getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_BACK) == null)
		{
			if(getOwner().isTransformed() && getOwner().getTransform().getBaseCloakDef() > 0)
				result += getOwner().getTransform().getBaseCloakDef();
			else
				result += getOwner().getTemplate().getBaseCloakDef();
		}

		return result;
	}

	@Override
	public double getMDef()
	{
		double result = 0.;

		if(getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR) == null)
		{
			if(getOwner().isTransformed() && getOwner().getTransform().getBaseLEarDef() > 0)
				result += getOwner().getTransform().getBaseLEarDef();
			else
				result += getOwner().getTemplate().getBaseLEarDef();
		}

		if(getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR) == null)
		{
			if(getOwner().isTransformed() && getOwner().getTransform().getBaseREarDef() > 0)
				result += getOwner().getTransform().getBaseREarDef();
			else
				result += getOwner().getTemplate().getBaseREarDef();
		}

		if(getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK) == null)
		{
			if(getOwner().isTransformed() && getOwner().getTransform().getBaseNecklaceDef() > 0)
				result += getOwner().getTransform().getBaseNecklaceDef();
			else
				result += getOwner().getTemplate().getBaseNecklaceDef();
		}

		if(getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER) == null)
		{
			if(getOwner().isTransformed() && getOwner().getTransform().getBaseLRingDef() > 0)
				result += getOwner().getTransform().getBaseLRingDef();
			else
				result += getOwner().getTemplate().getBaseLRingDef();
		}

		if(getOwner().getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER) == null)
		{
			if(getOwner().isTransformed() && getOwner().getTransform().getBaseRRingDef() > 0)
				result += getOwner().getTransform().getBaseRRingDef();
			else
				result += getOwner().getTemplate().getBaseRRingDef();
		}

		return result;
	}

	@Override
	public double getPAtkSpd()
	{
		if(getOwner().isMounted())
			return getOwner().calcStat(Stats.BASE_P_ATK_SPD, getOwner().getMount().getAtkSpdOnRide(), null, null);

		if(getOwner().isTransformed() && getOwner().getTransform().getBasePAtkSpd() > 0)
			return getOwner().calcStat(Stats.BASE_P_ATK_SPD, getOwner().getTransform().getBasePAtkSpd(), null, null);

		return super.getPAtkSpd();
	}

	@Override
	public double getMAtkSpd()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseMAtkSpd() > 0)
			return getOwner().getTransform().getBaseMAtkSpd();

		return super.getMAtkSpd();
	}

	@Override
	public double getShldDef()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseShldDef() > 0)
			return getOwner().getTransform().getBaseShldDef();

		return super.getShldDef();
	}

	@Override
	public int getAtkRange()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseAtkRange() > 0)
			return getOwner().getTransform().getBaseAtkRange();

		return super.getAtkRange();
	}

	@Override
	public double getShldRate()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseShldRate() > 0)
			return getOwner().getTransform().getBaseShldRate();

		return 0.;
		//return super.getShldRate(); TODO: [Bonux] Check.
	}

	@Override
	public double getPCritRate()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBasePCritRate() > 0)
			return getOwner().getTransform().getBasePCritRate();

		return super.getPCritRate();
	}

	@Override
	public double getMCritRate()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseMCritRate() > 0)
			return getOwner().getTransform().getBaseMCritRate();

		return super.getMCritRate();
	}

	@Override
	public double getRunSpd()
	{
		if(getOwner().isMounted())
			return getOwner().getMount().getRunSpdOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseRunSpd() > 0)
			return getOwner().getTransform().getBaseRunSpd();

		return super.getRunSpd();
	}

	@Override
	public double getWalkSpd()
	{
		if(getOwner().isMounted())
			return getOwner().getMount().getWalkSpdOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseWalkSpd() > 0)
			return getOwner().getTransform().getBaseWalkSpd();

		return super.getWalkSpd();
	}

	@Override
	public double getWaterRunSpd()
	{
		if(getOwner().isMounted())
			return getOwner().getMount().getWaterRunSpdOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseWaterRunSpd() > 0)
			return getOwner().getTransform().getBaseWaterRunSpd();

		return super.getWaterRunSpd();
	}

	@Override
	public double getWaterWalkSpd()
	{
		if(getOwner().isMounted())
			return getOwner().getMount().getWaterWalkSpdOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseWaterWalkSpd() > 0)
			return getOwner().getTransform().getBaseWaterWalkSpd();

		return super.getWaterWalkSpd();
	}

	public double getFlyRunSpd()
	{
		if(getOwner().isMounted())
			return getOwner().getMount().getWaterRunSpdOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseFlyRunSpd() > 0)
			return getOwner().getTransform().getBaseFlyRunSpd();

		return getOwner().getTemplate().getBaseFlyRunSpd();
	}

	public double getFlyWalkSpd()
	{
		if(getOwner().isMounted())
			return getOwner().getMount().getFlyWalkSpdOnRide();

		if(getOwner().isTransformed() && getOwner().getTransform().getBaseFlyWalkSpd() > 0)
			return getOwner().getTransform().getBaseFlyWalkSpd();

		return getOwner().getTemplate().getBaseFlyWalkSpd();
	}

	public double getRideRunSpd()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseRideRunSpd() > 0)
			return getOwner().getTransform().getBaseRideRunSpd();

		return getOwner().getTemplate().getBaseRideRunSpd();
	}

	public double getRideWalkSpd()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseRideWalkSpd() > 0)
			return getOwner().getTransform().getBaseRideWalkSpd();

		return getOwner().getTemplate().getBaseRideWalkSpd();
	}

	@Override
	public double getCollisionRadius()
	{
		if(getOwner().isMounted())
		{
			final int mountTemplate = getOwner().getMountNpcId();
			if(mountTemplate != 0)
			{
				final NpcTemplate mountNpcTemplate = NpcHolder.getInstance().getTemplate(mountTemplate);
				if(mountNpcTemplate != null)
					return mountNpcTemplate.getCollisionRadius();
			}
		}

		if(getOwner().isVisualTransformed() && getOwner().getVisualTransform().getCollisionRadius() > 0)
			return getOwner().getVisualTransform().getCollisionRadius();

		return getOwner().getBaseTemplate().getCollisionRadius();
	}

	@Override
	public double getCollisionHeight()
	{
		if(getOwner().isMounted())
		{
			final int mountTemplate = getOwner().getMountNpcId();
			if(mountTemplate != 0)
			{
				final NpcTemplate mountNpcTemplate = NpcHolder.getInstance().getTemplate(mountTemplate);
				if(mountNpcTemplate != null)
					return mountNpcTemplate.getCollisionHeight();
			}
		}

		if(getOwner().isVisualTransformed() && getOwner().getVisualTransform().getCollisionHeight() > 0)
			return getOwner().getVisualTransform().getCollisionHeight();

		return getOwner().getBaseTemplate().getCollisionHeight();
	}

	@Override
	public WeaponType getAttackType()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseAttackType() != WeaponType.NONE)
			return getOwner().getTransform().getBaseAttackType();

		return super.getAttackType();
	}

	@Override
	public int getRandDam()
	{
		if(getOwner().isTransformed() && getOwner().getTransform().getBaseRandDam() > 0)
			return getOwner().getTransform().getBaseRandDam();

		return super.getRandDam();
	}

	public double getBreathBonus()
	{
		return getOwner().getTemplate().getBaseBreathBonus();
	}

	public double getSafeFallHeight()
	{
		return getOwner().getTemplate().getBaseSafeFallHeight();
	}

	public int getLUC()
	{
		return getOwner().getTemplate().getBaseLUC();
	}

	public int getCHA()
	{
		return getOwner().getTemplate().getBaseCHA();
	}
}