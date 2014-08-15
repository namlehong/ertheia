package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;

public class HennaInfoPacket extends L2GameServerPacket
{
	private final Henna[] _hennas = new Henna[3];
	private final int _str, _con, _dex, _int, _wit, _men, _luc, _cha;
	private int _count;

	public HennaInfoPacket(Player player)
	{
		_count = 0;
		l2s.gameserver.templates.Henna h;
		for(int i = 0; i < 3; i++)
			if((h = player.getHenna(i + 1)) != null)
				_hennas[_count++] = new Henna(h.getSymbolId(), h.isForThisClass(player));

		_str = player.getHennaStatSTR();
		_con = player.getHennaStatCON();
		_dex = player.getHennaStatDEX();
		_int = player.getHennaStatINT();
		_wit = player.getHennaStatWIT();
		_men = player.getHennaStatMEN();
		_luc = player.getHennaStatLUC();
		_cha = player.getHennaStatCHA();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(_int); //equip INT
		writeC(_str); //equip STR
		writeC(_con); //equip CON
		writeC(_men); //equip MEM
		writeC(_dex); //equip DEX
		writeC(_wit); //equip WIT
		writeC(_luc);
		writeC(_cha);
		writeD(3); //interlude, slots?
		writeD(_count);
		for(int i = 0; i < _count; i++)
		{
			writeD(_hennas[i]._symbolId);
			writeD(_hennas[i]._valid ? _hennas[i]._symbolId : 0);
		}
		//TODO add DDD
	}

	private static class Henna
	{
		private int _symbolId;
		private boolean _valid;

		public Henna(int sy, boolean valid)
		{
			_symbolId = sy;
			_valid = valid;
		}
	}
}