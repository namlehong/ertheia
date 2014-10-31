package npc.model;

import java.util.StringTokenizer;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.commons.util.Rnd;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
 */
public class NewbieGuideInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int MAX_SUPPORT_LEVEL = 90;

	private static final int[][] BUFF_SETS = new int[][]{
		{ 15642, 1 }, // Путешественник - Поэма Рога
		{ 15643, 1 }, // Путешественник - Поэма Барабана
		{ 15644, 1 }, // Путешественник - Поэма Органа
		{ 15645, 1 }, // Путешественник - Поэма Гитары
		{ 15646, 1 }, // Путешественник - Поэма Арфы
		{ 15647, 1 },  // Путешественник - Поэма Лютни
		{ 15651, 1 },  // Путешественник - Соната Битвы
		{ 15652, 1 },  // Путешественник - Соната Движения
		{ 15653, 1 }  // Путешественник - Соната Расслабления
	};

	private static final int[] KNIGHTS_HARMONY = { 15648, 1 }; // Путешественник - Гармония Стража
	private static final int[] WARRIORS_HARMONY = { 15649, 1 }; // Путешественник - Гармония Берсерка
	private static final int[] WIZARDS_HARMONY = { 15650, 1 }; // Путешественник - Гармония Мага

	private static final int[] BLESSING_OF_PROTECTION = { 5182, 1 }; // Благословение Защиты

	private static int TIPS = -1;

	private static final int ADVENTURER_SUPPORT_GOODS = 32241; // Вещи Поддержки Путешественника
	private static final String ADVENTURER_SUPPORT_VAR = "@received_advent_support";

	private static final SchedulingPattern RESTART_DATE_PATTERN = new SchedulingPattern("30 6 * * *");

	public NewbieGuideInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if(val != 0)
			pom = npcId + "-" + val;
		else
			pom = String.valueOf(npcId);

		return "newbie_guide/" + pom + ".htm";
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equalsIgnoreCase("buffs"))
		{
			if(player.getLevel() > MAX_SUPPORT_LEVEL)
			{
				showChatWindow(player, "newbie_guide/blessing_list002.htm");
				return;
			}

			for(int[] skill : BUFF_SETS)
				getBuff(skill[0], skill[1], player);

			int setId = Integer.parseInt(st.nextToken());
			switch(setId)
			{
				case 1:
					getBuff(KNIGHTS_HARMONY[0], KNIGHTS_HARMONY[1], player);
					break;
				case 2:
					getBuff(WARRIORS_HARMONY[0], WARRIORS_HARMONY[1], player);
					break;
				case 3:
					getBuff(WIZARDS_HARMONY[0], WIZARDS_HARMONY[1], player);
					break;
			}

			if(!player.isPK() && player.getLevel() <= 39 && player.getClassLevel() < 2)
				getBuff(BLESSING_OF_PROTECTION[0], BLESSING_OF_PROTECTION[1], player);
		}
		else if(cmd.equalsIgnoreCase("receivebless"))
		{
			if(player.isPK() || player.getLevel() > 39 || player.getClassLevel() >= 2)
			{
				showChatWindow(player, "newbie_guide/pk_protect002.htm");
				return;
			}

			getBuff(BLESSING_OF_PROTECTION[0], BLESSING_OF_PROTECTION[1], player);
		}
		else if(cmd.equalsIgnoreCase("easeshilien"))
		{
			if(player.getDeathPenalty().getLevel() < 3)
			{
				showChatWindow(player, "newbie_guide/ease_shilien001.htm");
				return;
			}

			player.getDeathPenalty().reduceLevel();
		}
		else if(cmd.equalsIgnoreCase("tips"))
		{
			if(!player.getVarBoolean(ADVENTURER_SUPPORT_VAR))
			{
				long restartTime = RESTART_DATE_PATTERN.next(System.currentTimeMillis());
				if(restartTime < System.currentTimeMillis()) // Заглушка, крон не умеет работать с секундами.
					restartTime += 86400000L; // Добавляем сутки.

				player.setVar(ADVENTURER_SUPPORT_VAR, "true", restartTime);
				ItemFunctions.addItem(player, ADVENTURER_SUPPORT_GOODS, 1L, true);
			}

			if(TIPS < 0)
			{
				int i = 0;
				while(true)
				{
					i++;
					String html = HtmCache.getInstance().getNullable("newbie_guide/tips/tip-" + i + ".htm", player);
					if(html == null)
					{
						TIPS = i - 1;
						break;
					}
				}
			}
			showChatWindow(player, "newbie_guide/tips/tip-" + Rnd.get(1, TIPS) + ".htm");
		}
		else
			super.onBypassFeedback(player, command);
	}

	private void getBuff(int skillId, int skillLevel, Player player)
	{
		Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
		if(skill == null)
			return;

		int removed = player.getEffectList().stopEffects(skill);
		if(removed > 0)
			player.sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(skill.getId(), skill.getLevel()));

		skill.getEffects(this, player, false);
	}
}