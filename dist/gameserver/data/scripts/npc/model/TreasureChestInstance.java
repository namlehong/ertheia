package npc.model;

import l2s.commons.util.Rnd;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.ChestInstance;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.npc.NpcTemplate;


public class TreasureChestInstance extends ChestInstance
{
	private static final long serialVersionUID = 1L;

	private static final int TREASURE_BOMB_SKILL_ID = 4143;
	private static final int UNLOCK_SKILL_ID = 27;
	private static final int COMMON_TREASURE_OPEN_CHANCE = 25;

	public TreasureChestInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	public void tryOpen(Player player, Skill skill)
	{
		int chance = calcChance(player, skill);
		if(Rnd.chance(chance))
		{
			getAggroList().addDamageHate(player, 10000, 0);
			doDie(player);
		}
		else
		{
			fakeOpen(player);
			player.sendPacket(PlaySoundPacket.BROKEN_KEY);
		}
	}

	public int calcChance(Player player, Skill skill)
	{
		int skillId = skill.getId();
		int skillLvl = skill.getLevel();

		int npcLvl = getLevel();
		int playerLvl = player.getLevel();

		int npcLvlDiff = npcLvl - playerLvl;
		int baseDiff = playerLvl <= 77 ? 6 : 5;
		//Если сундук старше персонажа более чем на baseDiff, тогда награды не даем (не открываемся).
		if(isCommonTreasureChest() && npcLvlDiff >= baseDiff)
			return 0;

		if(skillId == UNLOCK_SKILL_ID)
		{
			int baseChance;
			switch(skillLvl)
			{
				case 1:
					baseChance = 98;
					break;
				case 2:
					baseChance = 84;
					break;
				case 3:
					baseChance = 99;
					break;
				case 4:
					baseChance = 84;
					break;
				case 5:
					baseChance = 88;
					break;
				case 6:
					baseChance = 90;
					break;
				case 7:
					baseChance = 89;
					break;
				case 8:
					baseChance = 88;
					break;
				case 9:
					baseChance = 86;
					break;
				case 10:
					baseChance = 90;
					break;
				case 11:
					baseChance = 87;
					break;
				case 12:
					baseChance = 89;
					break;
				case 13:
					baseChance = 89;
					break;
				case 14:
					baseChance = 89;
					break;
				default:
					baseChance = 0;
					break;
			}

			int chance = baseChance - (((npcLvl - (skillLvl * 4)) - 16) * 6);
			if(chance > baseChance)
				chance = baseChance;

			if(isCommonTreasureChest())
			{
				if(chance > COMMON_TREASURE_OPEN_CHANCE)
					chance = COMMON_TREASURE_OPEN_CHANCE;
			}

			return chance;
		}

		return skill.getActivateRate();
	}

	private void fakeOpen(Creature opener)
	{
		Skill bomb = SkillTable.getInstance().getInfo(TREASURE_BOMB_SKILL_ID, getBombLvl());
		if(bomb != null)
			doCast(bomb, opener, false);
		onDecay();
	}

	private int getBombLvl()
	{
		return getLevel() / 10;
	}

	private boolean isCommonTreasureChest()
	{
		int npcId = getNpcId();
		if(npcId >= 18265 && npcId <= 18286)
			return true;
		return false;
	}

	@Override
	public void onReduceCurrentHp(final double damage, final Creature attacker, Skill skill, final boolean awake, final boolean standUp, boolean directHp)
	{
		if(!isCommonTreasureChest())
			fakeOpen(attacker);
	}
}