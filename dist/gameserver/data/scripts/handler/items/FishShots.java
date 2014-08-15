package handler.items;

import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.item.WeaponTemplate;
import l2s.gameserver.templates.item.WeaponTemplate.WeaponType;

public class FishShots extends ScriptItemHandler
{
	private static int[] _skillIds = { 2181, 2182, 2183, 2184, 2185, 2186 };

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;
		int FishshotId = item.getItemId();

		boolean isAutoSoulShot = false;
		if(player.getAutoSoulShot().contains(FishshotId))
			isAutoSoulShot = true;

		ItemInstance weaponInst = player.getActiveWeaponInstance();
		WeaponTemplate weaponItem = player.getActiveWeaponTemplate();

		if(weaponInst == null || weaponItem.getItemType() != WeaponType.ROD)
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		// spiritshot is already active
		if(weaponInst.getChargedFishshot())
			return false;

		if(item.getCount() < 1)
		{
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(FishshotId);
				player.sendPacket(new ExAutoSoulShot(FishshotId, false), new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addString(item.getName()));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT);
			return false;
		}

		int grade = weaponItem.getGrade().extOrdinal();

		if(grade == 0 && FishshotId != 6535 || grade == 1 && FishshotId != 6536 || grade == 2 && FishshotId != 6537 || grade == 3 && FishshotId != 6538 || grade == 4 && FishshotId != 6539 || grade == 5 && FishshotId != 6540)
		{
			if(isAutoSoulShot)
				return false;
			player.sendPacket(SystemMsg.THAT_IS_THE_WRONG_GRADE_OF_SOULSHOT_FOR_THAT_FISHING_POLE);
			return false;
		}

		if(player.getInventory().destroyItem(item, 1L))
		{
			weaponInst.setChargedFishshot(true);
			player.sendPacket(SystemMsg.YOUR_SPIRITSHOT_HAS_BEEN_ENABLED);
			player.broadcastPacket(new MagicSkillUse(player, player, _skillIds[grade], 1, 0, 0));
		}
		return true;
	}
}
