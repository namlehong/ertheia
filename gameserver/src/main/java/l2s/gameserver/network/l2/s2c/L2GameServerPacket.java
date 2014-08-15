package l2s.gameserver.network.l2.s2c;

import l2s.commons.net.nio.impl.SendablePacket;
import l2s.gameserver.Config;
import l2s.gameserver.GameServer;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Element;
import l2s.gameserver.model.base.MultiSellIngredient;
import l2s.gameserver.model.items.CommissionItem;
import l2s.gameserver.model.items.ItemInfo;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.GameClient;
import l2s.gameserver.network.l2.ServerPacketOpcodes;
import l2s.gameserver.network.l2.components.IStaticPacket;
import l2s.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class L2GameServerPacket extends SendablePacket<GameClient> implements IStaticPacket
{
	private static final int IS_AUGMENTED = 1 << 0;
	private static final int IS_ELEMENTED = 1 << 1;
	private static final int HAVE_ENCHANT_OPTIONS = 1 << 2;
	private static final int VISUAL_CHANGED = 1 << 3;

	private static final Logger _log = LoggerFactory.getLogger(L2GameServerPacket.class);

	@Override
	public final boolean write()
	{
		try
		{
			if(writeOpcodes())
			{
				writeImpl();
				return true;
			}
		}
		catch(Exception e)
		{
			_log.error("Client: " + getClient() + " - Failed writing: " + getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber(), e);
		}
		return false;
	}

	protected ServerPacketOpcodes getOpcodes()
	{
		try
		{
			return ServerPacketOpcodes.valueOf(getClass().getSimpleName());
		}
		catch(Exception e)
		{
			_log.error("Cannot find serverpacket opcode: " + getClass().getSimpleName() + "!");
		}
		return null;
	}

	protected boolean writeOpcodes()
	{
		ServerPacketOpcodes opcodes = getOpcodes();
		if(opcodes == null)
			return false;

		int opcode = opcodes.getId();
		writeC(opcode);
		if(opcode == 0xFE)
			writeH(opcodes.getExId());

		return true;
	}

	protected abstract void writeImpl();

	protected void writeD(boolean b)
	{
		writeD(b ? 1 : 0);
	}

	protected void writeH(boolean b)
	{
		writeH(b ? 1 : 0);
	}

	protected void writeC(boolean b)
	{
		writeC(b ? 1 : 0);
	}

	/**
	 * Отсылает число позиций + массив
	 */
	protected void writeDD(int[] values, boolean sendCount)
	{
		if(sendCount)
			getByteBuffer().putInt(values.length);
		for(int value : values)
			getByteBuffer().putInt(value);
	}

	protected void writeDD(int[] values)
	{
		writeDD(values, false);
	}

	protected void writeItemInfo(ItemInstance item)
	{
		writeItemInfo(null, item, item.getCount());
	}

	protected void writeItemInfo(Player player, ItemInstance item)
	{
		writeItemInfo(player, item, item.getCount());
	}

	protected void writeItemInfo(ItemInstance item, long count)
	{
		writeItemInfo(null, item, count);
	}

	protected void writeItemInfo(Player player, ItemInstance item, long count)
	{
		int flags = 0;

		if(item.isAugmented())
			flags |= IS_AUGMENTED;

		if(item.getAttributeElementValue() > 0)
			flags |= IS_ELEMENTED;

		for(int enchantOption : item.getEnchantOptions())
		{
			if(enchantOption > 0)
			{
				flags |= HAVE_ENCHANT_OPTIONS;
				break;
			}
		}

		if(item.getVisualId() > 0)
			flags |= VISUAL_CHANGED;

		writeC(flags);
		writeD(item.getObjectId());
		writeD(item.getItemId());
		writeC(item.getEquipSlot());
		writeQ(count);
		writeC(item.getTemplate().getType2ForPackets());
		writeC(item.getCustomType1());
		writeC(item.isEquipped() ? 1 : 0);
		writeC(item.getCustomType2());
		writeQ(item.getBodyPart());
		writeH(item.getEnchantLevel());
		writeD(item.getShadowLifeTime());
		writeD(item.getTemporalLifeTime());

		if(player != null)
			writeC(!item.getTemplate().isBlocked(player, item));
		else
			writeC(0x01);

		if((flags & IS_AUGMENTED) == IS_AUGMENTED)
		{
			writeH(item.getVariation1Id());
			writeH(item.getVariation2Id());
		}

		if((flags & IS_ELEMENTED) == IS_ELEMENTED)
		{
			writeH(item.getAttackElement().getId());
			writeH(item.getAttackElementValue());
			writeH(item.getDefenceFire());
			writeH(item.getDefenceWater());
			writeH(item.getDefenceWind());
			writeH(item.getDefenceEarth());
			writeH(item.getDefenceHoly());
			writeH(item.getDefenceUnholy());
		}

		if((flags & HAVE_ENCHANT_OPTIONS) == HAVE_ENCHANT_OPTIONS)
		{
			writeH(item.getEnchantOptions()[0]);
			writeH(item.getEnchantOptions()[1]);
			writeH(item.getEnchantOptions()[2]);
		}

		if((flags & VISUAL_CHANGED) == VISUAL_CHANGED)
			writeD(item.getVisualId());
	}

	protected void writeItemInfo(ItemInfo item)
	{
		writeItemInfo(item, item.getCount());
	}

	protected void writeItemInfo(ItemInfo item, long count)
	{
		int flags = 0;

		if(item.getVariation1Id() > 0 || item.getVariation2Id() > 0)
			flags |= IS_AUGMENTED;

		if(item.getAttackElementValue() > 0 || item.getDefenceFire() > 0 || item.getDefenceWater() > 0 || item.getDefenceWind() > 0 || item.getDefenceEarth() > 0 || item.getDefenceHoly() > 0 || item.getDefenceUnholy() > 0)
			flags |= IS_ELEMENTED;

		for(int enchantOption : item.getEnchantOptions())
		{
			if(enchantOption > 0)
			{
				flags |= HAVE_ENCHANT_OPTIONS;
				break;
			}
		}

		if(item.getVisualId() > 0)
			flags |= VISUAL_CHANGED;

		writeC(flags);
		writeD(item.getObjectId());
		writeD(item.getItemId());
		writeC(item.getEquipSlot());
		writeQ(count);
		writeC(item.getItem().getType2ForPackets());
		writeC(item.getCustomType1());
		writeC(item.isEquipped() ? 1 : 0);
		writeC(item.getCustomType2());
		writeQ(item.getItem().getBodyPart());
		writeH(item.getEnchantLevel());
		writeD(item.getShadowLifeTime());
		writeD(item.getTemporalLifeTime());
		writeC(!item.isBlocked());

		if((flags & IS_AUGMENTED) == IS_AUGMENTED)
		{
			writeH(item.getVariation1Id());
			writeH(item.getVariation2Id());
		}

		if((flags & IS_ELEMENTED) == IS_ELEMENTED)
		{
			writeH(item.getAttackElement());
			writeH(item.getAttackElementValue());
			writeH(item.getDefenceFire());
			writeH(item.getDefenceWater());
			writeH(item.getDefenceWind());
			writeH(item.getDefenceEarth());
			writeH(item.getDefenceHoly());
			writeH(item.getDefenceUnholy());
		}

		if((flags & HAVE_ENCHANT_OPTIONS) == HAVE_ENCHANT_OPTIONS)
		{
			writeH(item.getEnchantOptions()[0]);
			writeH(item.getEnchantOptions()[1]);
			writeH(item.getEnchantOptions()[2]);
		}

		if((flags & VISUAL_CHANGED) == VISUAL_CHANGED)
			writeD(item.getVisualId());
	}

	protected void writeCommissionItem(CommissionItem item)
	{
		writeD(item.getItemId());
		writeC(item.getEquipSlot());
		writeQ(item.getCount());
		writeH(item.getItem().getType2ForPackets()); //??item.getCustomType1()??
		writeD(item.getItem().getBodyPart());
		writeH(item.getEnchantLevel());
		writeH(item.getCustomType2());
		writeH(item.getAttackElement());
		writeH(item.getAttackElementValue());
		writeH(item.getDefenceFire());
		writeH(item.getDefenceWater());
		writeH(item.getDefenceWind());
		writeH(item.getDefenceEarth());
		writeH(item.getDefenceHoly());
		writeH(item.getDefenceUnholy());
		writeH(item.getEnchantOptions()[0]);
		writeH(item.getEnchantOptions()[1]);
		writeH(item.getEnchantOptions()[2]);
	}

	protected void writeItemElements(MultiSellIngredient item)
	{
		if(item.getItemId() <= 0)
		{
			writeItemElements();
			return;
		}
		ItemTemplate i = ItemHolder.getInstance().getTemplate(item.getItemId());
		if(item.getItemAttributes().getValue() > 0)
		{
			if(i.isWeapon())
			{
				Element e = item.getItemAttributes().getElement();
				writeH(e.getId()); // attack element (-1 - none)
				writeH(item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e)); // attack element value
				writeH(0); // водная стихия (fire pdef)
				writeH(0); // огненная стихия (water pdef)
				writeH(0); // земляная стихия (wind pdef)
				writeH(0); // воздушная стихия (earth pdef)
				writeH(0); // темная стихия (holy pdef)
				writeH(0); // светлая стихия (dark pdef)
			}
			else if(i.isArmor())
			{
				writeH(-1); // attack element (-1 - none)
				writeH(0); // attack element value
				for(Element e : Element.VALUES)
					writeH(item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e));
			}
			else
				writeItemElements();
		}
		else
			writeItemElements();
	}

	protected void writeItemElements()
	{
		writeH(-1); // attack element (-1 - none)
		writeH(0x00); // attack element value
		writeH(0x00); // водная стихия (fire pdef)
		writeH(0x00); // огненная стихия (water pdef)
		writeH(0x00); // земляная стихия (wind pdef)
		writeH(0x00); // воздушная стихия (earth pdef)
		writeH(0x00); // темная стихия (holy pdef)
		writeH(0x00); // светлая стихия (dark pdef)
	}

	public String getType()
	{
		return "[S] " + getClass().getSimpleName();
	}

	public L2GameServerPacket packet(Player player)
	{
		return this;
	}

}