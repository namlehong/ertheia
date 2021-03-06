package l2s.gameserver.skills.effects;

import l2s.gameserver.Config;
import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.network.l2.s2c.ExRotation;
import l2s.gameserver.network.l2.s2c.FlyToLocationPacket;
import l2s.gameserver.stats.Env;
import l2s.gameserver.templates.skill.EffectTemplate;
import l2s.gameserver.utils.Location;
import l2s.gameserver.utils.PositionUtils;

public class EffectShadowStep extends EffectFlyAbstract
{
	private int _x, _y, _z;

	public EffectShadowStep(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public void onStart()
	{
		super.onStart();

		int px = getEffected().getX();
		int py = getEffected().getY();

		double ph = PositionUtils.convertHeadingToDegree(getEffected().getHeading()) + 180;
		if(ph > 360)
			ph -= 360;

		ph = (Math.PI * ph) / 180;
		
		_x = (int) (px + (30 * Math.cos(ph)));
		_y = (int) (py + (30 * Math.sin(ph)));
		_z = getEffected().getZ();

		if(Config.ALLOW_GEODATA)
		{
			Location destiny = GeoEngine.moveCheck(getEffector().getX(), getEffector().getY(), getEffector().getZ(), _x, _y, getEffector().getGeoIndex());
			_x = destiny.getX();
			_y = destiny.getY();
		}

		getEffector().abortAttack(true, true);
		getEffector().abortCast(true, true);
		getEffector().stopMove();

		getEffector().broadcastPacket(new FlyToLocationPacket(getEffector(), new Location(_x, _y, _z), FlyToLocationPacket.FlyType.DUMMY, getFlySpeed(), getFlyDelay(), getFlyAnimationSpeed()));
		getEffector().setXYZ(_x, _y, _z);
		getEffector().validateLocation(1);
		getEffector().setHeading(PositionUtils.calculateHeadingFrom(getEffector(), getEffected()));
		getEffector().broadcastPacket(new ExRotation(getEffector().getObjectId(), getEffector().getHeading()));
	}

	public boolean onActionTime()
	{
		return false;
	}
}