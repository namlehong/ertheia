package l2s.gameserver.handler.voicecommands;

import java.util.HashMap;
import java.util.Map;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.handler.voicecommands.impl.Cfg;
import l2s.gameserver.handler.voicecommands.impl.Debug;
import l2s.gameserver.handler.voicecommands.impl.Hellbound;
import l2s.gameserver.handler.voicecommands.impl.Help;
/* CCP Guard START
import l2s.gameserver.handler.voicecommands.impl.LockHWID;
** CCP Guard END*/
import l2s.gameserver.handler.voicecommands.impl.Offline;
import l2s.gameserver.handler.voicecommands.impl.Online;
import l2s.gameserver.handler.voicecommands.impl.Repair;
import l2s.gameserver.handler.voicecommands.impl.ServerInfo;
import l2s.gameserver.handler.voicecommands.impl.Wedding;
import l2s.gameserver.handler.voicecommands.impl.WhoAmI;
import l2s.gameserver.handler.voicecommands.impl.Password;
import l2s.gameserver.handler.voicecommands.impl.Security;

public class VoicedCommandHandler extends AbstractHolder
{
	private static final VoicedCommandHandler _instance = new VoicedCommandHandler();

	public static VoicedCommandHandler getInstance()
	{
		return _instance;
	}

	private Map<String, IVoicedCommandHandler> _datatable = new HashMap<String, IVoicedCommandHandler>();

	private VoicedCommandHandler()
	{
		registerVoicedCommandHandler(new Help());
		registerVoicedCommandHandler(new Hellbound());
		registerVoicedCommandHandler(new Cfg());
//		registerVoicedCommandHandler(new Offline());
		registerVoicedCommandHandler(new Repair());
//		registerVoicedCommandHandler(new ServerInfo());
		registerVoicedCommandHandler(new Wedding());
		registerVoicedCommandHandler(new WhoAmI());
		/* CCP Guard START
		registerVoicedCommandHandler(new LockHWID());
		** CCP Guard END*/
//		registerVoicedCommandHandler(new Debug());
		registerVoicedCommandHandler(new Online());
//		registerVoicedCommandHandler(new Password());
//		registerVoicedCommandHandler(new Security());
	}

	public void registerVoicedCommandHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		for(String element : ids)
			_datatable.put(element, handler);
	}

	public IVoicedCommandHandler getVoicedCommandHandler(String voicedCommand)
	{
		String command = voicedCommand;
		if(voicedCommand.indexOf(" ") != -1)
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));

		return _datatable.get(command);
	}

	@Override
	public int size()
	{
		return _datatable.size();
	}

	@Override
	public void clear()
	{
		_datatable.clear();
	}
}
