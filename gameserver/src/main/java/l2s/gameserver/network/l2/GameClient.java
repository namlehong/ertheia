package l2s.gameserver.network.l2;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import l2s.commons.dbutils.DbUtils;
import l2s.commons.net.nio.impl.MMOClient;
import l2s.commons.net.nio.impl.MMOConnection;
import l2s.gameserver.Config;
import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.CharSelectInfoPackage;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.authcomm.SessionKey;
import l2s.gameserver.network.authcomm.gs2as.PlayerLogout;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.security.SecondaryPasswordAuth;
import l2s.gameserver.utils.AdminFunctions;
import l2s.gameserver.utils.Language;
import l2s.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a client connected on Game Server
 */
public final class GameClient extends MMOClient<MMOConnection<GameClient>>
{
	private static final Logger _log = LoggerFactory.getLogger(GameClient.class);
	private static final String NO_IP = "?.?.?.?";

	public GameCrypt _crypt = null;

	public GameClientState _state;

	public static enum GameClientState
	{
		CONNECTED,
		AUTHED,
		IN_GAME,
		DISCONNECTED
	}

	/** Данные аккаунта */
	private String _login;
	private int _bonus = 1;
	private int _bonusExpire;
	private int _points = 0;
	private Language _language = Config.DEFAULT_LANG;

	private Player _activeChar;
	private SessionKey _sessionKey;
	private String _ip = NO_IP;
	private int revision = 0;

	private SecondaryPasswordAuth _secondaryAuth = null;

	private List<Integer> _charSlotMapping = new ArrayList<Integer>();

	public GameClient(MMOConnection<GameClient> con)
	{
		super(con);

		_state = GameClientState.CONNECTED;
		_ip = con.getSocket().getInetAddress().getHostAddress();
		_crypt = new GameCrypt();
	}

