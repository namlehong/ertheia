package l2s.gameserver.network.l2.s2c;

import java.util.HashMap;
import java.util.Map;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.RestartType;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.pledge.Clan;

public class DiePacket extends L2GameServerPacket
{
	private int _objectId;
	private boolean _fake;
	private boolean _sweepable;
	private boolean _isInLfc;

	private Map<RestartType, Boolean> _types = new HashMap<RestartType, Boolean>(RestartType.VALUES.length);

	public DiePacket(Creature cha)
	{
		_objectId = cha.getObjectId();
		_fake = !cha.isDead();

		_isInLfc = cha.isPlayer() && cha.getPlayer().getPendingLfcEnd();

		if(cha.isMonster())
			_sweepable = ((MonsterInstance) cha).isSweepActive() || ((MonsterInstance) cha).isRobbed() == 2;
		else if(cha.isPlayer())
		{
			Player player = (Player) cha;
			put(RestartType.FIXED, player.canFixedRessurect());
			put(RestartType.AGATHION, player.isAgathionResAvailable());
			put(RestartType.TO_VILLAGE, true);

			Clan clan = null;
			if(get(RestartType.TO_VILLAGE))
				clan = player.getClan();
			if(clan != null)
			{
				put(RestartType.TO_CLANHALL, clan.getHasHideout() > 0);
				put(RestartType.TO_CASTLE, clan.getCastle() > 0);
				put(RestartType.TO_FORTRESS, clan.getHasFortress() > 0);
			}

			for(GlobalEvent e : cha.getEvents())
				e.checkRestartLocs(player, _types);
		}
	}

	@Override
	protected final void writeImpl()
	{
		if(_fake || _isInLfc)
			return;

		writeD(_objectId);
		writeD(get(RestartType.TO_VILLAGE)); // to nearest village
		writeD(get(RestartType.TO_CLANHALL)); // to hide away
		writeD(get(RestartType.TO_CASTLE)); // to castle
		writeD(get(RestartType.TO_FLAG));// to siege HQ
		writeD(_sweepable ? 0x01 : 0x00); // sweepable  (blue glow)
		writeD(get(RestartType.FIXED));// FIXED
		writeD(get(RestartType.TO_FORTRESS));// fortress
		writeC(0); //show die animation
		writeD(get(RestartType.AGATHION));//agathion ress button
		writeD(0x00); //additional free space
	}

	private void put(RestartType t, boolean b)
	{
		_types.put(t, b);
	}

	private boolean get(RestartType t)
	{
		Boolean b = _types.get(t);
		return b != null && b;
	}
}