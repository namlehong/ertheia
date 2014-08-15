package services.petevolve;

import l2s.commons.dao.JdbcEntityState;
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
 * Date: 16.05.2008
 * Time: 12:19:36
 */
public class wolfevolve extends Functions
{
	private static final int WOLF = PetDataHolder.PET_WOLF_ID; //Чтоб было =), проверка на Wolf
	private static final int GREAT_WOLF_NPC_ID = PetDataHolder.GREAT_WOLF_ID;
	private static final int WOLF_COLLAR = 2375; // Ошейник Wolf
	private static final int GREAT_WOLF_NECKLACE = 9882; // Ожерелье Great Wolf

	public void evolve()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;

		PetInstance pl_pet = player.getPet();
		if(player.getInventory().getItemByItemId(WOLF_COLLAR) == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		if(pl_pet == null || pl_pet.isDead())
		{
			show("scripts/services/petevolve/evolve_no.htm", player, npc);
			return;
		}
		if(pl_pet.getNpcId() != WOLF)
		{
			show("scripts/services/petevolve/no_wolf.htm", player, npc);
			return;
		}
		if(pl_pet.getLevel() < 55)
		{
			show("scripts/services/petevolve/no_level.htm", player, npc);
			return;
		}

		int controlItemId = pl_pet.getControlItemObjId();
		pl_pet.unSummon(false);

		NpcTemplate template = NpcHolder.getInstance().getTemplate(GREAT_WOLF_NPC_ID);
		if(template == null)
			return;

		ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
		control.setItemId(GREAT_WOLF_NECKLACE);
		control.setEnchantLevel(template.level);
		control.setJdbcState(JdbcEntityState.UPDATED);
		control.update();

		player.sendItemList(false);

		show("scripts/services/petevolve/yes_wolf.htm", player, npc);
	}
}