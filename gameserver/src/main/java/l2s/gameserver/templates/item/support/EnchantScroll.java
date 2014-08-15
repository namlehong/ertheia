package l2s.gameserver.templates.item.support;

import l2s.gameserver.templates.item.ItemGrade;
import org.napile.primitive.Containers;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

public class EnchantScroll
{
	private final int _itemId;
	private final int _maxEnchant;
	private final EnchantType _type;
	private final ItemGrade _grade;
	private final FailResultType _resultType;
	private final int _variation;
	private boolean _showFailEffect;

	private IntSet _items = Containers.EMPTY_INT_SET;

	public EnchantScroll(int itemId, int variation, int maxEnchant, EnchantType type, ItemGrade grade, FailResultType resultType, boolean showFailEffect)
	{
		_itemId = itemId;
		_maxEnchant = maxEnchant;
		_type = type;
		_grade = grade;
		_resultType = resultType;
		_variation = variation;
		_showFailEffect = showFailEffect;
	}

	public void addItemId(int id)
	{
		if(_items.isEmpty())
			_items = new HashIntSet();

		_items.add(id);
	}

	public int getItemId()
	{
		return _itemId;
	}

	public int getMaxEnchant()
	{
		return _maxEnchant;
	}

	public ItemGrade getGrade()
	{
		return _grade;
	}

	public IntSet getItems()
	{
		return _items;
	}

	public EnchantType getType()
	{
		return _type;
	}

	public int getVariationId()
	{
		return _variation;
	}

	public FailResultType getResultType()
	{
		return _resultType;
	}

	public boolean showFailEffect()
	{
		return _showFailEffect;
	}
}
