package l2s.gameserver.data.xml.parser;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractFileParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.BeautyShopHolder;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.templates.beatyshop.BeautyColorTemplate;
import l2s.gameserver.templates.beatyshop.BeautySetTemplate;
import l2s.gameserver.templates.beatyshop.BeautyStyleTemplate;

/**
 * @author Bonux
 */
public final class BeautyShopParser extends AbstractFileParser<BeautyShopHolder>
{
	private static final BeautyShopParser _instance = new BeautyShopParser();

	public static BeautyShopParser getInstance()
	{
		return _instance;
	}

	protected BeautyShopParser()
	{
		super(BeautyShopHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/beauty_shop.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "beauty_shop.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		long defaultResetPrice = 0L;
 
		for(Iterator<Element> iterator = rootElement.elementIterator("config"); iterator.hasNext();)
		{
			Element element = iterator.next();

			Config.BEAUTY_SHOP_COIN_ITEM_ID = Integer.parseInt(element.attributeValue("coin_item_id"));
			defaultResetPrice = Long.parseLong(element.attributeValue("default_reset_price"));
		}

		for(Iterator<Element> iterator = rootElement.elementIterator("set"); iterator.hasNext();)
		{
			Element element = iterator.next();

			Race race = Race.valueOf(element.attributeValue("race"));
			Sex sex = Sex.valueOf(element.attributeValue("sex"));
			ClassType _class = element.attributeValue("class") == null ? null : ClassType.valueOf(element.attributeValue("class"));

			TIntObjectMap<BeautyStyleTemplate> hairs = new TIntObjectHashMap<BeautyStyleTemplate>();
			for(Iterator<Element> hairsIterator = element.elementIterator("hairs"); hairsIterator.hasNext();)
			{
				Element hairElement = hairsIterator.next();

				for(Iterator<Element> styleIterator = hairElement.elementIterator("style"); styleIterator.hasNext();)
				{
					Element styleElement = styleIterator.next();

					BeautyStyleTemplate hair = readStyle(styleElement, defaultResetPrice, true);
					hairs.put(hair.getId(), hair);
				}
			}

			TIntObjectMap<BeautyStyleTemplate> faces = new TIntObjectHashMap<BeautyStyleTemplate>();
			for(Iterator<Element> facesIterator = element.elementIterator("faces"); facesIterator.hasNext();)
			{
				Element faceElement = facesIterator.next();

				for(Iterator<Element> styleIterator = faceElement.elementIterator("style"); styleIterator.hasNext();)
				{
					Element styleElement = styleIterator.next();

					BeautyStyleTemplate face = readStyle(styleElement, defaultResetPrice, false);
					faces.put(face.getId(), face);
				}
			}

			getHolder().addTemplate(new BeautySetTemplate(race, sex, _class, hairs, faces));
		}
	}

	private BeautyStyleTemplate readStyle(Element styleElement, long defaultResetPrice, boolean hair)
	{
		int id = Integer.parseInt(styleElement.attributeValue("id"));
		long adena = styleElement.attributeValue("adena") == null ? 0L : Long.parseLong(styleElement.attributeValue("adena"));
		long coins = styleElement.attributeValue("coins") == null ? 0L : Long.parseLong(styleElement.attributeValue("coins"));
		long reset_price = styleElement.attributeValue("reset_price") == null ? defaultResetPrice : Long.parseLong(styleElement.attributeValue("reset_price"));

		TIntObjectMap<BeautyColorTemplate> colors = null;
		if(hair)
		{
			colors = new TIntObjectHashMap<BeautyColorTemplate>();
			for(Iterator<Element> colorIterator = styleElement.elementIterator("color"); colorIterator.hasNext();)
			{
				Element colorElement = colorIterator.next();

				int color_id = Integer.parseInt(colorElement.attributeValue("id"));
				long color_adena = colorElement.attributeValue("adena") == null ? 0L : Long.parseLong(colorElement.attributeValue("adena"));
				long color_coins = colorElement.attributeValue("coins") == null ? 0L : Long.parseLong(colorElement.attributeValue("coins"));

				colors.put(color_id, new BeautyColorTemplate(color_id, color_adena, color_coins));
			}

			if(colors.isEmpty())
				colors.put(101, new BeautyColorTemplate(101, 0, 0));
		}

		return new BeautyStyleTemplate(id, adena, coins, reset_price, colors);
	}
}
