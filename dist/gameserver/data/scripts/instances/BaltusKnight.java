/*
 * Copyright Mazaffaka Project (c) 2013.
 */

package instances;

import l2s.commons.threading.RunnableImpl;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.instancemanager.SpawnManager;
import l2s.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2s.gameserver.listener.actor.OnDeathListener;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaltusKnight extends Reflection
{
	private static final Logger _log = LoggerFactory.getLogger(Vullock.class);
	private static final int Antharas = 29223;
	private static final int Член_Экспедиции = 19133;
	private static final int Член_Экспедиции2 = 19136;
	private Location Antharasspawn = new Location(178664, 115096, -7733, 32767);
	private DeathListener _deathListener = new DeathListener();
	private CurrentHpListener _currentHpListener = new CurrentHpListener();

	@Override
	public void onPlayerEnter(Player player)
	{
		super.onPlayerEnter(player);
		ThreadPoolManager.getInstance().schedule(new AntarasSpawn(this), 1);

	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isNpc() && self.getNpcId() == Antharas)
			{
				for(Player p : getPlayers())
				{
					p.sendPacket(new SystemMessagePacket(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(5));
				}
				startCollapseTimer(5 * 60 * 1000L);
			}
		}
	}

	public class CurrentHpListener implements OnCurrentHpDamageListener
	{
		@Override
		public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
		{
			if(actor.getNpcId() == Antharas)
			{
				if(actor == null || actor.isDead())
				{
					return;
				}

				if(actor.getCurrentHp() <= 14850000) // С потолка.
				{
					actor.removeListener(_currentHpListener);
					SpawnManager.getInstance().spawn("derevo_drakona");
				}

				if(actor.getCurrentHp() <= 12850000) // С потолка.
				{
					actor.removeListener(_currentHpListener);
					SpawnManager.getInstance().spawn("derevo_drakona");
				}

				if(actor.getCurrentHp() <= 12550000) // С потолка.
				{
					actor.removeListener(_currentHpListener);
					SpawnManager.getInstance().spawn("derevo_drakona");
				}

				if(actor.getCurrentHp() <= 10850000) // С потолка.
				{
					actor.removeListener(_currentHpListener);
					SpawnManager.getInstance().spawn("derevo_drakona");
				}
			}
		}

	}

	public class AntarasSpawn extends RunnableImpl
	{
		Reflection _r;

		public AntarasSpawn(Reflection r)
		{
			_r = r;
		}

		@Override
		public void runImpl()
		{
			Location Loc = Antharasspawn;
			NpcInstance AntharasStay = addSpawnWithoutRespawn(Antharas, Loc, 0);
			NpcInstance Кейл = addSpawnWithoutRespawn(19132, new Location(174072, 113944, -7733, 2555), 0);
			NpcInstance Фело = addSpawnWithoutRespawn(19128, new Location(177656, 115128, -7733, 0), 0);
			NpcInstance Эйтельд = addSpawnWithoutRespawn(19129, new Location(177560, 115032, -7733, 1297), 0);
			NpcInstance Фаулла = addSpawnWithoutRespawn(19130, new Location(177608, 115240, -7733, 0), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции, new Location(177512, 115328, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции, new Location(177512, 115528, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции, new Location(177512, 115728, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции, new Location(177512, 115928, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции, new Location(177384, 115328, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции, new Location(177384, 115528, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции, new Location(177384, 115728, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции, new Location(177384, 115928, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции2, new Location(177512, 114488, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции2, new Location(177512, 114688, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции2, new Location(177512, 114288, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции2, new Location(177512, 114088, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции2, new Location(177384, 114688, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции2, new Location(177384, 114488, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции2, new Location(177384, 114288, -7733, 63813), 0);
			_r.addSpawnWithoutRespawn(Член_Экспедиции2, new Location(177384, 114088, -7733, 63813), 0);
			AntharasStay.addListener(_deathListener);
			AntharasStay.addListener(_currentHpListener);
		}
	}
}
