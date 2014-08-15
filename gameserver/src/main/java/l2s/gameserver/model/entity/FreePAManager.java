package l2s.gameserver.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.dao.AccountBonusDAO;
import l2s.gameserver.dao.FreePremiumAccountsDao;
import l2s.gameserver.dao.PremiumAccountRatesHolder;
import l2s.gameserver.dao.PremiumAccountRatesHolder.PremiumInfo;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.Bonus;
import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.authcomm.gs2as.BonusRequest;
import l2s.gameserver.network.l2.s2c.ExBR_PremiumStatePacket;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.scripts.Functions;

public class FreePAManager extends Functions
{
	public static void checkAndReward(Player player)
	{
		if(player == null)
			return;

		if(!FreePremiumAccountsDao.getFreePAList().isEmpty() && FreePremiumAccountsDao.getFreePAList() != null)
		{
			for(String account : FreePremiumAccountsDao.getFreePAList())
				if(player.getAccountName().equalsIgnoreCase(account))
				{
					return;
				}
		}
		FreePremiumAccountsDao.addAccount(player.getAccountName());	

		int bonus_group;
		int time;
		boolean isHours;

		if(Config.RANDOMIZE_FROM_PA_TABLE)
		{
			CopyOnWriteArrayList<PremiumInfo> all_info = PremiumAccountRatesHolder.getAllAquisions();
			PremiumInfo rnd_info = all_info.get(Rnd.get(all_info.size() - 1));
			bonus_group = rnd_info.getGroupNumber();
			time = rnd_info.getDays();
			isHours = rnd_info.isHours();
		}
		else
		{
			bonus_group = Config.FREE_PA_BONUS_GROUP_STATIC;
			time = Config.FREE_PA_BONUS_TIME_STATIC;
			isHours = Config.FREE_PA_IS_HOURS_STATIC;
		}
		
		int startTime = (int) (System.currentTimeMillis() / 1000);
		int bonusExpire;

		if(isHours)
			bonusExpire = startTime + time * 60 * 60;
		else
			bonusExpire = startTime + time * 24 * 60 * 60;

		switch(Config.SERVICES_RATE_TYPE)
		{
			case Bonus.BONUS_GLOBAL_ON_AUTHSERVER:
				AuthServerCommunication.getInstance().sendPacket(new BonusRequest(player.getAccountName(), bonus_group, bonusExpire));
				break;
			case Bonus.BONUS_GLOBAL_ON_GAMESERVER:
				AccountBonusDAO.getInstance().insert(player.getAccountName(), bonus_group, bonusExpire);
				break;
		}

		player.getNetConnection().setBonus(bonus_group);
		player.getNetConnection().setBonusExpire(bonusExpire);

		player.stopBonusTask();
		player.startBonusTask();

		if(player.getParty() != null)
			player.getParty().recalculatePartyData();	

		player.broadcastPacket(new MagicSkillUse(player, player, 23128, 1, 1, 0));
		player.sendPacket(new ExBR_PremiumStatePacket(player, true));	

		if(Config.ENABLE_FREE_PA_NOTIFICATION)
		{
			String delay_type = isHours ? player.isLangRus() ? "Часов" : "Hours" : player.isLangRus() ? "Дней" : "Days";
			player.sendPacket(new ExShowScreenMessage("Вам был выдан бесплатный премиум аккаунт на "+time+" "+delay_type+"! Приятной игры! ", 10000, ScreenMessageAlign.TOP_CENTER, true));
		}	
	}
}