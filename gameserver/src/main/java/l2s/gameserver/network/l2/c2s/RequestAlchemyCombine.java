package l2s.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.List;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExAlchemyCombinationResult;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Hien Son
**/

public final class RequestAlchemyCombine extends L2GameClientPacket
{
	private int 					ingredientAmount 	= 0;
	private List<CombineMaterial> 	materialList 		= new ArrayList<CombineMaterial>();
	private final int 				AIR_STONE			= 39461;
	private final int 				TEMPEST_STONE		= 39592;
	
	@Override
	protected void readImpl()
	{
		ingredientAmount = readD();
		for(int i=0; i<ingredientAmount; i++)
		{
			CombineMaterial mat = new CombineMaterial(readD(), readQ());
			
			materialList.add(mat);
		}
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
			return;
		}

		if(activeChar.getSkillLevel(17920, 0) < 1)
		{
			activeChar.sendPacket(SystemMsg.YOU_MUST_LEARN_THE_NECESSARY_SKILL_FIRST);
			return;
		}
		
		activeChar.getInventory().writeLock();
		try
		{
			for(CombineMaterial material : materialList)
			{
				if(material.getCount() == 0)
					continue;

				ItemInstance item = activeChar.getInventory().getItemByObjectId(material.getObjectID());
				if(item == null || item.getCount() < material.getCount())
				{
					activeChar.sendPacket(SystemMsg.NOT_ENOUGH_INGREDIENTS);
					return;
				}
			}

			long stoneAmount = 0;
			long totalPrice = 0;
			
			for(CombineMaterial material : materialList)
			{
				long itemCount = material.getCount();
				
				if(itemCount == 0)
					continue;
				
				ItemInstance item = activeChar.getInventory().getItemByObjectId(material.getObjectID());
				
				if(!item.canBeSold(activeChar) && item.getItemId() != 57)
					continue;
				
				if(!activeChar.getInventory().destroyItemByObjectId(material.getObjectID(), itemCount))
					continue;
				activeChar.sendPacket(SystemMessagePacket.removeItems(item.getItemId(), itemCount));
				
				totalPrice = totalPrice + item.getReferencePrice()*itemCount;
			}
			
			stoneAmount = getAirtoneAmount(totalPrice);
			
			if(materialList.size() > 2)
				stoneAmount = (long) Math.round(stoneAmount*5/3);
			
			//System.out.println("Stone amount " + stoneAmount);
			//there is a chance 1/5000 to have tempest stone
			//with the conversion rate 600 air stone to 1 tempest stone
			if(stoneAmount > 600)
			{
				if(Rnd.chance(0.02))
				{
					stoneAmount = (long)Math.floor(stoneAmount/600);
					ItemFunctions.addItem(activeChar, TEMPEST_STONE, stoneAmount, true);
					activeChar.sendPacket(new ExAlchemyCombinationResult(TEMPEST_STONE, stoneAmount));
				}
				else
				{
					ItemFunctions.addItem(activeChar, AIR_STONE, stoneAmount, true);
					activeChar.sendPacket(new ExAlchemyCombinationResult(AIR_STONE, stoneAmount));
				}
			}
			else
			{
				ItemFunctions.addItem(activeChar, AIR_STONE, stoneAmount, true);
				activeChar.sendPacket(new ExAlchemyCombinationResult(AIR_STONE, stoneAmount));
			}
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}

	}
	
	private long getAirtoneAmount(long itemPrice)
	{
		long stoneCount = 0;
		
		long fiftyMul = (long) Math.floor(itemPrice / 50000);
		long adenaLeft = (long) itemPrice%50000;
		long oddStoneCount;
		
		if(adenaLeft < 20000) oddStoneCount = 0;
		else if(adenaLeft < 35000) oddStoneCount = 1;
		else oddStoneCount = 2;
		
		stoneCount = fiftyMul*3 + oddStoneCount;
		
		return stoneCount;
	}
	
	private final class CombineMaterial
	{
		public int 		_obj_id;
		public long		_count;
		
		public CombineMaterial(int obj_id, long count)
		{
			_obj_id = obj_id;
			_count = count;
		}
		
		public int getObjectID()
		{
			return _obj_id;
		}
		
		public long getCount()
		{
			return _count;
		}
	}
}
