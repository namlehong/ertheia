package l2s.gameserver.model.actor.instances.player;

import l2s.gameserver.Config;

/**
 * Класс с бонусными рейтами для игрока
 */
public class Bonus
{
	public static final int NO_BONUS = 0;
	public static final int BONUS_GLOBAL_ON_AUTHSERVER = 1;
	public static final int BONUS_GLOBAL_ON_GAMESERVER = 2;

	private double rateXp = 1.;
	private double rateSp = 1.;
	private double questRewardRate = 1.;
	private double questDropRate = 1.;
	private double dropAdena = 1.;
	private double dropItems = 1.;
	private double dropSpoil = 1.;
	private double enchantAdd = 0.;
	

	private int bonusExpire;

	public double getRateXp()
	{
		return rateXp;
	}

	public void setRateXp(double rateXp)
	{
		if(!Config.ALLOW_PA_EXP)
		{
			this.rateXp = 1.0;
			return;
		}
		this.rateXp = rateXp;
	}

	public double getRateSp()
	{
		return rateSp;
	}

	public void setRateSp(double rateSp)
	{
		if(!Config.ALLOW_PA_SP)
		{
			this.rateSp = 1.0;
			return;
		}
		this.rateSp = rateSp;
	}

	public double getQuestRewardRate()
	{
		return questRewardRate;
	}

	public void setQuestRewardRate(double questRewardRate)
	{
		if(!Config.ALLOW_PA_QUEST_REWARD)
		{
			this.questRewardRate = 1.0;
			return;
		}
		this.questRewardRate = questRewardRate;
	}

	public double getQuestDropRate()
	{
		return questDropRate;
	}

	public void setQuestDropRate(double questDropRate)
	{
		if(!Config.ALLOW_PA_QUEST_DROP)
		{
			this.questDropRate = 1.0;
			return;
		}
		this.questDropRate = questDropRate;
	}

	public double getDropAdena()
	{
		return dropAdena;
	}

	public void setDropAdena(double dropAdena)
	{
		if(!Config.ALLOW_PA_ADENA)
		{
			this.dropAdena = 1.0;
			return;
		}
		this.dropAdena = dropAdena;
	}

	public double getDropItems()
	{
		return dropItems;
	}

	public void setDropItems(double dropItems)
	{
		if(!Config.ALLOW_PA_DROP_ITEMS)
		{
			this.dropItems = 1.0;
			return;
		}
		this.dropItems = dropItems;
	}

	public double getDropSpoil()
	{
		return dropSpoil;
	}

	public void setDropSpoil(double dropSpoil)
	{
		if(!Config.ALLOW_PA_DROP_SPOIL)
		{
			this.dropSpoil = 1.0;
			return;
		}
		this.dropSpoil = dropSpoil;
	}

	public int getBonusExpire()
	{
		return bonusExpire;
	}

	public void setBonusExpire(int bonusExpire)
	{
		this.bonusExpire = bonusExpire;
	}
	
	public void setEnchantAdd(double enchantAdd)
	{
		this.enchantAdd = enchantAdd;
	}	
	public double getEnchantAdd()
	{
		return enchantAdd;
	}	
}