package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.npc.FakePlayerPath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Bonux
 */
public final class FakePlayerPathHolder extends AbstractHolder
{
	private static final Logger _log = LoggerFactory.getLogger(FakePlayerPathHolder.class);

	private static final FakePlayerPathHolder _instance = new FakePlayerPathHolder();

	private TIntObjectMap<FakePlayerPath> _pathes = new TIntObjectHashMap<FakePlayerPath>(1000);

	public static FakePlayerPathHolder getInstance()
	{
		return _instance;
	}

	public void addPath(FakePlayerPath path)
	{
		_pathes.put(path.getId(), path);
	}

	public FakePlayerPath getPath(int id)
	{
		return _pathes.get(id);
	}

	public FakePlayerPath[] getAll()
	{
		return _pathes.values(new FakePlayerPath[_pathes.size()]);
	}

	@Override
	public int size()
	{
		return _pathes.size();
	}

	@Override
	public void clear()
	{
		_pathes.clear();
	}
}
