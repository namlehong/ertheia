package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.Config;
import l2s.gameserver.instancemanager.CursedWeaponsManager;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.instances.FakePlayerInstance;
import l2s.gameserver.model.items.Inventory;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.model.matching.MatchingRoom;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.skills.effects.EffectCubic;
import l2s.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public class FakePlayerInfo extends L2GameServerPacket
{
	private static final Logger _log = LoggerFactory.getLogger(FakePlayerInfo.class);

	private final Location _loc;

	private final int _objectId;
	private final String _name;
	private final int _race;
	private final int _sex;
	private final int _classId;
	private final int[] _inv;
	private final int _mAtkSpd;
	private final int _pAtkSpd;
	private final int _runSpd;
	private final int _walkSpd;
	private final int _swimRunSpd;
	private final int _swimWalkSpd;
	private final double _speedMove;
	private final double _speedAttack;
	private final double _colRadius;
	private final double _colHeight;
	private final int _hairStyle;
	private final int _hairColor;
	private final int _faceType;
	private final String _title;
	private final int _sit;
	private final int _run;
	private final int _combat;
	private final int _dead;
	private final int _enchant;
	private final int _noble;
	private final int _nameColor;
	private final int _pledgeRank;
	private final int _titleColor;
	private final int _curCp;
	private final int _curHp;
	private final int _maxHp;
	private final int _curMp;
	private final int _maxMp;
	private AbnormalEffect[] _abnormalEffects;

	public FakePlayerInfo(FakePlayerInstance player)
	{
		_objectId = player.getObjectId();
		_loc = player.getLoc();

		_name = player.getName();
		_nameColor = player.getNameColor();

		_title = player.getTitle();
		_titleColor = player.getTitleColor();

		_enchant = player.getEnchantEffect();

		_inv = new int[PcInventory.PAPERDOLL_MAX];
		for(int PAPERDOLL_ID : PAPERDOLL_ORDER)
		{
			switch(PAPERDOLL_ID)
			{
				case Inventory.PAPERDOLL_RHAND:
					_inv[PAPERDOLL_ID] = player.getTemplate().rhand;
					break;
				case Inventory.PAPERDOLL_LHAND:
					_inv[PAPERDOLL_ID] = player.getTemplate().lhand;
					break;
				case Inventory.PAPERDOLL_GLOVES:
					_inv[PAPERDOLL_ID] = player.getTemplate().gloves;
					break;
				case Inventory.PAPERDOLL_CHEST:
					_inv[PAPERDOLL_ID] = player.getTemplate().chest;
					break;
				case Inventory.PAPERDOLL_LEGS:
					_inv[PAPERDOLL_ID] = player.getTemplate().legs;
					break;
				case Inventory.PAPERDOLL_FEET:
					_inv[PAPERDOLL_ID] = player.getTemplate().feet;
					break;
				case Inventory.PAPERDOLL_BACK:
					_inv[PAPERDOLL_ID] = player.getTemplate().back;
					break;
				case Inventory.PAPERDOLL_LRHAND:
					if(player.getTemplate().rhand == player.getTemplate().lhand)
						_inv[PAPERDOLL_ID] = player.getTemplate().rhand;
					break;
				case Inventory.PAPERDOLL_HAIR:
					_inv[PAPERDOLL_ID] = player.getTemplate().hat;
					break;
				case Inventory.PAPERDOLL_DHAIR:
					_inv[PAPERDOLL_ID] = player.getTemplate().mask;
					break;
			}
		}

		_mAtkSpd = player.getMAtkSpd();
		_pAtkSpd = player.getPAtkSpd();
		_speedMove = player.getMovementSpeedMultiplier();
		_runSpd = (int) (player.getRunSpeed() / _speedMove);
		_walkSpd = (int) (player.getWalkSpeed() / _speedMove);

		_swimRunSpd = player.getSwimRunSpeed();
		_swimWalkSpd = player.getSwimWalkSpeed();
		_race = player.getRace().ordinal();
		_sex = player.getSex().ordinal();
		_classId = player.getClassId().getId();

		_speedAttack = player.getAttackSpeedMultiplier();
		_colRadius = player.getCollisionRadius();
		_colHeight = player.getCollisionHeight();

		_hairStyle = player.getHairStyle();
		_hairColor = player.getHairColor();
		_faceType = player.getFace();

		_sit = player.isSitting() ? 0 : 1; // standing = 1 sitting = 0
		_run = player.isRunning() ? 1 : 0; // running = 1 walking = 0
		_combat = player.isInCombat() ? 1 : 0;
		_dead = player.isAlikeDead() ? 1 : 0;
		_abnormalEffects = player.getAbnormalEffectsArray();

		_noble = player.isNoble() ? 1 : 0; // 0x01: symbol on char menu ctrl+I
		_pledgeRank = player.getPledgeRank().ordinal();

		_curHp = (int) player.getCurrentHp();
		_maxHp = player.getMaxHp();
		_curMp = (int) player.getCurrentMp();
		_maxMp = player.getMaxMp();
		_curCp = (int) player.getCurrentCp();
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z + Config.CLIENT_Z_SHIFT);
		writeD(0x00);
		writeD(_objectId);
		writeS(_name);
		writeD(_race);
		writeD(_sex);
		writeD(_classId);

		for(int PAPERDOLL_ID : PAPERDOLL_ORDER)
			writeD(_inv[PAPERDOLL_ID]);

		for(int PAPERDOLL_ID : PAPERDOLL_ORDER)
		{
			writeH(0x00);
			writeH(0x00);
		}

		writeD(0x00);
		writeD(0x00);

		writeD(0x00);
		writeD(0x00);

		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);

		writeD(_mAtkSpd);
		writeD(_pAtkSpd);

		writeD(0x00);

		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimRunSpd);
		writeD(_swimWalkSpd);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);

		writeF(_speedMove);
		writeF(_speedAttack);
		writeF(_colRadius);
		writeF(_colHeight);
		writeD(_hairStyle);
		writeD(_hairColor);
		writeD(_faceType);
		writeS(_title);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);

		writeC(_sit);
		writeC(_run);
		writeC(_combat);
		writeC(_dead);
		writeC(0x01);
		writeC(0x00); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
		writeC(0x00);
		writeH(0x00/*cubics.length*/);
		/*for(EffectCubic cubic : cubics)
			writeH(cubic == null ? 0 : cubic.getId());*/
		writeC(0x00); // find party members
		writeC(0x00);
		writeH(0x00);
		writeD(0x00);
		writeD(_classId);
		writeD(0x00);
		writeC(_enchant);

		writeC(0x00); // team circle around feet 1 = Blue, 2 = red

		writeD(0x00);
		writeC(_noble);
		writeC(0x00);

		writeC(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);

		writeD(_nameColor);
		writeD(_loc.h);
		writeD(_pledgeRank);
		writeD(0x00);
		writeD(_titleColor);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);

		writeD(0x01); // T2

		writeD(0x00);// Unknown1 (GOD)
		writeD(0x00);// Unknown2 (GOD)
		writeD(0x00);// Unknown3 (GOD)

		writeD(_curCp);
		writeD(_curHp);
		writeD(_maxHp);
		writeD(_curMp);
		writeD(_maxMp);

		writeD(0x00);// Unknown9 (GOD)
		writeC(0x00);// Unknown10 (GOD)
		writeD(0x00); // TAUTI

		writeD(_abnormalEffects.length);
		for(AbnormalEffect abnormal : _abnormalEffects)
			writeD(abnormal.ordinal());

		writeC(0x00); // TAUTI
	}

	public static final int[] PAPERDOLL_ORDER = {
			Inventory.PAPERDOLL_UNDER,
			Inventory.PAPERDOLL_HEAD,
			Inventory.PAPERDOLL_RHAND,
			Inventory.PAPERDOLL_LHAND,
			Inventory.PAPERDOLL_GLOVES,
			Inventory.PAPERDOLL_CHEST,
			Inventory.PAPERDOLL_LEGS,
			Inventory.PAPERDOLL_FEET,
			Inventory.PAPERDOLL_BACK,
			Inventory.PAPERDOLL_LRHAND,
			Inventory.PAPERDOLL_HAIR,
			Inventory.PAPERDOLL_DHAIR,
			Inventory.PAPERDOLL_RBRACELET,
			Inventory.PAPERDOLL_LBRACELET,
			Inventory.PAPERDOLL_DECO1,
			Inventory.PAPERDOLL_DECO2,
			Inventory.PAPERDOLL_DECO3,
			Inventory.PAPERDOLL_DECO4,
			Inventory.PAPERDOLL_DECO5,
			Inventory.PAPERDOLL_DECO6,
			Inventory.PAPERDOLL_BELT };
}