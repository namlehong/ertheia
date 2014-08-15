package l2s.gameserver.model.items.listeners;

import l2s.gameserver.listener.inventory.OnEquipListener;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.skills.EffectType;

public final class AccessoryListener implements OnEquipListener
{
	private static final AccessoryListener _instance = new AccessoryListener();

	public static AccessoryListener getInstance()
	{
		return _instance;
	}

	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable())
			return;

		Player player = (Player) actor;

		if(item.getBodyPart() == ItemTemplate.SLOT_L_BRACELET && item.getTemplate().getAttachedSkills().length > 0)
		{
			int agathionId = player.getAgathionId();
			int transformNpcId = player.getTransformId();
			for(Skill skill : item.getTemplate().getAttachedSkills())
			{
				if(agathionId > 0 && skill.getNpcId() == agathionId)
					player.setAgathion(0);
				if(skill.getNpcId() == transformNpcId && skill.hasEffect(EffectType.Transformation))
					player.setTransform(null);
			}
		}

		if(item.isAccessory() || item.getTemplate().isTalisman() || item.getTemplate().isBracelet() || item.getTemplate().isBrooch() || item.getTemplate().isJewel())
			player.sendUserInfo(true);
		// TODO [G1ta0] отладить отображение аксессуаров
		//player.sendPacket(new ItemListPacket(player, false));
		else
			player.broadcastCharInfo();
	}

	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		if(!item.isEquipable())
			return;

		Player player = (Player) actor;

		if(item.isAccessory() || item.getTemplate().isTalisman() || item.getTemplate().isBracelet() || item.getTemplate().isBrooch() || item.getTemplate().isJewel())
			player.sendUserInfo(true);
		// TODO [G1ta0] отладить отображение аксессуаров
		//player.sendPacket(new ItemListPacket(player, false));
		else
			player.broadcastCharInfo();
	}
}