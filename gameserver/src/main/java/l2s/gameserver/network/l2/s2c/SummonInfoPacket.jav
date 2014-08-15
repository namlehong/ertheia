package l2s.gameserver.network.l2.s2c;

import org.apache.commons.lang3.StringUtils;

import l2s.gameserver.Config;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.utils.Location;

/**
 * @reworked Bonux
 */
public class SummonInfoPacket extends L2GameServerPacket
{
	private static final int IS_IN_COMBAT = 1 << 0;
	private static final int IS_ALIKE_DEAD = 1 << 1;
	private static final int IS_TARGETABLE = 1 << 2;
	private static final int IS_SHOW_NAME = 1 << 3;

	private boolean can_writeImpl = false;

	private int _npcObjId, _npcId, _running, _showSpawnAnimation;
	private int _runSpd, _walkSpd, _mAtkSpd, _pAtkSpd, _rHand, _lHand, _enchantEffect;
	private int _karma, _pvpFlag, _clanId, _clanCrestId, _allyId, _allyCrestId, _formId, _titleColor;
	private double _collisionHeight, _collisionRadius, _currentCollisionHeight, _currentCollisionRadius;
	private double _atkSpdMul, _runSpdMul;
	private boolean _isAttackable, _isNameAbove, _isFlying, _isInWater;
	private Location _loc;
	private String _name = StringUtils.EMPTY;
	private String _title = StringUtils.EMPTY;
	private int _state;
	private NpcString _nameNpcString = NpcString.NONE;
	private NpcString _titleNpcString = NpcString.NONE;
	private TeamType _team;
	private int _currentHP, _currentMP, _currentCP, _maxHP, _maxMP, _maxCP, _transformId;
	private AbnormalEffect[] _abnormalEffects;
	private int _flags;

	public SummonInfoPacket(Servitor cha, Creature attacker)
	{
		if(cha.getPlayer() != null && cha.getPlayer().isInvisible())
			return;

		_npcId = cha.getNpcId();

		_isAttackable = cha.isAutoAttackable(attacker);

		_rHand = 0;
		_lHand = 0;

		_name = cha.getName();
		_title = cha.getTitle();
		_showSpawnAnimation = cha.getSpawnAnimation();

		_collisionHeight = cha.getCollisionHeight();
		_collisionRadius = cha.getCollisionRadius();
		_currentCollisionHeight = cha.getCurrentCollisionHeight();
		_currentCollisionRadius = cha.getCurrentCollisionRadius();

		_npcObjId = cha.getObjectId();
		_loc = cha.getLoc();
		_mAtkSpd = cha.getMAtkSpd();
		//
		Clan clan = cha.getClan();
		Alliance alliance = clan == null ? null : clan.getAlliance();
		//
		_clanId = clan == null ? 0 : clan.getClanId();
		_clanCrestId = clan == null ? 0 : clan.getCrestId();
		//
		_allyId = alliance == null ? 0 : alliance.getAllyId();
		_allyCrestId = alliance == null ? 0 : alliance.getAllyCrestId();

		_runSpd = cha.getRunSpeed();
		_walkSpd = cha.getWalkSpeed();
		_karma = cha.getKarma();
		_pvpFlag = cha.getPvpFlag();
		_pAtkSpd = cha.getPAtkSpd();
		_running = cha.isRunning() ? 1 : 0;
		_abnormalEffects = cha.getAbnormalEffectsArray();
		_isFlying = cha.isFlying();
		_isInWater = cha.isInWater();
		_team = cha.getTeam();
		_formId = cha.getFormId();
		_isNameAbove = cha.isNameAbove();
		_titleColor = cha.isServitor() ? 1 : 0;

		_currentHP = (int) cha.getCurrentHp();
		_currentMP = (int) cha.getCurrentMp();
		_currentCP = (int) cha.getCurrentCp();
		_maxHP = cha.getMaxHp();
		_maxMP = cha.getMaxMp();
		_maxCP = cha.getMaxCp();

		_atkSpdMul = cha.getAttackSpeedMultiplier();;
		_runSpdMul = cha.getMovementSpeedMultiplier();

		_transformId = cha.getVisualTransformId();

		_enchantEffect = cha.getEnchantEffect();

		if(cha.isInCombat())
			_flags |= IS_IN_COMBAT;

		if(cha.isAlikeDead())
			_flags |= IS_ALIKE_DEAD;

		if(cha.isTargetable(attacker))
			_flags |= IS_TARGETABLE;

		_flags |= IS_SHOW_NAME;

		can_writeImpl = true;
	}

	public SummonInfoPacket update()
	{
		_showSpawnAnimation = 1;
		return this;
	}

	@Override
	protected final void writeImpl()
	{
		if(!can_writeImpl)
			return;

		writeD(_npcObjId);
		writeC(_showSpawnAnimation); //  0=teleported  1=default   2=summoned
		writeC(0x25);
		writeC(_isAttackable ? 1 : 0);

		writeC(0xED);
		writeC(0xBF);
		writeC(0x4F);
		writeC(0x02);
		writeC(0x6C);

		writeC(7 + _title.length() * 2);

		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);

		writeCutS(_title);

		writeH(58);
		writeD(_npcId + 1000000); // npctype id c4
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z + Config.CLIENT_Z_SHIFT);
		writeD(_loc.h);
		writeD(_mAtkSpd);
		writeD(_pAtkSpd);
		writeCutF(_runSpdMul);
		writeCutF(_atkSpdMul);

		writeC(0x01); // UNK
		writeC(_running);

		writeH(0x00);
		writeH(_form);
		writeD(0x00);
		writeD(0x00);
		writeH(0x00);

		writeC(_pvpFlag);
		writeD(_karma);

		writeC(_flags);

		writeH(_abnormalEffects.length);
		for(AbnormalEffect abnormal : _abnormalEffects)
			writeH(abnormal.ordinal());
	}
}