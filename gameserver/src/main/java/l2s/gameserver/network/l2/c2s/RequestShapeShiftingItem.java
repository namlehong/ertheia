package l2s.gameserver.network.l2.c2s;

import org.apache.commons.lang3.ArrayUtils;
import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.data.xml.holder.AppearanceStoneHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.items.PcInventory;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExPut_Shape_Shifting_Target_Item_Result;
import l2s.gameserver.network.l2.s2c.ExShape_Shifting_Result;
import l2s.gameserver.network.l2.s2c.InventoryUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.ExItemType;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.templates.item.ItemType;
import l2s.gameserver.templates.item.support.AppearanceStone;
import l2s.gameserver.templates.item.support.AppearanceStone.ShapeTargetType;
import l2s.gameserver.templates.item.support.AppearanceStone.ShapeType;

/**
 * @author Bonux
**/
public class RequestShapeShiftingItem extends L2GameClientPacket
{
	private int _targetItemObjId;

	@Override
	protected void readImpl()
	{
		_targetItemObjId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		if(player.isActionsDisabled() || player.isInStoreMode() || player.isInTrade())
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		PcInventory inventory = player.getInventory();
		ItemInstance targetItem = inventory.getItemByObjectId(_targetItemObjId);
		ItemInstance stone = player.getAppearanceStone();
		if(targetItem == null || stone == null)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		if(stone.getOwnerId() != player.getObjectId())
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		if(!targetItem.canBeAppearance())
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		if(targetItem.getLocation() != ItemInstance.ItemLocation.INVENTORY && targetItem.getLocation() != ItemInstance.ItemLocation.PAPERDOLL)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		if((stone = inventory.getItemByObjectId(stone.getObjectId())) == null)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		AppearanceStone appearanceStone = AppearanceStoneHolder.getInstance().getAppearanceStone(stone.getItemId());
		if(appearanceStone == null)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		if(appearanceStone.getType() != ShapeType.RESTORE && targetItem.getVisualId() > 0 || appearanceStone.getType() == ShapeType.RESTORE && targetItem.getVisualId() == 0)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		if(!targetItem.isAccessory() && targetItem.getGrade() == ItemGrade.NONE)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		ItemGrade[] stoneGrades = appearanceStone.getGrades();
		if(stoneGrades != null && stoneGrades.length > 0)
		{
			if(!ArrayUtils.contains(stoneGrades, targetItem.getGrade()))
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}
		}

		ShapeTargetType[] targetTypes = appearanceStone.getTargetTypes();
		if(targetTypes == null || targetTypes.length == 0)
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		if(!ArrayUtils.contains(targetTypes, ShapeTargetType.ALL))
		{
			if(targetItem.isWeapon())
			{
				if(!ArrayUtils.contains(targetTypes, ShapeTargetType.WEAPON))
				{
					player.sendPacket(ExShape_Shifting_Result.FAIL);
					player.setAppearanceStone(null);
					player.setAppearanceExtractItem(null);
					return;
				}
			}
			else if(targetItem.isArmor())
			{
				if(!ArrayUtils.contains(targetTypes, ShapeTargetType.ARMOR))
				{
					player.sendPacket(ExShape_Shifting_Result.FAIL);
					player.setAppearanceStone(null);
					player.setAppearanceExtractItem(null);
					return;
				}
			}
			else
			{
				if(!ArrayUtils.contains(targetTypes, ShapeTargetType.ACCESSORY))
				{
					player.sendPacket(ExShape_Shifting_Result.FAIL);
					player.setAppearanceStone(null);
					player.setAppearanceExtractItem(null);
					return;
				}
			}
		}

