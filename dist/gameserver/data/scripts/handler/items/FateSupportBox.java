package handler.items;

import java.util.ArrayList;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassType2;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.SystemMessagePacket;
import l2s.gameserver.utils.ItemFunctions;

public class FateSupportBox extends SimpleItemHandler{
	/*
	26229	Fated Support Box: Ertheia Wizard (C-grade)
	26230	Fated Support Box: Ertheia Fighter (C-grade)
	26231	Fated Support Box: Ertheia Wizard (B-grade)
	26232	Fated Support Box: Ertheia Fighter (B-grade)
	26233	Fated Support Box: Ertheia Wizard (A-grade)
	26234	Fated Support Box: Ertheia Fighter (A-grade)
	26235	Fated Support Box: Ertheia Wizard - Stage 1 (S-grade)
	26236	Fated Support Box: Ertheia Fighter - Stage 1 (S-grade)
	26237	Fated Support Box: Ertheia Wizard - Stage 2 (S-grade)
	26238	Fated Support Box: Ertheia Fighter - Stage 2 (S-grade)
	26239	Fated Support Box: Sayha's Seer (R-grade)
	26240	Fated Support Box: Eviscerator (R-grade)
	37314	Fated Support Box
	37315	Fated Support Box: Fighter (C-grade)
	37316	Fated Support Box: Wizard (C-grade)
	37317	Fated Support Box: Warrior (C-grade)
	37318	Fated Support Box: Rogue (C-grade)
	37319	Fated Support Box: Kamael (C-grade)
	37320	Fated Support Box: Orc Fighter (C-grade)
	37321	Fated Support Box: Orc Wizard (C-grade)
	37322	Fated Support Box: Fighter (B-grade)
	37323	Fated Support Box: Wizard (B-grade)
	37324	Fated Support Box: Warrior (B-grade)
	37325	Fated Support Box: Rogue (B-grade)
	37326	Fated Support Box: Kamael (B-grade)
	37327	Fated Support Box: Orc Fighter (B-grade)
	37328	Fated Support Box: Orc Wizard (B-grade)
	37329	Fated Support Box: Fighter (A-grade)
	37330	Fated Support Box: Wizard (A-grade)
	37331	Fated Support Box: Warrior (A-grade)
	37332	Fated Support Box: Rogue (A-grade)
	37333	Fated Support Box: Kamael (A-grade)
	37334	Fated Support Box: Orc Fighter (A-grade)
	37335	Fated Support Box: Orc Wizard (A-grade)
	37336	Fated Support Box: Fighter (S-grade) (Stage 1)
	37337	Fated Support Box: Wizard (S-grade) (Stage 1)
	37338	Fated Support Box: Warrior (S-grade) (Stage 1)
	37339	Fated Support Box: Rogue (S-grade) (Stage 1)
	37340	Fated Support Box: Kamael (S-grade) (Stage 1)
	37341	Fated Support Box: Orc Fighter (S-grade) (Stage 1)
	37342	Fated Support Box: Orc Wizard (S-grade) (Stage 1)
	37343	Fated Support Box: Fighter (S-grade) (Stage 2)
	37344	Fated Support Box: Wizard (S-grade) (Stage 2)
	37345	Fated Support Box: Warrior (S-grade) (Stage 2)
	37346	Fated Support Box: Rogue (S-grade) (Stage 2)
	37347	Fated Support Box: Kamael (S-grade) (Stage 2)
	37348	Fated Support Box: Orc Fighter (S-grade) (Stage 2)
	37349	Fated Support Box: Orc Wizard (S-grade) (Stage 2)
	37350	Fated Support Box: Sigel Knight (R-grade)
	37351	Fated Support Box: Tyrr Warrior (R-grade)
	37352	Fated Support Box: Othell Rogue (R-grade)
	37353	Fated Support Box: Yul Archer (R-grade)
	37354	Fated Support Box: Feoh Wizard (R-grade)
	37355	Fated Support Box: Wynn Summoner (R-grade)
	37356	Fated Support Box: Aeore Healer (R-grade)
	37357	Fated Support Box: Iss Enchanter (R-grade)
	 */
	
