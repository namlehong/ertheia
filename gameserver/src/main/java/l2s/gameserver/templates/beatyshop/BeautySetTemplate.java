package l2s.gameserver.templates.beatyshop;

import gnu.trove.map.TIntObjectMap;

import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.Sex;

/**
 * @author Bonux
 */
public class BeautySetTemplate
{
	private final Race _race;
	private final Sex _sex;
	private final ClassType _class;
	private final TIntObjectMap<BeautyStyleTemplate> _hairs;
	private final TIntObjectMap<BeautyStyleTemplate> _faces;

	public BeautySetTemplate(Race race, Sex sex, ClassType _class, TIntObjectMap<BeautyStyleTemplate> hairs, TIntObjectMap<BeautyStyleTemplate> faces)
	{
		_race = race;
		_sex = sex;
		this._class = _class;
		_hairs = hairs;
		_faces = faces;
	}

	public Race getRace()
	{
		return _race;
	}

	public Sex getSex()
	{
		return _sex;
	}

	public ClassType getClassType()
	{
		return _class;
	}

	public BeautyStyleTemplate[] getHairs()
	{
		return _hairs.values(new BeautyStyleTemplate[_hairs.size()]);
	}

	public BeautyStyleTemplate getHair(int id)
	{
		return _hairs.get(id);
	}

	public BeautyStyleTemplate[] getFaces()
	{
		return _faces.values(new BeautyStyleTemplate[_faces.size()]);
	}

	public BeautyStyleTemplate getFace(int id)
	{
		return _faces.get(id);
	}
}
