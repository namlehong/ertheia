package l2s.gameserver.model.instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.Config;
import l2s.gameserver.GameServer;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.CtrlIntention;
import l2s.gameserver.ai.FakePlayerAI;
import l2s.gameserver.dao.FakePlayerDAO;
import l2s.gameserver.data.xml.holder.PlayerTemplateHolder;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.World;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.PledgeRank;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.network.l2.s2c.AutoAttackStartPacket;
import l2s.gameserver.network.l2.s2c.ChangeWaitTypePacket;
import l2s.gameserver.network.l2.s2c.FakePlayerInfo;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.taskmanager.AutoSaveManager;
import l2s.gameserver.templates.npc.FakePlayerTemplate;
import l2s.gameserver.templates.player.PlayerTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bonux
**/
public class FakePlayerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final Logger _log = LoggerFactory.getLogger(NpcInstance.class);

	private boolean _isSitting = false;
	private Future<?> _autoSaveTask;
	private int _pathId = -1;
	public boolean sittingTaskLaunched = false;

	public FakePlayerInstance(int objectId, FakePlayerTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onAction(final Player player, boolean shift)
	{
		if(!isTargetable(player))
		{
			player.sendActionFailed();
			return;
		}

		if(player.getTarget() != this)
			player.setTarget(this);
		else if(isAutoAttackable(player))
			player.getAI().Attack(this, false, shift);
		else if(player.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW)
		{
			if(!shift)
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this, Config.FOLLOW_RANGE);
			else
				player.sendActionFailed();
		}
		else
			player.sendActionFailed();
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		getTemplate().setSpawned(this);
		startAutoSaveTask();
		setRunning();
	}

	@Override
	protected void onDespawn()
	{
		stopAutoSaveTask();
		store();

		super.onDespawn();

		getTemplate().setSpawned(null);
	}

	@Override
	public FakePlayerAI getAI()
	{
		if(_ai == null)
			synchronized (this)
			{
				if(_ai == null)
					_ai = new FakePlayerAI(this);
			}

		return (FakePlayerAI) _ai;
	}

	@Override
	public FakePlayerTemplate getTemplate()
	{
		return (FakePlayerTemplate) super.getTemplate();
	}

	@Override
	public void broadcastCharInfoImpl()
	{
		for(Player player : World.getAroundPlayers(this))
			player.sendPacket(new FakePlayerInfo(this));
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		if(isInvisible())
			return Collections.emptyList();

		List<L2GameServerPacket> list = new ArrayList<L2GameServerPacket>();
		list.add(new FakePlayerInfo(this));

		boolean dualCast = isDualCastingNow();
		if(isCastingNow())
		{
			Creature castingTarget = getCastingTarget();
			Skill castingSkill = getCastingSkill();
			long animationEndTime = getAnimationEndTime();
			if(castingSkill != null && castingTarget != null && castingTarget.isCreature() && animationEndTime > 0)
				list.add(new MagicSkillUse(this, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0, dualCast));
		}

		if(dualCast)
		{
			Creature castingTarget = getDualCastingTarget();
			Skill castingSkill = getDualCastingSkill();
			long animationEndTime = getDualAnimationEndTime();
			if(castingSkill != null && castingTarget != null && castingTarget.isCreature() && animationEndTime > 0)
				list.add(new MagicSkillUse(this, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0, dualCast));
		}

		if(isInCombat())
			list.add(new AutoAttackStartPacket(getObjectId()));

		//list.add(RelationChangedPacket.update(forPlayer, this, forPlayer));

		if(isMoving || isFollow)
			list.add(movePacket());

		return list;
	}

	@Override
	public void setNpcState(int stateId)
	{
		//
	}

	public int getTitleColor()
	{
		return getTemplate().getTitleColor();
	}

	public int getNameColor()
	{
		return getTemplate().getNameColor();
	}

	public Race getRace()
	{
		return getClassId().getRace();
	}

	public Sex getSex()
	{
		return getTemplate().getSex();
	}

	public ClassId getClassId()
	{
		return getTemplate().getClassId();
	}

	public boolean isNoble()
	{
		return getTemplate().isNoble();
	}

	public int getHairStyle()
	{
		return getTemplate().getHairStyle();
	}

	public int getHairColor()
	{
		return getTemplate().getHairColor();
	}

	public int getFace()
	{
		return getTemplate().getFaceType();
	}

	public boolean isSitting()
	{
		return _isSitting;
	}

	public void setSitting(boolean val)
	{
		_isSitting = val;
	}

	public void sitDown()
	{
		if(isSitting() || sittingTaskLaunched || isAlikeDead())
			return;

		if(isStunned() || isSleeping() || isParalyzed() || isAttackingNow() || isCastingNow() || isMoving || isKnockDowned() || isKnockBacked() || isFlyUp())
			return;

		getAI().setIntention(CtrlIntention.AI_INTENTION_REST, null, null);

		broadcastPacket(new ChangeWaitTypePacket(this, ChangeWaitTypePacket.WT_SITTING));

		setSitting(true);
		sittingTaskLaunched = true;
		ThreadPoolManager.getInstance().schedule(new RunnableImpl(){
			@Override
			public void runImpl() throws Exception
			{
				sittingTaskLaunched = false;
				getAI().clearNextAction();
			}
		}, 2500);
	}

	public void standUp()
	{
		if(!isSitting() || sittingTaskLaunched || isAlikeDead())
			return;

		getAI().clearNextAction();
		broadcastPacket(new ChangeWaitTypePacket(this, ChangeWaitTypePacket.WT_STANDING));

		//setSitting(false);
		sittingTaskLaunched = true;
		ThreadPoolManager.getInstance().schedule(new RunnableImpl(){
			@Override
			public void runImpl() throws Exception
			{
				sittingTaskLaunched = false;
				setSitting(false);
				getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			}
		}, 2500);
	}

	public PledgeRank getPledgeRank()
	{
		return isNoble() ? PledgeRank.BARON : PledgeRank.VAGABOND;
	}

	@Override
	public boolean isFakePlayer()
	{
		return true;
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}

	public final int getPlayerId()
	{
		return getTemplate().getPlayerId();
	}

	public void store()
	{
		FakePlayerDAO.getInstance().insert(getPlayerId(), getLoc(), _pathId);
	}

	public void startAutoSaveTask()
	{
		if(!Config.AUTOSAVE)
			return;
		if(_autoSaveTask == null)
			_autoSaveTask = AutoSaveManager.getInstance().addAutoSaveTask(this);
	}

	public void stopAutoSaveTask()
	{
		if(_autoSaveTask != null)
			_autoSaveTask.cancel(false);
		_autoSaveTask = null;
	}

	public int getPathId()
	{
		return _pathId;
	}

	public void setPathId(int id)
	{
		_pathId = id;
	}

	@Override
	public boolean onTeleported()
	{
		if(super.onTeleported())
		{
			getAI().onTeleported();
			return true;
		}
		return false;
	}
}