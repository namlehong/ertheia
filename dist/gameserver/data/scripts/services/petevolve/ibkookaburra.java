package services.petevolve;

import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.data.xml.holder.PetDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * User: darkevil
 * Date: 07.06.2008
 * Time: 0:37:55
 */
public class ibkookaburra extends Functions
{
	private static final int BABY_KOOKABURRA = PetDataHolder.BABY_KOOKABURRA_ID;
	private static final int IMPROVED_BABY_KOOKABURRA = PetDataHolder.IMPROVED_BABY_KOOKABURRA_ID;
	private static final int BABY_KOOKABURRA_OCARINA = 6650;
	private static final int IMPROVED_KOOKABURRA_OCARINA = 10313;

	public void evolve()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		PetInstance pet = player.getPet();
		if(player.getInventory().getItemByItemId(BABY_KOOKABURRA_OCARINA) == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		else if(pet == null || pet.isDead())
		{
			show("scripts/services/petevolve/evolve_no.htm", player, npc);
			return;
		}
		if(pet.getNpcId() != BABY_KOOKABURRA)
		{
			show("scripts/services/petevolve/no_pet.htm", player, npc);
			return;
		}
		if(Config.ALT_IMPROVED_PETS_LIMITED_USE && !player.isMageClass())
		{
			show("scripts/services/petevolve/no_class_m.htm", player, npc);
			return;
		}
		if(pet.getLevel() < 55)
		{
			show("scripts/services/petevolve/no_level.htm", player, npc);
			return;
		}

		int controlItemId = pet.getControlItemObjId();
		pet.unSummon(false);

		NpcTemplate template = NpcHolder.getInstance().getTemplate(IMPROVED_BABY_KOOKABURRA);
		if(template == null)
			return;

		ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
		control.setItemId(IMPROVED_KOOKABURRA_OCARINA);
		control.setEnchantLevel(template.level);
		control.setJdbcState(JdbcEntityState.UPDATED);
		control.update();

		player.sendItemList(false);

		show("scripts/services/petevolve/yes_pet.htm", player, npc);
	}
}