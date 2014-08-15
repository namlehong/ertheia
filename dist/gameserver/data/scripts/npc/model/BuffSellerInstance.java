package npc.model;

import java.util.StringTokenizer;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author Bonux
 */
public class BuffSellerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int BUFF_PRICE = 200000; // 200 000 Adena

	private static final int[][] BUFF_SETS = new int[][]{
		{ 11517, 1 }, // Поэма Рога
		{ 11518, 1 }, // Поэма Барабана
		{ 11519, 1 }, // Поэма Органа
		{ 11520, 1 }, // Поэма Гитары
		{ 11521, 1 }, // Поэма Арфы
		{ 11522, 1 }  // Поэма Лютни
	};

	private static final int[] KNIGHTS_HARMONY = { 11523, 1 }; // Гармония Стража
	private static final int[] WARRIORS_HARMONY = { 11524, 1 }; // Гармония Берсерка
	private static final int[] WIZARDS_HARMONY = { 11525, 1 }; // Гармония Мага

	public BuffSellerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		StringTokenizer st = new StringTokenizer(command, "_");
		String cmd = st.nextToken();
		if(cmd.equals("buffs"))
		{
			if(!st.hasMoreTokens())
				return;

			String cmd2 = st.nextToken();
			if(cmd2.equals("buy"))
			{
				if(!st.hasMoreTokens())
					return;

				if(!player.reduceAdena(BUFF_PRICE, true))
				{
					showChatWindow(player, "default/" + getNpcId() + "-no_adena.htm");
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
			}
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