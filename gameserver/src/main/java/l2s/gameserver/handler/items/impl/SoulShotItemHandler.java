package l2s.gameserver.handler.items.impl;

import l2s.commons.util.Rnd;
import l2s.gameserver.instancemanager.WorldStatisticsManager;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.worldstatistics.CategoryType;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.item.ItemGrade;
import l2s.gameserver.templates.item.WeaponTemplate;

public class SoulShotItemHandler extends DefaultItemHandler
{
	private static final int[] GRADE_SKILLS = { 2039, 2150, 2151, 2152, 2153, 2154, 9193 };

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		Player player = (Player) playable;

		ItemInstance weaponInst = player.getActiveWeaponInstance();
		WeaponTemplate weaponItem = player.getActiveWeaponTemplate();

		int shotId = item.getItemId();
		boolean isAutoSoulShot = false;

		if(player.getAutoSoulShot().contains(shotId))
			isAutoSoulShot = true;

		if(weaponInst == null)
		{
			if(!isAutoSoulShot)
				player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		// soulshot is already active
		if(weaponInst.getChargedSoulshot() != ItemInstance.CHARGED_NONE)
			return false;

		int ssConsumption = weaponItem.getSoulShotCount();
		if(ssConsumption <= 0)
		{
			// Can't use soulshots
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(shotId);
				player.sendPacket(new ExAutoSoulShot(shotId, false));
				player.sendPacket(new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(shotId));
				return false;
			}
			player.sendPacket(SystemMsg.CANNOT_USE_SOULSHOTS);
			return false;
		}

		int[] reducedSoulshot = weaponItem.getReducedSoulshot();
		if(reducedSoulshot[0] > 0 && Rnd.chance(reducedSoulshot[0]))
			ssConsumption = reducedSoulshot[1];

		if(ssConsumption <= 0)
			return false;

		int grade = weaponItem.getGrade().extOrdinal();
		if(grade != item.getGrade().extOrdinal())
		{
			// wrong grade for weapon
			if(isAutoSoulShot)
				return false;

			player.sendPacket(SystemMsg.THE_SOULSHOT_YOU_ARE_ATTEMPTING_TO_USE_DOES_NOT_MATCH_THE_GRADE_OF_YOUR_EQUIPPED_WEAPON);
			return false;
		}

		if(!player.getInventory().destroyItem(item, ssConsumption))
		{
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(shotId);
				player.sendPacket(new ExAutoSoulShot(shotId, false));
				player.sendPacket(new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(shotId));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT);
			return false;
		}

		if(grade != ItemGrade.NONE.ordinal())
			WorldStatisticsManager.getInstance().updateStat(player, CategoryType.SS_CONSUMED, weaponItem.getGrade().extOrdinal(), ssConsumption);

		weaponInst.setChargedSoulshot(ItemInstance.CHARGED_SOULSHOT);
		player.sendPacket(SystemMsg.YOUR_SOULSHOTS_ARE_ENABLED);
		player.broadcastPacket(new MagicSkillUse(player, player, GRADE_SKILLS[grade], 1, 0, 0));
		return true;
	}
}