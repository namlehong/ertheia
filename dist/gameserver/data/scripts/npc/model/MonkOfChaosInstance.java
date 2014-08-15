package npc.model;

import java.util.Collection;

import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassLevel;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ItemFunctions;

/**
 * @author Bonux
 */
public class MonkOfChaosInstance extends NpcInstance
{
	private static final int CHAOS_POMANDER = 37374; // Благовоние Хаоса
	private static final int CHAOS_POMANDER_DUAL_CLASS = 37375; // Благовоние Хаоса

	private static final long CHAOS_SKILLS_CANCEL_PRICE = 100000000L;

	private static final long serialVersionUID = 1L;

	public MonkOfChaosInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equals("learn_chaos_skills"))
		{
			if(!player.isAwaked())
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_learn.htm");
				return;
			}

			ClassId classId = player.getClassId();
			if(classId.isOutdated())
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_learn.htm");
				return;
			}

			if(!player.isBaseClassActive() && !player.isDualClassActive())
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_subclass.htm");
				return;
			}

			showChaosSkillList(player);
		}
		else if(command.equals("cancel_chaos_skills"))
		{
			if(!player.isAwaked())
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_learn.htm");
				return;
			}

			ClassId classId = player.getClassId();
			if(classId.isOutdated())
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_learn.htm");
				return;
			}

			if(!player.isBaseClassActive() && !player.isDualClassActive())
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_subclass.htm");
				return;
			}

			if(player.getAdena() < CHAOS_SKILLS_CANCEL_PRICE)
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_adena.htm");
				return;
			}

			int cancelled = 0;
			Collection<SkillLearn> skillLearnList = SkillAcquireHolder.getInstance().getAvailableSkills(null, (player.isBaseClassActive() ? AcquireType.CHAOS : AcquireType.DUAL_CHAOS));
			for(SkillLearn learn : skillLearnList)
			{
				Skill skill = player.getKnownSkill(learn.getId());
				if(skill == null)
					continue;

				player.removeSkill(skill, true);
				cancelled++;
			}

			if(cancelled == 0)
			{
				showChatWindow(player, "default/" + getNpcId() + "-no_cancelled.htm");
				return;
			}

			player.reduceAdena(CHAOS_SKILLS_CANCEL_PRICE, true);
			ItemFunctions.addItem(player, (player.isBaseClassActive() ? CHAOS_POMANDER : CHAOS_POMANDER_DUAL_CLASS), cancelled, true);
			showChatWindow(player, "default/" + getNpcId() + "-cancelled.htm");
		}
		else
			super.onBypassFeedback(player, command);
	}
}
