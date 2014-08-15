package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.Zone;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.EventTriggerPacket;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.PlaySoundPacket;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

import instances.Isthina;

/**
 * Описать и сделать скилы
 */
public class IsthinaAI extends Fighter
{
	//NPC ID
	final int ISTINA_LIGHT = 29195;
	final int ISTINA_HARD = 29196;

	//SKILLS
	private static final Skill BARRIER_OF_REFLECTION = SkillTable.getInstance().getInfo(14215, 1);
	private static final Skill FLOOD = SkillTable.getInstance().getInfo(14220, 1);
	private static final Skill MANIFESTATION_OF_AUTHORITY = SkillTable.getInstance().getInfo(14289, 1);
	private static final Skill ACID_ERUPTION1 = SkillTable.getInstance().getInfo(14221, 1);
	private static final Skill ACID_ERUPTION2 = SkillTable.getInstance().getInfo(14222, 1);
	private static final Skill ACID_ERUPTION3 = SkillTable.getInstance().getInfo(14223, 1);

	//ITEMS
	final int DEATH_BLOW = 14219;
	final int ISTINA_MARK = 14218;

	//RING zone (Trigger)
	final int RED_RING = 14220101;
	final int BLUE_RING = 14220102;
	final int GREEN_RING = 14220103;

	//RING LOCATIONS
	final Zone RED_RING_LOC;
	final Zone BLUE_RING_LOC;
	final Zone GREEN_RING_LOC;

	private static final int ISTINAS_CREATION = 23125;
	private static final int SEALING_ENERGY = 19036;

	private boolean isHard = false;
	private ScheduledFuture<?> _effectCheckTask = null;
	private boolean _authorityLock = false;
	private boolean _hasFlood = false;
	private boolean _hasBarrier = false;
	private long _skillDelay = 30000;
	private long _noReuse = 0;
	private boolean finishLock = false;
	private boolean lastHitLock = false;

	private int _ring;

	private static Zone _zone;

	public IsthinaAI(NpcInstance actor)
	{
		super(actor);

		_zone = ReflectionUtils.getZone("[isthina_epic]");

		RED_RING_LOC = ReflectionUtils.getZone("[isthina_red_zone]");
		BLUE_RING_LOC = ReflectionUtils.getZone("[isthina_blue_zone]");
		GREEN_RING_LOC = ReflectionUtils.getZone("[isthina_green_zone]");
	}

	@Override
	public boolean isGlobalAI()
	{
		return false;
	}

	@Override
	protected void onEvtSpawn()
	{
		NpcInstance npc = getActor();
		isHard = npc.getNpcId() == 29196;
		super.onEvtSpawn();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance npc = getActor();
		if(_effectCheckTask == null)
			_effectCheckTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new EffectCheckTask(npc), 0, 2000);

