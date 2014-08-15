package l2s.gameserver.model.actor.instances.player;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.base.SubClassType;

public class SubClass
{
	public static final int CERTIFICATION_65 = 1 << 0;
	public static final int CERTIFICATION_70 = 1 << 1;
	public static final int CERTIFICATION_75 = 1 << 2;
	public static final int CERTIFICATION_80 = 1 << 3;

	public static final int DUALCERTIFICATION_85 = 1 << 0;
	public static final int DUALCERTIFICATION_90 = 1 << 1;
	public static final int DUALCERTIFICATION_95 = 1 << 2;
	public static final int DUALCERTIFICATION_99 = 1 << 3;

	private final Player _owner;

	private int _classId = 0;
	private int _defaultClassId = 0;
	private int _index = 1;

	private boolean _active = false;
	private SubClassType _type = SubClassType.BASE_CLASS;

	private int _level = 1;
	private long _exp = 0;
	private int _sp = 0;

	private int _maxLvl = Experience.getMaxLevel();
	private long _minExp = 0;
	private long _maxExp = Experience.LEVEL[_maxLvl + 1] - 1;

	private int _certification;
	private int _dualCertification;

	private double _hp = 1;
	private double _mp = 1;
	private double _cp = 1;

	private int _vitality = Player.MAX_VITALITY_POINTS;
	private int _usedVitalityPotions = 0;

	private int _abilitiesPoints = 0;

	public SubClass(Player owner)
	{
		_owner = owner;
	}

	public int getClassId()
	{
		return _classId;
	}

	public int getDefaultClassId()
	{
		return _defaultClassId;
	}

	public long getExp()
	{
		return _exp;
	}

	public long getMaxExp()
	{
		return _maxExp;
	}

	public void addExp(long val, boolean delevel)
	{
		setExp(_exp + val, delevel);
	}

	public int getSp()
	{
		return _sp;
	}

	public void addSp(long val)
	{
		setSp(_sp + val);
	}

	public int getLevel()
	{
		return _level;
	}

	public void setClassId(int id)
	{
		if(_classId == id)
			return;

		_classId = id;
	}

	public void setDefaultClassId(int id)
	{
		_defaultClassId = id;
	}

	public void setExp(long val, boolean delevel)
	{
		if(delevel)
			_exp = Math.min(Math.max(_minExp, val), _maxExp);
		else
			_exp = Math.min(Math.max(Experience.LEVEL[_level], val), _maxExp);
		_level = Experience.getLevel(_exp);
	}

	public void setSp(long spValue)
	{
		_sp = (int) Math.min(Math.max(0, spValue), Integer.MAX_VALUE);
	}

	public void setHp(double hpValue)
	{
		_hp = Math.max(0., hpValue);
	}

	public double getHp()
	{
		return _hp;
	}

	public void setMp(final double mpValue)
	{
		_mp = Math.max(0., mpValue);
	}

	public double getMp()
	{
		return _mp;
	}

	public void setCp(final double cpValue)
	{
		_cp = Math.max(0., cpValue);
	}

	public double getCp()
	{
		return _cp;
	}

	public void setActive(final boolean active)
	{
		_active = active;
	}

	public boolean isActive()
	{
		return _active;
	}

	public void setType(final SubClassType type)
	{
		if(_type == type)
			return;

		_type = type;

		if(_type == SubClassType.SUBCLASS)
		{
			_maxLvl = Experience.getMaxSubLevel();
			_minExp = Experience.LEVEL[Config.SUB_START_LEVEL];
			_maxExp = Experience.LEVEL[_maxLvl + 1] - 1;
			_level = Math.min(Math.max(Config.SUB_START_LEVEL, _level), _maxLvl);
		}
		else
		{
			if(_type == SubClassType.DUAL_SUBCLASS)
				_owner.getSubClassList().setDualSubClass(this);

			_maxLvl = Experience.getMaxLevel();
			_minExp = 0;
			_maxExp = Experience.LEVEL[_maxLvl + 1] - 1;
			_level = Math.min(Math.max(1, _level), _maxLvl);
		}
		_exp = Math.min(Math.max(Experience.LEVEL[_level], _exp), _maxExp);
	}

	public SubClassType getType()
	{
		return _type;
	}

	public boolean isBase()
	{
		return _type == SubClassType.BASE_CLASS;
	}

	public boolean isDual()
	{
		return _type == SubClassType.DUAL_SUBCLASS;
	}

	public int getCertification()
	{
		return _certification;
	}

	public void setCertification(int certification)
	{
		_certification = certification;
	}

	public void addCertification(int c)
	{
		_certification |= c;
	}

	public boolean isCertificationGet(int v)
	{
		return (_certification & v) == v;
	}

	public int getDualCertification()
	{
		return _dualCertification;
	}

	public void setDualCertification(int certification)
	{
		_dualCertification = certification;
	}

	public void addDualCertification(int c)
	{
		_dualCertification |= c;
	}

	public boolean isDualCertificationGet(int v)
	{
		return (_dualCertification & v) == v;
	}

	@Override
	public String toString()
	{
		return ClassId.VALUES[_classId].toString() + " " + _level;
	}

	public int getMaxLevel()
	{
		return _maxLvl;
	}

	public void setIndex(int i)
	{
		_index = i;
	}

	public int getIndex()
	{
		return _index;
	}

	public void setVitality(int val)
	{
		_vitality = val;
		if(_vitality > Player.MAX_VITALITY_POINTS)
			_vitality = Player.MAX_VITALITY_POINTS;
		if(_vitality < 0)
			_vitality = 0;
	}

	public int getVitality()
	{
		return _vitality;
	}

	public void setUsedVitalityPotions(int val)
	{
		_usedVitalityPotions = val;
		if(_usedVitalityPotions < 0)
			_usedVitalityPotions = 0;
	}

	public int getUsedVitalityPotions()
	{
		return _usedVitalityPotions;
	}

	public void setAbilitiesPoints(int value)
	{
		_abilitiesPoints = value;
	}

	public int getAbilitiesPoints()
	{
		return _abilitiesPoints;
	}
}