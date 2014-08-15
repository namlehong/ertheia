package l2s.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.item.support.AttributeStone;

/**
 * @author Bonux
 */
public class AttributeStoneHolder extends AbstractHolder
{
	private static AttributeStoneHolder _instance = new AttributeStoneHolder();
	private TIntObjectHashMap<AttributeStone> _attributeStones = new TIntObjectHashMap<AttributeStone>();

	public static AttributeStoneHolder getInstance()
	{
		return _instance;
	}

	public void addAttributeStone(AttributeStone attributeStone)
	{
		_attributeStones.put(attributeStone.getItemId(), attributeStone);
	}

	public AttributeStone getAttributeStone(int id)
	{
		return _attributeStones.get(id);
	}

	@Override
	public void log()
	{
		info("load " + size() + " attribute stone(s).");
	}

	@Override
	public int size()
	{
		return _attributeStones.size();
	}

	@Override
	public void clear()
	{
		_attributeStones.clear();
	}
}
