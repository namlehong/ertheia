package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.jump.JumpTrack;

/**
 * @author Bonux
 * @date  04/11/2011 18:31
 */
public final class JumpTracksHolder extends AbstractHolder
{
	private static final JumpTracksHolder _instance = new JumpTracksHolder();

	private TIntObjectMap<JumpTrack> _jumpingTracks = new TIntObjectHashMap<JumpTrack>();

	public static JumpTracksHolder getInstance()
	{
		return _instance;
	}

	public void addTrack(JumpTrack track)
	{
		_jumpingTracks.put(track.getId(), track);
	}

	public JumpTrack getTrack(int id)
	{
		return _jumpingTracks.get(id);
	}

	@Override
	public int size()
	{
		return _jumpingTracks.size();
	}

	@Override
	public void clear()
	{
		_jumpingTracks.clear();
	}
}
