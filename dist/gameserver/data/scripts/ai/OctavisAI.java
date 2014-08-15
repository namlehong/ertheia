package ai;

import instances.Octavis;

import l2s.commons.threading.RunnableImpl;
import l2s.commons.util.Rnd;
import l2s.gameserver.ThreadPoolManager;
import l2s.gameserver.ai.Fighter;
import l2s.gameserver.ai.DefaultAI;
import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

/**
 * Описать и сделать скилы
 */
public class OctavisAI extends Fighter
{
  private static final Location LAIR_CENTER = new Location(207190, 120574, -10009);
  private static final int VOLCANO_NPC = 19161;
  private static final int OCTAVIS_POWER_NPC = 18984;
  private static final int OCTAVIS_LIGHT_FIRST = 29191;
  private static final int OCTAVIS_LIGHT_BEAST = 29192;
  private static final int OCTAVIS_LIGHT_SECOND = 29193;
  private static final int OCTAVIS_LIGHT_THIRD = 29194;
  private static final int OCTAVIS_HARD_FIRST = 29209;
  private static final int OCTAVIS_HARD_BEAST = 29210;
  private static final int OCTAVIS_HARD_SECOND = 29211;
  private static final int OCTAVIS_HARD_THIRD = 29212;
  private static final int CHAIN_STRIKE = 10015;
  private static final int CHAIN_HYDRA = 10016;
  private final Skill VOLCANO_ZONE = SkillTable.getInstance().getInfo(14025, 1);
  private final Skill OCTAVIS_POWER1 = SkillTable.getInstance().getInfo(14028, 1);
  private final Skill OCTAVIS_POWER2 = SkillTable.getInstance().getInfo(14029, 1);
  private final Skill BEAST_HERO_MOVEMENT = SkillTable.getInstance().getInfo(14023, 1);
  private final Skill BEAST_ANCIENT_POWER = SkillTable.getInstance().getInfo(14024, 1);
  private final Skill OCTAVIS_RAIN_OF_ARROWS = SkillTable.getInstance().getInfo(14285, 1);

  private static final int[][] OCTAVIS_MOVE_POINTS = { { 207313, 120584, -10008 }, { 207641, 120626, -10008 }, { 208088, 120619, -10008 }, { 207988, 120926, -10014 }, { 207544, 121363, -10014 }, { 206856, 121378, -10014 }, { 206407, 120949, -10014 }, { 206365, 120275, -10014 }, { 206842, 119771, -10014 }, { 207488, 119759, -10014 }, { 207966, 120223, -10014 } };


	public OctavisAI(NpcInstance actor)
	{
		super(actor);
	}

	
	@Override
	public boolean isGlobalAI()
	{
		return false;
	}

