package services;

import java.util.Collection;

import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.instancemanager.ServerVariables;
import l2s.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.actor.instances.player.SubClass;
import l2s.gameserver.model.actor.instances.player.SubClassList;
import l2s.gameserver.model.actor.listener.CharListenerList;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
**/
public class Hi5ToGODTransfer extends Functions implements ScriptFile
{
	private class Listener implements OnPlayerEnterListener
	{
		@Override
		public void onPlayerEnter(Player player)
		{
			if(player.getCreateTime() > _godTransferDate)
				return;

			if(player.getVarBoolean(TRANSFER_VAR))
				return;

			checkTransferSkills(player);
			//addStoneOfDestiny(player); DEPRECATED IN LINDVIOR UPDATE
			addSubClassCirtificate(player);

			player.setVar(TRANSFER_VAR, true);
		}
	}

	private static final int STONE_OF_DESTINY = 17722;
	private static final int CHANGE_CLASS_CERTIFICATE = 30433;
	private static final int[] PEDANTS = { 15307, 15308, 15309 };
	private static final String TRANSFER_VAR = "@is_god_transfered";
	private static final String SERVER_TRANSFER_VAR = "@god_transfer_date";

	private final Listener _listener = new Listener();
	private long _godTransferDate = 0;

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(_listener);
		_godTransferDate = ServerVariables.getLong(SERVER_TRANSFER_VAR, 0) * 1000L;
		if(_godTransferDate == 0)
		{
			_godTransferDate = System.currentTimeMillis();
			ServerVariables.set(SERVER_TRANSFER_VAR, _godTransferDate / 1000L);
		}
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	private static void checkTransferSkills(Player player)
	{
		for(int pedantId : PEDANTS)
		{
			long countInInventory = player.getInventory().getCountOf(pedantId);
			long countInWarehouse = player.getWarehouse().getCountOf(pedantId);
			player.getInventory().removeItemByItemId(pedantId, countInInventory);
			player.getWarehouse().removeItemByItemId(pedantId, countInWarehouse);
		}

		SubClassList subClassList = player.getSubClassList();
		for(SubClass sub : subClassList.values())
		{
			AcquireType type = AcquireType.transferType(sub.getClassId());
			if(type == null)
				continue;

			Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(null, type);
			if(skills.isEmpty())
				continue;

			for(SkillLearn skill : skills)
				player.removeSkill(skill.getId(), true);

			ClassId classId = ClassId.VALUES[sub.getClassId()];
			switch(classId)
			{
				case CARDINAL:
					ItemFunctions.addItem(player, PEDANTS[0], 7, false);
					break;
				case EVAS_SAINT:
					ItemFunctions.addItem(player, PEDANTS[1], 7, false);
					break;
				case SHILLIEN_SAINT:
					ItemFunctions.addItem(player, PEDANTS[2], 7, false);
					break;
			}
		}
	}

	private static void addStoneOfDestiny(Player player)
	{
		SubClass baseSubClass = player.getBaseSubClass();
		if(baseSubClass.getLevel() <= 75)
			return;

		ItemFunctions.addItem(player, STONE_OF_DESTINY, 1, false);
	}

	private static void addSubClassCirtificate(Player player)
	{
		int subClassCount = 0;
		SubClassList subClassList = player.getSubClassList();
		for(SubClass sub : subClassList.values())
		{
			if(sub.isBase())
				continue;

			if(sub.getLevel() <= 40)
				continue;

			subClassCount++;
		}

		ItemInstance item = ItemFunctions.createItem(CHANGE_CLASS_CERTIFICATE);
		item.setCount(subClassCount);

		player.getWarehouse().addItem(item);
	}
}