package handler.bbs.custom;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.dao.LfcDAO;
import l2s.gameserver.dao.LfcDAO.Arenas;
import l2s.gameserver.dao.LfcStatisticDAO;
import l2s.gameserver.dao.LfcStatisticDAO.LocalStatistic;
import l2s.gameserver.dao.LfcStatisticDAO.GlobalStatistic;
import l2s.gameserver.instancemanager.LfcManager;
import l2s.gameserver.handler.bbs.CommunityBoardManager;
import l2s.gameserver.handler.bbs.ICommunityBoardHandler;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.ShowBoardPacket;
import l2s.gameserver.scripts.ScriptFile;
import l2s.gameserver.templates.item.ItemTemplate;

public class LfcBBSManager implements ScriptFile, ICommunityBoardHandler
{

	private static final Logger _log = LoggerFactory.getLogger(LfcBBSManager.class);
	
	@Override
	public void onLoad()
	{
		if(Config.ENABLE_LFC)
			CommunityBoardManager.getInstance().registerHandler(this);
	}
	
	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{}
	
	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_cbblfcstart",
			"_cbblfcallarenas",
			"_cbblfclenta",
			"_cbblfbest",
			"_cbblfcgetarenas",
			"_cbblfcquickarenas",
			"_cbblfcregister",
			"_cbblfccancel",
		};
	}

	@Override
	public void onBypassCommand(Player activeChar, String command)
	{
		if(!CommunityFunctions.checkPlayer(activeChar))
		{
			if(activeChar.isLangRus())
			{
				activeChar.sendMessage("Не соблюдены условия для использование данной функции");
				return;
			}	
			else
			{
				activeChar.sendMessage("You are not allowed to use this action in you current stance");
				return;
			}
		}		
		if(command.equals("_cbblfcstart")) //main page
			MainPage(activeChar);	
		if(command.equals("_cbblfcallarenas")) // all categories
			AllCategories(activeChar);	
		if(command.startsWith("_cbblfcgetarenas")) //all arenas by category
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			int category = Integer.parseInt(st.nextToken());	
			AllArenas(activeChar, category);
		}
		if(command.equals("_cbblfcquickarenas")) // Quick arenas
			QuickArenasSearch(activeChar);
			
		if(command.equals("_cbblfclenta")) // lenta boev
			ShowLocalStatistic(activeChar);		
		if(command.equals("_cbblfbest")) // lenta boev
			ShowGlobalStatistic(activeChar);					
						
		if(command.startsWith("_cbblfcregister")) //register arena
		{
			StringTokenizer st2 = new StringTokenizer(command, ";");
			st2.nextToken();
			int arenaId = Integer.parseInt(st2.nextToken());	
			RegisterArena(activeChar, arenaId);
		}		

		if(command.startsWith("_cbblfccancel")) //cancel arena
		{
			StringTokenizer st3 = new StringTokenizer(command, ";");
			st3.nextToken();
			int arenaId = Integer.parseInt(st3.nextToken());	
			CancelArena(activeChar, arenaId);
		}		
	}

	private void ShowGlobalStatistic(Player player)
	{
		//header
		StringBuilder html = new StringBuilder("<html><body><br><br><center>");
		//body
		html.append("<br>");
		html.append("<br1><br1><font color=\"LEVEL\">Best Players:</font> <br>");
		html.append("<table border=0 cellspacing=0 cellpadding=2 bgcolor=5A5A5A width=610>");
		html.append("<tr>");
		html.append("<td FIXWIDTH=5></td>");
		html.append("<td FIXWIDTH=100 align=center>Name</td>");
		html.append("<td FIXWIDTH=180 align=center>Wins</td>");
		html.append("<td FIXWIDTH=180 align=center>Looses</td>");
		html.append("<td FIXWIDTH=120 align=center>DonateCoins Games</td>");
		html.append("<td FIXWIDTH=120 align=center>DonateCoins Won</td>");
		html.append("<td FIXWIDTH=5></td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("<img src=\"L2UI.Squareblank\" width=\"1\" height=\"5\">");		
		int i = 0;
		GlobalStatistic[] array = LfcStatisticDAO.getSortedGlobalStatsArray();
		for(int j = 0; j < array.length; j++)
		//for(GlobalStatistic stat : LfcStatisticDAO.getSortedGlobalStatsArray())
		{
			GlobalStatistic stat = array[j];
			html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\">");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=610>");
			html.append("<tr> ");
			html.append("<td FIXWIDTH=5></td>");
			html.append("<td FIXWIDTH=100 align=center>" + stat.getCharName() + "</td>");
			html.append("<td FIXWIDTH=180 align=center>"+ stat.getWinCount()+"</td>");
			html.append("<td FIXWIDTH=180 align=center>"+ stat.getLooseCount()+"</td>");
			html.append("<td FIXWIDTH=120 align=center>"+ stat.getPayBattleCount()+" </td>");
			html.append("<td FIXWIDTH=120 align=center>"+ stat.getMoneyWin()+" </td>");
			html.append("<td FIXWIDTH=5></td>");
			html.append("</tr>");
			html.append("<tr><td height=5></td></tr>");
			html.append("</table>");
			html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\">");
			html.append("<img src=\"L2UI.SquareGray\" width=\"610\" height=\"1\">");
			
			i++;
			if(i > 29)
				break;
		}
		html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"2\">");
		html.append("<table cellpadding=0 cellspacing=2 border=0><tr>");
		html.append("<td><button value=\"Back\" action=\"bypass _cbblfcstart\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=16 ></td></tr></table>");
		html.append("<br>");
		html.append("<br>");
		html.append("</center>");
		html.append("</body>");
		html.append("</html>");
		ShowBoardPacket.separateAndSend(html.toString(), player);				
	}	
	private void ShowLocalStatistic(Player player)
	{
		//header
		StringBuilder html = new StringBuilder("<html><body><br><br><center>");
		//body
		html.append("<br>");
		html.append("<br1><br1><font color=\"LEVEL\">Battles Logs:</font> <br>");
		html.append("<table border=0 cellspacing=0 cellpadding=2 bgcolor=5A5A5A width=610>");
		html.append("<tr>");
		html.append("<td FIXWIDTH=5></td>");
		html.append("<td FIXWIDTH=100 align=center>Arena</td>");
		html.append("<td FIXWIDTH=180 align=center>Winner</td>");
		html.append("<td FIXWIDTH=180 align=center>Looser</td>");
		html.append("<td FIXWIDTH=120 align=center>Prize</td>");
		html.append("<td FIXWIDTH=5></td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("<img src=\"L2UI.Squareblank\" width=\"1\" height=\"5\">");		
		for(LocalStatistic stat : LfcStatisticDAO.getArrangeLocalFights())
		{
			html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\">");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=610>");
			html.append("<tr> ");
			html.append("<td FIXWIDTH=5></td>");
			html.append("<td FIXWIDTH=100 align=center>" + stat.getArenaNameRu() + "</td>");
			html.append("<td FIXWIDTH=180 align=center>"+ stat.getWinner()+"</td>");
			html.append("<td FIXWIDTH=180 align=center>"+ stat.getLooser()+"</td>");
			html.append("<td FIXWIDTH=120 align=center>"+stat.getWonItemCount()+" "+stat.getWonItemName()+" </td>");
			html.append("<td FIXWIDTH=5></td>");
			html.append("</tr>");
			html.append("<tr><td height=5></td></tr>");
			html.append("</table>");
			html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\">");
			html.append("<img src=\"L2UI.SquareGray\" width=\"610\" height=\"1\">");
		}
		html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"2\">");
		html.append("<table cellpadding=0 cellspacing=2 border=0><tr>");
		html.append("<td><button value=\"Back\" action=\"bypass _cbblfcstart\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=16 ></td></tr></table>");
		html.append("<br>");
		html.append("<br>");
		html.append("</center>");
		html.append("</body>");
		html.append("</html>");
		ShowBoardPacket.separateAndSend(html.toString(), player);				
	}
	
	private void CancelArena(Player player, int arenaId)
	{
		Arenas arena = LfcDAO.getArenaByArenaId(arenaId);
		if(!arena.isArenaOpen())
		{
			onBypassCommand(player, "_cbblfcgetarenas;"+arena.getCategory()+"");	
			return;
		}	
		if(arena.isMoneyFight())
			LfcManager.returnBid(arena);		
		arena.setPlayerOne(null);
		arena.setPlayerTwo(null);
		player.setArenaIdForLogout(0);
		BroadCastToWorld(""+player.getName()+" cancelled registration is Arena: "+arena.getArenaNameRu()+"");		
		onBypassCommand(player, "_cbblfcgetarenas;"+arena.getCategory()+"");	
	}
	
	private void RegisterArena(Player player, int arenaId)
	{
		Arenas arena = LfcDAO.getArenaByArenaId(arenaId);
		if(!player.getAntiFlood().canLfcChoose())
		{
			player.sendMessage("You can register once in 5min to any arena that is possible for you!");
			onBypassCommand(player, "_cbblfcgetarenas;"+arena.getCategory()+"");	
			return;
		}		
		if(player.getInventory().getCountOf(arena.getKeyToArena()) < arena.getKeyCount() || !player.getInventory().destroyItemByItemId(arena.getKeyToArena(), arena.getKeyCount()))
		{
			onBypassCommand(player, "_cbblfcgetarenas;"+arena.getCategory()+"");	
			return;				
		}			
		if(arena.getPlayerOne() == null)
			arena.setPlayerOne(player);
		else if(arena.getPlayerTwo() == null)
			arena.setPlayerTwo(player);
		player.setPendingLfcStart(true);
		player.setArenaIdForLogout(arenaId);
		BroadCastToWorld(""+player.getName()+" Registered on Arena "+arena.getArenaNameRu()+"");	
		onBypassCommand(player, "_cbblfcgetarenas;"+arena.getCategory()+"");	
	
	}
	
	private void QuickArenasSearch(Player player)
	{
		//header
		StringBuilder html = new StringBuilder("<html><body><br><br><center>");
		//body
		html.append("<br>");
		html.append("<br1><br1><font color=\"LEVEL\">Fast Arena Search:</font> <br>");
		html.append("<table border=0 cellspacing=0 cellpadding=2 bgcolor=5A5A5A width=610>");
		html.append("<tr>");
		html.append("<td FIXWIDTH=5></td>");
		html.append("<td FIXWIDTH=100 align=center>Arena</td>");
		html.append("<td FIXWIDTH=180 align=center>Bid</td>");
		html.append("<td FIXWIDTH=180 align=center>Prize</td>");
		html.append("<td FIXWIDTH=120 align=center>Category</td>");
		html.append("<td FIXWIDTH=150 align=center>Status</td>");
		html.append("<td FIXWIDTH=5></td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("<img src=\"L2UI.Squareblank\" width=\"1\" height=\"5\">");		
		for(Arenas arena : LfcDAO.getAllOpenedArenas())
		{
			html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\">");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=610>");
			html.append("<tr> ");
			html.append("<td FIXWIDTH=5></td>");
			html.append("<td FIXWIDTH=100 align=center>" + arena.getArenaNameRu() + "</td>");
			if(!arena.isMoneyFight())
				html.append("<td FIXWIDTH=180 align=center>Free Arena</td>");
			else
			{
				int keyId = arena.getKeyToArena();
				long count = arena.getKeyCount();
				ItemTemplate template = ItemHolder.getInstance().getTemplate(keyId);
				html.append("<td FIXWIDTH=180 align=center> "+template.getName()+" "+count+" count </td>");
			}	
			int couponId = arena.getCouponId();
			long couponCount = arena.getCouponCount();
			ItemTemplate coupon = ItemHolder.getInstance().getTemplate(couponId);
			html.append("<td FIXWIDTH=180 align=center>"+coupon.getName()+" "+couponCount+" count </td>");
			String cat_name = arena.getCategoryName();
			html.append("<td FIXWIDTH=120 align=center>"+cat_name+"</td>");
			if(!checkPlayer(arena, player))
				html.append("<td FIXWIDTH=150 align=center><font color=FF3355> Not Avaliable </font></td>");
			else
			{	
				switch(arena.getArenaStatus(player))
				{
					case 3:
						html.append("<td FIXWIDTH=150 align=center><button value=\"Cancel\" action=\"bypass _cbblfccancel;"+arena.getArenaId()+"\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=32 ></td>");							
						break;					
					case 0:
						html.append("<td FIXWIDTH=150 align=center><button value=\"Vacant\" action=\"bypass _cbblfcregister;"+arena.getArenaId()+"\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=32 ></td>");							
						break;
					case 1:
						html.append("<td FIXWIDTH=150 align=center><button value=\"Waiting for opponent\" action=\"bypass _cbblfcregister;"+arena.getArenaId()+"\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=32 ></td>");							
						break;		
					case 2:
						html.append("<td FIXWIDTH=150 align=center><font color=00FCA0>Battle in progress</font></td>");						
						break;							
				}			
					
			}
			html.append("<td FIXWIDTH=5></td>");
			html.append("</tr>");
			html.append("<tr><td height=5></td></tr>");
			html.append("</table>");
			html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\">");
			html.append("<img src=\"L2UI.SquareGray\" width=\"610\" height=\"1\">");
		}
		html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"2\">");
		html.append("<table cellpadding=0 cellspacing=2 border=0><tr>");
		html.append("<td><button value=\"Back\" action=\"bypass _cbblfcstart\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=16 ></td></tr></table>");
		html.append("<br>");
		html.append("<br>");
		html.append("</center>");
		html.append("</body>");
		html.append("</html>");
		ShowBoardPacket.separateAndSend(html.toString(), player);			
	}
	
	private void AllArenas(Player player, int category)
	{
		//header
		StringBuilder html = new StringBuilder("<html><body><br><br><center>");
		//body
		html.append("<br>");
		html.append("<br1><br1><font color=\"LEVEL\">Choose the Arena:</font> <br>");
		html.append("<table border=0 cellspacing=0 cellpadding=2 bgcolor=5A5A5A width=610>");
		html.append("<tr>");
		html.append("<td FIXWIDTH=5></td>");
		html.append("<td FIXWIDTH=100 align=center>Arena</td>");
		html.append("<td FIXWIDTH=180 align=center>Bid</td>");
		html.append("<td FIXWIDTH=180 align=center>Prize</td>");
		html.append("<td FIXWIDTH=120 align=center>Level</td>");
		html.append("<td FIXWIDTH=150 align=center>Status</td>");
		html.append("<td FIXWIDTH=5></td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("<img src=\"L2UI.Squareblank\" width=\"1\" height=\"5\">");		

		for(Arenas arena : LfcDAO.getArenasByCategory(category))
		{
			html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\">");
			html.append("<table border=0 cellspacing=0 cellpadding=0 width=610>");
			html.append("<tr> ");
			html.append("<td FIXWIDTH=5></td>");
			html.append("<td FIXWIDTH=100 align=center>" + arena.getArenaNameRu() + "</td>");
			if(!arena.isMoneyFight())
				html.append("<td FIXWIDTH=180 align=center>Free Arena</td>");
			else
			{
				int keyId = arena.getKeyToArena();
				long count = arena.getKeyCount();
				ItemTemplate template = ItemHolder.getInstance().getTemplate(keyId);
				html.append("<td FIXWIDTH=180 align=center> "+template.getName()+" "+count+" count. </td>");
			}	
			int couponId = arena.getCouponId();
			long couponCount = arena.getCouponCount();
			ItemTemplate coupon = ItemHolder.getInstance().getTemplate(couponId);
			html.append("<td FIXWIDTH=180 align=center>"+coupon.getName()+" "+couponCount+" count. </td>");
			int min_level = arena.getArenMinLevel();
			int max_level = arena.getArenMaxLevel();
			html.append("<td FIXWIDTH=120 align=center>"+min_level+"-"+max_level+"</td>");
			if(!checkPlayer(arena, player))
				html.append("<td FIXWIDTH=150 align=center><font color=FF3355> Not avaliable </font></td>");
			else
			{	
				switch(arena.getArenaStatus(player))
				{
					case 3:
						html.append("<td FIXWIDTH=150 align=center><button value=\"Cancel\" action=\"bypass _cbblfccancel;"+arena.getArenaId()+"\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=32 ></td>");							
						break;				
					case 0:
						html.append("<td FIXWIDTH=150 align=center><button value=\"Vacant\" action=\"bypass _cbblfcregister;"+arena.getArenaId()+"\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=32 ></td>");							
						break;
					case 1:
						html.append("<td FIXWIDTH=150 align=center><button value=\"Wait for opponent\" action=\"bypass _cbblfcregister;"+arena.getArenaId()+"\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=32 ></td>");							
						break;		
					case 2:
						html.append("<td FIXWIDTH=150 align=center><font color=00FCA0>Battle in progress </font></td>");						
						break;							
				}		
			}
			html.append("<td FIXWIDTH=5></td>");
			html.append("</tr>");
			html.append("<tr><td height=5></td></tr>");
			html.append("</table>");
			html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"3\">");
			html.append("<img src=\"L2UI.SquareGray\" width=\"610\" height=\"1\">");
		}
		html.append("<img src=\"L2UI.SquareBlank\" width=\"610\" height=\"2\">");
		html.append("<table cellpadding=0 cellspacing=2 border=0><tr>");
		html.append("<td><button value=\"Back\" action=\"bypass _cbblfcallarenas\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=16 ></td></tr></table>");
		html.append("<br>");
		html.append("<br>");
		html.append("</center>");
		html.append("</body>");
		html.append("</html>");
		ShowBoardPacket.separateAndSend(html.toString(), player);		
	}
	
	private static boolean checkPlayer(Arenas arena, Player player)
	{
		int key_needed = arena.getKeyToArena();
		long key_count =  arena.getKeyCount();
		int min_level = arena.getArenMinLevel();
		int max_level = arena.getArenMaxLevel();
		
		if(key_needed != 0)
			if(player.getInventory().getCountOf(key_needed) < key_count)
				return false;
		if(player.getLevel() < min_level || player.getLevel() > max_level)
			return false;
		
		boolean isOurArena = true;
		for(Arenas ar : LfcDAO.getAllArenas())
		{
			if(ar.getPlayerOne() == player || ar.getPlayerTwo() == player)
			{
				if(ar == arena)
					isOurArena = true;
				else	
					isOurArena = false;
			}
		}
		if(player.getPendingLfcStart() && !isOurArena)
			return false;
		return isOurArena;	
			
	}
	
	private static int getAllArenasCount()
	{
		return LfcDAO.AllArenasCount();
	}
	
	private void AllCategories(Player player)
	{
		StringBuilder html = new StringBuilder("<html><body><br><br><center>");
		html.append("<br><img src=\"L2UI.Squareblank\" width=\"1\" height=\"5\">");	//todo find image
		html.append("<br1><br1><font color=\"LEVEL\">Choose the category:</font> <br>");
		html.append("<table>");
		for(String name : LfcDAO.getAllCategoryNames())
			html.append("<tr><td></td><td></td><td><button value=\""+name+"\" action=\"bypass _cbblfcgetarenas;"+LfcDAO.getCategoryIdByName(name)+"\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=35 ></td></tr><tr><td></td></tr>");	
		html.append("<tr><td></td><td></td><td><button value=\"Back\" action=\"bypass _cbblfcstart\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=16 ></td></tr><tr><td></td></tr>");			
		html.append("</table>");
		html.append("<br><img src=\"L2UI.Squareblank\" width=\"1\" height=\"5\"></body></html>");	//todo find image
		ShowBoardPacket.separateAndSend(html.toString(), player);
	}
	
	private void MainPage(Player player)
	{
		StringBuilder html = new StringBuilder("<html><body><br><br><center>");
		//html.append("<br><img src=\"L2UI.Squareblank\" width=\"1\" height=\"5\"> </center>");	//todo find image
		html.append("<br1><br1><font color=\"LEVEL\">Good day "+player.getName()+"!</font> <br>");
		html.append("<br1><br1>All avaliable Arenas: <font color=\"LEVEL\"> "+getAllArenasCount()+" </font><br>"); //all
		html.append("<br1><br1>All open arenas <font color=\"LEVEL\"> "+LfcDAO.getAllOpenArenasSize()+" </font><br>"); //opened
		html.append("<br1><br1>Played battles::<font color=\"LEVEL\"> "+LfcStatisticDAO.getAllBattles()+" </font>. Donate Coins won:<font color=\"LEVEL\"> "+LfcStatisticDAO.getAllMoney()+"</font><br>"); //opened
		html.append("I will help you to start: <br><br>");
		html.append("<table><tr><td></td><td></td><td><button value=\"All Arenas\" action=\"bypass _cbblfcallarenas\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=16 ></td>");
		html.append("<td><button value=\"Fast Search\" action=\"bypass _cbblfcquickarenas\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=16 ></td>");		
		html.append("<td><button value=\"Battle Logs\" action=\"bypass _cbblfclenta\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=16 ></td>");		
		html.append("<td><button value=\"Best Players\" action=\"bypass _cbblfbest\" back=\"l2ui_ct1.button_df_small_down\" fore=\"l2ui_ct1.button_df_small\" width=120 height=16 ></td></tr></table>");		
		html.append("<br><img src=\"L2UI.Squareblank\" width=\"1\" height=\"5\"> </center></body></html>");	//todo find image
		ShowBoardPacket.separateAndSend(html.toString(), player);
	}
			
	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{}
	
	private static void BroadCastToWorld(String text)
	{
		for(Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if(player.getVarBoolean("lfcNotes"))
			{
				player.sendPacket(new ExShowScreenMessage(text, 7000, ScreenMessageAlign.TOP_CENTER, true));
				
			}
		}
	}	
}