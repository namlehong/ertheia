package l2mq.callback;

import java.util.List;

import l2mq.L2MQ;
import l2s.gameserver.Config;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.pledge.Alliance;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.UnitMember;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.s2c.SayPacket2;
import l2s.gameserver.utils.MapUtils;

import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;

import blood.BloodConfig;


/**
 * The echo worker polls jobs from a job server and execute the echo function.
 *
 * The echo worker illustrates how to setup a basic worker
 */
public class ChatterSay implements GearmanFunction {

	@Override
    public byte[] work(String function, byte[] data, GearmanFunctionCallback callback) throws Exception 
    {

		String result 	= "";
		String dataStr = new String(data);
		String[] wordList = dataStr.split(";", 4);
		
		if(wordList.length < 4)
			return "error".getBytes();
		
		String 	senderName 		= wordList[0];
		String	receiverName 	= wordList[1];
		int		chatTypeOrd		= Integer.parseInt(wordList[2]);
		String	_text				= wordList[3];
		
		
		ChatType _type = ChatType.TELL;
		
		for(ChatType _chatType: ChatType.VALUES)
		{
			if(_chatType.ordinal() == chatTypeOrd)
			{
				_type = _chatType;
				break;
			}
		}
		
		if(_type == ChatType.TELL && senderName.equalsIgnoreCase(BloodConfig.SUPPORTER_NAME))
		{
			Player receiver = GameObjectsStorage.getPlayer(receiverName);
			
			if(receiver == null)
				return "error".getBytes();
			
			if(receiver != null)
				receiver.sendPacket(new SayPacket2(1, _type, senderName, _text));
			
			return result.getBytes();
		}
		
		Player activeChar = GameObjectsStorage.getPlayer(senderName);
		
		if(activeChar == null)
			return "error".getBytes();
		
		SayPacket2 cs = new SayPacket2(activeChar.getObjectId(), _type, senderName, _text);
		
		switch(_type)
		{
			case TELL:
				Player receiver = GameObjectsStorage.getPlayer(receiverName);
				if(receiver == null)
					return "error".getBytes();
				
				if(receiver != null)
					receiver.sendPacket(cs);
				break;
				
			case PARTY:
				if(activeChar.isInParty())
					for(Player member : activeChar.getParty().getPartyMembers())
					{
						if(member.isFakePlayer())
							L2MQ.chat(member, _type, activeChar.getName(), _text);
						else
							member.sendPacket(cs);
					}
				
				break;
				
			case CLAN:
				if(activeChar.getClan() != null)
					for(UnitMember member : activeChar.getClan())
						if(member.isOnline())
						{
							if(member.getPlayer().isFakePlayer())
								L2MQ.chat(member.getPlayer(), _type, activeChar.getName(), _text);
							else
								member.getPlayer().sendPacket(cs);
						}
				break;
				
			case ALLIANCE:
				if(activeChar.getClan() != null && activeChar.getClan().getAlliance() != null)
				{
					Alliance ally = activeChar.getClan().getAlliance(); 
					for(Clan clan_member : ally.getMembers())
						if(clan_member != null)
							for(UnitMember member : clan_member)
								if(member.isOnline())
								{
									if(member.getPlayer().isFakePlayer())
										L2MQ.chat(member.getPlayer(), _type, activeChar.getName(), _text);
									else
										member.getPlayer().sendPacket(cs);
								}
//					activeChar.getClan().getAlliance().broadcastToOnlineMembers(cs);
				}
				break;
				
			case TRADE:
				if(activeChar != null)
				{
					if(Config.GLOBAL_TRADE_CHAT)
						announce(activeChar, cs);
					else
						shout(activeChar, cs);
				}
				
				break;
				
			case SHOUT:
				if(activeChar != null)
				{
					if(Config.GLOBAL_SHOUT)
						announce(activeChar, cs);
					else
						shout(activeChar, cs);
				}
				
				break;
			case ALL:

				List<Player> list = World.getAroundPlayers(activeChar);

				if(list != null)
				{
					for(Player player : list)
					{
						if(player == activeChar || player.getReflection() != activeChar.getReflection() || player.isBlockAll() || player.getBlockList().contains(activeChar))
							continue;

						cs.setCharName(activeChar.getVisibleName(player));
						if(player.isFakePlayer())
							L2MQ.chat(player, _type, activeChar.getName(), _text);
						else
							player.sendPacket(cs);
					}
				}

				break;
			
			default:
		}
		
		return result.getBytes();
    }
	
	private static void shout(Player activeChar, SayPacket2 cs)
	{
		int rx = MapUtils.regionX(activeChar);
		int ry = MapUtils.regionY(activeChar);
		int offset = Config.SHOUT_OFFSET;

		for(Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if(player == activeChar || activeChar.getReflection() != player.getReflection() || player.isBlockAll() || player.getBlockList().contains(activeChar))
				continue;

			int tx = MapUtils.regionX(player);
			int ty = MapUtils.regionY(player);

			if(tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset || activeChar.isInRangeZ(player, Config.CHAT_RANGE))
				player.sendPacket(cs);
		}
	}
	
	private static void announce(Player activeChar, SayPacket2 cs)
	{
		for(Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if(player == activeChar || activeChar.getReflection() != player.getReflection() || player.isBlockAll() || player.getBlockList().contains(activeChar))
				continue;

			player.sendPacket(cs);
		}
	}

}
