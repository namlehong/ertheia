package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.instancemanager.CursedWeaponsManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.base.InvisibleType;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.model.matching.MatchingRoom;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.skills.effects.EffectCubic;
import l2s.gameserver.utils.Location;

/**
 * @reworked by Bonux
 */
public class UIPacket extends L2GameServerPacket
{
	private boolean can_writeImpl = false, partyRoom;
	private int _runSpd, _walkSpd, _swimRunSpd, _swimWalkSpd, _flRunSpd, _flWalkSpd, _flyRunSpd, _flyWalkSpd, _relation;
	private double move_speed, attack_speed, col_radius, col_height;
	private Location _loc, _fishLoc;
	private int obj_id, vehicle_obj_id, _race, sex, base_class, level, curCp, maxCp, _enchant, _weaponFlag;
	private long _exp;
	private int curHp, maxHp, curMp, maxMp, curLoad, maxLoad, rec_left, rec_have;
	private int _str, _con, _dex, _int, _wit, _men, _luc, _cha, _sp, ClanPrivs, InventoryLimit;
	private int _patk, _patkspd, _pdef, _matk, _matkspd;
	private int _pEvasion, _pAccuracy, _pCrit, _mEvasion, _mAccuracy, _mCrit;
	private int _mdef, pvp_flag, karma, hair_style, hair_color, face, gm_commands, fame, vitality;
	private int clan_id, clan_crest_id, ally_id, ally_crest_id, large_clan_crest_id;
	private int private_store, can_crystalize, pk_kills, pvp_kills, class_id, agathion, _partySubstitute;
	private int noble, hero, mount_id, cw_level;
	private int name_color, running, pledge_class, pledge_type, title_color, transformation;
	private int defenceFire, defenceWater, defenceWind, defenceEarth, defenceHoly, defenceUnholy;
	private int mount_type;
	private String _name, title;
	private EffectCubic[] cubics;
	private Element attackElement;
	private int attackElementValue;
	private boolean isFlying, _allowMap;
	private int talismans;
	private int _jewelsLimit;
	private double _expPercent;
	private TeamType _team;
	private AbnormalEffect[] _abnormalEffects;
	private final int _raidPoints;

