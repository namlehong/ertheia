package handler.bbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.handler.bbs.CommunityBoardManager;
import l2s.gameserver.handler.bbs.ICommunityBoardHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ShowBoardPacket;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.scripts.Scripts;
import l2s.gameserver.tables.ClanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommunityBoard implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityBoard.class);

	@Override
	public void onLoad()
	{
		if(Config.COMMUNITYBOARD_ENABLED)
		{
			_log.info("CommunityBoard: service loaded.");
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload()
	{
		if(Config.COMMUNITYBOARD_ENABLED)
			CommunityBoardManager.getInstance().removeHandler(this);
	}

	@Override
	public void onShutdown()
	{}

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_bbshome",
			"_bbslink",
			"_bbsmultisell",
			"_bbspage",
			"_bbsscripts"
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		String html = "";
		if("bbshome".equals(cmd))
		{
			StringTokenizer p = new StringTokenizer(Config.BBS_DEFAULT, "_");
			String dafault = p.nextToken();
			if(dafault.equals(cmd))
			{
				html = HtmCache.getInstance().getNotNull("scripts/handler/bbs/bbs_top.htm", player);

				int favCount = 0;
				Connection con = null;
				PreparedStatement statement = null;
				ResultSet rset = null;
				try
				{
					con = DatabaseFactory.getInstance().getConnection();
					statement = con.prepareStatement("SELECT count(*) as cnt FROM `bbs_favorites` WHERE `object_id` = ?");
					statement.setInt(1, player.getObjectId());
					rset = statement.executeQuery();
					if(rset.next())
						favCount = rset.getInt("cnt");
				}
				catch(Exception e)
				{}
				finally
				{
					DbUtils.closeQuietly(con, statement, rset);
				}

				html = html.replace("<?fav_count?>", String.valueOf(favCount));
				html = html.replace("<?clan_count?>", String.valueOf(CommunityClan.getClanList(null, false).size()));
				html = html.replace("<?market_count?>", String.valueOf(CommunityBoardManager.getInstance().getIntProperty("col_count")));
			}
			else
			{
				onBypassCommand(player, Config.BBS_DEFAULT);
				return;
			}
		}
		else if("bbslink".equals(cmd))
			html = HtmCache.getInstance().getNotNull("scripts/handler/bbs/bbs_homepage.htm", player);
		else if(bypass.startsWith("_bbspage"))
		{
			//Example: "bypass _bbspage:index".
			String[] b = bypass.split(":");
			String page = b[1];
			html = HtmCache.getInstance().getNotNull("scripts/handler/bbs/pages/" + page + ".htm", player);
		}
		else if(bypass.startsWith("_bbsmultisell"))
		{
			//Example: "_bbsmultisell:10000;_bbspage:index" or "_bbsmultisell:10000;_bbshome" or "_bbsmultisell:10000"...
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String[] mBypass = st2.nextToken().split(":");
			String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
			if(pBypass != null)
			{
				ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(pBypass);
				if(handler != null)
					handler.onBypassCommand(player, pBypass);
			}

			int listId = Integer.parseInt(mBypass[1]);
			MultiSellHolder.getInstance().SeparateAndSend(listId, player, 0);
			return;
		}
		else if(bypass.startsWith("_bbsscripts"))
		{
			//Example: "_bbsscripts:events.GvG.GvG:addGroup;_bbspage:index" or "_bbsscripts:events.GvG.GvG:addGroup;_bbshome" or "_bbsscripts:events.GvG.GvG:addGroup"...
			StringTokenizer st2 = new StringTokenizer(bypass, ";");
			String sBypass = st2.nextToken().substring(12);
			String pBypass = st2.hasMoreTokens() ? st2.nextToken() : null;
			if(pBypass != null)
			{
				ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(pBypass);
				if(handler != null)
					handler.onBypassCommand(player, pBypass);
			}

			String[] word = sBypass.split("\\s+");
			String[] args = sBypass.substring(word[0].length()).trim().split("\\s+");
			String[] path = word[0].split(":");
			if(path.length != 2)
				return;

			Scripts.getInstance().callScripts(player, path[0], path[1], word.length == 1 ? new Object[] {} : new Object[] { args });
			return;
		}

		ShowBoardPacket.separateAndSend(html, player);
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{}
}
