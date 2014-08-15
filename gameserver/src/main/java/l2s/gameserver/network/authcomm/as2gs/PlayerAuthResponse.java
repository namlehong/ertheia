package l2s.gameserver.network.authcomm.as2gs;

import l2s.gameserver.Config;
import l2s.gameserver.dao.AccountBonusDAO;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Bonus;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.authcomm.ReceivablePacket;
import l2s.gameserver.network.authcomm.SessionKey;
import l2s.gameserver.network.authcomm.gs2as.PlayerInGame;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.CharacterSelectionInfoPacket;
import l2s.gameserver.network.l2.s2c.ExLoginVitalityEffectInfo;
import l2s.gameserver.network.l2.s2c.LoginFail;
import l2s.gameserver.network.l2.s2c.ServerCloseSocketPacket;

public class PlayerAuthResponse extends ReceivablePacket
{
	private String account;
	private boolean authed;
	private int playOkId1;
	private int playOkId2;
	private int loginOkId1;
	private int loginOkId2;
	private int bonus;
	private int bonusExpire;
	private int points;
	private String hwid;

	@Override
	public void readImpl()
	{
		account = readS();
		authed = readC() == 1;
		if(authed)
		{
			playOkId1 = readD();
			playOkId2 = readD();
			loginOkId1 = readD();
			loginOkId2 = readD();
			bonus = readD();
			bonusExpire = readD();
			points = readD();
		}
		hwid = readS();
	}

	@Override
	protected void runImpl()
	{
		SessionKey skey = new SessionKey(loginOkId1, loginOkId2, playOkId1, playOkId2);
		GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(account);
		if(client == null)
			return;

		if(authed && client.getSessionKey().equals(skey))
		{
			client.setAuthed(true);
			client.setState(GameClient.GameClientState.AUTHED);
			switch(Config.SERVICES_RATE_TYPE)
			{
				case Bonus.NO_BONUS:
					bonus = 1;
					bonusExpire = 0;
					break;
				case Bonus.BONUS_GLOBAL_ON_GAMESERVER:
					int[] bonuses = AccountBonusDAO.getInstance().select(account);
					bonus = bonuses[0];
					bonusExpire = bonuses[1];
					break;
			}
			client.setBonus(bonus);
			client.setBonusExpire(bonusExpire);
			client.setPoints(points);

			GameClient oldClient = AuthServerCommunication.getInstance().addAuthedClient(client);
			if(oldClient != null)
			{
				oldClient.setAuthed(false);
				Player activeChar = oldClient.getActiveChar();
				if(activeChar != null)
				{
					//FIXME [G1ta0] сообщение чаще всего не показывается, т.к. при закрытии соединения очередь на отправку очищается
					oldClient.checkTimesToSwitchAccounts();
					activeChar.sendPacket(SystemMsg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
					activeChar.logout();
				}
				else
				{
					oldClient.close(ServerCloseSocketPacket.STATIC);
				}
			}

			sendPacket(new PlayerInGame(client.getLogin()));

			CharacterSelectionInfoPacket csi = new CharacterSelectionInfoPacket(client.getLogin(), client.getSessionKey().playOkID1);
			client.sendPacket(csi);
			client.sendPacket(new ExLoginVitalityEffectInfo(client.hasBonus(), 0)); //TODO: [Bonux].
			client.setCharSelection(csi.getCharInfo());
			client.checkHwid(hwid);
		}
		else
		{
			client.close(new LoginFail(LoginFail.ACCESS_FAILED_TRY_LATER));
		}
	}
}