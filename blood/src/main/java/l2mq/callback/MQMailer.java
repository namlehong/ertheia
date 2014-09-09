package l2mq.callback;

import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.model.mail.Mail;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ExNoticePostArrived;
import l2s.gameserver.utils.ItemFunctions;

import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;


/**
 * The echo worker polls jobs from a job server and execute the echo function.
 *
 * The echo worker illustrates how to setup a basic worker
 */
public class MQMailer implements GearmanFunction {

	@SuppressWarnings("unused")
	@Override
    public byte[] work(String function, byte[] data, GearmanFunctionCallback callback) throws Exception 
    {

		String result 	= "OK";
		String dataStr = new String(data);
		String[] wordList = dataStr.split(";");
		
		if(wordList.length < 4)
			return null;
		
		String account_name = wordList[0];
		int charId = Integer.parseInt(wordList[1]);
		String charName = CharacterDAO.getInstance().getNameByObjectId(charId);
		
		if(charName == null || charName.isEmpty())
			return null;
		
		String mailTitle = wordList[2];
		String mailBody = wordList[3];
		
		
		Mail mail = new Mail();
		mail.setSenderId(1);
		mail.setSenderName("System");
		mail.setReceiverId(charId);
		mail.setReceiverName(charName);
		
		/* TODO create item loop here */
		if(wordList.length >= 5)
		{
			String[] items_data = wordList[4].split(",");
			String[] item_data;
			for(String item: items_data)
			{
				item_data = item.split("-");
				mail.addAttachment(getMailItem(Integer.parseInt(item_data[0]), Integer.parseInt(item_data[1])));
			}
		}
//		mail.addAttachment(getMailItem(57));  // material box lv 3
		
		mail.setTopic(mailTitle);
		mail.setBody(mailBody);
		
		mail.setUnread(true);
		mail.setType(Mail.SenderType.NORMAL);
		mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
		mail.save();
		
		Player target = GameObjectsStorage.getPlayer(charId);
		
		if(target != null)
		{
			target.sendPacket(ExNoticePostArrived.STATIC_TRUE);
			target.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
		}
		
		return result.getBytes();
    }
	
	public ItemInstance getMailItem(int itemId)
	{
		return getMailItem(itemId, 1);
	}
	
	public ItemInstance getMailItem(int itemId, int count)
	{
		ItemInstance item = ItemFunctions.createItem(itemId);
		item.setLocation(ItemInstance.ItemLocation.MAIL);
		item.setCount(count);
		item.save();
		
		return item;
	}

}