	@Override
	protected void onDisconnection()
	{
		final Player player;

		setState(GameClientState.DISCONNECTED);
		player = getActiveChar();
		setActiveChar(null);

		if(player != null)
		{
			player.setNetConnection(null);
			player.scheduleDelete();
		}

		if(getSessionKey() != null)
		{
			if(isAuthed())
			{
				AuthServerCommunication.getInstance().removeAuthedClient(getLogin());
				AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(getLogin()));
			}
			else
			{
				AuthServerCommunication.getInstance().removeWaitingClient(getLogin());
			}
		}
	}

	@Override
	protected void onForcedDisconnection()
	{
		// TODO Auto-generated method stub

	}

	public void markRestoredChar(int charslot) throws Exception
	{
		int objid = getObjectIdForSlot(charslot);
		if(objid < 0)
			return;

		if(_activeChar != null && _activeChar.getObjectId() == objid)
			_activeChar.setDeleteTimer(0);

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE obj_id=?");
			statement.setInt(1, objid);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void markToDeleteChar(int charslot) throws Exception
	{
		int objid = getObjectIdForSlot(charslot);
		if(objid < 0)
			return;

		if(_activeChar != null && _activeChar.getObjectId() == objid)
			_activeChar.setDeleteTimer((int) (System.currentTimeMillis() / 1000));

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE characters SET deletetime=? WHERE obj_id=?");
			statement.setLong(1, (int) (System.currentTimeMillis() / 1000L));
			statement.setInt(2, objid);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("data error on update deletime char:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	public void deleteChar(int charslot) throws Exception
	{
		//have to make sure active character must be nulled
		if(_activeChar != null)
			return;

		int objid = getObjectIdForSlot(charslot);
		if(objid == -1)
			return;

		CharacterDAO.getInstance().deleteCharByObjId(objid);
	}

	public Player loadCharFromDisk(int charslot)
	{
		int objectId = getObjectIdForSlot(charslot);
		if(objectId == -1)
			return null;

		Player character = null;
		Player oldPlayer = GameObjectsStorage.getPlayer(objectId);

		if(oldPlayer != null)
			if(oldPlayer.isInOfflineMode() || oldPlayer.isLogoutStarted())
			{
				// оффтрейдового чара проще выбить чем восстанавливать
				oldPlayer.kick();
				return null;
			}
			else
			{
				checkTimesToSwitchAccounts();
				oldPlayer.sendPacket(SystemMsg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);

				GameClient oldClient = oldPlayer.getNetConnection();
				if(oldClient != null)
				{
					oldClient.setActiveChar(null);
					oldClient.closeNow(false);
				}
				oldPlayer.setNetConnection(this);
				character = oldPlayer;
			}

		if(character == null)
			character = Player.restore(objectId);

		if(character != null)
			setActiveChar(character);
		else
			_log.warn("could not restore obj_id: " + objectId + " in slot:" + charslot);

		return character;
	}

	public int getObjectIdForSlot(int charslot)
	{
		if(charslot < 0 || charslot >= _charSlotMapping.size())
		{
			_log.warn(getLogin() + " tried to modify Character in slot " + charslot + " but no characters exits at that slot.");
			return -1;
		}
		return _charSlotMapping.get(charslot);
	}

	public Player getActiveChar()
	{
		return _activeChar;
	}

	/**
	 * @return Returns the sessionId.
	 */
	public SessionKey getSessionKey()
	{
		return _sessionKey;
	}

	public String getLogin()
	{
		return _login;
	}

	public void setLoginName(String loginName)
	{
		_login = loginName;

		if(Config.EX_SECOND_AUTH_ENABLED)
			_secondaryAuth = new SecondaryPasswordAuth(this);
	}

	public void setActiveChar(Player player)
	{
		_activeChar = player;
		if(player != null)
			player.setNetConnection(this);
	}

	public void setSessionId(SessionKey sessionKey)
	{
		_sessionKey = sessionKey;
	}

	public void setCharSelection(CharSelectInfoPackage[] chars)
	{
		_charSlotMapping.clear();

		for(CharSelectInfoPackage element : chars)
		{
			int objectId = element.getObjectId();
			_charSlotMapping.add(objectId);
		}
	}

	public void setCharSelection(int c)
	{
		_charSlotMapping.clear();
		_charSlotMapping.add(c);
	}

	public int getRevision()
	{
		return revision;
	}

	public void setRevision(int revision)
	{
		this.revision = revision;
	}

	@Override
	public boolean encrypt(final ByteBuffer buf, final int size)
	{
		_crypt.encrypt(buf.array(), buf.position(), size);
		buf.position(buf.position() + size);
		return true;
	}

	@Override
	public boolean decrypt(ByteBuffer buf, int size)
	{
		boolean ret = _crypt.decrypt(buf.array(), buf.position(), size);

		return ret;
	}

	public void sendPacket(L2GameServerPacket gsp)
	{
		if(isConnected())
			getConnection().sendPacket(gsp);
	}

	public void sendPacket(L2GameServerPacket... gsp)
	{
		if(isConnected())
			getConnection().sendPacket(gsp);
	}

	public void sendPackets(List<L2GameServerPacket> gsp)
	{
		if(isConnected())
			getConnection().sendPackets(gsp);
	}

	public void close(L2GameServerPacket gsp)
	{
		if(isConnected())
			getConnection().close(gsp);
	}

	public String getIpAddr()
	{
		return _ip;
	}

	public byte[] enableCrypt()
	{
		byte[] key = BlowFishKeygen.getRandomKey();
		_crypt.setKey(key);
		return key;
	}

	public boolean hasBonus()
	{
		return _bonusExpire > System.currentTimeMillis() / 1000L;
	}

	public int getBonusType()
	{
		return _bonus;
	}

	public int getBonusExpire()
	{
		return _bonusExpire;
	}

	public int getPoints()
	{
		return _points;
	}

	public Language getLanguage()
	{
		return _language;
	}

	public void setBonus(int bonus)
	{
		_bonus = bonus;
	}

	public void setBonusExpire(int bonusExpire)
	{
		_bonusExpire = bonusExpire;
	}

	public void setPoints(int points)
	{
		_points = points;
	}

	public void setLanguage(Language language)
	{
		_language = language;
	}

	public GameClientState getState()
	{
		return _state;
	}

	public void setState(GameClientState state)
	{
		_state = state;
	}

	public SecondaryPasswordAuth getSecondaryAuth()
	{
		return _secondaryAuth;
	}

	private int _failedPackets = 0;
	private int _unknownPackets = 0;

	public void onPacketReadFail()
	{
		if(_failedPackets++ >= 10)
		{
			_log.warn("Too many client packet fails, connection closed : " + this);
			closeNow(true);
		}
	}

	public void onUnknownPacket()
	{
		if(_unknownPackets++ >= 10)
		{
			_log.warn("Too many client unknown packets, connection closed : " + this);
			closeNow(true);
		}
	}

	@Override
	public String toString()
	{
		return _state + " IP: " + getIpAddr() + (_login == null ? "" : " Account: " + _login) + (_activeChar == null ? "" : " Player : " + _activeChar);
	}

	public boolean secondaryAuthed()
	{
		if(!Config.EX_SECOND_AUTH_ENABLED)
			return true;

		return getSecondaryAuth().isAuthed();
	}

	public String getHWID()
	{
		return "";
	}

	public void checkHwid(String allowedHwid) 
	{
		//
	}
	
	public void checkTimesToSwitchAccounts()
	{
		Player player = getActiveChar();
		if(player == null) //incase something went wrong
			return;
		if(Config.BAN_FOR_ACCOUNT_SWITCH_TIMES <= 0)
			return;
			
		if(getFirstAcoountSwitch() == 0L || System.currentTimeMillis() - getFirstAcoountSwitch() > 60000L * Config.MIN_DELAY_TO_COUNT_SWITCH) //time passed or first time
		{
			setTimesSwiched(1);
			setFirstAcoountSwitch(System.currentTimeMillis());
			return;
		}	
		int times = getTimesToSwitch() + 1;
		int jail_time = Config.JAIL_TIME_FOR_ACC_SWITCH;
		if(times > Config.BAN_FOR_ACCOUNT_SWITCH_TIMES && player.getVar("jailed") == null) //exploit :)
		{
			player.setVar("jailedFrom", player.getX() + ";" + player.getY() + ";" + player.getZ() + ";" + player.getReflectionId(), -1);
			player.setVar("jailed", jail_time, -1);
			player.startUnjailTask(player, jail_time); //min
			player.teleToLocation(Location.findPointToStay(player, AdminFunctions.JAIL_SPAWN, 50, 200), ReflectionManager.JAIL);
			if(player.isInStoreMode())
				player.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
			player.sitDown(null);
			player.block();
			player.sendMessage("You moved to jail, time to escape - " + jail_time + " minutes, reason - switch too many accounts .");		
			setTimesSwiched(1);
			setFirstAcoountSwitch(System.currentTimeMillis());			
		}
		else
			setTimesSwiched(times);	
	}
	
	private void setTimesSwiched(int times)
	{
		updLogin(times, -1);
	}
	
	private int getTimesToSwitch()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		int times = 0;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM accounts");
			rset = statement.executeQuery();
			while(rset.next())
			{
				times = rset.getInt("acc_switch_times");
				break;
			}	
		}	
		catch(Exception e)
		{
			_log.info("bad statement");
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}	
		return times;	
	}
	
	private long getFirstAcoountSwitch()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		long first_time = 0;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM accounts");
			rset = statement.executeQuery();
			while(rset.next())
			{
				first_time = rset.getLong("first_acc_switch_time");
				break;
			}	
		}	
		catch(Exception e)
		{
			_log.info("bad statement");
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}	
		return first_time;		
	}
	
	private void setFirstAcoountSwitch(long time)
	{
		updLogin(-1, time);
	}
	
	private void updLogin(int switchedTimes, long firstAccountSwitchTime)
	{
		String Statament1 = "UPDATE accounts SET first_acc_switch_time=? WHERE login=?";
		String Statament2 = "UPDATE accounts SET acc_switch_times=? WHERE obj_id=?";
		
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = switchedTimes == -1 ? con.prepareStatement(Statament1) : con.prepareStatement(Statament2);
			statement.setString(1, getLogin());
			if(switchedTimes == -1)
				statement.setLong(2, firstAccountSwitchTime);
			else
				statement.setInt(2, switchedTimes);
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}	
	}
}