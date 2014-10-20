package l2s.gameserver.handler.voicecommands.impl;

import org.apache.commons.lang3.math.NumberUtils;
import l2s.commons.text.PrintfFormat;
import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.GlobalEvent;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.scripts.Functions;

public class Cfg extends Functions implements IVoicedCommandHandler
{
	private String[] _commandList = new String[] { "lang", "cfg" };

	public static final PrintfFormat cfg_row = new PrintfFormat("<table><tr><td width=5></td><td width=120>%s:</td><td width=100>%s</td></tr></table>");
	public static final PrintfFormat cfg_button = new PrintfFormat("<button width=%d back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h user_cfg %s\" value=\"%s\">");

	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		if(!Config.ALLOW_VOICED_COMMANDS)
			return false;

		if(command.equals("cfg2"))
			if(args != null)
			{
				String[] param = args.split(" ");
				if(param.length == 2)
				{
					if(param[0].equalsIgnoreCase("dli"))
						if(param[1].equalsIgnoreCase("on"))
							activeChar.setVar("DroplistIcons", "1", -1);
						else if(param[1].equalsIgnoreCase("of"))
							activeChar.unsetVar("DroplistIcons");

					if(param[0].equalsIgnoreCase("lang"))
					{
						if(!Config.USE_CLIENT_LANG)
							activeChar.setLanguage(param[1]);
						else
							activeChar.sendMessage(new CustomMessage("l2s.gameserver.handler.voicecommands.impl.Cfg.useVoicedCommand.Lang", activeChar));
					}

					if(param[0].equalsIgnoreCase("noe"))
						if(param[1].equalsIgnoreCase("on"))
							activeChar.setVar("NoExp", "1", -1);
						else if(param[1].equalsIgnoreCase("of"))
							activeChar.unsetVar("NoExp");

					if(param[0].equalsIgnoreCase(Player.NO_TRADERS_VAR))
						if(param[1].equalsIgnoreCase("on"))
						{
							activeChar.setNotShowTraders(true);
							activeChar.setVar(Player.NO_TRADERS_VAR, "1", -1);
						}
						else if(param[1].equalsIgnoreCase("of"))
						{
							activeChar.setNotShowTraders(false);
							activeChar.unsetVar(Player.NO_TRADERS_VAR);
						}

					if(param[0].equalsIgnoreCase(Player.NO_ANIMATION_OF_CAST_VAR))
						if(param[1].equalsIgnoreCase("on"))
						{
							activeChar.setNotShowBuffAnim(true);
							activeChar.setVar(Player.NO_ANIMATION_OF_CAST_VAR, "1", -1);
						}
						else if(param[1].equalsIgnoreCase("of"))
						{
							activeChar.setNotShowBuffAnim(false);
							activeChar.unsetVar(Player.NO_ANIMATION_OF_CAST_VAR);
						}

					if(param[0].equalsIgnoreCase("noShift"))
						if(param[1].equalsIgnoreCase("on"))
							activeChar.setVar("noShift", "1", -1);
						else if(param[1].equalsIgnoreCase("of"))
							activeChar.unsetVar("noShift");

					if(Config.SERVICES_ENABLE_NO_CARRIER && param[0].equalsIgnoreCase("noCarrier"))
					{
						int time = NumberUtils.toInt(param[1], Config.SERVICES_NO_CARRIER_DEFAULT_TIME);

						if(time > Config.SERVICES_NO_CARRIER_MAX_TIME)
							time = Config.SERVICES_NO_CARRIER_MAX_TIME;
						else if(time < Config.SERVICES_NO_CARRIER_MIN_TIME)
							time = Config.SERVICES_NO_CARRIER_MIN_TIME;

						activeChar.setVar("noCarrier", String.valueOf(time), -1);
					}

					if(param[0].equalsIgnoreCase("translit"))
						if(param[1].equalsIgnoreCase("on"))
							activeChar.setVar("translit", "tl", -1);
						else if(param[1].equalsIgnoreCase("la"))
							activeChar.setVar("translit", "tc", -1);
						else if(param[1].equalsIgnoreCase("of"))
							activeChar.unsetVar("translit");

					if(param[0].equalsIgnoreCase("autoloot"))
						activeChar.setAutoLoot(Boolean.parseBoolean(param[1]));
					if(param[0].equalsIgnoreCase("autolootonlyadena"))
						activeChar.setAutoLootOnlyAdena(Boolean.parseBoolean(param[1]));
					if(param[0].equalsIgnoreCase("autolooth"))
						activeChar.setAutoLootHerbs(Boolean.parseBoolean(param[1]));
					if(param[0].equalsIgnoreCase("lfc"))
					{
						if(param[1].equalsIgnoreCase("on"))
							activeChar.setVar("lfcNotes", "on", -1);
						else if(param[1].equalsIgnoreCase("of"))
							activeChar.unsetVar("lfcNotes");		
					}		
				}
			}

