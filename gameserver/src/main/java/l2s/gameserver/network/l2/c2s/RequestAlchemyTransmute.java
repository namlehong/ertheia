package l2s.gameserver.network.l2.c2s;

import l2s.commons.util.Rnd;
import l2s.gameserver.data.xml.holder.RecipeHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExAlchemyCombinationResult;
import l2s.gameserver.network.l2.s2c.ExAlchemyTransmuteResult;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.templates.item.RecipeTemplate;
import l2s.gameserver.templates.item.RecipeTemplate.RecipeComponent;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Hien Son
**/

public final class RequestAlchemyTransmute extends L2GameClientPacket
{
	private int producingAmount;
	private int alchemySkillId;
	private int alchemySkillLevel;
	
	@Override
	protected void readImpl()
	{
		producingAmount 	= readD();
		readH();			//this is always 0x0A, don't know what does it mean
		alchemySkillId 	= readD();
		alchemySkillLevel	= readD();
		// read the ingredients
		// *not necessary*
		// readD(): number of ingredients
		// each block of ingredient:
		// readD(): ingredient id
		// readQ(): ingredient amount
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInStoreMode())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isProcessingRequest())
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			activeChar.sendActionFailed();
			return;
		}

		RecipeTemplate recipe = RecipeHolder.getInstance().getRecipeByRecipeId(alchemySkillId*100 + alchemySkillLevel);

		if(recipe == null || recipe.getMaterials().length == 0)
		{
			activeChar.sendPacket(SystemMsg.EXPERIMENT_FAILED_PLEASE_TRY_AGAIN);
			activeChar.sendActionFailed();
			return;
		}
		
		if(activeChar.getSkillLevel(alchemySkillId, 0) < alchemySkillLevel)
		{
			activeChar.sendPacket(SystemMsg.YOU_MUST_LEARN_THE_NECESSARY_SKILL_FIRST);
			activeChar.sendActionFailed();
			return;
		}
		boolean isSuccess = Rnd.chance(recipe.getSuccessRate());
		activeChar.getInventory().writeLock();
		try
		{
			RecipeComponent[] materials = recipe.getMaterials();

			for(RecipeComponent material : materials)
			{
				if(material.getCount() == 0)
					continue;

				ItemInstance item = activeChar.getInventory().getItemByItemId(material.getItemId());
				if(item == null || item.getCount() < material.getCount()*producingAmount)
				{
					activeChar.sendPacket(SystemMsg.NOT_ENOUGH_INGREDIENTS);
					return;
				}
			}

			for(RecipeComponent material : materials)
			{
				long itemCount = material.getCount()*producingAmount;
				
				if(itemCount == 0)
					continue;
				
				//in case the experiment fails, only remove some of the ingredient
				if(!isSuccess)
					itemCount = Math.round(Math.random()*itemCount);
				
				if(!activeChar.getInventory().destroyItemByItemId(material.getItemId(), itemCount))
					continue;
				activeChar.sendPacket(SystemMessagePacket.removeItems(material.getItemId(), itemCount));
			}
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}

		RecipeComponent product = recipe.getRandomProduct();
		int itemId = product.getItemId();
		long itemsCount = product.getCount()*producingAmount;
		
		if(isSuccess)
		{
			ItemFunctions.addItem(activeChar, itemId, itemsCount, true);
			activeChar.sendPacket(new ExAlchemyTransmuteResult(itemsCount));
		}
		else
		{
			activeChar.sendPacket(new SystemMessagePacket(SystemMsg.EXPERIMENT_FAILED_PLEASE_TRY_AGAIN));
			activeChar.sendPacket(new ExAlchemyTransmuteResult(0));
		}

	}
}
