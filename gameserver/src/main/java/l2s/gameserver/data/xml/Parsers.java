package l2s.gameserver.data.xml;

import l2s.gameserver.data.StringHolder;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.xml.holder.BuyListHolder;
import l2s.gameserver.data.xml.parser.AirshipDockParser;
import l2s.gameserver.data.xml.parser.AppearanceStoneParser;
import l2s.gameserver.data.xml.parser.ArmorSetsParser;
import l2s.gameserver.data.xml.parser.AttributeStoneParser;
import l2s.gameserver.data.xml.parser.BaseStatsBonusParser;
import l2s.gameserver.data.xml.parser.BeautyShopParser;
import l2s.gameserver.data.xml.parser.ClassDataParser;
import l2s.gameserver.data.xml.parser.CrystallizationDataParser;
import l2s.gameserver.data.xml.parser.CubicParser;
import l2s.gameserver.data.xml.parser.DomainParser;
import l2s.gameserver.data.xml.parser.DoorParser;
import l2s.gameserver.data.xml.parser.EnchantItemParser;
import l2s.gameserver.data.xml.parser.EnchantSkillGroupParser;
import l2s.gameserver.data.xml.parser.EventParser;
import l2s.gameserver.data.xml.parser.FakePlayerPathParser;
import l2s.gameserver.data.xml.parser.FakePlayersParser;
import l2s.gameserver.data.xml.parser.FishDataParser;
import l2s.gameserver.data.xml.parser.HennaParser;
import l2s.gameserver.data.xml.parser.HitCondBonusParser;
import l2s.gameserver.data.xml.parser.InstantZoneParser;
import l2s.gameserver.data.xml.parser.ItemParser;
import l2s.gameserver.data.xml.parser.JumpTracksParser;
import l2s.gameserver.data.xml.parser.KarmaIncreaseDataParser;
import l2s.gameserver.data.xml.parser.LevelBonusParser;
import l2s.gameserver.data.xml.parser.LevelUpRewardParser;
import l2s.gameserver.data.xml.parser.MultiSellParser;
import l2s.gameserver.data.xml.parser.NpcParser;
import l2s.gameserver.data.xml.parser.OptionDataParser;
import l2s.gameserver.data.xml.parser.PetDataParser;
import l2s.gameserver.data.xml.parser.PetitionGroupParser;
import l2s.gameserver.data.xml.parser.PlayerTemplateParser;
import l2s.gameserver.data.xml.parser.ProductDataParser;
import l2s.gameserver.data.xml.parser.RecipeParser;
import l2s.gameserver.data.xml.parser.ResidenceParser;
import l2s.gameserver.data.xml.parser.RestartPointParser;
import l2s.gameserver.data.xml.parser.RestorationInfoParser;
import l2s.gameserver.data.xml.parser.ShuttleTemplateParser;
import l2s.gameserver.data.xml.parser.SkillAcquireParser;
import l2s.gameserver.data.xml.parser.SoulCrystalParser;
import l2s.gameserver.data.xml.parser.SpawnParser;
import l2s.gameserver.data.xml.parser.StaticObjectParser;
import l2s.gameserver.data.xml.parser.StatuesSpawnParser;
import l2s.gameserver.data.xml.parser.TransformTemplateParser;
import l2s.gameserver.data.xml.parser.VariationDataParser;
import l2s.gameserver.data.xml.parser.ZoneParser;
import l2s.gameserver.instancemanager.ReflectionManager;
import l2s.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 * @date  20:55/30.11.2010
 */
public abstract class Parsers
{
	public static void parseAll()
	{
		HtmCache.getInstance().reload();
		StringHolder.getInstance().load();
		//
		SkillTable.getInstance().load(); // - SkillParser.getInstance();
		RestorationInfoParser.getInstance().load();
		EnchantSkillGroupParser.getInstance().load();
		OptionDataParser.getInstance().load();
		VariationDataParser.getInstance().load();
		ItemParser.getInstance().load();
		RecipeParser.getInstance().load();
		CrystallizationDataParser.getInstance().load();
		//
		BaseStatsBonusParser.getInstance().load();
		BeautyShopParser.getInstance().load();
		LevelBonusParser.getInstance().load();
		KarmaIncreaseDataParser.getInstance().load();
		HitCondBonusParser.getInstance().load();
		PlayerTemplateParser.getInstance().load();
		ClassDataParser.getInstance().load();
		TransformTemplateParser.getInstance().load();
		NpcParser.getInstance().load();
		PetDataParser.getInstance().load();

		DomainParser.getInstance().load();
		RestartPointParser.getInstance().load();

		StaticObjectParser.getInstance().load();
		DoorParser.getInstance().load();
		ZoneParser.getInstance().load();
		SpawnParser.getInstance().load();
		StatuesSpawnParser.getInstance().load();
		InstantZoneParser.getInstance().load();

		ReflectionManager.getInstance().init();
		//
		AirshipDockParser.getInstance().load();
		SkillAcquireParser.getInstance().load();
		//
		ResidenceParser.getInstance().load();
		ShuttleTemplateParser.getInstance().load();
		EventParser.getInstance().load();
		// support(cubic & agathion)
		CubicParser.getInstance().load();
		//
		BuyListHolder.getInstance();
		MultiSellParser.getInstance().load();
		ProductDataParser.getInstance().load();
		// AgathionParser.getInstance();
		// item support
		HennaParser.getInstance().load();
		JumpTracksParser.getInstance().load();
		EnchantItemParser.getInstance().load();
		AttributeStoneParser.getInstance().load();
		AppearanceStoneParser.getInstance().load();
		SoulCrystalParser.getInstance().load();
		ArmorSetsParser.getInstance().load();
		FishDataParser.getInstance().load();

		LevelUpRewardParser.getInstance().load();

		FakePlayerPathParser.getInstance().load();
		FakePlayersParser.getInstance().load();

		// etc
		PetitionGroupParser.getInstance().load();
	}
}