	/*
	
	37135,37136,37137,37138,37139, //plate
	37140,37141,37142,37143,37135, // karmian
	37144,37145,37146,37147,37135, //plate leather
	37148,37149,37150,37151,37152, // doomH
	37153,37154,37155,37156,37157, // avadon
	37158,37159,37160,37148, // doomL
	37161,37162,37163,37164,37165, // nightmare
	37166,37167,37168,37169,37170, // dcr
	37171,37172,37173,37174,37167, //dcl
	37175,37176,37177,37178,37179,37180, // ic
	37181,37182,37183,37184,37185, //arcana
	37186,37187,37188,37189, // dra
	37190,37191,37192,37193,37194,37195, // dyn H
	37196,37197,37198,37199,37200,37201, // dyn R
	37202,37203,37204,37205,37206, // dynL
	37224,37225,37226, // ringC
	37227,37228,37229, // ringB
	37230,37231,37232, // ringA
	37233,37234,37235, // ringS
	37236,37237,37238, // ringDyn
	
	26224,26225,26226,26227,26228,
	
	sw, blunt, dual, mggicsw, spear, dag, bow, acient, rapier, crossbow, 2h, first, staff,
	37242,37243,37244,37245,37246,37247,37248,37249,37250,37251,37252,37253,26224,
	37254,37255,37256,37257,37258,37259,37260,37261,37262,37263,37264,37265,26225,
	37266,37267,37268,37269,37270,37271,37272,37273,37274,37275,37276,37277,26226,
	
	37278,37279,37280,37281,37282,37283,37376,37284,37285,37286,37287,37288,26227,
	sw, blunt, dual, mggicsw, spear, dag, bow, acient, rapier, crossbow, 2h, first
	37289,37290,37291,37289,37292,37293,37377,37295,37296,37294,37297,37298,26228,
	 */
	private final int FATED_SUPPORT_BOX 		= 37314;
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
	
	private final int[][] boxes_by_type 		= {
		ertheia_wizard_box,
		ertheia_fighter_box,
		other_fighter_box,
		other_wizard_box,
		other_warrior_box,
		other_rogue_box,
		kamael_box,
		orc_fighter_box,
		orc_mage_box,
	};
	
	private final int[][] ring_sets				= {
		{37224,37224,37225,37226,37226},
		{37227,37227,37228,37229,37229},
		{37230,37230,37231,37232,37232},
		{37233,37233,37234,37235,37235},
		{37236,37236,37237,37238,37238}
	};
	private final int[][] weapon_sets 			= {
		// 0-sw, 1-blunt, 2-dual, 3-mggicsw, 4-spear, 5-dag, 6-bow, 
		// 7-acient, 8-rapier, 9-crossbow, 10-2h, 11-first, 12-staff, 13-shield
		{37242,37243,37244,37245,37246,37247,37248,37249,37250,37251,37252,37253,26224,37139},
		{37254,37255,37256,37257,37258,37259,37260,37261,37262,37263,37264,37265,26225,37152},
		{37266,37267,37268,37269,37270,37271,37272,37273,37274,37275,37276,37277,26226,37165},
		{37278,37279,37280,37281,37282,37283,37376,37284,37285,37286,37287,37288,26227,37180},
		{37289,37290,37291,37289,37292,37293,37377,37295,37296,37294,37297,37298,26228,37195}
	};
	
	private final int[][][] armor_sets 			= {
		// 0-H, 1-R, 2-L
		{{37135,37136,37137,37138}, {37140,37141,37142,37143,37135}, {37144,37145,37146,37147,37135}},
		{{37148,37149,37150,37151}, {37153,37154,37155,37156,37157}, {37158,37159,37160,37148}},
		{{37161,37162,37163,37164}, {37166,37167,37168,37169,37170}, {37171,37172,37173,37174,37167}},
		{{37175,37176,37177,37178,37179}, {37181,37182,37183,37184,37185}, {37186,37187,37188,37189}},
		{{37190,37191,37192,37193,37194}, {37196,37197,37198,37199,37200,37201}, {37202,37203,37204,37205,37206}},
	};

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
		