	@Override
	protected void onEvtSpawn()
	{
		final NpcInstance npc = getActor();

		npc.setRandomWalk(false);
		npc.setRunning();

		if((npc.getNpcId() == 19161) || (npc.getNpcId() == 18984))
		{
			npc.setIsInvul(true);
			return;
		}

		if((npc.getNpcId() == 29191) || (npc.getNpcId() == 29209))
		{
			npc.setIsInvul(true);

			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					Octavis refl = null;
					if(npc.getReflection() instanceof Octavis)
						refl = (Octavis) npc.getReflection();	
					if(refl != null)		
					{

						if(refl.status == 1 && refl.volcanos != null && !refl.volcanos.isEmpty())
						{
							for(NpcInstance volcano : refl.volcanos)
								for(Player player : refl.getPlayers())
								{
									if(Rnd.chance(1))
									{
										volcano.teleToLocation(player.getLoc(), refl);
										volcano.setTarget(volcano);
										volcano.doCast(VOLCANO_ZONE, volcano, true);
									}
								}
						}
						for(Player player : refl.getPlayers())
						{
							if(Rnd.chance(1))
							{
								npc.setTarget(player);
								npc.doCast(OCTAVIS_RAIN_OF_ARROWS, player, true);
								break;
							}
						}
					}

					ThreadPoolManager.getInstance().schedule(this, 5000L);
				}
			}
			, 5000L);
		}

		if((npc.getNpcId() != 29194) && (npc.getNpcId() != 29212))
		{
			npc.setIsInvul(false);
		}

		if((npc.getNpcId() == 29194) || (npc.getNpcId() == 29212))
		{
			npc.setIsInvul(false);

			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					Octavis refl = null;
					if(npc.getReflection() instanceof Octavis)
						refl = (Octavis) npc.getReflection();	
					if(refl != null)		
					{				
						if(refl.volcanos != null && refl.status == 3)
						{
							if(Rnd.chance(50))
							{
								NpcInstance volcano = refl.volcanos.get(0);
								volcano.teleToLocation(LAIR_CENTER, refl);
								Skill skill = Rnd.chance(50) ? OCTAVIS_POWER1 : OCTAVIS_POWER2;
								volcano.doCast(skill, volcano, true);

								if(skill == OCTAVIS_POWER1)
								{
									refl.octavisPower.teleToLocation(LAIR_CENTER, refl);
									refl.octavisPower.setNpcState((refl.octavisPower.getNpcState() + 1) % 7);
								}
							}
						}
					}	

					ThreadPoolManager.getInstance().schedule(this, 60000L);
				}
			}
			, 60000L);
		}

		if(isOctavisBeast(npc))
		{
			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					boolean canBeKilled = false;
					if(npc.getCurrentHp() / npc.getMaxHp() < 0.5D)
					{
						canBeKilled = true;
					}
					Octavis refl = null;
					if(npc.getReflection() instanceof Octavis)
						refl = (Octavis) npc.getReflection();	
					if(refl != null)		
					{		
						NpcInstance octavis = findOctavis(refl);
						if(octavis != null)
						{
							if(octavis.isInvul())
							{
								octavis.setIsInvul(!canBeKilled);
							}
							if(npc != null)
								ThreadPoolManager.getInstance().schedule(this, 3000L);
						}
					}
				}
			}	
			, 3000L);
		}

		startMovement(npc);
  
		super.onEvtSpawn();
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance npc = getActor();
		if(npc.getNpcId() == 29192)
			return;

		Octavis refl = null;
		if(npc.getReflection() instanceof Octavis)
			refl = (Octavis) npc.getReflection();	
		if(refl != null)		
		{
			NpcInstance octavis = findOctavis(refl);
			if(octavis == null)
				return;	
			if(((npc.getNpcId() == 29191) || (npc.getNpcId() == 29209)) && (refl.status == 1))
			{
				double hp = npc.getCurrentHp() / npc.getMaxHp();
				if((hp <= 0.5D) && (npc.getNpcState() == 0))
				{
					int effect = (int)(hp * 10.0D);
					if((effect < 5) && (effect > 0))
						npc.setNpcState(effect);
					else if(effect == 0)
						npc.setNpcState(5);
				}
				else if(npc.getCurrentHp() / npc.getMaxHp() <= 0.01D)
				{
					npc.setNpcState(6);
					npc.setNpcState(0);
					refl.nextSpawn();
					npc.deleteMe();
				}
				else if((npc.getCurrentHp() / npc.getMaxHp() > 0.5D) && (npc.getNpcState() == 1))
				{
					npc.setNpcState(6);
					npc.setNpcState(0);
				}
			}
			else if(((npc.getNpcId() == 29193) || (npc.getNpcId() == 29211)) && (npc.getCurrentHp() / npc.getMaxHp() <= 0.01D) && (refl.status == 2))
			{
				refl.nextSpawn();
				npc.deleteMe();
			}
			else if(((npc.getNpcId() == 29194) || (npc.getNpcId() == 29212)) && (npc.getCurrentHp() / npc.getMaxHp() <= 0.01D) && (refl.status == 3))
			{
				npc.doDie(attacker);
				npc.decayMe();
				refl.nextSpawn();
			}				
		}	
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		NpcInstance actor = getActor();

		if((isOctavisBeast(actor)) && ((skill.getId() == 10015) || (skill.getId() == 10016)))
			if(Rnd.chance(40))
			{
				if(Rnd.chance(50))
					actor.doCast(BEAST_HERO_MOVEMENT, caster, true);
				else
					actor.doCast(BEAST_ANCIENT_POWER, caster, true);
			}
	}
	
	private NpcInstance findOctavis(Octavis instance)
	{
		NpcInstance octavis = null;
		for(NpcInstance instanceNpc : instance.getNpcs())
		{
			int npcId = instanceNpc.getNpcId();
			if((npcId == 29191) || (npcId == 29209) || (npcId == 29193) || (npcId == 29211) || (npcId == 29194) || (npcId == 29212))
			{
				octavis = instanceNpc;
				break;
			}
		}
		return octavis;
	}	

	private boolean isOctavisBeast(int npcId)
	{
		return (npcId == 29192) || (npcId == 29210);
	}	
	
	private boolean isOctavisBeast(NpcInstance npc)
	{
		return isOctavisBeast(npc.getNpcId());
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
		NpcInstance actor = getActor();
		if(actor.getNpcId() == 29192 || actor.getNpcId() == 29209)
			return;
		super.onEvtAggression(target, aggro);
	}

	private void startMovement(final NpcInstance npc)
	{
		int npcId = npc.getNpcId();
	
		if((npcId != 29191) && (npcId != 29209))
			return;
			
		Octavis refl = null;
		if(npc.getReflection() instanceof Octavis)
			refl = (Octavis) npc.getReflection();	
		if(refl != null)		
		{
			NpcInstance beast = null;

			for(NpcInstance instanceNpc : refl.getNpcs())
			{
				int lookupNpcId = instanceNpc.getNpcId();
				if(isOctavisBeast(lookupNpcId))
				{
					beast = instanceNpc;
					break;
				}
			}

			final NpcInstance finalizedBeast = beast;

			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl()
				{
					if(finalizedBeast != null && finalizedBeast.isMoving)
					{
						int selectedIndex = -1;
						for(int i = 2; i < OCTAVIS_MOVE_POINTS.length; i++)
						{
							int[] point = OCTAVIS_MOVE_POINTS[i];
							if(selectedIndex < 0)
							{
								selectedIndex = i;
							}
							else
							{
								double selectedDistance = PositionUtils.calculateDistance(finalizedBeast.getX(), finalizedBeast.getY(), 0, OCTAVIS_MOVE_POINTS[selectedIndex][0], OCTAVIS_MOVE_POINTS[selectedIndex][1], 0, false);
								double currentDistance = PositionUtils.calculateDistance(finalizedBeast.getX(), finalizedBeast.getY(), 0, point[0], point[1], 0, false);

								if(currentDistance < selectedDistance)
								{
									selectedIndex = i;
								}
							}
						}

						selectedIndex++;
						if(selectedIndex >= OCTAVIS_MOVE_POINTS.length)
						{
							selectedIndex = 2;
						}

						Location loc = new Location(OCTAVIS_MOVE_POINTS[selectedIndex][0], OCTAVIS_MOVE_POINTS[selectedIndex][1], OCTAVIS_MOVE_POINTS[selectedIndex][2]);

						finalizedBeast.setRunning();
						DefaultAI ai = (DefaultAI) finalizedBeast.getAI();
						ai.addTaskMove(Location.findPointToStay(loc, 250, finalizedBeast.getGeoIndex()), true);						
					}

					double angle = PositionUtils.convertHeadingToDegree(finalizedBeast.getHeading());
					double radians = Math.toRadians(angle);
					double radius = 120.0D;
					int x = (int)(Math.cos(3.141592653589793D + radians - 0.0D) * radius);
					int y = (int)(Math.sin(3.141592653589793D + radians - 0.0D) * radius);

					npc.setRunning();
					DefaultAI ai = (DefaultAI) npc.getAI();
					ai.addTaskMove(Location.findPointToStay(new Location(finalizedBeast.getX() + x, finalizedBeast.getY() + y, finalizedBeast.getZ()), 250, npc.getGeoIndex()), true);							

					ThreadPoolManager.getInstance().schedule(this, 50L);
				}
			}
			, 50L);
		}
	}	
}