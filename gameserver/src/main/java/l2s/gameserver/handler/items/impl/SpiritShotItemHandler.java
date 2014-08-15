package l2s.gameserver.handler.items.impl;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.item.WeaponTemplate;

public class SpiritShotItemHandler extends DefaultItemHandler
{
	private static final int[] GRADE_SKILLS = { 2061, 2155, 2156, 2157, 2158, 2159, 9194 };

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
				player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SPIRITSHOTS);
			return false;
		}

		// spiritshot is already active
		if(weaponInst.getChargedSpiritshot() != ItemInstance.CHARGED_NONE)
			return false;

		int spsConsumption = weaponItem.getSpiritShotCount();
		if(spsConsumption <= 0)
		{
			// Can't use Spiritshots
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(shotId);
				player.sendPacket(new ExAutoSoulShot(shotId, false));
				player.sendPacket(new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(shotId));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_MAY_NOT_USE_SPIRITSHOTS);
			return false;
		}

		int[] reducedSpiritshot = weaponItem.getReducedSpiritshot();
		if(reducedSpiritshot[0] > 0 && Rnd.chance(reducedSpiritshot[0]))
			spsConsumption = reducedSpiritshot[1];

		if(spsConsumption <= 0)
			return false;

		int grade = weaponItem.getGrade().extOrdinal();
		if(grade != item.getGrade().extOrdinal())
		{
			// wrong grade for weapon
			if(isAutoSoulShot)
				return false;

			player.sendPacket(SystemMsg.YOUR_SPIRITSHOT_DOES_NOT_MATCH_THE_WEAPONS_GRADE);
			return false;
		}

		if(!player.getInventory().destroyItem(item, spsConsumption))
		{
			if(isAutoSoulShot)
			{
				player.removeAutoSoulShot(shotId);
				player.sendPacket(new ExAutoSoulShot(shotId, false));
				player.sendPacket(new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(shotId));
				return false;
			}
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT);
			return false;
		}

		weaponInst.setChargedSpiritshot(ItemInstance.CHARGED_SPIRITSHOT);
		player.sendPacket(SystemMsg.YOUR_SPIRITSHOT_HAS_BEEN_ENABLED);
		player.broadcastPacket(new MagicSkillUse(player, player, GRADE_SKILLS[grade], 1, 0, 0));
		return true;
	}
}