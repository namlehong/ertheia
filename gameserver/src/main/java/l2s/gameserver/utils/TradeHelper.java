package l2s.gameserver.utils;

import l2s.gameserver.Config;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.instancemanager.WorldStatisticsManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.items.TradeItem;
import l2s.gameserver.model.worldstatistics.CategoryType;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;

public final class TradeHelper
{
	private TradeHelper()
	{}

	public static boolean checksIfCanOpenStore(Player player, int storeType)
	{
		if(!player.getPlayerAccess().UseTrade)
		{
			player.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_____);
			return false;
		}

		if(player.getLevel() < Config.SERVICES_TRADE_MIN_LEVEL)
		{
			player.sendMessage(new CustomMessage("trade.NotHavePermission", player).addNumber(Config.SERVICES_TRADE_MIN_LEVEL));
			return false;
		}

		String tradeBan = player.getVar("tradeBan");
		if(tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
		{
			player.sendPacket(SystemMsg.YOU_ARE_CURRENTLY_BLOCKED_FROM_USING_THE_PRIVATE_STORE_AND_PRIVATE_WORKSHOP);
			return false;
		}

		String BLOCK_ZONE = storeType == Player.STORE_PRIVATE_MANUFACTURE ? Zone.BLOCKED_ACTION_PRIVATE_WORKSHOP : Zone.BLOCKED_ACTION_PRIVATE_STORE;
		if(player.isActionBlocked(BLOCK_ZONE))
			if(!Config.SERVICES_NO_TRADE_ONLY_OFFLINE || Config.SERVICES_NO_TRADE_ONLY_OFFLINE && player.isInOfflineMode())
			{
				player.sendPacket(storeType == Player.STORE_PRIVATE_MANUFACTURE ? SystemMsg.YOU_CANNOT_OPEN_A_PRIVATE_WORKSHOP_HERE : SystemMsg.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE);
				return false;
			}

		if(player.isCastingNow())
		{
			player.sendPacket(SystemMsg.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL);
			return false;
		}

		if(player.isInCombat())
		{
			player.sendPacket(SystemMsg.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			return false;
		}

		if(player.isChaosFestivalParticipant())
		{
			player.sendPacket(SystemMsg.YOU_CANNOT_OPEN_A_PRIVATE_STORE_OR_WORKSHOP_IN_THE_CEREMONY_OF_CHAOS);
			return false;
		}
		

		if(player.isActionsDisabled() || player.isMounted() || player.isInOlympiadMode() || player.isInDuel() || player.isProcessingRequest())
			return false;

		if(Config.SERVICES_TRADE_ONLY_FAR)
		{
			boolean tradenear = false;
			for(Player p : World.getAroundPlayers(player, Config.SERVICES_TRADE_RADIUS, 200))
				if(p.isInStoreMode())
				{
					tradenear = true;
					break;
				}

			if(World.getAroundNpc(player, Config.SERVICES_TRADE_RADIUS + 100, 200).size() > 0)
				tradenear = true;

			if(tradenear)
			{
				player.sendMessage(new CustomMessage("trade.OtherTradersNear", player));
				return false;
			}
		}

		return true;
	}

	public final static void purchaseItem(Player buyer, Player seller, TradeItem item)
	{
		long price = item.getCount() * item.getOwnersPrice();
		if(!item.getItem().isStackable())
		{
			if(item.getEnchantLevel() > 0)
			{
				seller.sendPacket(new SystemMessagePacket(SystemMsg.S2S3_HAS_BEEN_SOLD_TO_C1_AT_THE_PRICE_OF_S4_ADENA).addName(buyer).addInteger(item.getEnchantLevel()).addItemName(item.getItemId()).addLong(price));
				buyer.sendPacket(new SystemMessagePacket(SystemMsg.S2S3_HAS_BEEN_PURCHASED_FROM_C1_AT_THE_PRICE_OF_S4_ADENA).addName(seller).addInteger(item.getEnchantLevel()).addItemName(item.getItemId()).addLong(price));
			}
			else
			{
				seller.sendPacket(new SystemMessagePacket(SystemMsg.S2_IS_SOLD_TO_C1_FOR_THE_PRICE_OF_S3_ADENA).addName(buyer).addItemName(item.getItemId()).addLong(price));
				buyer.sendPacket(new SystemMessagePacket(SystemMsg.S2_HAS_BEEN_PURCHASED_FROM_C1_AT_THE_PRICE_OF_S3_ADENA).addName(seller).addItemName(item.getItemId()).addLong(price));
			}
		}
		else
		{
			seller.sendPacket(new SystemMessagePacket(SystemMsg.S2_S3_HAVE_BEEN_SOLD_TO_C1_FOR_S4_ADENA).addName(buyer).addItemName(item.getItemId()).addLong(item.getCount()).addLong(price));
			buyer.sendPacket(new SystemMessagePacket(SystemMsg.S3_S2_HAS_BEEN_PURCHASED_FROM_C1_FOR_S4_ADENA).addName(seller).addItemName(item.getItemId()).addLong(item.getCount()).addLong(price));
		}
		WorldStatisticsManager.getInstance().updateStat(seller, CategoryType.PRIVATE_SELL_COUNT, 1L);
	}

	public final static long getTax(Player seller, long price)
	{
		long tax = (long) (price * Config.SERVICES_TRADE_TAX / 100);
		if(seller.isInZone(Zone.ZoneType.offshore))
			tax = (long) (price * Config.SERVICES_OFFSHORE_TRADE_TAX / 100);
		if(Config.SERVICES_TRADE_TAX_ONLY_OFFLINE && !seller.isInOfflineMode())
			tax = 0;
		if(Config.SERVICES_PARNASSUS_NOTAX && seller.getReflection() == ReflectionManager.PARNASSUS)
			tax = 0;

		return tax;
	}

	/**
	 * Отключение режима торговли у персонажа, оф. трейдеров кикает.
	 */
	public static void cancelStore(Player activeChar)
	{
		activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
		if(activeChar.isInOfflineMode())
		{
			activeChar.setOfflineMode(false);
			activeChar.kick();
		}
		else
			activeChar.broadcastCharInfo();
	}
}