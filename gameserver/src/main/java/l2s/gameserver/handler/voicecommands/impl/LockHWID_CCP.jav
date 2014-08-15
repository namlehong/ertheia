package l2s.gameserver.handler.voicecommands.impl;

import l2s.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2s.gameserver.model.Player;

public class LockHWID implements IVoicedCommandHandler
{
	private final String[] _commandList = new String[] { "lock-hwid", "unlock-hwid" };

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if(command.equalsIgnoreCase("lock-hwid"))
		{
			ccpGuard.managers.HwidManager.updateHwidInfo(activeChar, ccpGuard.managers.HwidInfo.LockType.PLAYER_LOCK);
			activeChar.sendMessage("Теперь ваш персонаж приклеплен к этому компьютеру. Вход с другого компьютера невозможен!");
			return true;
		}
		if(command.equalsIgnoreCase("unlock-hwid"))
		{
			ccpGuard.managers.HwidManager.updateHwidInfo(activeChar, ccpGuard.managers.HwidInfo.LockType.NONE);
			activeChar.sendMessage("Привязка к компьютеру отменена. Теперь возможен вход с любого компьютера!");
			return true;
		}
		return false;
	}
}