package l2s.gameserver.stats.funcs;

import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.stats.Env;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.tables.EnchantHPBonusTable;
import l2s.gameserver.templates.item.ItemQuality;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.ItemType;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;

public class FuncEnchant extends Func
{
	public FuncEnchant(Stats stat, int order, Object owner, double value)
	{
		super(stat, order, owner);
	}

	@Override
	public void calc(Env env)
	{
		ItemInstance item = (ItemInstance) owner;

		int enchant = item.getEnchantLevel();
		int overenchant = Math.max(0, enchant - 3);
		int overenchantR1 = Math.max(0, enchant - 6);
		int overenchantR2 = Math.max(0, enchant - 9);
		int overenchantR3 = Math.max(0, enchant - 12);
		boolean isBlessed = item.getTemplate().getQuality() == ItemQuality.BLESSED;

		switch(stat)
		{
			case SHIELD_DEFENCE:
			case MAGIC_DEFENCE:
			case POWER_DEFENCE:
			{
				env.value += enchant + overenchant * 2 * (isBlessed ? 1.6 : 1.0);
				return;
			}

			case MAX_HP:
			{
				env.value += EnchantHPBonusTable.getInstance().getHPBonus(item) * (isBlessed ? 1.6 : 1.0); // TODO: [Bonux] Проверить на оффе.
				return;
			}

			case MAGIC_ATTACK:
			{
				switch(item.getTemplate().getGrade().getCrystalId())
				{
					case ItemTemplate.CRYSTAL_R:
						env.value += 5 * (enchant + overenchant + overenchantR1 + overenchantR2 + overenchantR3) * (isBlessed ? 1.6 : 1.0);
						break;
					case ItemTemplate.CRYSTAL_S:
						env.value += 4 * (enchant + overenchant) * (isBlessed ? 1.6 : 1.0);
						break;
					case ItemTemplate.CRYSTAL_A:
						env.value += 3 * (enchant + overenchant) * (isBlessed ? 1.6 : 1.0);
						break;
					case ItemTemplate.CRYSTAL_B:
						env.value += 3 * (enchant + overenchant) * (isBlessed ? 1.6 : 1.0);
						break;
					case ItemTemplate.CRYSTAL_C:
						env.value += 3 * (enchant + overenchant) * (isBlessed ? 1.6 : 1.0);
						break;
					case ItemTemplate.CRYSTAL_D:
					case ItemTemplate.CRYSTAL_NONE:
						env.value += 2 * (enchant + overenchant) * (isBlessed ? 1.6 : 1.0);
						break;
				}
				return;
			}

			case POWER_ATTACK:
			{
				ItemType itemType = item.getItemType();
				boolean isBow = itemType == WeaponType.BOW;
				boolean isCrossbow = itemType == WeaponType.CROSSBOW;
				boolean isSword = (itemType == WeaponType.DUALFIST || itemType == WeaponType.DUAL || itemType == WeaponType.BIGSWORD || itemType == WeaponType.SWORD || itemType == WeaponType.RAPIER || itemType == WeaponType.ANCIENTSWORD) && item.getTemplate().getBodyPart() == ItemTemplate.SLOT_LR_HAND;
				switch(item.getTemplate().getGrade().getCrystalId())
				{
					case ItemTemplate.CRYSTAL_R:
						if(isBow)
							env.value += 12 * (enchant + overenchant + overenchantR1 + overenchantR2 + overenchantR3) * (isBlessed ? 1.6 : 1.0);
						else if(isSword || isCrossbow)
							env.value += 7 * (enchant + overenchant + overenchantR1 + overenchantR2 + overenchantR3) * (isBlessed ? 1.6 : 1.0);
						else
							env.value += 6 * (enchant + overenchant + overenchantR1 + overenchantR2 + overenchantR3) * (isBlessed ? 1.6 : 1.0);
						break;
					case ItemTemplate.CRYSTAL_S:
						if(isBow)
							env.value += 10 * (enchant + overenchant);
						else if(isCrossbow)
							env.value += 7 * (enchant + overenchant);
						else if(isSword)
							env.value += 6 * (enchant + overenchant);
						else
							env.value += 5 * (enchant + overenchant);
						break;
					case ItemTemplate.CRYSTAL_A:
						if(isBow)
							env.value += 8 * (enchant + overenchant);
						else if(isCrossbow)
							env.value += 6 * (enchant + overenchant);
						else if(isSword)
							env.value += 5 * (enchant + overenchant);
						else
							env.value += 4 * (enchant + overenchant);
						break;
					case ItemTemplate.CRYSTAL_B:
					case ItemTemplate.CRYSTAL_C:
						if(isBow)
							env.value += 6 * (enchant + overenchant);
						else if(isCrossbow)
							env.value += 5 * (enchant + overenchant);
						else if(isSword)
							env.value += 4 * (enchant + overenchant);
						else
							env.value += 3 * (enchant + overenchant);
						break;
					case ItemTemplate.CRYSTAL_D:
					case ItemTemplate.CRYSTAL_NONE:
						if(isBow)
							env.value += 4 * (enchant + overenchant);
						else if(isCrossbow)
							env.value += 3 * (enchant + overenchant);
						else
							env.value += 2 * (enchant + overenchant);
						break;
				}
			}
		}
	}
}