		String dialog = HtmCache.getInstance().getNotNull("command/cfg.htm", activeChar);

		dialog = dialog.replaceFirst("%lang%", activeChar.getVar("lang@", activeChar.getLanguage().getShortName()).toUpperCase());
		dialog = dialog.replaceFirst("%dli%", activeChar.getVarBoolean("DroplistIcons") ? "On" : "Off");
		dialog = dialog.replaceFirst("%noe%", activeChar.getVarBoolean("NoExp") ? "On" : "Off");
		dialog = dialog.replaceFirst("%notraders%", activeChar.getVarBoolean("notraders") ? "On" : "Off");
		dialog = dialog.replaceFirst("%notShowBuffAnim%", activeChar.getVarBoolean("notShowBuffAnim") ? "On" : "Off");
		dialog = dialog.replaceFirst("%noShift%", activeChar.getVarBoolean("noShift") ? "On" : "Off");
		dialog = dialog.replaceFirst("%noCarrier%", Config.SERVICES_ENABLE_NO_CARRIER ? (activeChar.getVarBoolean("noCarrier") ? activeChar.getVar("noCarrier") : "0") : "N/A");
		dialog = dialog.replaceFirst("%lfc%", activeChar.getVarBoolean("lfcNotes") ? "On" : "Off");
		String tl = activeChar.getVar("translit");
		if(tl == null)
			dialog = dialog.replaceFirst("%translit%", "Off");
		else if(tl.equals("tl"))
			dialog = dialog.replaceFirst("%translit%", "On");
		else
			dialog = dialog.replaceFirst("%translit%", "Lt");

		String additional = "";

		if(Config.AUTO_LOOT_INDIVIDUAL)
		{
			String bt;
			if(activeChar.isAutoLootEnabled())
				bt = cfg_button.sprintf(new Object[] { 100, "autoloot false", new CustomMessage("common.Disable", activeChar).toString() });
			else
				bt = cfg_button.sprintf(new Object[] { 100, "autoloot true", new CustomMessage("common.Enable", activeChar).toString() });
			additional += cfg_row.sprintf(new Object[] { "Auto-loot", bt });

			if(activeChar.isAutoLootOnlyAdenaEnabled())
				bt = cfg_button.sprintf(new Object[] { 100, "autolootonlyadena false", new CustomMessage("common.Disable", activeChar).toString() });
			else
				bt = cfg_button.sprintf(new Object[] { 100, "autolootonlyadena true", new CustomMessage("common.Enable", activeChar).toString() });
			additional += cfg_row.sprintf(new Object[] { "Auto-loot adena", bt });

			if(activeChar.isAutoLootHerbsEnabled())
				bt = cfg_button.sprintf(new Object[] { 100, "autolooth false", new CustomMessage("common.Disable", activeChar).toString() });
			else
				bt = cfg_button.sprintf(new Object[] { 100, "autolooth true", new CustomMessage("common.Enable", activeChar).toString() });
			additional += cfg_row.sprintf(new Object[] { "Auto-loot herbs", bt });
		}

		dialog = dialog.replaceFirst("%additional%", additional);

		StringBuilder events = new StringBuilder();
		for(GlobalEvent e : activeChar.getEvents())
			events.append(e.toString()).append("<br>");
		dialog = dialog.replace("%events%", events.toString());

		show(dialog, activeChar);

		return true;
	}

	public String[] getVoicedCommandList()
	{
		return _commandList;
	}
}