package ai;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.ai.Guard;
import l2s.gameserver.data.xml.holder.NpcHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Skill.SkillType;
import l2s.gameserver.model.instances.GuardInstance;
import l2s.gameserver.model.instances.MonsterInstance;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.skills.AbnormalEffect;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

import instances.Kartia;

public class KartiaGuardSpec extends Guard
{

	public KartiaGuardSpec(NpcInstance actor)
	{
		super(actor);
	}
	
	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		if(getActor().getNpcId() == 33641 || getActor().getNpcId() == 33643 || getActor().getNpcId() == 33645)
			if(target.isPlayer())
				return;
		super.onEvtAggression(target, aggro);
	}


	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		final NpcInstance actor = getActor();
		
		actor.setRunning();

		actor.setBusy(true);
		actor.setHaveRandomAnim(false);
	}		
}
