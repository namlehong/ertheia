package l2s.gameserver.stats.funcs;

import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.tables.EnchantHPBonusTable;
import l2s.gameserver.templates.item.ItemQuality;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.ItemType;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;

public class FuncEnchantRGrade extends Func
{
	public FuncEnchantRGrade(Stats stat, int order, Object owner, double value)
	{
		super(stat, order, owner);
	}

	@Override
	public void calc(Env env)
	{
		ItemInstance item = (ItemInstance) owner;

		int enchant = item.getEnchantLevel();
		if(enchant < 4)
			return;

		boolean isBlessed = item.getTemplate().getQuality() == ItemQuality.BLESSED;
		double value = 0;
		switch(stat)
		{
			case P_EVASION_RATE:
			case M_EVASION_RATE:
			case P_ACCURACY_COMBAT:
			case M_ACCURACY_COMBAT:
			{
				value = 0.2D;
				break;
			}
			case MAGIC_ATTACK:
			{
				value = 1.4D;
				break;
			}
			case POWER_ATTACK:
			{
				value = 2.0D;
				break;
			}
			case P_CRITICAL_DAMAGE:
			case M_CRITICAL_DAMAGE:
			{
				value = 0.34D;
				break;
			}
			case RUN_SPEED:
			{
				value = 0.6D;
				break;
			}
		}

		if(enchant == 4)
			env.value += Math.ceil(value * (isBlessed ? 1.5D : 1.0D));
		else
			env.value += Math.ceil(value * ((isBlessed ? 1.5D : 1.0D) * (enchant * 2 - 9)));
	}
}