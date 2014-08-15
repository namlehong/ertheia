package handler.items;

import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;

public class BeastShot extends ScriptItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;

		Player player = (Player) playable;

		boolean isAutoSoulShot = false;
		if(player.getAutoSoulShot().contains(item.getItemId()))
			isAutoSoulShot = true;

		int deadServitors = 0;
		Servitor[] servitors = player.getServitors();
		if(servitors.length > 0)
		{
			loop: for(int i = 0; i < servitors.length; i++)
			{
				Servitor s = servitors[i];
				if(s.isDead())
				{
					deadServitors++;
					continue;
				}

				int consumption = 0;
				int skillid = 0;

				switch(item.getItemId())
				{
					case 6645:
					case 20332:
					{
						if(s.getChargedSoulShot())
							continue loop;

						consumption = s.getSoulshotConsumeCount();
						if(!player.getInventory().destroyItem(item, consumption))
						{
							player.sendPacket(SystemMsg.YOU_DONT_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_PETSERVITOR);
							return false;
						}
						s.chargeSoulShot();
						skillid = 2033;
						break;
					}
					case 6646:
					case 20333:
					{
						if(s.getChargedSpiritShot() > 0)
							continue loop;

						consumption = s.getSpiritshotConsumeCount();
						if(!player.getInventory().destroyItem(item, consumption))
						{
							player.sendPacket(SystemMsg.YOU_DONT_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_PETSERVITOR);
							return false;
						}
						s.chargeSpiritShot(ItemInstance.CHARGED_SPIRITSHOT);
						skillid = 2008;
						break;
					}
					case 6647:
					case 20334:
					{
						if(s.getChargedSpiritShot() > 1)
							continue loop;

						consumption = s.getSpiritshotConsumeCount();
						if(!player.getInventory().destroyItem(item, consumption))
						{
							player.sendPacket(SystemMsg.YOU_DONT_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_PETSERVITOR);
							return false;
						}
						s.chargeSpiritShot(ItemInstance.CHARGED_BLESSED_SPIRITSHOT);
						skillid = 2009;
						break;
					}
				}

				s.broadcastPacket(new MagicSkillUse(s, s, skillid, 1, 0, 0));
			}

			if(deadServitors == servitors.length && !isAutoSoulShot)
			{
				player.sendPacket(SystemMsg.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET_OR_SERVITOR);
				return false;
			}
		}
		else if(!isAutoSoulShot)
		{
			player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
			return false;
		}

		return true;
	}
}