package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;

import l2s.gameserver.model.entity.events.GlobalEvent;

/**
 * @author VISTALL
 * @date  16:28/10.12.2010
 */
public interface SpawnableObject extends Serializable
{
	void spawnObject(GlobalEvent event);

	void despawnObject(GlobalEvent event);

	void refreshObject(GlobalEvent event);
}