	public UIPacket(Player player)
	{
		if(player.isCursedWeaponEquipped())
		{
			_name = player.getCursedWeaponName(player);

			title = "";
			clan_crest_id = 0;
			ally_crest_id = 0;
			large_clan_crest_id = 0;
			cw_level = CursedWeaponsManager.getInstance().getLevel(player.getCursedWeaponEquippedId());
		}
		else
		{
			_name = player.getName();

			Clan clan = player.getClan();
			Alliance alliance = clan == null ? null : clan.getAlliance();
			//
			clan_id = clan == null ? 0 : clan.getClanId();
			clan_crest_id = clan == null ? 0 : clan.getCrestId();
			large_clan_crest_id = clan == null ? 0 : clan.getCrestLargeId();
			//
			ally_id = alliance == null ? 0 : alliance.getAllyId();
			ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();

			cw_level = 0;
			title = player.getTitle();
		}

		if(player.getPlayerAccess().GodMode && player.getInvisibleType() == InvisibleType.GM)
			title += "[I]";
		if(player.isPolymorphed())
			if(NpcHolder.getInstance().getTemplate(player.getPolyId()) != null)
				title += " - " + NpcHolder.getInstance().getTemplate(player.getPolyId()).name;
			else
				title += " - Polymorphed";

		if(player.isMounted())
		{
			_enchant = 0;
			mount_id = player.getMountNpcId() + 1000000;
			mount_type = player.getMountType().ordinal();
		}
		else
		{
			_enchant = player.getEnchantEffect();
			mount_id = 0;
			mount_type = 0;
		}

		_weaponFlag = player.getActiveWeaponInstance() == null ? 0x14 : 0x28;

		move_speed = player.getMovementSpeedMultiplier();
		_runSpd = (int) (player.getRunSpeed() / move_speed);
		_walkSpd = (int) (player.getWalkSpeed() / move_speed);

		_flRunSpd = 0; // TODO
		_flWalkSpd = 0; // TODO

		if(player.isFlying())
		{
			_flyRunSpd = _runSpd;
			_flyWalkSpd = _walkSpd;
		}
		else
		{
			_flyRunSpd = 0;
			_flyWalkSpd = 0;
		}

		_swimRunSpd = player.getSwimRunSpeed();
		_swimWalkSpd = player.getSwimWalkSpeed();

		if(player.getClan() != null)
		{
			_relation |= RelationChangedPacket.USER_RELATION_CLAN_MEMBER;
			if(player.isClanLeader())
				_relation |= RelationChangedPacket.USER_RELATION_CLAN_LEADER;
		}

		for(GlobalEvent e : player.getEvents())
			_relation = e.getUserRelation(player, _relation);

		_loc = player.getLoc();
		obj_id = player.getObjectId();
		vehicle_obj_id = player.isInBoat() ? player.getBoat().getBoatId() : 0x00;
		_race = player.getRace().ordinal();
		sex = player.getSex().ordinal();
		base_class = ClassId.VALUES[player.getBaseDefaultClassId()].getFirstParent(sex).getId();
		level = player.getLevel();
		_exp = player.getExp();
		_expPercent = Experience.getExpPercent(player.getLevel(), player.getExp());
		_str = player.getSTR();
		_dex = player.getDEX();
		_con = player.getCON();
		_int = player.getINT();
		_wit = player.getWIT();
		_men = player.getMEN();
		_luc = player.getLUC();
		_cha = player.getCHA();
		curHp = (int) player.getCurrentHp();
		maxHp = player.getMaxHp();
		curMp = (int) player.getCurrentMp();
		maxMp = player.getMaxMp();
		curLoad = player.getCurrentLoad();
		maxLoad = player.getMaxLoad();
		_sp = player.getIntSp();
		_patk = player.getPAtk(null);
		_patkspd = player.getPAtkSpd();
		_pdef = player.getPDef(null);
		_pEvasion = player.getPEvasionRate(null);
		_pAccuracy = player.getPAccuracy();
		_pCrit = player.getPCriticalHit(null);
		_mEvasion = player.getMEvasionRate(null);
		_mAccuracy = player.getMAccuracy();
		_mCrit = player.getMCriticalHit(null, null);
		_matk = player.getMAtk(null, null);
		_matkspd = player.getMAtkSpd();
		_mdef = player.getMDef(null, null);
		pvp_flag = player.getPvpFlag(); // 0=white, 1=purple, 2=purpleblink
		karma = player.getKarma();
		attack_speed = player.getAttackSpeedMultiplier();
		col_radius = player.getCollisionRadius();
		col_height = player.getCollisionHeight();
		hair_style = player.getBeautyHairStyle() > 0 ? player.getBeautyHairStyle() : player.getHairStyle();
		hair_color = player.getBeautyHairColor() > 0 ? player.getBeautyHairColor() : player.getHairColor();
		face = player.getBeautyFace() > 0 ? player.getBeautyFace() : player.getFace();
		gm_commands = player.isGM() || player.getPlayerAccess().CanUseAltG ? 1 : 0;
		// builder level активирует в клиенте админские команды
		clan_id = player.getClanId();
		ally_id = player.getAllyId();
		private_store = player.getPrivateStoreType();
		can_crystalize = player.getSkillLevel(Skill.SKILL_CRYSTALLIZE) > 0 ? 1 : 0;
		pk_kills = player.getPkKills();
		pvp_kills = player.getPvpKills();
		cubics = player.getCubics().toArray(new EffectCubic[player.getCubics().size()]);
		_abnormalEffects = player.getAbnormalEffectsArray();
		ClanPrivs = player.getClanPrivileges();
		rec_left = player.getRecomLeft(); //c2 recommendations remaining
		rec_have = player.getRecomHave(); //c2 recommendations received
		InventoryLimit = player.getInventoryLimit();
		class_id = player.getClassId().getId();
		maxCp = player.getMaxCp();
		curCp = (int) player.getCurrentCp();
		_team = player.getTeam();
		noble = player.isNoble() || player.isGM() && Config.GM_HERO_AURA ? 1 : 0; //0x01: symbol on char menu ctrl+I
		hero = player.isHero() || player.isGM() && Config.GM_HERO_AURA ? 1 : 0; //0x01: Hero Aura and symbol
		//fishing = _cha.isFishing() ? 1 : 0; // Fishing Mode
		_fishLoc = player.getFishLoc();
		name_color = player.getNameColor();
		running = player.isRunning() ? 0x01 : 0x00; //changes the Speed display on Status Window
		pledge_class = player.getPledgeRank().ordinal();
		pledge_type = player.getPledgeType();
		title_color = player.getTitleColor();
		transformation = player.getVisualTransformId();
		attackElement = player.getAttackElement();
		attackElementValue = player.getAttack(attackElement);
		defenceFire = player.getDefence(Element.FIRE);
		defenceWater = player.getDefence(Element.WATER);
		defenceWind = player.getDefence(Element.WIND);
		defenceEarth = player.getDefence(Element.EARTH);
		defenceHoly = player.getDefence(Element.HOLY);
		defenceUnholy = player.getDefence(Element.UNHOLY);
		agathion = player.getAgathionId();
		fame = player.getFame();
		vitality = player.getVitality();
		partyRoom = player.getMatchingRoom() != null && player.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING && player.getMatchingRoom().getLeader() == player;
		isFlying = player.isInFlyingTransform();
		talismans = player.getTalismanCount();
		_jewelsLimit = player.getJewelsLimit();
		_allowMap = player.isActionBlocked(Zone.BLOCKED_ACTION_MINIMAP);
		_partySubstitute = player.isPartySubstituteStarted()  ? 1 : 0;
		_raidPoints = 0;//TODO: RaidBossSpawnManager.getInstance().getPointsForOwnerId(obj_id);

		can_writeImpl = true;
	}

