package l2s.gameserver.handler.items.impl;

import java.util.List;

import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.SkillListPacket;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 */
public class ForgottenScrollItemHandler extends DefaultItemHandler
{
	private static final int FORGOTTEN_SCROLL_SKILL_ID = 2790;

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!playable.isPlayer())
			return false;

		Player player = (Player) playable;

		if(item.getCount() < 1)
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			return false;
		}

		List<SkillLearn> list = SkillAcquireHolder.getInstance().getSkillLearnListByItemId(player, item.getItemId());
		if(list.isEmpty())
		{
			player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		// проверяем ли есть нужные скилы
		boolean alreadyHas = true;
		for(SkillLearn learn : list)
		{
			if(player.getSkillLevel(learn.getId()) != learn.getLevel())
			{
				alreadyHas = false;
				break;
			}
		}
		if(alreadyHas)
		{
			player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		// проверка по уровне
		boolean wrongLvl = false;
		for(SkillLearn learn : list)
		{
			if(player.getLevel() < learn.getMinLevel())
				wrongLvl = true;
		}

		if(wrongLvl)
		{
			player.sendPacket(new SystemMessagePacket(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
			return false;
		}

		if(!player.consumeItem(item.getItemId(), 1L))
			return false;

		for(SkillLearn skillLearn : list)
		{
			Skill skill = SkillTable.getInstance().getInfo(skillLearn.getId(), skillLearn.getLevel());
			if(skill == null)
				continue;

			player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(), skill.getLevel()));

			player.addSkill(skill, true);
		}

		player.updateStats();
		player.sendPacket(new SkillListPacket(player));
		// Анимация изучения книги над головой чара (на самом деле, для каждой книги своя анимация, но они одинаковые)
		player.broadcastPacket(new MagicSkillUse(player, player, FORGOTTEN_SCROLL_SKILL_ID, 1, 1, 0));
		return true;
	}
}