package npc.model;

import l2s.commons.util.Rnd;
import l2s.gameserver.ai.CtrlEvent;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.MagicSkillUse;
import l2s.gameserver.network.l2.s2c.NpcHtmlMessagePacket;
import l2s.gameserver.scripts.Functions;
import l2s.gameserver.tables.SkillTable;
import l2s.gameserver.templates.npc.NpcTemplate;
import l2s.gameserver.utils.Location;

/**
 * 3 Служебных НПЦ на 5, 6м этаже Tully Workshop
 *
 * @author pchayka
 */
public class WorkshopServantInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private static final int[] medals = {
			10427,
			//Tully's Platinum Medal
			10428,
			//Tully's Tin Medal
			10429,
			//Tully's Lead Medal
			10430,
			//Tully's Zinc Medal
			10431,
			//Tully's Copper Medal
	};
	private static final String[] phrases = {
			"We won't let you go with this knowledge! Die!",
			"Mysterious Agent has failed! Kill him!",
			"Mates! Attack those fools!",
	};

	public WorkshopServantInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if(!canBypassCheck(player, this))
			return;

		if(command.startsWith("getmedals"))
		{
			for(int i = 0; i < medals.length; i++)
				if(player.getInventory().getItemByItemId(medals[i]) != null)
				{
					player.sendPacket(new NpcHtmlMessagePacket(player, this).setHtml("Ingenious Contraption:<br><br>You already have one of the medals. Cannot proceed."));
					return;
				}

			Functions.addItem(player, medals[Rnd.get(0, 4)], 1);
			player.sendPacket(new NpcHtmlMessagePacket(player, this).setHtml("Ingenious Contraption:<br><br>The medal for access to Anomic Founrdy has been given."));

		}
		else if(command.startsWith("requestteleport"))
			player.teleToLocation(-12220, 279713, -13595);
		else if(command.startsWith("teletoroof"))
			player.teleToLocation(22616, 244888, 11062);
		else if(command.startsWith("teleto7thfloor"))
			player.teleToLocation(-12520, 280120, -11649);
		else if(command.startsWith("acceptjob"))
		{
			broadcastPacket(new MagicSkillUse(this, player, 5526, 1, 0, 0));
			player.altOnMagicUse(player, SkillTable.getInstance().getInfo(5526, 1));
			player.teleToLocation(22616, 244888, 11062);
		}
		else if(command.startsWith("rejectjob"))
		{
			for(NpcInstance challenger : World.getAroundNpc(this, 600, 300))
			{
				challenger.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 5000);
				switch(challenger.getNpcId())
				{
					case 25600:
						Functions.npcSay(challenger, phrases[0]);
						break;
					case 25601:
						Functions.npcSay(challenger, phrases[1]);
						break;
					case 25602:
						Functions.npcSay(challenger, phrases[2]);
						break;
					default:
						break;
				}
			}
			Functions.npcSay(this, "Oh...");
			doDie(null);
		}
		else if(command.startsWith("tryanomicentry"))
		{
			if(!player.isInParty())
			{
				player.sendPacket(SystemMsg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
				return;
			}
			Party party = player.getParty();
			if(!party.isLeader(player))
			{
				player.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
				return;
			}
			for(Player p : party.getPartyMembers())
				if(!this.isInRange(p, 500))
				{
					player.sendPacket(SystemMsg.YOU_ARE_TOO_FAR_FROM_THE_NPC_FOR_THAT_TO_WORK);
					return;
				}
			for(int i = 0; i < medals.length; i++)
				if(!hasItem(party, medals[i]))
				{
					player.sendMessage("In order to enter the Anomic Foundry your party should be carrying all 5 medals of Tully");
					return;
				}
			party.Teleport(new Location(25512, 247240, -2656));
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if(val == 0)
			pom = String.valueOf(npcId);
		else
			pom = npcId + "-" + val;

		if(getNpcId() == 32372)
			if(this.isInZone("[tully5]"))
				return "default/32372-floor.htm";
		return "default/" + pom + ".htm";
	}

	private boolean hasItem(Party party, int itemId)
	{
		for(Player p : party.getPartyMembers())
			if(p.getInventory().getItemByItemId(itemId) != null)
				return true;
		return false;
	}
}