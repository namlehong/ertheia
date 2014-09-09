package l2s.gameserver.templates;

import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;

public class CreatureTemplate
{
	private final static int[] EMPTY_ATTRIBUTES = new int[6];

	private int _baseINT;
	private int _baseSTR;
	private int _baseCON;
	private int _baseMEN;
	private int _baseDEX;
	private int _baseWIT;

	private int _baseAtkRange;
	private int _baseRandDam;

	private double _baseHpMax;
	private double _baseCpMax;
	private double _baseMpMax;

	/** HP Regen base */
	private double _baseHpReg;

	/** MP Regen base */
	private double _baseMpReg;

	/** CP Regen base */
	private double _baseCpReg;

	private double _basePAtk;
	private double _baseMAtk;
	private double _basePDef;
	private double _baseMDef;
	private double _basePAtkSpd;
	private double _baseMAtkSpd;
	private double _baseShldDef;
	private double _baseShldRate;
	private double _basePCritRate;
	private double _baseMCritRate;
	private double _baseRunSpd;
	private double _baseWalkSpd;
	private double _baseWaterRunSpd;
	private double _baseWaterWalkSpd;
	private double _baseFlyRunSpd;
	private double _baseFlyWalkSpd;

	private int[] _baseAttributeAttack;
	private int[] _baseAttributeDefence;

	private double _collisionRadius;
	private double _collisionHeight;

	private final WeaponType _baseAttackType;

	public CreatureTemplate(StatsSet set)
	{
		_baseINT = set.getInteger("baseINT", 1);
		_baseSTR = set.getInteger("baseSTR", 1);
		_baseCON = set.getInteger("baseCON", 1);
		_baseMEN = set.getInteger("baseMEN", 1);
		_baseDEX = set.getInteger("baseDEX", 1);
		_baseWIT = set.getInteger("baseWIT", 1);
		_baseHpMax = set.getDouble("baseHpMax", 0);
		_baseCpMax = set.getDouble("baseCpMax", 0);
		_baseMpMax = set.getDouble("baseMpMax", 0);
		_baseHpReg = set.getDouble("baseHpReg", 1.);
		_baseCpReg = set.getDouble("baseCpReg", 1.);
		_baseMpReg = set.getDouble("baseMpReg", 1.);
		_basePAtk = set.getDouble("basePAtk", 0);
		_baseMAtk = set.getDouble("baseMAtk", 0);
		_basePDef = set.getDouble("basePDef", 0);
		_baseMDef = set.getDouble("baseMDef", 0);
		_basePAtkSpd = set.getDouble("basePAtkSpd", 0);
		_baseMAtkSpd = set.getDouble("baseMAtkSpd", 333);
		_baseShldDef = set.getDouble("baseShldDef", 0);
		_baseAtkRange = set.getInteger("baseAtkRange", 0);
		_baseRandDam = set.getInteger("baseRandDam", 0);
		_baseShldRate = set.getDouble("baseShldRate", 0);
		_basePCritRate = set.getDouble("basePCritRate", 0);
		_baseMCritRate = set.getDouble("baseMCritRate", 0);
		_baseRunSpd = set.getDouble("baseRunSpd", 0);
		_baseWalkSpd = set.getDouble("baseWalkSpd", 0);
		_baseWaterRunSpd = set.getDouble("baseWaterRunSpd", 50);
		_baseWaterWalkSpd = set.getDouble("baseWaterWalkSpd", 50);
		_baseFlyRunSpd = set.getDouble("baseFlyRunSpd", 0);
		_baseFlyWalkSpd = set.getDouble("baseFlyWalkSpd", 0);
		_baseAttributeAttack = set.getIntegerArray("baseAttributeAttack", EMPTY_ATTRIBUTES);
		_baseAttributeDefence = set.getIntegerArray("baseAttributeDefence", EMPTY_ATTRIBUTES);
		_collisionRadius = set.getDouble("collision_radius", 5);
		_collisionHeight = set.getDouble("collision_height", 5);
		_baseAttackType = WeaponType.valueOf(set.getString("baseAttackType", "FIST").toUpperCase());
	}

	public int getId()
	{
		return 0;
	}

	public int getBaseINT()
	{
		return _baseINT;
	}

	public int getBaseSTR()
	{
		return _baseSTR;
	}

	public int getBaseCON()
	{
		return _baseCON;
	}

	public int getBaseMEN()
	{
		return _baseMEN;
	}

	public int getBaseDEX()
	{
		return _baseDEX;
	}

	public int getBaseWIT()
	{
		return _baseWIT;
	}

	public double getBaseHpMax(int level)
	{
		return _baseHpMax;
	}

	public double getBaseMpMax(int level)
	{
		return _baseMpMax;
	}

	public double getBaseCpMax(int level)
	{
		return _baseCpMax;
	}

	public double getBaseHpReg(int level)
	{
		return _baseHpReg;
	}

	public double getBaseMpReg(int level)
	{
		return _baseMpReg;
	}

	public double getBaseCpReg(int level)
	{
		return _baseCpReg;
	}

	public double getBasePAtk()
	{
		return _basePAtk;
	}