		ExItemType[] itemTypes = appearanceStone.getItemTypes();
		if(itemTypes != null && itemTypes.length > 0)
		{
			if(!ArrayUtils.contains(itemTypes, targetItem.getExType()))
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}
		}

		ItemInstance extracItem = player.getAppearanceExtractItem();
		int extracItemId = 0;
		if(appearanceStone.getType() != ShapeType.RESTORE && appearanceStone.getType() != ShapeType.FIXED)
		{
			if(extracItem == null)
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			if(!extracItem.canBeAppearance())
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			if(extracItem.getLocation() != ItemInstance.ItemLocation.INVENTORY && extracItem.getLocation() != ItemInstance.ItemLocation.PAPERDOLL)
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			/*if(!extracItem.isAccessory() && extracItem.getGrade() == ItemGrade.NONE)
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}*/

			if(!extracItem.isAccessory() && targetItem.getGrade().ordinal() < extracItem.getGrade().ordinal())
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			if(extracItem.getVisualId() > 0)
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			if(targetItem.getExType() != extracItem.getExType())
			{
				if(!(targetItem.getExType() == ExItemType.UPPER_PIECE && extracItem.getExType() == ExItemType.FULL_BODY && (extracItem.getBodyPart() == ItemTemplate.SLOT_FORMAL_WEAR || targetItem.getGrade().ordinal() < ItemGrade.R.ordinal())))
				{
					player.sendPacket(ExShape_Shifting_Result.FAIL);
					player.setAppearanceStone(null);
					player.setAppearanceExtractItem(null);
					return;
				}
			}

			// Запрет на обработку чужих вещей, баг может вылезти на серверных лагах
			if(extracItem.getOwnerId() != player.getObjectId())
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}
			extracItemId = extracItem.getItemId();
		}

		// Запрет на обработку чужих вещей, баг может вылезти на серверных лагах
		if(targetItem.getOwnerId() != player.getObjectId())
		{
			player.sendPacket(ExShape_Shifting_Result.FAIL);
			player.setAppearanceStone(null);
			player.setAppearanceExtractItem(null);
			return;
		}

		inventory.writeLock();
		try
		{
			long cost = appearanceStone.getCost();
			if(cost > player.getAdena())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_MODIFY_AS_YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			if(stone.getCount() < 1L)
			{
				player.sendPacket(ExShape_Shifting_Result.FAIL);
				player.setAppearanceStone(null);
				player.setAppearanceExtractItem(null);
				return;
			}

			if(appearanceStone.getType() == ShapeType.NORMAL)
			{
				if(!inventory.destroyItem(extracItem, 1L))
				{
					player.sendPacket(ExShape_Shifting_Result.FAIL);
					player.setAppearanceStone(null);
					player.setAppearanceExtractItem(null);
					return;
				}
			}

			inventory.destroyItem(stone, 1L);
			player.reduceAdena(cost);

			boolean equipped = false;
			if(equipped = targetItem.isEquipped())
			{
				inventory.isRefresh = true;
				inventory.unEquipItem(targetItem);
			}

			switch(appearanceStone.getType())
			{
				case RESTORE:
					targetItem.setVisualId(0);
					break;
				case NORMAL:
				case BLESSED:
					targetItem.setVisualId(extracItem.getItemId());
					break;
				case FIXED:
					targetItem.setVisualId(appearanceStone.getExtractItemId());
					break;
			}

			targetItem.setJdbcState(JdbcEntityState.UPDATED);
			targetItem.update();

			if(equipped)
			{
				inventory.equipItem(targetItem);
				inventory.isRefresh = false;
			}
			player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_SPENT_S1_ON_A_SUCCESSFUL_APPEARANCE_MODIFICATION).addLong(cost));
		}
		finally
		{
			inventory.writeUnlock();
		}

		player.sendPacket(new InventoryUpdatePacket().addModifiedItem(player, targetItem));

		player.setAppearanceStone(null);
		player.setAppearanceExtractItem(null);
		player.sendPacket(new ExShape_Shifting_Result(ExShape_Shifting_Result.SUCCESS_RESULT, targetItem.getItemId(), extracItemId));
	}
}