		double lastPercentHp = (npc.getCurrentHp() + damage) / npc.getMaxHp();
		double currentPercentHp = npc.getCurrentHp() / npc.getMaxHp();
		if((lastPercentHp > 0.9D) && (currentPercentHp <= 0.9D))
			onPercentHpReached(npc, 90);
		else if((lastPercentHp > 0.8D) && (currentPercentHp <= 0.8D))
			onPercentHpReached(npc, 80);
		else if((lastPercentHp > 0.7D) && (currentPercentHp <= 0.7D))
			onPercentHpReached(npc, 70);
		else if((lastPercentHp > 0.6D) && (currentPercentHp <= 0.6D))
			onPercentHpReached(npc, 60);
		else if((lastPercentHp > 0.5D) && (currentPercentHp <= 0.5D))
			onPercentHpReached(npc, 50);
		else if((lastPercentHp > 0.45D) && (currentPercentHp <= 0.45D))
			onPercentHpReached(npc, 45);
		else if((lastPercentHp > 0.4D) && (currentPercentHp <= 0.4D))
			onPercentHpReached(npc, 40);
		else if((lastPercentHp > 0.35D) && (currentPercentHp <= 0.35D))
			onPercentHpReached(npc, 35);
		else if((lastPercentHp > 0.3D) && (currentPercentHp <= 0.3D))
			onPercentHpReached(npc, 30);
		else if((lastPercentHp > 0.25D) && (currentPercentHp <= 0.25D))
			onPercentHpReached(npc, 25);
		else if((lastPercentHp > 0.2D) && (currentPercentHp <= 0.2D))
			onPercentHpReached(npc, 20);
		else if((lastPercentHp > 0.15D) && (currentPercentHp <= 0.15D))
			onPercentHpReached(npc, 15);
		else if((lastPercentHp > 0.1D) && (currentPercentHp <= 0.1D))
			onPercentHpReached(npc, 10);
		else if((!lastHitLock) && (currentPercentHp <= 0.05D))
		{
			lastHitLock = true;
			onPercentHpReached(npc, 5);
		}
		if (!finishLock)
		{
			double seed = Rnd.get(1, 100);
			if ((seed < 2) && (!_authorityLock))
				authorityField(npc);
		}
		super.onEvtAttacked(attacker, damage);
	}

	public void onPercentHpReached(NpcInstance npc, int percent)
	{
		if ((percent == 5) && (!finishLock))
		{
			finishLock = true;
			npc.setIsInvul(true);
			npc.startParalyzed();
			npc.teleToLocation(new Location(-177123, 146938, -11389), npc.getReflection());
			npc.setTargetable(false);

			for (Player player : npc.getReflection().getPlayers())
			{
				if((player.getTarget() != null))
				{
					player.setTarget(null);
					player.abortAttack(true, true);
					player.abortCast(true, true);
					player.sendActionFailed();
				}
			}
			Isthina refl = null;
			if(npc.getReflection() instanceof Isthina)
				refl = (Isthina) npc.getReflection();	
			if(refl != null)	
				refl.presentBallista(npc);
			return;
		}
		
		byte acidsCount = (byte)(Rnd.get(1,3));
		int playerInside = npc.getReflection().getPlayers().size();
		if(playerInside == 0)
		{
			System.out.println("No Players inside?");
		}
		List<Player> unluckPlayers = new ArrayList<Player>(acidsCount);
		for (byte i = 0; i < acidsCount; i = (byte)(i + 1))
		{
			while (unluckPlayers.size() < playerInside)
			{
				Player unluckyPlayer = npc.getReflection().getPlayers().get(Rnd.get(playerInside - 1));
				if(!unluckPlayers.contains(unluckyPlayer))
				{
					unluckPlayers.add(unluckyPlayer);
					break;
				}
			}
		}

		int index = 0;
		if(unluckPlayers.isEmpty())
		{
			System.out.println("No Players inside? 199");
		}
		for(Player player : unluckPlayers)
		{
			Isthina refl = null;
			if(npc.getReflection() instanceof Isthina)
				refl = (Isthina) npc.getReflection();
			if(refl != null)
			{	
				NpcInstance camera = refl.acidEruptionCameras.get(index);
				camera.teleToLocation(player.getLoc(), player.getReflection());
				Skill skillToCast;
				if(Rnd.get() <= 0.5D)
					skillToCast = ACID_ERUPTION3;
				else
					skillToCast = ACID_ERUPTION2;
				camera.doCast(skillToCast, player, false);
				index++;
			}
			else
				System.out.println("No reflection?");
		}	
	
	if (((percent >= 50) && (percent % 10 == 0)) || ((percent < 50) && (percent % 5 == 0)))
		if(!npc.getEffectList().containsEffects(FLOOD.getId()) && !npc.isCastingNow())
			npc.doCast(FLOOD, npc, false);

	if (npc.getNpcId() == 29196)
	{
		if ((percent <= 50) && (percent % 5 == 0))
			for (short i = 0; i < 7; i = (short)(i + 1))
				npc.getReflection().addSpawnWithoutRespawn(23125, npc.getLoc(), 0);

		int energyCount = Rnd.get(1, 4);
		for (int i = 0; i < energyCount; i++)
			npc.getReflection().addSpawnWithoutRespawn(19036, npc.getLoc(), 0);
	}

	}

	private void authorityField(final NpcInstance npc)
	{
		_authorityLock = true;

		double seed = Rnd.get();

		final int ring = (seed >= 0.33D) && (seed < 0.66D) ? 1 : seed < 0.33D ? 0 : 2;
		_ring = ring;
		if(seed < 0.33D)
			npc.broadcastPacket(new ExShowScreenMessage(NpcString.ISTINAS_SOUL_STONE_STARTS_POWERFULLY_ILLUMINATING_IN_GREEN, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, 0, true));
		else
		{
			if((seed >= 0.33D) && (seed < 0.66D))
				npc.broadcastPacket(new ExShowScreenMessage(NpcString.ISTINAS_SOUL_STONE_STARTS_POWERFULLY_ILLUMINATING_IN_BLUE, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, 0, true));
			else
				npc.broadcastPacket(new ExShowScreenMessage(NpcString.ISTINAS_SOUL_STONE_STARTS_POWERFULLY_ILLUMINATING_IN_RED, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, 0, true));
		}
		npc.broadcastPacket(new PlaySoundPacket("istina.istina_voice_01"));

		ThreadPoolManager.getInstance().schedule(new runAuthorityRing(npc), 10000L);
	}

	private class EffectCheckTask extends RunnableImpl
	{
		private NpcInstance _npc;

		public EffectCheckTask(NpcInstance npc)
		{
			_npc = npc;
		}
		
		@Override
		public void runImpl()
		{
			if(_npc == null)
			{
				if(_effectCheckTask != null)
					_effectCheckTask.cancel(false);
			}

			boolean hasBarrier = false;
			boolean hasFlood = false;
			if(_npc.getEffectList().containsEffects(BARRIER_OF_REFLECTION))
			{
				hasBarrier = true;
				if(hasFlood)
					return;
			}
			else
			{
				if(_npc.getEffectList().containsEffects(FLOOD))
					hasFlood = true;
				if(hasBarrier)
					return;
			}
			if((_hasBarrier) && (!hasBarrier))
			{
				_npc.setNpcState(2);
				//TODO[K] - Use skill 
				_npc.setNpcState(0);
				_npc.broadcastPacket(new ExShowScreenMessage(NpcString.POWERFUL_ACIDIC_ENERGY_IS_ERUPTING_FROM_ISTINAS_BODY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, 0, true));
			}
			else if((!_hasBarrier) && (hasBarrier))
				_npc.setNpcState(1);

			if((_hasFlood) && (hasFlood))
				_npc.broadcastPacket(new ExShowScreenMessage(NpcString.ISTINA_GETS_FURIOUS_AND_RECKLESSLY_CRAZY, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, 0, true));
			else if((_hasFlood) && (!hasFlood))
				_npc.broadcastPacket(new ExShowScreenMessage(NpcString.BERSERKER_OF_ISTINA_HAS_BEEN_DISABLED, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, 0, true));
		}
	}

	private class runAuthorityRing extends RunnableImpl
	{
		private NpcInstance _npc;

		runAuthorityRing(NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl()
		{
			NpcInstance npc = _npc;

			Zone zones;
			if(_ring == 2)
			{
				npc.broadcastPacket(new EventTriggerPacket(RED_RING, false));
				npc.broadcastPacket(new EventTriggerPacket(GREEN_RING, false));
				npc.broadcastPacket(new EventTriggerPacket(BLUE_RING, false));
				zones = RED_RING_LOC;
			}
			else if(_ring == 0)
			{
				npc.broadcastPacket(new EventTriggerPacket(BLUE_RING, false));
				npc.broadcastPacket(new EventTriggerPacket(GREEN_RING, false));
				npc.broadcastPacket(new EventTriggerPacket(RED_RING, false));
				zones = GREEN_RING_LOC;
			}
			else
			{
				npc.broadcastPacket(new EventTriggerPacket(RED_RING, false));
				npc.broadcastPacket(new EventTriggerPacket(BLUE_RING, false));
				npc.broadcastPacket(new EventTriggerPacket(GREEN_RING, false));
				zones = BLUE_RING_LOC;
			}
			for(Player player : _zone.getInsidePlayers())
			{
				if(!player.isInZone(zones))
					MANIFESTATION_OF_AUTHORITY.getEffects(npc, player, false, false);
			}
			_authorityLock = false;
		}
	}
}