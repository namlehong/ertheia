package npc.model;

import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

public class LeasonInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public LeasonInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		if(player.getReflection() == ReflectionManager.DEFAULT)
		{
			showChatWindow(player, "default/buff_fail.htm");
			return;			
		}
		if(command.startsWith("buff"))
		{
			showChatWindow(player, "default/buffchar_ok.htm");
			return;			
		}
		if(command.startsWith("buffSummon"))
		{
			if(player.getServitors().length > 0)
				showChatWindow(player, "default/buffsummon_ok.htm");
			else
				showChatWindow(player, "default/buff_nosummon.htm");
			return;			
		}	
		if(command.startsWith("teleToi"))
		{
			player.teleToLocation(114649, 11115, -5120, ReflectionManager.DEFAULT);
			return;			
		}			
		if(command.startsWith("teleCruma"))
		{
			player.teleToLocation(17225, 114173, -3440, ReflectionManager.DEFAULT);
			return;			
		}		
		else
			super.onBypassFeedback(player, command);
	}	
}
