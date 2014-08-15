package handler.bbs.custom;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.handler.bbs.CommunityBoardManager;
import l2s.gameserver.handler.bbs.ICommunityBoardHandler;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.actor.instances.player.BookMarkList;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.network.l2.s2c.ShowBoardPacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class CommunityTeleport implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityTeleport.class);

	private final TIntObjectHashMap<TeleportList> _teleportLists = new TIntObjectHashMap<TeleportList>();

	@Override
	public void onLoad()
	{
		if(Config.COMMUNITYBOARD_ENABLED && Config.BBS_TELEPORT_ENABLED)
		{
			CommunityBoardManager.getInstance().registerHandler(this);
			loadTeleportList();
		}
	}

	@Override
	public void onReload()
	{
		if(Config.COMMUNITYBOARD_ENABLED && Config.BBS_TELEPORT_ENABLED)
		{
			CommunityBoardManager.getInstance().removeHandler(this);
			_teleportLists.clear();
			loadTeleportList();
		}
	}

	@Override
	public void onShutdown()
	{}

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_cbbsteleport",
			"_cbbsteleportlist",
			"_cbbstppoints",
			"_cbbstpsavepoint",
			"_cbbsteleportpoint",
			"_cbbsteleportdelete"
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		if(!CommunityFunctions.checkPlayer(player))
		{
			if(player.isLangRus())
			{
				player.sendMessage("Не соблюдены условия для использование данной функции");
				return;
			}	
			else
			{
				player.sendMessage("You are not allowed to use this action in you current stance");
				return;
			}
		}
		if(player.isTerritoryFlagEquipped())
		{
			if(player.isLangRus())
			{
				player.sendMessage("Не соблюдены условия для использование данной функции");
				return;
			}	
			else
			{
				player.sendMessage("You are not allowed to use this action in you current stance");
				return;
			}
		}	
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		String html = "";

		if("cbbsteleport".equals(cmd))
		{
			int listId = Integer.parseInt(st.nextToken());
			int pointId = Integer.parseInt(st.nextToken());
			if(player.isPK() && !Config.ALLOW_TELEPORT_PK)
			{
				html = HtmCache.getInstance().getNotNull("scripts/handler/bbs/pages/teleports-pk.htm", player);
			}
			else
			{
				teleport(player, listId, pointId);
				onBypassCommand(player, "_cbbsteleportlist");
				return;
			}
		}
		if("cbbsteleportlist".equals(cmd))
		{
			html = HtmCache.getInstance().getNotNull("scripts/handler/bbs/pages/teleports.htm", player);
			int listId = 0;
			if(st.hasMoreTokens())
				listId = Integer.parseInt(st.nextToken());
				
			html = html.replace("<?price?>", String.valueOf(Config.BBS_TELEPORT_DEFAULT_ITEM_COUNT));
			html = html.replace("<?item_name?>", HtmlUtils.htmlItemName(Config.BBS_TELEPORT_DEFAULT_ITEM_ID));
			html = html.replace("<?teleport_list?>", generateTeleportList(player, listId));
			
		}
		if("cbbstppoints".equals(cmd))
		{
			if(player.isPK() && !Config.ALLOW_TELEPORT_PK)
				html = HtmCache.getInstance().getNotNull("scripts/handler/bbs/pages/teleports-pk.htm", player);
				
			else
			{
				
				html = HtmCache.getInstance().getNotNull("scripts/handler/bbs/pages/teleports-bm.htm", player);

				CBteleport tp;
				Connection con = null;
				PreparedStatement statement = null;
				ResultSet rs = null;
				StringBuilder html1 = new StringBuilder();
				try
				{
					con = DatabaseFactory.getInstance().getConnection();
					statement = con.prepareStatement("SELECT * FROM bbs_teleport_bm WHERE char_id=?;");
					statement.setLong(1, player.getObjectId());
					rs = statement.executeQuery();
					if(getCountTP(player) == 0)
					{
						html1.append("<tr>");
						html1.append("<td>");
						if(player.isLangRus())
							html1.append("На данный момент нету сохраненных точек телепорта");
						else
							html1.append("There is no teleports bookmarks this time");
						html1.append("</td>");
						html1.append("</tr>");	
					}	
					while(rs.next())
					{
						tp = new CBteleport();
						tp.PlayerId = rs.getInt("char_id");
						tp.TpName = rs.getString("name");
						tp.xC = rs.getInt("x");
						tp.yC = rs.getInt("y");
						tp.zC = rs.getInt("z");
						html1.append("<tr>");
						html1.append("<td>");
						html1.append("<button value=\"" + tp.TpName + "\" action=\"bypass _cbbsteleportpoint_" + tp.xC + "_" + tp.yC + "_" + tp.zC + "\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
						html1.append("</td>");
						html1.append("<td>");
						html1.append("<button value=\"Удалить\" action=\"bypass _cbbsteleportdelete_" + tp.TpName + "\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
						html1.append("</td>");
						html1.append("</tr>");
					}
				}	
				
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				finally
				{
					DbUtils.closeQuietly(con, statement, rs);
				}
				StringBuilder content = new StringBuilder();
				
				if(player.isLangRus())
					content.append("<tr><td> Введите имя для сохранение: </td></tr>");
				else
					content.append("<tr><td> Enter your name for this teleport bookmark: </td></tr>");
					
				content.append("<tr><td><edit var=\"newfname\" width=150></td></tr>");
				content.append("<tr><td><button value=\"OK\" action=\"bypass _cbbstpsavepoint_ $newfname \" width=150 height=15></td></tr>");

				html = html.replace("<?price_save?>", String.valueOf(Config.TELEPORT_BM_COUNT_SAVE));
				html = html.replace("<?item_name_save?>", HtmlUtils.htmlItemName(Config.TELEPORT_BM_ITEM_SAVE));
				
				html = html.replaceFirst("<?bookmarkssave?>",content.toString());	

				html = html.replace("<?price_go?>", String.valueOf(Config.TELEPORT_BM_COUNT_GO));
				html = html.replace("<?item_name_go?>", HtmlUtils.htmlItemName(Config.TELEPORT_BM_ITEM_GO));
				
				html = html.replace("<?bookmarkslist?>",html1);				
									
			}	
		}
		if("cbbsteleportpoint".equals(cmd))
		{
			if(!checkCond(player, false))
			{
				onBypassCommand(player, "_cbbstppoints");
				return;			
			}	
			
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());	
			int z = Integer.parseInt(st.nextToken());
			
			Functions.removeItem(player, Config.TELEPORT_BM_ITEM_GO, Config.TELEPORT_BM_COUNT_GO);
			
			player.teleToLocation(x,y,z);

			onBypassCommand(player, "_cbbstppoints");
			return;				
			
		}
		if("cbbsteleportdelete".equals(cmd))
		{
			String name = st.nextToken();

			Connection con = null;
			PreparedStatement statement = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("DELETE FROM bbs_teleport_bm WHERE char_id=? AND name=?");
				statement.setInt(1, player.getObjectId());
				statement.setString(2, name);
				statement.execute();
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
			
			onBypassCommand(player, "_cbbstppoints");
			return;					
		
		}
		if("cbbstpsavepoint".equals(cmd))
		{
			if(!st.hasMoreTokens())
			{
				onBypassCommand(player, "_cbbsteleportlist");
				return;			
			}
			String bmName = st.nextToken();
			if(bmName.equals(" ") || bmName.isEmpty() || tpNameExist(player,bmName))
			{
				if(player.isLangRus())	
					player.sendMessage("Такое название для телепорта уже существует.");
				else
					player.sendMessage("You cannot use the same teleport name twice.");
					
				onBypassCommand(player, "_cbbsteleportlist");
				return;
			}
			
			if(!checkCond(player,true))
			{
				html = HtmCache.getInstance().getNotNull("scripts/handler/bbs/pages/teleports-no-cond.htm", player);
				ShowBoardPacket.separateAndSend(html, player);
				return;
			}
			
			Functions.removeItem(player, Config.TELEPORT_BM_ITEM_SAVE, Config.TELEPORT_BM_COUNT_SAVE);
			
			Connection con = null;
			PreparedStatement statement = null;
			
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("INSERT INTO bbs_teleport_bm (char_id,name,x,y,z) VALUES (?,?,?,?,?)");
				statement.setInt(1, player.getObjectId());
				statement.setString(2, bmName);
				statement.setInt(3, player.getX());
				statement.setInt(4, player.getY());
				statement.setInt(5, player.getZ());
				statement.execute();
			}
			
			catch(Exception e)
			{
				_log.warn("CommunityTeleport: cannot save tp book mark for player "+player.getName()+"");
				e.printStackTrace();
			}
			
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
			html = HtmCache.getInstance().getNotNull("scripts/handler/bbs/pages/teleports-success.htm", player);	
		}		
		ShowBoardPacket.separateAndSend(html, player);
	}
	public class CBteleport
	{
		public int PlayerId = 0; // charID
		public String TpName = ""; // Loc name
		public int xC = 0; // Location coords
		public int yC = 0; //
		public int zC = 0; //
	}	
	private boolean checkCond(Player player, boolean save)
	{
		if(player.isDead())
			return false;
			
		if(player.getTeam() !=	TeamType.NONE)
			return false;
			
		if(player.isFlying() || player.isInFlyingTransform())
			return false;		
			
		if(player.isInBoat())
			return false;		
			
		if(player.isInStoreMode() || player.isInTrade() || player.isInOfflineMode())
			return false;	
			
		if(player.isInDuel())
			return false;		
			
		if(player.isCursedWeaponEquipped())
			return false;
			
		if(save)
		{
			if(player.isInZone(ZoneType.SIEGE) || player.isInZone(ZoneType.RESIDENCE) || player.isInZone(ZoneType.HEADQUARTER) || player.isInZone(ZoneType.battle_zone) ||player.isInZone(ZoneType.ssq_zone) || player.isInZone(ZoneType.no_restart) || player.isInZone(ZoneType.offshore) || player.isInZone(ZoneType.epic) || player.isInOlympiadMode() || player.isOnSiegeField())
				return false;
				
			if(player.getReflection() != ReflectionManager.DEFAULT)
				return false;
				
			if(Functions.getItemCount(player, Config.TELEPORT_BM_ITEM_SAVE) < Config.TELEPORT_BM_COUNT_SAVE)
			{
				if(player.isLangRus())
					player.sendMessage("У вас не хватает нужных вещей для выполнение опрации");
				else
					player.sendMessage("You have not enough item to procced the operation");			
				return false;
			}	
				
			if(getCountTP(player) >= Config.MAX_BOOK_MARKS)
			{
				if(player.isLangRus())
					player.sendMessage("Вы достигли лимит сохраняемых точек телепорта.");
					
				else
					player.sendMessage("You have reached the limit of maximum savings of the teleport bookmarks");	
					
				return false;
			}	
		}
		if(!save)
		
			if(Functions.getItemCount(player, Config.TELEPORT_BM_ITEM_GO) < Config.TELEPORT_BM_COUNT_GO)
			{
				if(player.isLangRus())
					player.sendMessage("У вас не хватает нужных вещей для выполнение опрации");
					
				else
					player.sendMessage("You have not enough item to procced the operation");
					
				return false;
			}	
		return true;
	}
	private int getCountTP(Player player)
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		int i = 0;
		
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT name FROM bbs_teleport_bm WHERE char_id=?");
			statement.setInt(1, player.getObjectId());
			rset = statement.executeQuery();
			while(rset.next())
			{
				i++;
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		
		return i;		
	
	}
	private boolean tpNameExist(Player player, String bmName)
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		boolean isExist = false;
		
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT name FROM bbs_teleport_bm WHERE char_id=?");
			statement.setInt(1, player.getObjectId());
			rset = statement.executeQuery();
			while(rset.next())
			{
				String name = rset.getString("name");
				if(name.equals(bmName))
					isExist = true;
			}
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		
		return isExist;		
	}	
	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{}

	private void teleport(Player player, int listId, int pointId)
	{
		TeleportList list = _teleportLists.get(listId);
		if(list == null)
			return;

		TeleportPoint point = list.getPoint(pointId);
		if(point == null)
			return;

		if(!BookMarkList.checkFirstConditions(player) || !BookMarkList.checkTeleportConditions(player))
			return;

		if(player.getReflection().isDefault())
		{
			int castleId = point.getCastleId();
			Castle castle = castleId > 0 ? ResidenceHolder.getInstance().getResidence(Castle.class, castleId) : null;
			// Нельзя телепортироваться в города, где идет осада
			if(castle != null && castle.getSiegeEvent().isInProgress())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
				return;
			}
		}

		int itemId = point.getItemId();
		int itemCount = point.getItemCount();
		if(Functions.getItemCount(player, itemId) < itemCount)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
			return;
		}

		Location loc = Location.findPointToStay(point.getLocation(), 50, 100, player.getGeoIndex());
		player.teleToLocation(loc);
		Functions.removeItem(player, itemId, itemCount);
	}

	public void loadTeleportList()
	{
		Document doc = null;
		File file = new File(Config.DATAPACK_ROOT, "data/bbs_teleports.xml");
		if(!file.exists())
		{
			_log.warn("CommunityTeleport: bbs_teleports.xml file is missing.");
			return;
		}

		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			doc = factory.newDocumentBuilder().parse(file);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			parseTeleportList(doc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void parseTeleportList(Document doc)
	{
		for(Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if("list".equalsIgnoreCase(n.getNodeName()))
			{
				for(Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if("teleport_list".equalsIgnoreCase(d.getNodeName()))
					{
						final int listId = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
						final String listEnName = d.getAttributes().getNamedItem("name_en").getNodeValue();
						final String listRuName = d.getAttributes().getNamedItem("name_ru").getNodeValue();
						TeleportList teleportList = new TeleportList(listId, listEnName, listRuName);
						for(Node t = d.getFirstChild(); t != null; t = t.getNextSibling())
						{
							if("point".equalsIgnoreCase(t.getNodeName()))
							{
								final int x = Integer.parseInt(t.getAttributes().getNamedItem("x").getNodeValue());
								final int y = Integer.parseInt(t.getAttributes().getNamedItem("y").getNodeValue());
								final int z = Integer.parseInt(t.getAttributes().getNamedItem("z").getNodeValue());
								final int castleId = t.getAttributes().getNamedItem("castle_id") == null ? 0 : Integer.parseInt(t.getAttributes().getNamedItem("castle_id").getNodeValue());
								final String enName = t.getAttributes().getNamedItem("name_en").getNodeValue();
								final String ruName = t.getAttributes().getNamedItem("name_ru").getNodeValue();
								final int itemId = t.getAttributes().getNamedItem("item_id") == null ? Config.BBS_TELEPORT_DEFAULT_ITEM_ID : Integer.parseInt(t.getAttributes().getNamedItem("item_id").getNodeValue());
								final int itemCount = t.getAttributes().getNamedItem("item_count") == null ? Config.BBS_TELEPORT_DEFAULT_ITEM_COUNT : Integer.parseInt(t.getAttributes().getNamedItem("item_count").getNodeValue());
								TeleportPoint teleportPoint = new TeleportPoint(new Location(x, y, z), castleId, enName, ruName, itemId, itemCount);
								teleportList.addPoint(teleportPoint);
							}
						}
						_teleportLists.put(listId, teleportList);
					}
				}
			}
		}
	}

	private String generateTeleportList(Player player, int listId)
	{
		StringBuilder result = new StringBuilder();
		if(listId == 0)
		{
			int[] keys = _teleportLists.keys();
			Arrays.sort(keys);
			for(int id : keys)
			{
				TeleportList list = _teleportLists.get(id);
				result.append("<tr><td><button value=\"");
				result.append(player.isLangRus() ? list.getRuName() : list.getEnName());
				result.append("\" action=\"bypass _cbbsteleportlist_" + list.getId() + "\" width=250 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>\n");
			}
		}
		else
		{
			TeleportList list = _teleportLists.get(listId);
			if(list == null || list.getPointsIds().length == 0)
			{
				result.append(player.isLangRus() ? "Ошибка! Недоступный список телепортов." : "Error! A disabled list teleports.");
			}
			else
			{
				int[] pointsIds = list.getPointsIds();
				Arrays.sort(pointsIds);
				for(int pointId : pointsIds)
				{
					TeleportPoint point = list.getPoint(pointId);
					result.append("<tr><td><button value=\"");
					result.append(player.isLangRus() ? point.getRuName() : point.getEnName());
					result.append("\" action=\"bypass _cbbsteleport_" + listId + "_" + pointId + "\" width=250 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>\n");
				}
				result.append("<tr><td><br></td></tr>\n");
				result.append("<tr><td><button value=\"");
				result.append(player.isLangRus() ? "Назад" : "Back");
				result.append("\" action=\"bypass _cbbsteleportlist\" width=250 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>\n");
			}
		}
		return result.toString();
	}

	private static class TeleportList
	{
		private final int _id;
		private final String _enName;
		private final String _ruName;
		private final TIntObjectHashMap<TeleportPoint> _points;

		public TeleportList(int id, String enName, String ruName)
		{
			_id = id;
			_enName = enName;
			_ruName = ruName;
			_points = new TIntObjectHashMap<TeleportPoint>();
		}

		public int getId()
		{
			return _id;
		}

		public String getEnName()
		{
			return _enName;
		}

		public String getRuName()
		{
			return _ruName;
		}

		public void addPoint(TeleportPoint point)
		{
			_points.put(_points.size(), point);
		}

		public int[] getPointsIds()
		{
			return _points.keys();
		}

		public TeleportPoint getPoint(int id)
		{
			return _points.get(id);
		}
	}

	private static class TeleportPoint
	{
		private final Location _loc;
		private final int _castleId;
		private final String _enName;
		private final String _ruName;
		private final int _itemId;
		private final int _itemCount;

		public TeleportPoint(Location loc, int castleId, String enName, String ruName, int itemId, int itemCount)
		{
			_loc = loc;
			_castleId = castleId;
			_enName = enName;
			_ruName = ruName;
			_itemId = itemId;
			_itemCount = itemCount;
		}

		public Location getLocation()
		{
			return _loc;
		}

		public int getCastleId()
		{
			return _castleId;
		}

		public String getEnName()
		{
			return _enName;
		}

		public String getRuName()
		{
			return _ruName;
		}

		public int getItemId()
		{
			return _itemId;
		}

		public int getItemCount()
		{
			return _itemCount;
		}
	}
}