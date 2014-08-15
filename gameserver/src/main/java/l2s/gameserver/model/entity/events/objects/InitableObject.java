package l2s.gameserver.model.entity.events.objects;

import java.io.Serializable;

import l2s.gameserver.model.entity.events.GlobalEvent;

/**
 * @author VISTALL
 * @date 11:38/30.06.2011
 */
public interface InitableObject extends Serializable
{
	void initObject(GlobalEvent e);
}
