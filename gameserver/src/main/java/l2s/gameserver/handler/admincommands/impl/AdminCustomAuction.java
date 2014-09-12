package l2s.gameserver.handler.admincommands.impl;

import java.util.Calendar;
import java.util.StringTokenizer;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ItemHolder;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.instancemanager.itemauction.AuctionItem;
import l2s.gameserver.instancemanager.itemauction.ItemAuction;
import l2s.gameserver.instancemanager.itemauction.ItemAuctionInstance;
import l2s.gameserver.instancemanager.itemauction.ItemAuctionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.templates.item.ItemTemplate;
import l2s.gameserver.utils.HtmlUtils;
import l2s.gameserver.utils.TimeUtils;

/**
 * @author Bonux
**/
public class AdminCustomAuction implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_custom_auction,
		admin_delete_custom_auction,
		admin_custom_auction_info,
		admin_create_custom_auction
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().ManageCustomItemAuction)
			return false;

		if(!Config.ALT_CUSTOM_ITEM_AUCTION_ENABLED)
		{
			activeChar.sendMessage(activeChar.isLangRus() ? "Аукцион отключен. Операция невозможна." : "Auction disabled. Operation not possible.");
			return false;
		}

		StringTokenizer st = new StringTokenizer(fullString);
		st.nextToken();

		ItemAuctionInstance instance;
		int auctionId;
		switch(command)
		{
			case admin_custom_auction:
				showMainPage(activeChar);
				break;
			case admin_delete_custom_auction:
				instance = ItemAuctionManager.getInstance().getManagerInstance(ItemAuctionManager.CUSTOM_AUCTION_MANAGER_ID);
				if(instance == null)
				{
					activeChar.sendMessage(activeChar.isLangRus() ? "Аукцион по какой-то причине не инициализирован. Удаление невозможно." : "Auction for some reason has not been initialized. You can not delete.");
					return false;
				}

				auctionId = Integer.parseInt(st.nextToken());

				if(instance.deleteAuctionAndCheck(auctionId))
				{
					activeChar.sendMessage(activeChar.isLangRus() ? "Аукцион успешно удален." : "Auction deleted successfully.");
					showMainPage(activeChar);
				}
				else
				{
					activeChar.sendMessage(activeChar.isLangRus() ? "Вероятно аукцион активен или же не все ставки были забраны. Удаление невозможно." : "Probably active auction or not all bets were returned. You can not delete.");
					showMainPage(activeChar);
					return false;
				}
				break;
			case admin_custom_auction_info:
				auctionId = Integer.parseInt(st.nextToken());
				showInfoPage(activeChar, auctionId);
				break;
			case admin_create_custom_auction:
				if(!st.hasMoreTokens())
				{
					showCreatePage(activeChar);
					return true;
				}

				instance = ItemAuctionManager.getInstance().getManagerInstance(ItemAuctionManager.CUSTOM_AUCTION_MANAGER_ID);
				if(instance == null)
				{
					activeChar.sendMessage(activeChar.isLangRus() ? "Аукцион по какой-то причине не инициализирован. Создание невозможно." : "Auction for some reason has not been initialized. Create impossible.");
					return false;
				}

				int auctionItemId = Integer.parseInt(st.nextToken());
				ItemTemplate auctionItemTemplate = ItemHolder.getInstance().getTemplate(auctionItemId);
				if(auctionItemTemplate == null)
				{
					activeChar.sendMessage(activeChar.isLangRus() ? "Указанный разыгрываемый предмет не существует. Создание невозможно." : "The specified auction item does not exist. Create impossible.");
					showCreatePage(activeChar);
					return false;
				}

				int auctionItemEnchant = Integer.parseInt(st.nextToken());
				long auctionItemCount = Long.parseLong(st.nextToken());

				int bidItemId = Integer.parseInt(st.nextToken());
				ItemTemplate bidItemTemplate = ItemHolder.getInstance().getTemplate(bidItemId);
				if(bidItemTemplate == null)
				{
					activeChar.sendMessage(activeChar.isLangRus() ? "Указанный предмет ставки не существует. Создание невозможно." : "The specified bid item does not exist. Create impossible.");
					showCreatePage(activeChar);
					return false;
				}

				long bidItemCount = Long.parseLong(st.nextToken());

				int startTimeHour = Integer.parseInt(st.nextToken());
				int startTimeMinute = Integer.parseInt(st.nextToken());
				int startTimeDay = Integer.parseInt(st.nextToken());
				int startTimeMonth = Integer.parseInt(st.nextToken()) - 1;
				Calendar startTime = Calendar.getInstance();
				startTime.set(Calendar.DAY_OF_MONTH, startTimeDay);
				startTime.set(Calendar.MONTH, startTimeMonth);
				startTime.set(Calendar.HOUR_OF_DAY, startTimeHour);
				startTime.set(Calendar.MINUTE, startTimeMinute);
				startTime.set(Calendar.SECOND, 0);
				startTime.set(Calendar.MILLISECOND, 0);
				while(startTime.getTimeInMillis() < System.currentTimeMillis())
					startTime.add(Calendar.YEAR, 1);

				int endTimeHour = Integer.parseInt(st.nextToken());
				int endTimeMinute = Integer.parseInt(st.nextToken());
				int endTimeDay = Integer.parseInt(st.nextToken());
				int endTimeMonth = Integer.parseInt(st.nextToken()) - 1;
				Calendar endTime = Calendar.getInstance();
				endTime.set(Calendar.DAY_OF_MONTH, endTimeDay);
				endTime.set(Calendar.MONTH, endTimeMonth);
				endTime.set(Calendar.HOUR_OF_DAY, endTimeHour);
				endTime.set(Calendar.MINUTE, endTimeMinute);
				endTime.set(Calendar.SECOND, 0);
				endTime.set(Calendar.MILLISECOND, 0);
				while(endTime.getTimeInMillis() < System.currentTimeMillis())
					endTime.add(Calendar.YEAR, 1);

				if(endTime.before(startTime))
				{
					activeChar.sendMessage(activeChar.isLangRus() ? "Неверно указано время проведения аукциона. Дата начала позже, чем дата окончания. Создание невозможно." : "Incorrectly stated the time of the auction. Start date later than the date of finish. Create impossible.");
					showCreatePage(activeChar);
					return false;
				}

				if(instance.createAndInitCustomAuction(startTime, endTime, auctionItemId, auctionItemCount, auctionItemEnchant, bidItemId, bidItemCount))
				{
					activeChar.sendMessage(activeChar.isLangRus() ? "Аукцион успешно создан." : "Auction created successfully.");
					showMainPage(activeChar);
				}
				else
				{
					activeChar.sendMessage(activeChar.isLangRus() ? "Аукцион для заданного промежутка времени уже существует. Создание невозможно." : "The auction is for a specified period of time already exists. Create impossible.");
					showCreatePage(activeChar);
					return false;
				}
				break;
		}

		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private static void showMainPage(Player player)
	{
		NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(5);
		html.setFile("admin/custom_auction.htm");

		StringBuilder sb = new StringBuilder();
		if(!Config.ALT_CUSTOM_ITEM_AUCTION_ENABLED)
			sb.append(player.isLangRus() ? "Аукцион отключен." : "Auction disabled.");
		else
		{
			ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(ItemAuctionManager.CUSTOM_AUCTION_MANAGER_ID);
			if(instance == null)
				sb.append(player.isLangRus() ? "Аукцион по какой-то причине не инициализирован." : "Auction for some reason has not been initialized.");
			else
			{
				sb.append("<FONT color=\"LEVEL\">");
				sb.append(player.isLangRus() ? "Существующие аукционы:" : "Existing auctions:");
				sb.append("</FONT>");
				sb.append("<br>");

				boolean have = false;
				for(ItemAuction auction : instance.getAuctions())
				{
					sb.append("<a action=\"bypass -h admin_custom_auction_info ");
					sb.append(auction.getAuctionId());
					sb.append("\">");

					AuctionItem auctionItem = auction.getAuctionItem();
					if(auctionItem == null)
						sb.append(player.isLangRus() ? "Аукцион неисправен" : "Auction is defective");
					else
					{
						if(auctionItem.getEnchantLevel() > 0)
						{
							sb.append("+");
							sb.append(auctionItem.getEnchantLevel());
							sb.append(" ");
						}
						sb.append(HtmlUtils.htmlItemName(auctionItem.getItemId()));
						sb.append(" - ");
						sb.append(auctionItem.getCount());
						sb.append(player.isLangRus() ? "шт." : "pcs.");
						if(auction.getEndingTime() < System.currentTimeMillis())
						{
							sb.append(" (");
							sb.append(player.isLangRus() ? "Завершен" : "Finished");
							sb.append(")");
						}
						else if(auction.getStartingTime() <= System.currentTimeMillis() && auction.getEndingTime() > System.currentTimeMillis())
						{
							sb.append(" (");
							sb.append(player.isLangRus() ? "В процессе" : "In progress");
							sb.append(")");
						}
					}
					sb.append("</a><br1>");

					have = true;
				}

				if(!have)
					sb.append(player.isLangRus() ? "Добавленных аукционов нет." : "Добавленных аукционов нет.");

				sb.append("<br>");
				sb.append("<button value=\"");
				sb.append(player.isLangRus() ? "Создать" : "Create");
				sb.append("\" action=\"bypass -h admin_create_custom_auction\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
			}
		}

		html.replace("<?AUCTION_INFO?>", sb.toString());

		player.sendPacket(html);
	}

	private static void showCreatePage(Player player)
	{
		NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(5);
		html.setFile("admin/custom_auction.htm");

		StringBuilder sb = new StringBuilder();
		if(!Config.ALT_CUSTOM_ITEM_AUCTION_ENABLED)
			sb.append(player.isLangRus() ? "Аукцион отключен." : "Auction disabled.");
		else
		{
			ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(ItemAuctionManager.CUSTOM_AUCTION_MANAGER_ID);
			if(instance == null)
				sb.append(player.isLangRus() ? "Аукцион по какой-то причине не инициализирован." : "Auction for some reason has not been initialized.");
			else
			{
				sb.append("<FONT color=\"LEVEL\">");
				sb.append(player.isLangRus() ? "Новый аукцион:" : "New auction:");
				sb.append("</FONT>");
				sb.append("<br>");

				sb.append("<FONT color=\"6DFFCE\">");
				sb.append(player.isLangRus() ? "Разыгрываемый предмет:" : "Auction item:");
				sb.append("</FONT>");
				sb.append("<br1>");
				sb.append("<table><tr>");
				sb.append("<td>");
				sb.append("ID:");
				sb.append("</td>");
				sb.append("<td>");
				sb.append(player.isLangRus() ? "Количество:" : "Count:");
				sb.append("</td>");
				sb.append("<td>");
				sb.append(player.isLangRus() ? "Заточка:" : "Enchant:");
				sb.append("</td>");
				sb.append("</tr><tr>");
				sb.append("<td><edit var=\"auctionItemId\" width=70></td>");
				sb.append("<td><edit var=\"auctionItemCount\" width=70></td>");
				sb.append("<td><edit var=\"auctionItemEnchant\" width=70></td>");
				sb.append("</tr></table>");
				sb.append("<br><br>");

				sb.append("<FONT color=\"6DFFCE\">");
				sb.append(player.isLangRus() ? "Ставка:" : "Bid:");
				sb.append("</FONT>");
				sb.append("<br1>");
				sb.append("<table><tr>");
				sb.append("<td>");
				sb.append("ID:");
				sb.append("</td>");
				sb.append("<td>");
				sb.append(player.isLangRus() ? "Количество:" : "Count:");
				sb.append("</td>");
				sb.append("</tr><tr>");
				sb.append("<td><edit var=\"bidItemId\" width=70></td>");
				sb.append("<td><edit var=\"bidItemCount\" width=70></td>");
				sb.append("</tr></table>");
				sb.append("<br><br>");

				sb.append("<FONT color=\"6DFFCE\">");
				sb.append(player.isLangRus() ? "Время начала:" : "Start time:");
				sb.append("</FONT>");
				sb.append("<br1>");
				sb.append("<table><tr>");
				sb.append("<td>");
				sb.append(player.isLangRus() ? "Час:" : "Hours:");
				sb.append("</td>");
				sb.append("<td>");
				sb.append(player.isLangRus() ? "Минуты:" : "Minutes:");
				sb.append("</td>");
				sb.append("<td>");
				sb.append(player.isLangRus() ? "День:" : "Day:");
				sb.append("</td>");
				sb.append("<td>");
				sb.append(player.isLangRus() ? "Месяц:" : "Month:");
				sb.append("</td>");
				sb.append("</tr><tr>");
				sb.append("<td><edit var=\"startTimeHour\" width=60></td>");
				sb.append("<td><edit var=\"startTimeMinute\" width=60></td>");
				sb.append("<td><edit var=\"startTimeDay\" width=60></td>");
				sb.append("<td><edit var=\"startTimeMonth\" width=60></td>");
				sb.append("</tr></table>");
				sb.append("<br><br>");

				sb.append("<FONT color=\"6DFFCE\">");
				sb.append(player.isLangRus() ? "Время окончания:" : "Finish time:");
				sb.append("</FONT>");
				sb.append("<br1>");
				sb.append("<table><tr>");
				sb.append("<td>");
				sb.append(player.isLangRus() ? "Час:" : "Hours:");
				sb.append("</td>");
				sb.append("<td>");
				sb.append(player.isLangRus() ? "Минуты:" : "Minutes:");
				sb.append("</td>");
				sb.append("<td>");
				sb.append(player.isLangRus() ? "День:" : "Day:");
				sb.append("</td>");
				sb.append("<td>");
				sb.append(player.isLangRus() ? "Месяц:" : "Month:");
				sb.append("</td>");
				sb.append("</tr><tr>");
				sb.append("<td><edit var=\"endTimeHour\" width=60></td>");
				sb.append("<td><edit var=\"endTimeMinute\" width=60></td>");
				sb.append("<td><edit var=\"endTimeDay\" width=60></td>");
				sb.append("<td><edit var=\"endTimeMonth\" width=60></td>");
				sb.append("</tr></table>");
				sb.append("<br><br>");

				sb.append("<button value=\"");
				sb.append(player.isLangRus() ? "Создать" : "Create");
				sb.append("\" action=\"bypass -h admin_create_custom_auction $auctionItemId $auctionItemEnchant $auctionItemCount $bidItemId $bidItemCount $startTimeHour $startTimeMinute $startTimeDay $startTimeMonth $endTimeHour $endTimeMinute $endTimeDay $endTimeMonth\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
				sb.append("<button value=\"");
				sb.append(player.isLangRus() ? "Отмена" : "Cancel");
				sb.append("\" action=\"bypass -h admin_custom_auction\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
				sb.append("<br>");
			}
		}

		html.replace("<?AUCTION_INFO?>", sb.toString());

		player.sendPacket(html);
	}

	private static void showInfoPage(Player player, int auctionId)
	{
		NpcHtmlMessagePacket html = new NpcHtmlMessagePacket(5);
		html.setFile("admin/custom_auction.htm");

		StringBuilder sb = new StringBuilder();
		if(!Config.ALT_CUSTOM_ITEM_AUCTION_ENABLED)
			sb.append(player.isLangRus() ? "Аукцион отключен." : "Auction disabled.");
		else
		{
			ItemAuctionInstance instance = ItemAuctionManager.getInstance().getManagerInstance(ItemAuctionManager.CUSTOM_AUCTION_MANAGER_ID);
			if(instance == null)
				sb.append(player.isLangRus() ? "Аукцион по какой-то причине не инициализирован." : "Auction for some reason has not been initialized.");
			else
			{
				ItemAuction auction = instance.getAuction(auctionId);
				if(auction == null)
					sb.append(player.isLangRus() ? "Аукцион не найден." : "Auction not found.");
				else
				{
					AuctionItem auctionItem = auction.getAuctionItem();
					if(auctionItem == null)
						sb.append(player.isLangRus() ? "Аукцион неисправен." : "Auction is defective.");
					else
					{
						sb.append("<FONT color=\"LEVEL\">");
						sb.append(player.isLangRus() ? "Информация о аукционе:" : "Auction info:");
						sb.append("</FONT>");
						sb.append("<br>");

						sb.append("<FONT color=\"6DFFCE\">");
						sb.append(player.isLangRus() ? "Основное:" : "Main:");
						sb.append("</FONT>");
						sb.append("<br1>");

						sb.append("ID: ");
						sb.append(auction.getAuctionId());
						sb.append("<br1>");

						sb.append(player.isLangRus() ? "Статус: " : "Status: ");
						sb.append(auction.getAuctionState().toString());
						sb.append("<br1>");

						sb.append(player.isLangRus() ? "Время начала: " : "Start time: ");
						sb.append(TimeUtils.toSimpleFormat(auction.getStartingTime()));
						sb.append("<br1>");

						sb.append(player.isLangRus() ? "Время окончания: " : "Finish time: ");
						sb.append(TimeUtils.toSimpleFormat(auction.getEndingTime()));
						sb.append("<br>");

						sb.append("<FONT color=\"6DFFCE\">");
						sb.append(player.isLangRus() ? "Разыгрываемый предмет:" : "Auction item:");
						sb.append("</FONT>");
						sb.append("<br1>");

						sb.append("ID: ");
						sb.append(auctionItem.getItemId());
						sb.append("<br1>");

						sb.append(player.isLangRus() ? "Название: " : "Name: ");
						if(auctionItem.getEnchantLevel() > 0)
						{
							sb.append("+");
							sb.append(auctionItem.getEnchantLevel());
							sb.append(" ");
						}
						sb.append(HtmlUtils.htmlItemName(auctionItem.getItemId()));
						sb.append("<br1>");

						sb.append(player.isLangRus() ? "Количество: " : "Count: ");
						sb.append(auctionItem.getCount());
						sb.append("<br>");

						sb.append("<FONT color=\"6DFFCE\">");
						sb.append(player.isLangRus() ? "Ставка:" : "Bid:");
						sb.append("</FONT>");
						sb.append("<br1>");

						sb.append("ID: ");
						sb.append(auctionItem.getActionBidItemId());
						sb.append("<br1>");

						sb.append(player.isLangRus() ? "Название: " : "Name: ");
						sb.append(HtmlUtils.htmlItemName(auctionItem.getActionBidItemId()));
						sb.append("<br1>");

						sb.append(player.isLangRus() ? "Стартовое количество: " : "Start count: ");
						sb.append(auctionItem.getAuctionInitBid());
						sb.append("<br>");

						sb.append("<button value=\"");
						sb.append(player.isLangRus() ? "Удалить" : "Delete");
						sb.append("\" action=\"bypass -h admin_delete_custom_auction ");
						sb.append(auction.getAuctionId());
						sb.append("\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
						sb.append("<button value=\"");
						sb.append(player.isLangRus() ? "Назад" : "Back");
						sb.append("\" action=\"bypass -h admin_custom_auction\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
					}
				}
			}
		}

		html.replace("<?AUCTION_INFO?>", sb.toString());

		player.sendPacket(html);
	}
}