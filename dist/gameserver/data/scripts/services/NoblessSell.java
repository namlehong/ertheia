package services;

import l2s.gameserver.Config;
import l2s.gameserver.instancemanager.QuestManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.entity.olympiad.Olympiad;
import l2s.gameserver.model.quest.Quest;
import l2s.gameserver.model.quest.QuestState;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SkillListPacket;
import l2s.gameserver.scripts.Functions;
import quests._10385_RedThreadofFate;

public class NoblessSell extends Functions
{
	public void get()
	{
		Player player = getSelf();

		if(player.isNoble())
			return;

		if(player.getSubLevel() < 75)
		{
			player.sendMessage("You must make sub class level 75 first.");
			return;
		}

		if(player.getInventory().destroyItemByItemId(Config.SERVICES_NOBLESS_SELL_ITEM, Config.SERVICES_NOBLESS_SELL_PRICE))
		{
			makeSubQuests();
			becomeNoble();
		}
		else if(Config.SERVICES_NOBLESS_SELL_ITEM == 57)
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		else
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
	}

	public void makeSubQuests()
	{
		Player player = getSelf();
		if(player == null)
			return;
		Quest q = QuestManager.getQuest(_10385_RedThreadofFate.class);
		QuestState qs = player.getQuestState(q.getClass());
		if(qs != null)
			qs.exitCurrentQuest(true);
		q.newQuestState(player, Quest.COMPLETED);
	}

	public void becomeNoble()
	{
		Player player = getSelf();
		if(player == null || player.isNoble())
			return;

		Olympiad.addNoble(player);
		player.setNoble(true);
		player.updatePledgeRank();
		player.checkNobleSkills();
		player.sendPacket(new SkillListPacket(player));
		player.broadcastUserInfo(true);
	}
}