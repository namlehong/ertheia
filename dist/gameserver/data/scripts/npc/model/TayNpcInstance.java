package npc.model;

import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2s.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.ReflectionUtils;

import instances.Sansililion;

/**
 * Для работы с инстами - SOA
 */
public final class TayNpcInstance extends NpcInstance
{
	private static final long serialVersionUID = 5984176213940365077L;
	
	private static final int soaSansilion = 171;
	private static final Location LOC_OUT = new Location(-178465, 153685, 2488);

	public TayNpcInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.equalsIgnoreCase("request_startWorld"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(r instanceof Sansililion)
				{
					Sansililion sInst = (Sansililion) r;
					if(sInst.getStatus() == 0)
						sInst.startWorld();
				}
			}
		}
		else if(command.equalsIgnoreCase("request_exchange"))
		{
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(r instanceof Sansililion)
				{
					Sansililion sInst = (Sansililion) r;
					if(sInst.getStatus() == 2)
					{
						int amount = 0;
						int points = sInst._points;
						if(points > 1 && points < 800)
							amount = 10;
						else if(points < 1600)
							amount = 60;
						else if(points < 2000)
							amount = 160;
						else if(points < 2000)
							amount = 160;
						else if(points < 2400)
							amount = 200;
						else if(points < 2800)
							amount = 240;
						else if(points < 3200)
							amount = 280;
						else if(points < 3600)
							amount = 320;
						else if(points < 4000)
							amount = 360;
						else if(points > 4000)
							amount = 400;

						if(amount > 0)
							Functions.addItem(player, 17602, amount);

						player.teleToLocation(LOC_OUT, ReflectionManager.DEFAULT);
						sInst.collapse();
					}
				}
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if(val == 0)
		{
			String htmlpath = null;
			Reflection r = player.getActiveReflection();
			if(r != null)
			{
				if(r instanceof Sansililion)
				{
					Sansililion sInst = (Sansililion) r;
					if(sInst.getStatus() == 2)
						htmlpath = "default/33152-1.htm";
					else if(sInst.getStatus() == 1)
					{
						if(player.getEffectList().containsEffects(14228))
						{
							player.sendPacket(new ExShowScreenMessage(NpcString.SOLDER_TIE_RECEIVED_S1_PRIECES_OF_BIO_ENERGY_RESIDUE, 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, 1, -1, false, "40"));
							player.getEffectList().stopEffects(14228);
							sInst._points += 40;
							sInst._lastBuff = 0;
						}
						else
						{
							if(player.getEffectList().containsEffects(14229))
							{
								player.sendPacket(new ExShowScreenMessage(NpcString.SOLDER_TIE_RECEIVED_S1_PRIECES_OF_BIO_ENERGY_RESIDUE, 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, 1, -1, false, "60"));							
								player.getEffectList().stopEffects(14229);
								sInst._points += 60;
								sInst._lastBuff = 0;
							}
							else
							{
								if(player.getEffectList().containsEffects(14230))
								{
									player.sendPacket(new ExShowScreenMessage(NpcString.SOLDER_TIE_RECEIVED_S1_PRIECES_OF_BIO_ENERGY_RESIDUE, 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, 1, -1, false, "80"));
									player.getEffectList().stopEffects(14230);
									sInst._points += 80;
									sInst._lastBuff = 0;
								}
							}
						}
					}
					else
						htmlpath = "default/33152-0.htm";
					if(htmlpath != null)
						player.sendPacket(new NpcHtmlMessagePacket(player, this, htmlpath, val));
				}
			}
			else
				super.showChatWindow(player, val);
		}
		else
			super.showChatWindow(player, val);
	}
}
