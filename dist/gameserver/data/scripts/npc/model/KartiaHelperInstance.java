package npc.model;

import instances.Kartia;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.ReflectionUtils;

/**
	Iqman
 */
public final class KartiaHelperInstance extends NpcInstance
{
	
	private static final int KARTIA_SOLO85 = 205;
	private static final int KARTIA_SOLO90 = 206;
	private static final int KARTIA_SOLO95 = 207;
	private static final int KARTIA_PARTY85 = 208;
	private static final int KARTIA_PARTY90 = 209;
	private static final int KARTIA_PARTY95 = 210;

	public KartiaHelperInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;
		
		if(command.equalsIgnoreCase("request_Kartia_P_85"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if(player.canReenterInstance(KARTIA_PARTY85))
					if(r instanceof Kartia)
					{
						Kartia kInst = (Kartia) r;
						if(kInst.getStatus() >= 2)			
							player.teleToLocation(-111281, -14239, -11428, r);
						else
							player.teleToLocation(-119830, -10547, -11925, r);
					}		
				
			}
			else if(player.canEnterInstance(KARTIA_PARTY85))
				ReflectionUtils.enterReflection(player, new Kartia(), KARTIA_PARTY85);
		}
		else if(command.equalsIgnoreCase("request_Kartia_P_90"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if(player.canReenterInstance(KARTIA_PARTY90))
					if(r instanceof Kartia)
					{
						Kartia kInst = (Kartia) r;
						if(kInst.getStatus() >= 2)					
							player.teleToLocation(-111281, -14239, -11428, r);
						else
							player.teleToLocation(-119830, -10547, -11925, r);
					}					
					
			}
			else if(player.canEnterInstance(KARTIA_PARTY90))
				ReflectionUtils.enterReflection(player, new Kartia(), KARTIA_PARTY90);
		}	
		else if(command.equalsIgnoreCase("request_Kartia_P_95"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if(player.canReenterInstance(KARTIA_PARTY95))
					if(r instanceof Kartia)
					{
						Kartia kInst = (Kartia) r;
						if(kInst.getStatus() >= 2)				
							player.teleToLocation(-111281, -14239, -11428, r);
						else
							player.teleToLocation(-119830, -10547, -11925, r);
					}					
					
			}
			else if(player.canEnterInstance(KARTIA_PARTY95))
				ReflectionUtils.enterReflection(player, new Kartia(), KARTIA_PARTY95);
		}	
		else if(command.equalsIgnoreCase("request_Kartia_S_85"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if(player.canReenterInstance(KARTIA_SOLO85))
					if(r instanceof Kartia)
					{
						Kartia kInst = (Kartia) r;
						if(kInst.getStatus() >= 2)		
							player.teleToLocation(-111281, -14239, -11428, r);
						else
							player.teleToLocation(-110262, -10547, -11925, r);
					}				
			}
			else if(player.canEnterInstance(KARTIA_SOLO85))
				ReflectionUtils.enterReflection(player, new Kartia(), KARTIA_SOLO85);
		}	
		else if(command.equalsIgnoreCase("request_Kartia_S_90"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if(player.canReenterInstance(KARTIA_SOLO90))
					if(r instanceof Kartia)
					{
						Kartia kInst = (Kartia) r;
						if(kInst.getStatus() >= 2)	
							player.teleToLocation(-111281, -14239, -11428, r);
						else
							player.teleToLocation(-110262, -10547, -11925, r);
					}					
			}
			else if(player.canEnterInstance(KARTIA_SOLO90))
				ReflectionUtils.enterReflection(player, new Kartia(), KARTIA_SOLO90);
		}
		else if(command.equalsIgnoreCase("request_Kartia_S_95"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if(player.canReenterInstance(KARTIA_SOLO95))
					if(r instanceof Kartia)
					{
						Kartia kInst = (Kartia) r;
						if(kInst.getStatus() >= 2)		
							player.teleToLocation(-111281, -14239, -11428, r);
						else
							player.teleToLocation(-110262, -10547, -11925, r);
					}					
			}
			else if(player.canEnterInstance(KARTIA_SOLO95))
				ReflectionUtils.enterReflection(player, new Kartia(), KARTIA_SOLO95);
		}
		else if(command.equalsIgnoreCase("Kartia_DeselectWarrior"))
		{
			Reflection r = player.getActiveReflection();	
			if (r != null)
			{
				if(r instanceof Kartia)
				{
					Kartia kInst = (Kartia) r;	
					kInst.deselectSupport("WARRIOR");
				}	
			}
		}	
		else if(command.equalsIgnoreCase("Kartia_DeselectArcher"))
		{
			Reflection r = player.getActiveReflection();	
			if (r != null)
			{
				if(r instanceof Kartia)
				{
					Kartia kInst = (Kartia) r;	
					kInst.deselectSupport("ARCHER");
				}	
			}
		}		
		else if(command.equalsIgnoreCase("Kartia_DeselectSummoner"))
		{
			Reflection r = player.getActiveReflection();	
			if (r != null)
			{
				if(r instanceof Kartia)
				{
					Kartia kInst = (Kartia) r;	
					kInst.deselectSupport("SUMMONER");
				}	
			}
		}		
		else if(command.equalsIgnoreCase("Kartia_DeselectHealer"))
		{
			Reflection r = player.getActiveReflection();	
			if (r != null)
			{
				if(r instanceof Kartia)
				{
					Kartia kInst = (Kartia) r;	
					kInst.deselectSupport("HEALER");
				}	
			}
		}		
		else
			super.onBypassFeedback(player, command);
	}
}
