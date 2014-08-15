package l2s.gameserver.templates.npc;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.lang.reflect.Constructor;

import l2s.gameserver.ai.FakePlayerAI;
import l2s.gameserver.data.xml.holder.PlayerTemplateHolder;
import l2s.gameserver.idfactory.IdFactory;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.model.instances.FakePlayerInstance;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2s.gameserver.templates.player.PlayerTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public final class FakePlayerTemplate extends NpcTemplate
{
	private static final Logger _log = LoggerFactory.getLogger(FakePlayerTemplate.class);

	private final int _id;
	private final ClassId _classId;
	private final Sex _sex;
	private final boolean _isNoble;
	private final int _nameColor;
	private final int _titleColor;
	private final int _hairStyle;
	private final int _hairColor;
	private final int _faceType;

	public final int chest;
	public final int legs;
	public final int feet;
	public final int gloves;
	public final int back;
	public final int hat;
	public final int mask;

	private final TIntIntMap _buffs = new TIntIntHashMap();

	private final PlayerTemplate _playerTemplate;

	private FakePlayerInstance _spawned = null;

	private Class<FakePlayerInstance> _classType = FakePlayerInstance.class;

	@SuppressWarnings("unchecked")
	private Constructor<FakePlayerInstance> _constructorType = (Constructor<FakePlayerInstance>) FakePlayerInstance.class.getConstructors()[0];

	public FakePlayerTemplate(StatsSet set)
	{
		super(set);

		_id = set.getInteger("id");

		_classId = set.getEnum("class_id", ClassId.class);
		_sex = set.getEnum("sex", Sex.class);
		_isNoble = set.getBool("is_noble", false);
		_nameColor = Integer.decode("0x" + set.getString("name_color", "FFFFFF"));
		_titleColor = Integer.decode("0x" + set.getString("title_color", "FFFF77"));
		_hairStyle = set.getInteger("hair_style", 0);
		_hairColor = set.getInteger("hair_color", 0);
		_faceType = set.getInteger("face_type", 0);

		chest = set.getInteger("chest", 0);
		legs = set.getInteger("legs", 0);
		feet = set.getInteger("feet", 0);
		gloves = set.getInteger("gloves", 0);
		back = set.getInteger("back", 0);
		hat = set.getInteger("hat", 0);
		mask = set.getInteger("mask", 0);

		_playerTemplate = PlayerTemplateHolder.getInstance().getPlayerTemplate(_classId.getRace(), _classId, _sex);
	}

	public final FakePlayerInstance getSpawned()
	{
		return _spawned;
	}

	public final void setSpawned(FakePlayerInstance player)
	{
		_spawned = player;
	}

	public final int getPlayerId()
	{
		return _id;
	}

	public final ClassId getClassId()
	{
		return _classId;
	}

	public final Sex getSex()
	{
		return _sex;
	}

	public final boolean isNoble()
	{
		return _isNoble;
	}

	public final int getNameColor()
	{
		return _nameColor;
	}

	public final int getTitleColor()
	{
		return _titleColor;
	}

	public final int getHairStyle()
	{
		return _hairStyle;
	}

	public final int getHairColor()
	{
		return _hairColor;
	}

	public final int getFaceType()
	{
		return _faceType;
	}

	public void addBuff(int skillId, int skillLevel)
	{
		_buffs.put(skillId, skillLevel);
	}

	public TIntIntMap getBuffs()
	{
		return _buffs;
	}

	@Override
	public int getBaseINT()
	{
		return _playerTemplate.getBaseINT();
	}

	@Override
	public int getBaseSTR()
	{
		return _playerTemplate.getBaseSTR();
	}

	@Override
	public int getBaseCON()
	{
		return _playerTemplate.getBaseCON();
	}

	@Override
	public int getBaseMEN()
	{
		return _playerTemplate.getBaseMEN();
	}

	@Override
	public int getBaseDEX()
	{
		return _playerTemplate.getBaseDEX();
	}

	@Override
	public int getBaseWIT()
	{
		return _playerTemplate.getBaseWIT();
	}

	@Override
	public double getBaseHpMax(int level)
	{
		return _classId.getBaseHp(level);
	}

	@Override
	public double getBaseCpMax(int level)
	{
		return _classId.getBaseCp(level);
	}

	@Override
	public double getBaseMpMax(int level)
	{
		return _classId.getBaseMp(level);
	}

	@Override
	public double getBaseHpReg(int level)
	{
		return _playerTemplate.getBaseHpReg(level);
	}

	@Override
	public double getBaseMpReg(int level)
	{
		return _playerTemplate.getBaseMpReg(level);
	}

	@Override
	public double getBaseCpReg(int level)
	{
		return _playerTemplate.getBaseCpReg(level);
	}

	@Override
	public double getBasePAtk()
	{
		return _playerTemplate.getBasePAtk();
	}

	@Override
	public double getBaseMAtk()
	{
		return _playerTemplate.getBaseMAtk();
	}

	@Override
	public double getBasePDef()
	{
		return _playerTemplate.getBasePDef();
	}

	@Override
	public double getBaseMDef()
	{
		return _playerTemplate.getBaseMDef();
	}

	@Override
	public double getBasePAtkSpd()
	{
		return _playerTemplate.getBasePAtkSpd();
	}

	@Override
	public double getBaseMAtkSpd()
	{
		return _playerTemplate.getBaseMAtkSpd();
	}

	@Override
	public double getBaseShldDef()
	{
		return _playerTemplate.getBaseShldDef();
	}

	@Override
	public int getBaseAtkRange()
	{
		return _playerTemplate.getBaseAtkRange();
	}

	@Override
	public double getBaseShldRate()
	{
		return _playerTemplate.getBaseShldRate();
	}

	@Override
	public double getBasePCritRate()
	{
		return _playerTemplate.getBasePCritRate();
	}

	@Override
	public double getBaseMCritRate()
	{
		return _playerTemplate.getBaseMCritRate();
	}

	@Override
	public double getBaseRunSpd()
	{
		return _playerTemplate.getBaseRunSpd();
	}

	@Override
	public double getBaseWalkSpd()
	{
		return _playerTemplate.getBaseWalkSpd();
	}

	@Override
	public double getBaseWaterRunSpd()
	{
		return _playerTemplate.getBaseWaterRunSpd();
	}

	@Override
	public double getBaseWaterWalkSpd()
	{
		return _playerTemplate.getBaseWaterWalkSpd();
	}

	@Override
	public int[] getBaseAttributeAttack()
	{
		return _playerTemplate.getBaseAttributeAttack();
	}

	@Override
	public int[] getBaseAttributeDefence()
	{
		return _playerTemplate.getBaseAttributeDefence();
	}

	@Override
	public double getCollisionRadius()
	{
		return _playerTemplate.getCollisionRadius();
	}

	@Override
	public double getCollisionHeight()
	{
		return _playerTemplate.getCollisionHeight();
	}

	@Override
	public WeaponType getBaseAttackType()
	{
		return _playerTemplate.getBaseAttackType();
	}

	@Override
	public Class<? extends FakePlayerInstance> getInstanceClass()
	{
		return _classType;
	}

	@Override
	public Constructor<? extends FakePlayerInstance> getInstanceConstructor()
	{
		return _constructorType;
	}

	@Override
	public FakePlayerInstance getNewInstance()
	{
		try
		{
			return _constructorType.newInstance(IdFactory.getInstance().getNextId(), this);
		}
		catch(Exception e)
		{
			_log.error("Unable to create instance of fake player " + getPlayerId(), e);
		}

		return null;
	}

	@Override
	protected void setType(String type)
	{
		//
	}

	@Override
	protected void setAI(String ai)
	{
		//
	}
}