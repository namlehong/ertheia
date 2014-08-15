package actions;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.RaidBossInstance;
import l2s.gameserver.model.reward.RewardData;
import l2s.gameserver.model.reward.RewardGroup;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.model.reward.RewardType;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.utils.HtmlUtils;

public abstract class RewardListInfo
{
	private static final NumberFormat pf = NumberFormat.getPercentInstance(Locale.ENGLISH);
	private static final NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);

	static
	{
		pf.setMaximumFractionDigits(4);
		df.setMinimumFractionDigits(2);
	}

	public static void showInfo(Player player, NpcInstance npc)
	{
		final int diff = npc.calculateLevelDiffForDrop(player.isInParty() ? player.getParty().getLevel() : player.getLevel());
		double mod = npc.calcStat(Stats.REWARD_MULTIPLIER, 1., player, null);
		mod *= Experience.penaltyModifier(diff, 9);

		//@SuppressWarnings("unused")
		//boolean icons = player.getVarBoolean("DroplistIcons");

		if(mod <= 0)
		{
			npc.showChatWindow(player, "actions/rewardlist_to_weak.htm", "<?npc_name?>", HtmlUtils.htmlNpcName(npc.getNpcId()));
			return;
		}

		if(npc.getTemplate().getRewards().isEmpty())
		{
			npc.showChatWindow(player, "actions/rewardlist_empty.htm", "<?npc_name?>", HtmlUtils.htmlNpcName(npc.getNpcId()));
			return;
		}

		StringBuilder builder = new StringBuilder(100);
		for(Map.Entry<RewardType, RewardList> entry : npc.getTemplate().getRewards().entrySet())
		{
			RewardList rewardList = entry.getValue();

			switch(entry.getKey())
			{
				case RATED_GROUPED:
					ratedGroupedRewardList(builder, npc, rewardList, player, mod);
					break;
				case NOT_RATED_GROUPED:
					notRatedGroupedRewardList(builder, rewardList, mod);
					break;
				case NOT_RATED_NOT_GROUPED:
					notGroupedRewardList(builder, rewardList, 1.0, mod);
					break;
				case SWEEP:
					notGroupedRewardList(builder, rewardList, Config.RATE_DROP_SPOIL * player.getRateSpoil(), mod);
					break;
			}
		}

		npc.showChatWindow(player, "actions/rewardlist_info.htm", "<?npc_name?>", HtmlUtils.htmlNpcName(npc.getNpcId()), "%info%", builder.toString());
	}

	public static void ratedGroupedRewardList(StringBuilder builder, NpcInstance npc, RewardList list, Player player, double mod)
	{
		StringBuilder tmp = new StringBuilder();
		tmp.append("<table width=270 border=0>");
		tmp.append("<tr><td><table width=270 border=0><tr><td><font color=\"aaccff\">").append(list.getType()).append("</font></td></tr></table></td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareWhite\" width=270 height=1> </td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");

		int replaced = 0;
		for(RewardGroup g : list)
		{
			List<RewardData> items = g.getItems();

			boolean replace = false;
			for(RewardData d : items)
			{
				if(!d.getItem().isHerb())
				{
					replace = true;
					break;
				}
			}

			if(!replace)
				continue;

			double gchance = g.getChance();
			double gmod = mod;
			double grate;
			double gmult;

			double rateDrop = (npc instanceof RaidBossInstance ? Config.RATE_DROP_RAIDBOSS :  npc.isSiegeGuard() ? Config.RATE_DROP_SIEGE_GUARD : Config.RATE_DROP_ITEMS * player.getRateItems());
			double rateAdena = Config.RATE_DROP_ADENA * player.getRateAdena();

			if(g.isAdena())
			{
				if(rateAdena == 0)
					continue;

				grate = rateAdena;

				if(gmod > 10)
				{
					gmod *= g.getChance() / RewardList.MAX_CHANCE;
					gchance = RewardList.MAX_CHANCE;
				}

				grate *= gmod;
			}
			else
			{
				if(rateDrop == 0)
					continue;

				grate = rateDrop;

				if(g.notRate())
					grate = Math.min(gmod, 1.0);
				else
					grate *= gmod;
			}

			gmult = Math.ceil(grate);

			tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");
			tmp.append("<tr><td>");
			tmp.append("<table width=270 border=0 bgcolor=333333>");
			tmp.append("<tr><td width=170><font color=\"a2a0a2\">Group Chance: </font><font color=\"b09979\">").append(pf.format(gchance / RewardList.MAX_CHANCE)).append("</font></td>");
			tmp.append("<td width=100 align=right>");
			tmp.append("</td></tr>");
			tmp.append("</table>").append("</td></tr>");

			tmp.append("<tr><td><table>");
			for(RewardData d : items)
			{
				double imult = d.notRate() ? 1.0 : gmult;
				String icon = d.getItem().getIcon();
				if(icon == null || icon.equals(StringUtils.EMPTY))
					icon = "icon.etc_question_mark_i00";
				tmp.append("<tr><td width=32><img src=").append(icon).append(" width=32 height=32></td><td width=238>").append(HtmlUtils.htmlItemName(d.getItemId())).append("<br1>");
				tmp.append("<font color=\"b09979\">[").append(Math.round(d.getMinDrop() * (g.isAdena() ? gmult : 1.0))).append(" - ").append(Math.round(d.getMaxDrop() * imult)).append("]&nbsp;");
				tmp.append(pf.format(d.getChance() / RewardList.MAX_CHANCE)).append("</font></td></tr>");
				replaced++;
			}
			tmp.append("</table></td></tr>");
		}

		tmp.append("</table>");
		if(replaced > 0)
			builder.append(tmp.toString());
	}

	public static void notRatedGroupedRewardList(StringBuilder builder, RewardList list, double mod)
	{
		StringBuilder tmp = new StringBuilder();
		tmp.append("<table width=270 border=0>");
		tmp.append("<tr><td><table width=270 border=0><tr><td><font color=\"aaccff\">").append(list.getType()).append("</font></td></tr></table></td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareWhite\" width=270 height=1> </td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");

		int replaced = 0;
		for(RewardGroup g : list)
		{
			List<RewardData> items = g.getItems();

			boolean replace = false;
			for(RewardData d : items)
			{
				if(!d.getItem().isHerb())
				{
					replace = true;
					break;
				}
			}

			if(!replace)
				continue;

			double gchance = g.getChance();

			tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");
			tmp.append("<tr><td>");
			tmp.append("<table width=270 border=0 bgcolor=333333>");
			tmp.append("<tr><td width=170><font color=\"a2a0a2\">Group Chance: </font><font color=\"b09979\">").append(pf.format(gchance / RewardList.MAX_CHANCE)).append("</font></td>");
			tmp.append("<td width=100 align=right>");
			tmp.append("</td></tr>");
			tmp.append("</table>").append("</td></tr>");

			tmp.append("<tr><td><table>");
			for(RewardData d : items)
			{
				String icon = d.getItem().getIcon();
				if(icon == null || icon.equals(StringUtils.EMPTY))
					icon = "icon.etc_question_mark_i00";
				tmp.append("<tr><td width=32><img src=").append(icon).append(" width=32 height=32></td><td width=238>").append(HtmlUtils.htmlItemName(d.getItemId())).append("<br1>");
				tmp.append("<font color=\"b09979\">[").append(Math.round(d.getMinDrop())).append(" - ").append(Math.round(d.getMaxDrop())).append("]&nbsp;");
				tmp.append(pf.format(d.getChance() / RewardList.MAX_CHANCE)).append("</font></td></tr>");
				replaced++;
			}
			tmp.append("</table></td></tr>");
		}

		tmp.append("</table>");
		if(replaced > 0)
			builder.append(tmp.toString());
	}

	public static void notGroupedRewardList(StringBuilder builder, RewardList list, double rate, double mod)
	{
		StringBuilder tmp = new StringBuilder();
		tmp.append("<table width=270 border=0>");
		tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");
		tmp.append("<tr><td><table width=270 border=0><tr><td><font color=\"aaccff\">").append(list.getType()).append("</font></td></tr></table></td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareWhite\" width=270 height=1> </td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");

		tmp.append("<tr><td><table>");

		int replaced = 0;
		for(RewardGroup g : list)
		{
			List<RewardData> items = g.getItems();
			double gmod = mod;
			double grate;
			double gmult;

			if(rate == 0)
				continue;

			grate = rate;

			if(g.notRate())
				grate = Math.min(gmod, 1.0);
			else
				grate *= gmod;

			gmult = Math.ceil(grate);

			for(RewardData d : items)
			{
				if(d.getItem().isHerb())
					continue;

				double imult = d.notRate() ? 1.0 : gmult;
				String icon = d.getItem().getIcon();
				if(icon == null || icon.equals(StringUtils.EMPTY))
					icon = "icon.etc_question_mark_i00";
				tmp.append("<tr><td width=32><img src=").append(icon).append(" width=32 height=32></td><td width=238>").append(HtmlUtils.htmlItemName(d.getItemId())).append("<br1>");
				tmp.append("<font color=\"b09979\">[").append(d.getMinDrop()).append(" - ").append(Math.round(d.getMaxDrop() * imult)).append("]&nbsp;");
				tmp.append(pf.format(d.getChance() / RewardList.MAX_CHANCE)).append("</font></td></tr>");
				replaced++;
			}
		}

		tmp.append("</table></td></tr>");
		tmp.append("</table>");
		if(replaced > 0)
			builder.append(tmp.toString());
	}
}