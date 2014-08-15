package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.HashMap;
import java.util.Map;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.dao.CharacterDAO;
import l2s.gameserver.templates.npc.FakePlayerTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: Bonux
 */
public final class FakePlayersHolder extends AbstractHolder
{
	private static final Logger _log = LoggerFactory.getLogger(FakePlayersHolder.class);

	private static final FakePlayersHolder _instance = new FakePlayersHolder();

	private TIntObjectMap<FakePlayerTemplate> _players = new TIntObjectHashMap<FakePlayerTemplate>(1000);
	private Map<String, FakePlayerTemplate> _playerNames = new HashMap<String, FakePlayerTemplate>(1000);

	public static FakePlayersHolder getInstance()
	{
		return _instance;
	}

	public void addTemplate(FakePlayerTemplate template)
	{
		if(CharacterDAO.getInstance().getObjectIdByName(template.name) > 0 || getTemplate(template.name) != null)
		{
			_log.warn(getClass().getSimpleName() + ": Fake player template ID[" + template.getPlayerId() + "] cannot added. Name already exists!");
			return;
		}
		_players.put(template.getPlayerId(), template);
		_playerNames.put(template.name.toLowerCase(), template);
	}

	public FakePlayerTemplate getTemplate(int id)
	{
		return _players.get(id);
	}

	public FakePlayerTemplate getTemplate(String name)
	{
		return _playerNames.get(name.toLowerCase());
	}

	public FakePlayerTemplate[] getAll()
	{
		return _players.values(new FakePlayerTemplate[_players.size()]);
	}

	@Override
	public int size()
	{
		return _players.size();
	}

	@Override
	public void clear()
	{
		_playerNames.clear();
		_players.clear();
	}
}
