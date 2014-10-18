package services;


import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.authcomm.gs2as.ReduceAccountPoints;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Hien Son
 * Date: 24.09.2014
 **/
public class GoldEinhasad extends Functions implements ScriptFile
{
	private static int GOLD_EINHASAD	= 4356;
	private static double EXCHANGE_RATE	= 0.9;
	
	
	public void exchange_gold(String arg[])
	{
		Player player = getSelf();
		
		int exchange_amount = Integer.parseInt(arg[0]);
		
		if(exchange_amount < 0) 
			return;
		
		if(player == null) 
			return;
		
		ItemInstance gold_coin = player.getInventory().getItemByItemId(GOLD_EINHASAD);
		
		if(gold_coin == null)
		{
			show("scripts/services/GoldEinhasad/nocoin.htm", player);
			return;
		}
		
		int coinAmount = (int) gold_coin.getCount();
		
		if(coinAmount < exchange_amount)
		{
			show("scripts/services/GoldEinhasad/nocoin.htm", player);
			return;
		}
		
		int creditAmount = (int) Math.floor(exchange_amount*EXCHANGE_RATE);
		
		removeItem(player, GOLD_EINHASAD, exchange_amount);
		
		//player.increasePremiumPoints(creditAmount);
		player.getNetConnection().setPoints((int) (player.getPremiumPoints() + creditAmount));
		AuthServerCommunication.getInstance().sendPacket(new ReduceAccountPoints(player.getAccountName(), 0-creditAmount));
		player.sendPacket(new ExShowScreenMessage("Bạn vừa được cộng thêm " + creditAmount + " điểm credit", 7000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		
		//for logging purpose
		int coinExchanged = player.getVarInt("coin_exchanged");
		
		coinExchanged += exchange_amount;
		
		player.setVar("coin_exchanged", coinExchanged);
		
	}
	
	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		System.out.println("Gold exchange onload");
	}

	@Override
	public void onReload() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShutdown() {
		// TODO Auto-generated method stub
		
	}
	
}