	public double getBaseMAtk()
	{
		return _baseMAtk;
	}

	public double getBasePDef()
	{
		return _basePDef;
	}

	public double getBaseMDef()
	{
		return _baseMDef;
	}

	public double getBasePAtkSpd()
	{
		return _basePAtkSpd;
	}

	public double getBaseMAtkSpd()
	{
		return _baseMAtkSpd;
	}

	public double getBaseShldDef()
	{
		return _baseShldDef;
	}

	public int getBaseAtkRange()
	{
		return _baseAtkRange;
	}

	public int getBaseRandDam()
	{
		return _baseRandDam;
	}

	public double getBaseShldRate()
	{
		return _baseShldRate;
	}

	public double getBasePCritRate()
	{
		return _basePCritRate;
	}

	public double getBaseMCritRate()
	{
		return _baseMCritRate;
	}

	public double getBaseRunSpd()
	{
		return _baseRunSpd;
	}

	public double getBaseWalkSpd()
	{
		return _baseWalkSpd;
	}

	public double getBaseWaterRunSpd()
	{
		return _baseWaterRunSpd;
	}

	public double getBaseWaterWalkSpd()
	{
		return _baseWaterWalkSpd;
	}

	public double getBaseFlyRunSpd()
	{
		return _baseFlyRunSpd;
	}

	public double getBaseFlyWalkSpd()
	{
		return _baseFlyWalkSpd;
	}

	public int[] getBaseAttributeAttack()
	{
		return _baseAttributeAttack;
	}

	public int[] getBaseAttributeDefence()
	{
		return _baseAttributeDefence;
	}

	public double getCollisionRadius()
	{
		return _collisionRadius;
	}

	public double getCollisionHeight()
	{
		return _collisionHeight;
	}

	public WeaponType getBaseAttackType()
	{
		return _baseAttackType;
	}

	public static StatsSet getEmptyStatsSet()
	{
		return new StatsSet();
	}
	
	/**
	 * New Method
	 */

	public void update(StatsSet set)
	{
		_baseINT = set.getInteger("baseINT", _baseINT);
		_baseSTR = set.getInteger("baseSTR", _baseSTR);
		_baseCON = set.getInteger("baseCON", _baseCON);
		_baseMEN = set.getInteger("baseMEN", _baseMEN);
		_baseDEX = set.getInteger("baseDEX", _baseDEX);
		_baseWIT = set.getInteger("baseWIT", _baseWIT);
		_baseHpMax = set.getDouble("baseHpMax", _baseHpMax);
		_baseCpMax = set.getDouble("baseCpMax", _baseCpMax);
		_baseMpMax = set.getDouble("baseMpMax", _baseMpMax);
		_baseHpReg = set.getDouble("baseHpReg", _baseHpReg);
		_baseCpReg = set.getDouble("baseCpReg", _baseCpReg);
		_baseMpReg = set.getDouble("baseMpReg", _baseMpReg);
		_basePAtk = set.getDouble("basePAtk", _basePAtk);
		_baseMAtk = set.getDouble("baseMAtk", _baseMAtk);
		_basePDef = set.getDouble("basePDef", _basePDef);
		_baseMDef = set.getDouble("baseMDef", _baseMDef);
		_basePAtkSpd = set.getDouble("basePAtkSpd", _basePAtkSpd);
		_baseMAtkSpd = set.getDouble("baseMAtkSpd", _baseMAtkSpd);
		_baseShldDef = set.getDouble("baseShldDef", _baseShldDef);
		_baseAtkRange = set.getInteger("baseAtkRange", _baseAtkRange);
		_baseRandDam = set.getInteger("baseRandDam", _baseRandDam);
		_baseShldRate = set.getDouble("baseShldRate", _baseShldRate);
		_basePCritRate = set.getDouble("basePCritRate", _basePCritRate);
		_baseMCritRate = set.getDouble("baseMCritRate", _baseMCritRate);
//		_baseRunSpd = set.getDouble("baseRunSpd", _baseRunSpd);
//		_baseWalkSpd = set.getDouble("baseWalkSpd", _baseWalkSpd);
//		_baseWaterRunSpd = set.getDouble("baseWaterRunSpd", _baseWaterRunSpd);
//		_baseWaterWalkSpd = set.getDouble("baseWaterWalkSpd", _baseWaterWalkSpd);
//		_baseFlyRunSpd = set.getDouble("baseFlyRunSpd", _baseFlyRunSpd);
//		_baseFlyWalkSpd = set.getDouble("baseFlyWalkSpd", _baseFlyWalkSpd);
		_baseAttributeAttack = set.getIntegerArray("baseAttributeAttack", _baseAttributeAttack);
		_baseAttributeDefence = set.getIntegerArray("baseAttributeDefence", _baseAttributeDefence);
		_collisionRadius = set.getDouble("collision_radius", _collisionRadius);
		_collisionHeight = set.getDouble("collision_height", _collisionHeight);
//		_baseAttackType = WeaponType.valueOf(set.getString("baseAttackType", _baseAttackType.toString()).toUpperCase());
	}
}