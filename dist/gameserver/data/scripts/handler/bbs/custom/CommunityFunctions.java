package handler.bbs.custom;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;

public class CommunityFunctions
{
	public static boolean checkPlayer(Player player)
	{
		if(Config.ALLOW_PVPCB_ABNORMAL) // restriction to do in configs. if false
		{
			if(player.getVar("jailed") != null)
			{
				return false;
			}
			if(!Config.CB_DEATH) //if dead or alike dead
				if(player.isDead() || player.isAlikeDead() || player.isFakeDeath())
					return false;
			if(!Config.CAN_USE_BBS_IN_EVENTS)
				if(player.isInPvPEvent())
					return false;
			if(!Config.CB_ACTION) //if is in action or so
				if(player.isCastingNow() || player.isInCombat() || player.isAttackingNow() || player.getPvpFlag() > 0)
					return false;

			if(!Config.CB_OLY) //if is in olympiad
				if(player.isInOlympiadMode() || player.getOlympiadObserveGame() != null)
					return false;

			if(!Config.CB_CHAOS_FESTIVAL) //if is in olympiad
				if(player.isChaosFestivalParticipant())
					return false;

			if(!Config.CB_FLY) //if is in fly mode.
				if(player.isFlying() || player.isInFlyingTransform())
					return false;

			if(!Config.CB_VEICHLE) //if is in veichle.
				if(player.isInBoat())
					return false;

			if(!Config.CB_MOUNTED) //if is riding.
				if(player.isMounted())
					return false;

			if(!Config.CB_CANT_MOVE) //if cannot move
				if(player.isMovementDisabled() || player.isDecontrolled() || player.isStunned() || player.isSleeping() || player.isRooted() || player.isImmobilized())
					return false;

			if(!Config.CB_STORE_MODE) //if is in storre mode.
				if(player.isInStoreMode() || player.isInTrade() || player.isInOfflineMode())
					return false;

			if(!Config.CB_FISHING) //if is fishing
				if(player.isFishing())
					return false;

			if(!Config.CB_TEMP_ACTION) //if not certantly in game.
				if(player.isLogoutStarted() || player.isTeleporting())
					return false;

			if(!Config.CB_DUEL) //if is in duel.
				if(player.isInDuel())
					return false;

			if(!Config.CB_CURSED) //if have cursed weapon.
				if(player.isCursedWeaponEquipped())
					return false;

			if(!Config.CB_PK) //if is pk.
				if(player.isPK())
					return false;

			if(Config.CB_LEADER) //if is clan leader.
				if(!player.isClanLeader())
					return false;

			if(Config.CB_NOBLE) //if is noble.
				if(!player.isNoble())
					return false;

			if(!Config.CB_TERITORY) //if is in territory siege.
				if(player.isOnSiegeField())
					return false;

			if(Config.CB_PEACEZONE_ONLY) //if is territory siege in progress.
				if(!player.isInZonePeace())
					return false;
			if(player.getLfcGame() != null || player.getPendingLfcEnd())
				return false;		
			if(!player.isActive())
				return false;
			return true;
		}
		else
			return true;
	}
}