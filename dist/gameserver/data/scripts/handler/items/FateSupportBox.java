package handler.items;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassType2;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.ItemFunctions;

public class FateSupportBox extends SimpleItemHandler{
	
	private final int[] level_check 			= {40, 52, 61, 76, 81};
	private final int[] ertheia_wizard_box 		= {26229, 26231, 26233, 26235, 26237};
	private final int[] ertheia_fighter_box 	= {26230, 26232, 26234, 26236, 26238};
	private final int[] other_fighter_box 		= {37315, 37322, 37329, 37336, 37343};
	private final int[] other_wizard_box 		= {37316, 37323, 37330, 37337, 37344};
	private final int[] other_warrior_box 		= {37317, 37324, 37331, 37338, 37345};
	private final int[] other_rogue_box 		= {37318, 37325, 37332, 37339, 37346};
	private final int[] kamael_box 				= {37319, 37326, 37333, 37340, 37347};
	private final int[] orc_fighter_box 		= {37320, 37327, 37334, 37341, 37348};
	private final int[] orc_mage_box 			= {37321, 37328, 37335, 37342, 37349};

	@Override
	protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl) {
		// TODO Auto-generated method stub
		int itemId = item.getItemId();
		
		if(player.getLevel() < 40 || player.getLevel() > 84)
			return false;
		
		if(!canBeExtracted(player, item))
			return false;

		if(!reduceItem(player, item))
			return false;
		
		int current_index = 0;
		
		for(int i=0; i < level_check.length; i++)
			if(level_check[i] <= player.getLevel())
				current_index = i;
		
		int[] box_by_grade = null;
		
		ClassId currentClass = player.getClassId();
		
		switch (player.getRace()) {
		case ERTHEIA:
			box_by_grade = currentClass.isMage() ? ertheia_wizard_box : ertheia_fighter_box;
			break;
			
		case ORC:
			box_by_grade = currentClass.isMage() ? orc_mage_box : orc_fighter_box;
			break;
			
		case KAMAEL:
			box_by_grade = kamael_box;
			break;

		default:			
			if(currentClass.isOfType2(ClassType2.WARRIOR))
				box_by_grade = other_warrior_box;
			else if(currentClass.isOfType2(ClassType2.ROGUE))
				box_by_grade = other_rogue_box;
			else
				box_by_grade = currentClass.isMage() ? other_wizard_box : other_fighter_box;
		}
		
		ItemFunctions.addItem(player, box_by_grade[current_index], 1, true);
		
		
		player.sendPacket(SystemMessagePacket.removeItems(itemId, 1));
		return true;
	}
}
