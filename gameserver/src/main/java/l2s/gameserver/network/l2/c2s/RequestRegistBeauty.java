package l2s.gameserver.network.l2.c2s;
 
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.BeautyShopHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExResponseBeautyListPacket;
import l2s.gameserver.network.l2.s2c.ExResponseBeautyRegistResetPacket;
import l2s.gameserver.templates.beatyshop.BeautySetTemplate;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.ItemFunctions;
 
public class RequestRegistBeauty extends L2GameClientPacket
{
	private int _hairStyle, _face, _hairColor;
 
	@Override
	protected void readImpl()
	{
		_hairStyle = readD();
		_face = readD();
		_hairColor = readD();
	}
 
	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		BeautySetTemplate set = BeautyShopHolder.getInstance().getTemplate(activeChar);
		if(set == null)
		{
			activeChar.sendPacket(new ExResponseBeautyRegistResetPacket(activeChar, ExResponseBeautyRegistResetPacket.REGIST, 0), new ExResponseBeautyListPacket(activeChar, ExResponseBeautyListPacket.HAIR_LIST));
			return;
		}

		long reqAdena = 0;
		long reqCoins = 0;
		boolean change = false;
 
		if(_hairStyle > 0 && _hairColor > 0 && (_hairStyle != activeChar.getBeautyHairStyle() || _hairColor != activeChar.getBeautyHairColor()))
		{
			if(set.getHair(_hairStyle) == null || set.getHair(_hairStyle).getColor(_hairColor) == null)
			{
				activeChar.sendPacket(new ExResponseBeautyRegistResetPacket(activeChar, ExResponseBeautyRegistResetPacket.REGIST, 0), new ExResponseBeautyListPacket(activeChar, ExResponseBeautyListPacket.HAIR_LIST));
				return;
			}
 
			if(_hairStyle != activeChar.getBeautyHairStyle())
			{
				reqAdena += set.getHair(_hairStyle).getAdena() + set.getHair(_hairStyle).getColor(_hairColor).getAdena();
				reqCoins += set.getHair(_hairStyle).getCoins() + set.getHair(_hairStyle).getColor(_hairColor).getCoins();
			}
			else
			{
				reqAdena += set.getHair(_hairStyle).getColor(_hairColor).getAdena();
				reqCoins += set.getHair(_hairStyle).getColor(_hairColor).getCoins();
			}
 
			change = true;
		}
 
		if(_face > 0 && _face != activeChar.getBeautyFace())
		{
			if(set.getFace(_face) == null)
			{
				activeChar.sendPacket(new ExResponseBeautyRegistResetPacket(activeChar, ExResponseBeautyRegistResetPacket.REGIST, 0), new ExResponseBeautyListPacket(activeChar, ExResponseBeautyListPacket.HAIR_LIST));
				return;
			}
 
			reqAdena += set.getFace(_face).getAdena();
			reqCoins += set.getFace(_face).getCoins();
			change = true;
		}
 
		if(!change || activeChar.getAdena() < reqAdena || ItemFunctions.getItemCount(activeChar, Config.BEAUTY_SHOP_COIN_ITEM_ID) < reqCoins)
		{
			activeChar.sendPacket(new ExResponseBeautyRegistResetPacket(activeChar, ExResponseBeautyRegistResetPacket.REGIST, 0), new ExResponseBeautyListPacket(activeChar, ExResponseBeautyListPacket.HAIR_LIST));
			return;
		}
 
		if(reqAdena > 0)
			activeChar.getInventory().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, reqAdena);
 
		if(reqCoins > 0)
			activeChar.getInventory().destroyItemByItemId(Config.BEAUTY_SHOP_COIN_ITEM_ID, reqCoins);
 
		if(_hairStyle > 0)
		{
			activeChar.setBeautyHairStyle(_hairStyle);
			activeChar.setBeautyHairColor(_hairColor);
		}
 
		if(_face > 0)
			activeChar.setBeautyFace(_face);
 
		activeChar.sendPacket(new ExResponseBeautyRegistResetPacket(activeChar, ExResponseBeautyRegistResetPacket.REGIST, 1), new ExResponseBeautyListPacket(activeChar, ExResponseBeautyListPacket.HAIR_LIST));
	}
}