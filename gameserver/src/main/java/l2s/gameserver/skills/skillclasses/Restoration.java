package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.data.xml.holder.RestorationInfoHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.skill.restoration.RestorationInfo;
import l2s.gameserver.templates.skill.restoration.RestorationItem;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
 */
public class Restoration extends Skill
{
	public Restoration(final StatsSet set)
	{
		super(set);
	}

	@Override
	public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!activeChar.isPlayable())
			return false;

		if(activeChar.isPlayer())
		{
			Player player = (Player) activeChar;
			if(player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - 10)
			{
				player.sendPacket(SystemMsg.THE_CORRESPONDING_WORK_CANNOT_BE_PROCEEDED_BECAUSE_THE_INVENTORY_WEIGHTQUANTITY_LIMIT_HAS_BEEN_EXCEEDED);
				return false;
			}
		}
		return true;
	}

	@Override
	public void useSkill(final Creature activeChar, final List<Creature> targets)
	{
		if(!activeChar.isPlayable())
			return;

		RestorationInfo restorationInfo = RestorationInfoHolder.getInstance().getRestorationInfo(this);
		if(restorationInfo == null)
			return;

		Playable playable = (Playable) activeChar;
		int itemConsumeId = restorationInfo.getItemConsumeId();
		int itemConsumeCount = restorationInfo.getItemConsumeCount();
		if(itemConsumeId > 0 && itemConsumeCount > 0)
		{
			if(ItemFunctions.getItemCount(playable, itemConsumeId) < itemConsumeCount)
			{
				playable.sendPacket(SystemMsg.THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL);
				return;
			}

			ItemFunctions.removeItem(playable, itemConsumeId, itemConsumeCount, true);
		}

		List<RestorationItem> restorationItems = restorationInfo.getRandomGroupItems();
		if(restorationItems == null || restorationItems.size() == 0)
			return;

		for(Creature target : targets)
		{
			if(target != null)
			{
				for(RestorationItem item : restorationItems)
				{
					ItemFunctions.addItem(playable, item.getId(), item.getRandomCount(), true);
				}

				getEffects(activeChar, target, false);
			}
		}

		super.useSkill(activeChar, targets);
	}
}