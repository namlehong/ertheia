package l2s.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.IStaticPacket;

public class PlayerMessageStack
{
	private static PlayerMessageStack _instance;

	private final Map<Integer, List<IStaticPacket>> _stack = new HashMap<Integer, List<IStaticPacket>>();

	public static PlayerMessageStack getInstance()
	{
		if(_instance == null)
			_instance = new PlayerMessageStack();
		return _instance;
	}

	public PlayerMessageStack()
	{
		//TODO: загрузка из БД
	}

	public void mailto(int char_obj_id, IStaticPacket message)
	{
		Player cha = GameObjectsStorage.getPlayer(char_obj_id);
		if(cha != null)
		{
			cha.sendPacket(message);
			return;
		}

		synchronized (_stack)
		{
			List<IStaticPacket> messages;
			if(_stack.containsKey(char_obj_id))
				messages = _stack.remove(char_obj_id);
			else
				messages = new ArrayList<IStaticPacket>();
			messages.add(message);
			//TODO: сохранение в БД
			_stack.put(char_obj_id, messages);
		}
	}

	public void CheckMessages(Player cha)
	{
		List<IStaticPacket> messages = null;
		synchronized (_stack)
		{
			if(!_stack.containsKey(cha.getObjectId()))
				return;
			messages = _stack.remove(cha.getObjectId());
		}
		if(messages == null || messages.size() == 0)
			return;
		//TODO: удаление из БД
		for(IStaticPacket message : messages)
			cha.sendPacket(message);
	}
}