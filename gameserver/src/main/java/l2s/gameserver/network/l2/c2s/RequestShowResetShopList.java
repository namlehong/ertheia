package l2s.gameserver.network.l2.c2s;
 
import l2s.gameserver.data.xml.holder.BeautyShopHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExResponseBeautyRegistResetPacket;
import l2s.gameserver.templates.beatyshop.BeautySetTemplate;
import l2s.gameserver.templates.item.ItemTemplate;
 
public class RequestShowResetShopList extends L2GameClientPacket
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
			activeChar.sendPacket(new ExResponseBeautyRegistResetPacket(activeChar, ExResponseBeautyRegistResetPacket.RESET, 0));
			return;
		}
 
		long reqAdena = 0;
		boolean reset = false;
 
		if(_hairStyle > 0 && _hairColor > 0)
		{
			if(set.getHair(_hairStyle) == null)
			{
				activeChar.sendPacket(new ExResponseBeautyRegistResetPacket(activeChar, ExResponseBeautyRegistResetPacket.RESET, 0));
				return;
			}
 
			reqAdena += set.getHair(_hairStyle).getResetPrice();
			reset = true;
		}
 
		if(_face > 0)
		{
			if(set.getFace(_face) == null)
			{
				activeChar.sendPacket(new ExResponseBeautyRegistResetPacket(activeChar, ExResponseBeautyRegistResetPacket.RESET, 0));
				return;
			}
 
			reqAdena += set.getFace(_face).getResetPrice();
			reset = true;
		}
 
		if(!reset || activeChar.getAdena() < reqAdena)
		{
			activeChar.sendPacket(new ExResponseBeautyRegistResetPacket(activeChar, ExResponseBeautyRegistResetPacket.RESET, 0));
			return;
		}
 
		activeChar.getInventory().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, reqAdena);
 
		if(_hairStyle > 0)
		{
			activeChar.setBeautyHairStyle(0);
			activeChar.setBeautyHairColor(0);
		}
 
		if(_face > 0)
			activeChar.setBeautyFace(0);
 
		activeChar.sendPacket(new ExResponseBeautyRegistResetPacket(activeChar, ExResponseBeautyRegistResetPacket.RESET, 1));
	}
}