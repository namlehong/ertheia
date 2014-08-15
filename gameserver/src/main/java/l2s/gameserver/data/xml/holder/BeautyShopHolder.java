package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.templates.beatyshop.BeautySetTemplate;

/**
 * @author Bonux
**/
public final class BeautyShopHolder extends AbstractHolder
{
	private static final BeautyShopHolder _instance = new BeautyShopHolder();

	private TIntObjectMap<TIntObjectMap<TIntObjectMap<BeautySetTemplate>>> _templates = new TIntObjectHashMap<TIntObjectMap<TIntObjectMap<BeautySetTemplate>>>();

	public static BeautyShopHolder getInstance()
	{
		return _instance;
	}

	public void addTemplate(BeautySetTemplate template)
	{
		TIntObjectMap<TIntObjectMap<BeautySetTemplate>> templatesByRace = _templates.get(template.getRace().ordinal());
		if(templatesByRace == null)
		{
			templatesByRace = new TIntObjectHashMap<TIntObjectMap<BeautySetTemplate>>();
			_templates.put(template.getRace().ordinal(), templatesByRace);
		}

		TIntObjectMap<BeautySetTemplate> templatesBySex = templatesByRace.get(template.getSex().ordinal());
		if(templatesBySex == null)
		{
			templatesBySex = new TIntObjectHashMap<BeautySetTemplate>();
			templatesByRace.put(template.getSex().ordinal(), templatesBySex);
		}
	
		if(template.getClassType() == null)
		{
			for(ClassType _class : ClassType.VALUES)
				templatesBySex.put(_class.ordinal(), template);
		}
		else
			templatesBySex.put(template.getClassType().ordinal(), template);
	}

	public BeautySetTemplate getTemplate(Player player)
	{
		TIntObjectMap<TIntObjectMap<BeautySetTemplate>> templatesByRace = _templates.get(player.getRace().ordinal());
		if(templatesByRace == null)
			return null;

		TIntObjectMap<BeautySetTemplate> templatesBySex = templatesByRace.get(player.getSex().ordinal());
		if(templatesBySex == null)
			return null;

		return templatesBySex.get(player.getBaseClassType().ordinal());
	}

	@Override
	public int size()
	{
		return _templates.size();
	}

	@Override
	public void clear()
	{
		_templates.clear();
	}
}
