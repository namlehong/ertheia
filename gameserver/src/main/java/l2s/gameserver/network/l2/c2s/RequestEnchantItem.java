package l2s.gameserver.network.l2.c2s;

import l2s.commons.dao.JdbcEntityState;
import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.EnchantItemHolder;
import l2s.gameserver.instancemanager.WorldStatisticsManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.model.worldstatistics.CategoryType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.EnchantResultPacket;
import l2s.gameserver.network.l2.s2c.InventoryUpdatePacket;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.stats.Formulas;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.support.EnchantScroll;
import l2s.gameserver.templates.item.support.EnchantVariation;
import l2s.gameserver.templates.item.support.EnchantVariation.EnchantLevel;
import l2s.gameserver.utils.ItemFunctions;
import l2s.gameserver.utils.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestEnchantItem extends L2GameClientPacket
{
	private static final int ENCHANT_DELAY = 1500;

	private static final Logger _log = LoggerFactory.getLogger(RequestEnchantItem.class);

	private static final int SUCCESS_VISUAL_EFF_ID = 5965;
	private static final int FAIL_VISUAL_EFF_ID = 5949;
	
	private int _objectId, _catalystObjId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_catalystObjId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(player.isActionsDisabled())
		{
			player.setEnchantScroll(null);
			player.sendActionFailed();
			return;
		}

		if(player.isInTrade())
		{
			player.setEnchantScroll(null);
			player.sendActionFailed();
			return;
		}

		if(player.isInStoreMode())
		{
			player.setEnchantScroll(null);
			player.sendPacket(EnchantResultPacket.CANCEL);
			player.sendPacket(SystemMsg.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			player.sendActionFailed();
			return;
		}

		if(System.currentTimeMillis() <= (player.getLastEnchantItemTime() + ENCHANT_DELAY))
		{
			player.sendActionFailed();
			return;
		}

		PcInventory inventory = player.getInventory();
		inventory.writeLock();
		try
		{
			ItemInstance item = inventory.getItemByObjectId(_objectId);
			ItemInstance scroll = player.getEnchantScroll();
			ItemInstance catalyst = _catalystObjId > 0 ? inventory.getItemByObjectId(_catalystObjId) : null;
			if(!ItemFunctions.checkCatalyst(item, catalyst))
				catalyst = null;

			if(item == null || scroll == null)
			{
				player.sendActionFailed();
				return;
			}

			EnchantScroll enchantScroll = EnchantItemHolder.getInstance().getEnchantScroll(scroll.getItemId());
			if(enchantScroll == null)
			{
				player.sendActionFailed();
				return;
			}

			if(enchantScroll.getMaxEnchant() != -1 && item.getEnchantLevel() >= enchantScroll.getMaxEnchant())
			{
				player.sendPacket(EnchantResultPacket.CANCEL);
				player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
				player.sendActionFailed();
				return;
			}

			if(enchantScroll.getItems().size() > 0)
			{
				if(!enchantScroll.getItems().contains(item.getItemId()))
				{
					player.sendPacket(EnchantResultPacket.CANCEL);
					player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
					player.sendActionFailed();
					return;
				}
			}
			else
			{
				if(enchantScroll.getGrade().extOrdinal() != item.getGrade().extOrdinal())
				{
					player.sendPacket(EnchantResultPacket.CANCEL);
					player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
					player.sendActionFailed();
					return;
				}

				int itemType = item.getTemplate().getType2();
				switch(enchantScroll.getType())
				{
					case ARMOR:
						if(itemType == ItemTemplate.TYPE2_WEAPON)
						{
							player.sendPacket(EnchantResultPacket.CANCEL);
							player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
							player.sendActionFailed();
							return;
						}
						break;
					case WEAPON:
						if(itemType == ItemTemplate.TYPE2_SHIELD_ARMOR || itemType == ItemTemplate.TYPE2_ACCESSORY)
						{
							player.sendPacket(EnchantResultPacket.CANCEL);
							player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
							player.sendActionFailed();
							return;
						}
						break;
				}
			}

			if(!item.canBeEnchanted())
			{
				player.sendPacket(EnchantResultPacket.CANCEL);
				player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
				player.sendActionFailed();
				return;
			}

			if(!inventory.destroyItem(scroll, 1L) || catalyst != null && !inventory.destroyItem(catalyst, 1L))
			{
				player.sendPacket(EnchantResultPacket.CANCEL);
				player.sendActionFailed();
				return;
			}

			boolean equipped = false;
			if(equipped = item.isEquipped())
			{
				inventory.isRefresh = true;
				inventory.unEquipItem(item);
			}

			EnchantVariation variation = EnchantItemHolder.getInstance().getEnchantVariation(enchantScroll.getVariationId());
			if(variation == null)
			{
				player.sendActionFailed();
				_log.warn("RequestEnchantItem: Cannot find variation ID[" + enchantScroll.getVariationId() + "] for enchant scroll ID[" + enchantScroll.getItemId() + "]!");
				return;
			}

			int newEnchantLvl = item.getEnchantLevel() + 1;
			EnchantLevel enchantLevel = variation.getLevel(newEnchantLvl);
			if(enchantLevel == null)
			{
				player.sendActionFailed();
				_log.warn("RequestEnchantItem: Cannot find variation ID[" + enchantScroll.getVariationId() + "] enchant level[" + newEnchantLvl + "] for enchant scroll ID[" + enchantScroll.getItemId() + "]!");
				return;
			}

			double chance = enchantLevel.getBaseChance();
			if(item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
				chance = enchantLevel.getFullBodyChance();
			else if(item.getTemplate().isMagicWeapon())
				chance = enchantLevel.getMagicWeaponChance();

			if(catalyst != null)
				chance += ItemFunctions.getCatalystPower(catalyst.getItemId());
			if(player.getBonus().getEnchantAdd() > 0.)
				chance += player.getBonus().getEnchantAdd();

			if(item.getGrade() != ItemGrade.NONE)
				chance *= player.getEnchantChanceModifier();

			if(item.isWeapon())
				WorldStatisticsManager.getInstance().updateStat(player, CategoryType.WEAPON_ENCHANT_TRY, item.getGrade().extOrdinal(), item.getEnchantLevel() + 1);
			else
				WorldStatisticsManager.getInstance().updateStat(player, CategoryType.ARMOR_ENCHANT_TRY, item.getGrade().extOrdinal(), item.getEnchantLevel() + 1);

			boolean isLuckTriggered = Formulas.calcLuckEnchant(player);
			
			if(Rnd.chance(chance) || isLuckTriggered)
			{
				item.setEnchantLevel(newEnchantLvl);
				item.setJdbcState(JdbcEntityState.UPDATED);
				item.update();

				if(equipped)
				{
					inventory.equipItem(item);
					inventory.isRefresh = false;
				}

				player.sendPacket(new InventoryUpdatePacket().addModifiedItem(player, item));

				if(item.isWeapon())
					WorldStatisticsManager.getInstance().updateStat(player, CategoryType.WEAPON_ENCHANT_MAX, item.getGrade().extOrdinal(), item.getEnchantLevel());
				else
					WorldStatisticsManager.getInstance().updateStat(player, CategoryType.ARMOR_ENCHANT_MAX, item.getGrade().extOrdinal(), item.getEnchantLevel());

				player.sendPacket(new EnchantResultPacket(0, 0, 0, item.getEnchantLevel()));

				if(isLuckTriggered)
				{
					player.sendPacket(new SystemMessage(4244)); //Lady Luck smiles on you
					player.broadcastPacket(new SystemMessage(SystemMessage.C1_HAS_SUCCESSFULY_ENCHANTED_A__S2_S3).addName(player).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
					player.broadcastPacket(new MagicSkillUse(player, player, 18103, 1, 500, 1500)); //visual effect four-leaves clover
				}
				else if(enchantLevel.haveSuccessVisualEffect())
				{
					player.broadcastPacket(new SystemMessage(SystemMessage.C1_HAS_SUCCESSFULY_ENCHANTED_A__S2_S3).addName(player).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
					player.broadcastPacket(new MagicSkillUse(player, player, SUCCESS_VISUAL_EFF_ID, 1, 500, 1500));
				}
			}
			else
			{
				switch(enchantScroll.getResultType())
				{
					case CRYSTALS:
						if(item.isEquipped())
							player.sendDisarmMessage(item);

						Log.LogItem(player, Log.EnchantFail, item);

						if(!inventory.destroyItem(item, 1L))
						{
							player.sendActionFailed();
							return;
						}

						int crystalId = item.getGrade().getCrystalId();
						if(crystalId > 0 && item.getCrystalCountOnEchant() > 0)
						{
							int crystalAmount = item.getCrystalCountOnEchant() / 2;

							player.sendPacket(new EnchantResultPacket(1, crystalId, crystalAmount, 0));
							ItemFunctions.addItem(player, crystalId, crystalAmount, true);
						}
						else
							player.sendPacket(EnchantResultPacket.FAILED_NO_CRYSTALS);

						if(enchantScroll.showFailEffect())
							player.broadcastPacket(new MagicSkillUse(player, player, FAIL_VISUAL_EFF_ID, 1, 500, 1500));
						break;
					case DROP_ENCHANT:
						item.setEnchantLevel(0);
						item.setJdbcState(JdbcEntityState.UPDATED);
						item.update();

						if(equipped)
						{
							inventory.equipItem(item);
							inventory.isRefresh = false;
						}

						player.sendPacket(new InventoryUpdatePacket().addModifiedItem(player, item));
						player.sendPacket(SystemMsg.THE_BLESSED_ENCHANT_FAILED);
						player.sendPacket(EnchantResultPacket.BLESSED_FAILED);
						break;
					case NOTHING:
						player.sendPacket(EnchantResultPacket.ANCIENT_FAILED);
						break;
				}
			}
		}
		finally
		{
			inventory.writeUnlock();

			player.updateStats();
		}

		player.setLastEnchantItemTime(System.currentTimeMillis());
	}
}