	@Override
	protected final void writeImpl()
	{
		if(!can_writeImpl)
			return;

		writeD(obj_id);

		writeD(372 + _name.length() * 2 + title.length() * 2);
		writeH(23);

		writeB(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF});

		writeD(_relation);
		writeH(16 + _name.length() * 2);
		writeH(_name.length());
		writeCutS(_name);
		writeC(gm_commands);
		writeC(_race);
		writeC(sex);
		writeD(base_class);
		writeD(class_id);
		writeC(level);

		writeH(18);
		writeH(_str);
		writeH(_dex);
		writeH(_con);
		writeH(_int);
		writeH(_wit);
		writeH(_men);
		writeH(_luc);
		writeH(_cha);

		writeH(14);
		writeD(maxHp);
		writeD(maxMp);
		writeD(maxCp);

		writeH(38);
		writeD(curHp);
		writeD(curMp);
		writeD(curCp);
		writeQ(_sp);
		writeQ(_exp);
		writeF(_expPercent);

		writeH(4);
		writeH(_enchant);

		writeH(15);
		writeD(hair_style);
		writeD(hair_color);
		writeD(face);
		writeC(0x00);  //переключения прически/головного убора

		writeH(6);
		writeC(mount_type);
		writeC(private_store);
		writeC(can_crystalize);
		writeC(0x00);

		writeH(56);
		writeH(_weaponFlag);
		writeD(_patk);
		writeD(_patkspd);
		writeD(_pdef);
		writeD(_pEvasion);
		writeD(_pAccuracy);
		writeD(_pCrit);
		writeD(_matk);
		writeD(_matkspd);
		writeD(_patkspd);
		writeD(_mEvasion);
		writeD(_mdef);
		writeD(_mAccuracy);
		writeD(_mCrit);

		writeH(14);
		writeH(defenceFire);
		writeH(defenceWater);
		writeH(defenceWind);
		writeH(defenceEarth);
		writeH(defenceHoly);
		writeH(defenceUnholy);

		writeH(18);
		writeD(_loc.x);
		writeD(_loc.y);
		writeD(_loc.z + Config.CLIENT_Z_SHIFT);
		writeD(vehicle_obj_id);

		writeH(18);
		writeH(_runSpd);
		writeH(_walkSpd);
		writeH(_swimRunSpd);
		writeH(_swimWalkSpd);
		writeH(_flRunSpd);
		writeH(_flWalkSpd);
		writeH(_flyRunSpd);
		writeH(_flyWalkSpd);

		writeH(18);
		writeF(move_speed);
		writeF(attack_speed);

		writeH(18);
		writeF(col_radius);
		writeF(col_height);

		writeH(5);
		writeC(attackElement.getId());
		writeH(attackElementValue);

		writeH(32 + title.length() * 2);
		writeH(title.length());
		writeCutS(title);
		writeH(pledge_type);
		writeD(clan_id);
		writeD(large_clan_crest_id);
		writeD(clan_crest_id);
		writeD(ClanPrivs);
		writeC(0x01);
		writeD(ally_id);
		writeD(ally_crest_id);
		writeC(partyRoom ? 0x01 : 0x00);

		writeH(22);
		writeC(pvp_flag);
		writeD(karma);
		writeC(noble);
		writeC(hero);
		writeC(pledge_class);
		writeD(pk_kills);
		writeD(pvp_kills);
		writeH(rec_left);
		writeH(rec_have);

		writeH(15);
		writeD(vitality);
		writeC(0x01);
		writeD(fame);
		writeD(0x00); // Рейдовые Очки

		writeH(9);
		writeC(talismans);
		writeC(_jewelsLimit);
		writeC(_team.ordinal());
		writeC(0); // Светиться вокруг персонажа красный пунтктирный круг.
		writeC(0);
		writeC(0);
		writeC(0);

		writeH(4);
		writeC(isFlying ? 0x02 : 0x00);
		writeC(running);

		writeH(10);
		writeD(name_color);
		writeD(title_color);

		writeH(9);
		writeD(0);
		writeH(InventoryLimit);
		writeC(0); //при 1 не показывает титул

		writeH(9);
		writeC(1);
		writeH(0);
		writeD(0);
	}
}