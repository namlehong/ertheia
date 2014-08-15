package npc.model;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;
import l2s.gameserver.data.xml.holder.MultiSellHolder;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.base.ClassLevel;

/**
 * @author iqman
 */

public final class VoucherTraderInstance extends NpcInstance
{
	private static final int SignOfAllegiance = 17739;
	private static final int SignOfPledge = 17740;
	private static final int SignOfSincerity = 17741;
	private static final int SignOfWill = 17742;
	private static final int SealOfAllegiance = 17743;
	private static final int SealOfPledge = 17744;
	private static final int SealOfSincerity = 17745;
	private static final int SealOfWill = 17746;


	public VoucherTraderInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onMenuSelect(Player player, int ask, int reply)
	{
		switch (reply)
		{
			case 0:
				int sign = -1; int seal = -1; int exp = -1;
				switch (getNpcId())
				{
					case 33385:
						sign = 17739;
						seal = 17743;
						exp = 60000000;
						break;
					case 33386:
						sign = 17740;
						seal = 17744;
						exp = 66000000;
						break;
					case 33387:
						sign = 17741;
						seal = 17745;
						exp = 68000000;
						break;
					case 33388:
						sign = 17742;
						seal = 17746;
						exp = 76000000;
				}
				if(player.getInventory().getCountOf(sign) == 0)
				{
					showChatWindow(player, "default/voucher_no_item.htm");
					return;
				}
				if (player.getVar(""+getNpcId()+"") != null && (Long.parseLong(player.getVar(""+getNpcId()+"")) > System.currentTimeMillis()))
				{
					showChatWindow(player, "default/voucher_time_left.htm");
					return;
				}

				player.setVar(""+getNpcId()+"", String.valueOf(System.currentTimeMillis() + 86400000L), -1);
				Functions.removeItem(player, sign, 1);
				Functions.addItem(player, seal, 20);
				player.addExpAndSp(exp, 0);
				showChatWindow(player, "default/"+getNpcId()+"-list.htm");
				return;
			case 1:
				switch (getNpcId())
				{
					case 33385:
						MultiSellHolder.getInstance().SeparateAndSend(720, player, 0);
						break;
					case 33386:
						MultiSellHolder.getInstance().SeparateAndSend(723, player, 0);
						break;
					case 33387:
						MultiSellHolder.getInstance().SeparateAndSend(722, player, 0);
						break;
					case 33388:
						MultiSellHolder.getInstance().SeparateAndSend(721, player, 0);
						break;
				}
				return;
			case 2:
				showChatWindow(player, "default/voucher_info.htm");
				return;
			case 3:
				Reflection instance = player.getReflection();
				if (instance != null)
				{
					if(instance.getReturnLoc() != null)
						player.teleToLocation(instance.getReturnLoc(), ReflectionManager.DEFAULT);
					else
						player.setReflection(ReflectionManager.DEFAULT);
				}
				return;
			case 4:
				showChatWindow(player, "default/voucher_talisman_select.htm");
				return;
			case 11:
				showChatWindow(player, "default/voucher_talisman_select_class.htm");
				return;
			case 12:
				if(!player.isAwaked())
				{
					showChatWindow(player, "default/voucher_talisman_no_class.htm");
					return;
				}

				int npcOffset = (getNpcId() - 33385) * 8;
				switch(player.getClassId().getBaseAwakedClassId().getId())
				{
					case 139:
						MultiSellHolder.getInstance().SeparateAndSend(735 + npcOffset, player, 0);
						break;
					case 140:
						MultiSellHolder.getInstance().SeparateAndSend(736 + npcOffset, player, 0);
						break;
					case 141:
						MultiSellHolder.getInstance().SeparateAndSend(737 + npcOffset, player, 0);
						break;
					case 142:
						MultiSellHolder.getInstance().SeparateAndSend(738 + npcOffset, player, 0);
						break;
					case 143:
						MultiSellHolder.getInstance().SeparateAndSend(739 + npcOffset, player, 0);
						break;
					case 144:
						MultiSellHolder.getInstance().SeparateAndSend(740 + npcOffset, player, 0);
						break;
					case 145:
						MultiSellHolder.getInstance().SeparateAndSend(741 + npcOffset, player, 0);
						break;
					case 146:
						MultiSellHolder.getInstance().SeparateAndSend(742 + npcOffset, player, 0);
					//TODO: Add multisells for Ertheia race.
				}
		}	
	}		
}