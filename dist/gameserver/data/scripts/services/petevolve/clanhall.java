package services.petevolve;

import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.data.xml.holder.PetDataHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.PetInstance;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.templates.pet.PetData;

public class clanhall extends Functions
{
	// -- Pet ID --
	private static final int GREAT_WOLF = PetDataHolder.GREAT_WOLF_ID;
	private static final int WHITE_WOLF = PetDataHolder.WGREAT_WOLF_ID;
	private static final int FENRIR = PetDataHolder.FENRIR_WOLF_ID;
	private static final int WHITE_FENRIR = PetDataHolder.WFENRIR_WOLF_ID;
	private static final int WIND_STRIDER = PetDataHolder.STRIDER_WIND_ID;
	private static final int RED_WIND_STRIDER = PetDataHolder.RED_STRIDER_WIND_ID;
	private static final int STAR_STRIDER = PetDataHolder.STRIDER_STAR_ID;
	private static final int RED_STAR_STRIDER = PetDataHolder.RED_STRIDER_STAR_ID;
	private static final int TWILING_STRIDER = PetDataHolder.STRIDER_TWILIGHT_ID;
	private static final int RED_TWILING_STRIDER = PetDataHolder.RED_STRIDER_TWILIGHT_ID;

	// -- First Item ID --
	private static final int GREAT_WOLF_NECKLACE = 9882;
	private static final int FENRIR_NECKLACE = 10426;
	private static final int WIND_STRIDER_ITEM = 4422;
	private static final int STAR_STRIDER_ITEM = 4423;
	private static final int TWILING_STRIDER_ITEM = 4424;

	// -- Second Item ID --
	private static final int WHITE_WOLF_NECKLACE = 10307;
	private static final int WHITE_FENRIR_NECKLACE = 10611;
	private static final int RED_WS_ITEM = 10308;
	private static final int RED_SS_ITEM = 10309;
	private static final int RED_TW_ITEM = 10310;

	public void greatsw(String[] direction)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if(player.getInventory().getCountOf(fwd ? GREAT_WOLF_NECKLACE : WHITE_WOLF_NECKLACE) > 1)
		{
			show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if(player.getServitors().length > 0)
		{
			show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? GREAT_WOLF_NECKLACE : WHITE_WOLF_NECKLACE);
		if(collar == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}

		PetData petDataTemplate = PetDataHolder.getInstance().getTemplateByItemId(collar.getItemId());
		if(petDataTemplate == null)
			return;

		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(petDataTemplate.getNpcId());
		if(petTemplate == null)
			return;

		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if(petDataTemplate.getNpcId() != (fwd ? GREAT_WOLF : WHITE_WOLF))
		{
			show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if(pet.getLevel() < 55)
		{
			show("scripts/services/petevolve/error_lvl_greatw.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? WHITE_WOLF_NECKLACE : GREAT_WOLF_NECKLACE);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessagePacket.obtainItems((fwd ? WHITE_WOLF_NECKLACE : GREAT_WOLF_NECKLACE), 1, 0));
		show("scripts/services/petevolve/end_msg3_gwolf.htm", player, npc);
	}

	public void fenrir(String[] direction)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if(player.getInventory().getCountOf(fwd ? FENRIR_NECKLACE : WHITE_FENRIR_NECKLACE) > 1)
		{
			show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if(player.getServitors().length > 0)
		{
			show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? FENRIR_NECKLACE : WHITE_FENRIR_NECKLACE);
		if(collar == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}

		PetData petDataTemplate = PetDataHolder.getInstance().getTemplateByItemId(collar.getItemId());
		if(petDataTemplate == null)
			return;

		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(petDataTemplate.getNpcId());
		if(petTemplate == null)
			return;

		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if(petDataTemplate.getNpcId() != (fwd ? FENRIR : WHITE_FENRIR))
		{
			show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if(pet.getLevel() < 55)
		{
			show("scripts/services/petevolve/error_lvl_fenrir.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? WHITE_FENRIR_NECKLACE : FENRIR_NECKLACE);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessagePacket.obtainItems((fwd ? WHITE_FENRIR_NECKLACE : FENRIR_NECKLACE), 1, 0));
		show("scripts/services/petevolve/end_msg2_fenrir.htm", player, npc);
	}

	public void wstrider(String[] direction)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if(player.getInventory().getCountOf(fwd ? WIND_STRIDER_ITEM : RED_WS_ITEM) > 1)
		{
			show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if(player.getServitors().length > 0)
		{
			show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? WIND_STRIDER_ITEM : RED_WS_ITEM);
		if(collar == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}

		PetData petDataTemplate = PetDataHolder.getInstance().getTemplateByItemId(collar.getItemId());
		if(petDataTemplate == null)
			return;

		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(petDataTemplate.getNpcId());
		if(petTemplate == null)
			return;

		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if(petDataTemplate.getNpcId() != (fwd ? WIND_STRIDER : RED_WIND_STRIDER))
		{
			show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if(pet.getLevel() < 55)
		{
			show("scripts/services/petevolve/error_lvl_strider.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? RED_WS_ITEM : WIND_STRIDER_ITEM);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessagePacket.obtainItems((fwd ? RED_WS_ITEM : WIND_STRIDER_ITEM), 1, 0));
		show("scripts/services/petevolve/end_msg_strider.htm", player, npc);
	}

	public void sstrider(String[] direction)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if(player.getInventory().getCountOf(fwd ? STAR_STRIDER_ITEM : RED_SS_ITEM) > 1)
		{
			show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if(player.getServitors().length > 0)
		{
			show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? STAR_STRIDER_ITEM : RED_SS_ITEM);
		if(collar == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}

		PetData petDataTemplate = PetDataHolder.getInstance().getTemplateByItemId(collar.getItemId());
		if(petDataTemplate == null)
			return;

		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(petDataTemplate.getNpcId());
		if(petTemplate == null)
			return;

		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if(petDataTemplate.getNpcId() != (fwd ? STAR_STRIDER : RED_STAR_STRIDER))
		{
			show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if(pet.getLevel() < 55)
		{
			show("scripts/services/petevolve/error_lvl_strider.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? RED_SS_ITEM : STAR_STRIDER_ITEM);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessagePacket.obtainItems((fwd ? RED_SS_ITEM : STAR_STRIDER_ITEM), 1, 0));
		show("scripts/services/petevolve/end_msg_strider.htm", player, npc);
	}

	public void tstrider(String[] direction)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if(player == null || npc == null)
			return;
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if(player.getInventory().getCountOf(fwd ? TWILING_STRIDER_ITEM : RED_TW_ITEM) > 1)
		{
			show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if(player.getServitors().length > 0)
		{
			show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? TWILING_STRIDER_ITEM : RED_TW_ITEM);
		if(collar == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}

		PetData petDataTemplate = PetDataHolder.getInstance().getTemplateByItemId(collar.getItemId());
		if(petDataTemplate == null)
			return;

		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(petDataTemplate.getNpcId());
		if(petTemplate == null)
			return;

		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if(petDataTemplate.getNpcId() != (fwd ? TWILING_STRIDER : RED_TWILING_STRIDER))
		{
			show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if(pet.getLevel() < 55)
		{
			show("scripts/services/petevolve/error_lvl_strider.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? RED_TW_ITEM : TWILING_STRIDER_ITEM);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessagePacket.obtainItems((fwd ? RED_TW_ITEM : TWILING_STRIDER_ITEM), 1, 0));
		show("scripts/services/petevolve/end_msg_strider.htm", player, npc);
	}
}