package l2s.gameserver.model.entity.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author VISTALL
 * @date 10:27/24.02.2011
 */
public abstract class EventOwner implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Set<GlobalEvent> _events = new HashSet<GlobalEvent>(2);

	@SuppressWarnings("unchecked")
	public <E extends GlobalEvent> E getEvent(Class<E> eventClass)
	{
		for(GlobalEvent e : _events)
		{
			if(e.getClass() == eventClass) // fast hack
				return (E) e;
			if(eventClass.isAssignableFrom(e.getClass())) //FIXME [VISTALL]    какойто другой способ определить
				return (E) e;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public <E extends GlobalEvent> List<E> getEvents(Class<E> eventClass)
	{
		List<E> events = new ArrayList<E>();
		for(GlobalEvent e : _events)
		{
			if(e.getClass() == eventClass) // fast hack
			{
				events.add((E) e);
				continue;
			}
			if(eventClass.isAssignableFrom(e.getClass())) //FIXME [VISTALL]    какойто другой способ определить
			{
				events.add((E) e);
				continue;
			}
		}
		return events;
	}

	public void addEvent(GlobalEvent event)
	{
		_events.add(event);
	}

	public void removeEvent(GlobalEvent event)
	{
		_events.remove(event);
	}

	public Set<GlobalEvent> getEvents()
	{
		return _events;
	}
}
