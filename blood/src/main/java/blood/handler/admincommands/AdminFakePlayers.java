package blood.handler.admincommands;

import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
//import l2s.gameserver.model.Effect;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.tables.FakePlayersTable;
//import l2s.gameserver.tables.PetDataTable;
//import l2s.gameserver.templates.item.ItemTemplate.Grade;
import l2s.gameserver.utils.ItemFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blood.BloodConfig;
import blood.FPCInfo;
import blood.ai.FPCDefaultAI;
import blood.base.FPCParty;
import blood.base.FPCRole;
import blood.base.FPCSpawnStatus;
import blood.data.holder.FPItemHolder;
import blood.table.FPCMerchantTable;
import blood.utils.ClassFunctions;

public class AdminFakePlayers implements IAdminCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(AdminFakePlayers.class);
	
	private static enum Commands
	{
		admin_fp_debug,
		admin_fp_attack,
		admin_fp_check,
		admin_fp_party_check,
		admin_fp_quota,
		admin_fp_padding,
		admin_reload_merchant,
		admin_clear_inv,
		admin_fp_spawn,
		admin_fp_equip,
		admin_fp_class, 
		}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;

		switch(command)
		{
			case admin_fp_attack:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //fp_attack on");
					activeChar.sendMessage("USAGE: //fp_attack off");
					return false;
				}
				
				String mode = wordList[1];
				
				if(mode.equals("on")){
					BloodConfig.AI_ATTACK_ALLOW = true;
				}
				else if(mode.equals("off")){
					BloodConfig.AI_ATTACK_ALLOW = false;
				}else{
					BloodConfig.AI_ATTACK_ALLOW = !BloodConfig.AI_ATTACK_ALLOW;
				}
				
				_log.info("AI status: "+BloodConfig.AI_ATTACK_ALLOW);
				
				break;
				
			case admin_fp_debug:
				
				String debugTargetName = null;
				Player debug_player = null;
				
				if(wordList.length < 2)
				{
					GameObject obj = activeChar.getTarget();
					debug_player = GameObjectsStorage.getPlayer(obj.getObjectId());
					
				}
				else
				{
					debugTargetName = wordList[1];
					debug_player = GameObjectsStorage.getPlayer(debugTargetName);
				}
				
				if(debug_player == null)
				{
					activeChar.sendMessage("USAGE: //fp_debug charName");
					return false;
				}
				
				
				if(debug_player.getAI() instanceof FPCDefaultAI)
				{
					FPCDefaultAI ai = (FPCDefaultAI) debug_player.getAI();
					ai.toggleDebug();
				}
				return true;
				
			case admin_fp_quota:
				
				if(wordList.length < 3)
				{
					activeChar.sendMessage("USAGE: //admin_fp_quota role num");
					activeChar.sendMessage("USAGE: ROLE LIST");
					for (FPCRole role : FPCRole.values()) 
			    	{
						activeChar.sendMessage("USAGE: " + role.getName());
			    	}
					return false;
				}
				else
				{
					for (FPCRole role : FPCRole.values()) 
			    	{
						if(role.getName().equals(wordList[1]))
						{
							role.setQuota(Integer.parseInt(wordList[2]));
							activeChar.sendMessage("INFO: Set role "+ role.getName() + " quota into " + wordList[2]);
						}
			    	}
				}
				
				break;

			case admin_fp_padding:
				
				if(wordList.length < 3)
				{
					activeChar.sendMessage("USAGE: //admin_fp_padding role num");
					activeChar.sendMessage("USAGE: ROLE LIST");
					for (FPCRole role : FPCRole.values()) 
			    	{
						activeChar.sendMessage("USAGE: " + role.getName());
			    	}
					return false;
				}
				else
				{
					for (FPCRole role : FPCRole.values()) 
			    	{
						if(role.getName() == wordList[1])
						{
							role.setQuotaAdjustRate(Integer.parseInt(wordList[2]));
							activeChar.sendMessage("INFO: Set role "+ role.getName() + " padding into " + wordList[2]);
						}
			    	}
				}
				
				break;
				
			case admin_fp_check:
				String report;
				int totalOnline = 0;
				int totalFPC = 0;
				for(Player player : GameObjectsStorage.getAllPlayersForIterate())
					totalOnline++;
				
				for(FPCSpawnStatus status: FPCSpawnStatus.values())
		    	{
					report = "Status: " + status + " size: " + status.getSize();
					activeChar.sendMessage(report);
					
		    	}
		    	
		    	for(FPCRole role: FPCRole.values())
		    	{
		    		report = "Role: " + role + " size: " + role.getSize() + " quota: "+role.getQuota();
		    		activeChar.sendMessage(report);
		    		totalFPC += role.getSize();
		    	}

				report = "Total online: " + totalOnline + " players";
				activeChar.sendMessage(report);
				report = "Real player online: " + (totalOnline - totalFPC) + " players";
				activeChar.sendMessage(report);
				break;
			case admin_fp_party_check:
				GameObject target = activeChar.getTarget();
				if(!target.isPlayer() || !target.getPlayer().isFakePlayer())
					return false;
				
				FPCParty party = FPCInfo.getInstance(target.getPlayer()).getParty();
				if(party == null)
					return false;
				
				party.debug();
				
				break;
			
			case admin_reload_merchant:
				FPCMerchantTable.GetMerchantItemListFromDB();
				break;
				
			case admin_clear_inv:
				ItemInstance[] itemArray = activeChar.getInventory().getItems();
				if(itemArray != null && itemArray.length > 0)
					for(ItemInstance item : itemArray)
					{
						ItemFunctions.removeItem(activeChar, item.getItemId(), item.getCount(), false);
					}
				activeChar.getInventory().store();
				activeChar.sendMessage("Clear Inventory.");
			break;
			
			case admin_fp_spawn:
				if(wordList.length < 2)
				{
					activeChar.sendMessage("USAGE: //fp_spawn [char_name]");
					return false;
				}
				String char_name = wordList[1];
//				GameObjectsStorage.getPlayer(char_name);
//				Player player = GameObjectsStorage.getPlayer(char_name);
				int playerId = CharacterDAO.getInstance().getObjectIdByName(char_name);
				_log.info("spawn: "+playerId+ " name: "+char_name);
				FPCInfo newChar = new FPCInfo(playerId);
            	newChar.spawn();
//            	newChar.setRole(FPCRole.NEXUS_EVENT);
//				World.getPlayer(char_name);
			break;
			case admin_fp_equip:
				FPItemHolder.equip(activeChar, false);
			break;
			case admin_fp_class:
				ClassFunctions.upClass(activeChar);
			break;
		}
		return true;
	}
	

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

}