		switch (item.getItemId()) {
		case FATED_SUPPORT_BOX:
			useFatedSupportBox(player, item, ctrl);
			break;

		default:
			useFatedSupportBoxByGrade(player, item, ctrl);
			break;
		}
		player.sendPacket(SystemMessagePacket.removeItems(itemId, 1));
		return true;
	}
	
	private void useFatedSupportBoxByGrade(Player player, ItemInstance item, boolean ctrl) {
		
		int level_index = 0;
		
		for(int[] box_by_grade: boxes_by_type){
			for(int i=0; i<box_by_grade.length; i++){
				if(item.getItemId() == box_by_grade[i])
					level_index = i;
			}
		}
		
		ClassId classId = player.getClassId();
		
		switch (classId.getType2()) {
		case KNIGHT:
			for(int item_id: armor_sets[level_index][0])
				ItemFunctions.addItem(player, item_id, 1, true);
			ItemFunctions.addItem(player, weapon_sets[level_index][13], 1, true);
			ItemFunctions.addItem(player, weapon_sets[level_index][1], 1, true);
			break;
			
		case WARRIOR:
			for(int item_id: armor_sets[level_index][classId.isOfRace(Race.ERTHEIA) ? 2 : 0])
				ItemFunctions.addItem(player, item_id, 1, true);
			
			if(classId.equalsOrChildOf(ClassId.BERSERKER))
				ItemFunctions.addItem(player, weapon_sets[level_index][7], 1, true);
			else if(classId.equalsOrChildOf(ClassId.WARLORD))
				ItemFunctions.addItem(player, weapon_sets[level_index][4], 1, true);
			else if(classId.equalsOrChildOf(ClassId.GLADIATOR))
				ItemFunctions.addItem(player, weapon_sets[level_index][2], 1, true);
			else if(classId.equalsOrChildOf(ClassId.DESTROYER))
				ItemFunctions.addItem(player, weapon_sets[level_index][10], 1, true);
			else if(classId.equalsOrChildOf(ClassId.WARSMITH)){
				ItemFunctions.addItem(player, weapon_sets[level_index][1], 1, true);
				ItemFunctions.addItem(player, weapon_sets[level_index][13], 1, true);
			}else{
				ItemFunctions.addItem(player, weapon_sets[level_index][11], 1, true);
			}
			
			break;
			
		case ROGUE:
			for(int item_id: armor_sets[level_index][2])
				ItemFunctions.addItem(player, item_id, 1, true);
			ItemFunctions.addItem(player, weapon_sets[level_index][5], 1, true);
			break;
			
		case ARCHER:
			for(int item_id: armor_sets[level_index][2])
				ItemFunctions.addItem(player, item_id, 1, true);
			ItemFunctions.addItem(player, weapon_sets[level_index][classId.isOfRace(Race.KAMAEL) ? 9 : 6], 1, true);
			break;
			
		case ENCHANTER:
			for(int item_id: armor_sets[level_index][classId.isMage() ? 1 : 0])
				ItemFunctions.addItem(player, item_id, 1, true);
			
			ItemFunctions.addItem(player, weapon_sets[level_index][classId.isMage() ? 3 : 2], 1, true);
			break;

		default:
			for(int item_id: armor_sets[level_index][1])
				ItemFunctions.addItem(player, item_id, 1, true);
			ItemFunctions.addItem(player, weapon_sets[level_index][classId.isOfRace(Race.ERTHEIA) ? 12 : classId.isOfRace(Race.KAMAEL) ? 8 : 3], 1, true);
			break;
		}
		
		for(int item_id: ring_sets[level_index])
			ItemFunctions.addItem(player, item_id, 1, true);
		
	}

	private void useFatedSupportBox(Player player, ItemInstance item, boolean ctrl){
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
	}

}
