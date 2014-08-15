package l2s.gameserver.handler.voicecommands.impl;

import l2s.gameserver.Config;
import l2s.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.scripts.Functions;

public class Offline extends Functions implements IVoicedCommandHandler
{
	private String[] _commandList = new String[] { "offline" };

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		if(!Config.ALLOW_VOICED_COMMANDS)
			return false;

		if(!Config.SERVICES_OFFLINE_TRADE_ALLOW)
			return false;

		if(activeChar.getOlympiadObserveGame() != null || activeChar.getOlympiadGame() != null || Olympiad.isRegisteredInComp(activeChar) || activeChar.isPK())
		{
			activeChar.sendActionFailed();
			return false;
		}

		if(activeChar.getLevel() < Config.SERVICES_OFFLINE_TRADE_MIN_LEVEL)
		{
			show(new CustomMessage("voicedcommandhandlers.Offline.LowLevel", activeChar).addNumber(Config.SERVICES_OFFLINE_TRADE_MIN_LEVEL), activeChar);
			return false;
		}

		if(!activeChar.isInZone(Zone.ZoneType.offshore) && Config.SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE)
		{
			show(new CustomMessage("trade.OfflineNoTradeZoneOnlyOffshore", activeChar), activeChar);
			return false;
		}

		if(!activeChar.isInStoreMode())
		{
			show(new CustomMessage("voicedcommandhandlers.Offline.IncorrectUse", activeChar), activeChar);
			return false;
		}

		if(activeChar.getNoChannelRemained() > 0)
		{
			show(new CustomMessage("voicedcommandhandlers.Offline.BanChat", activeChar), activeChar);
			return false;
		}

		if(activeChar.isActionBlocked(Zone.BLOCKED_ACTION_PRIVATE_STORE))
		{
			show(new CustomMessage("trade.OfflineNoTradeZone", activeChar), activeChar);
			return false;
		}

		if(Config.SERVICES_OFFLINE_TRADE_PRICE > 0 && Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM > 0)
		{
			if(getItemCount(activeChar, Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM) < Config.SERVICES_OFFLINE_TRADE_PRICE)
			{
				show(new CustomMessage("voicedcommandhandlers.Offline.NotEnough", activeChar).addItemName(Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM).addNumber(Config.SERVICES_OFFLINE_TRADE_PRICE), activeChar);
				return false;
			}
			removeItem(activeChar, Config.SERVICES_OFFLINE_TRADE_PRICE_ITEM, Config.SERVICES_OFFLINE_TRADE_PRICE);
		}

		activeChar.offline();
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}