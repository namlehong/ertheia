package l2s.gameserver.geodata;

import l2s.commons.geometry.Shape;

public interface GeoCollision
{
	public Shape getShape();

	public byte[][] getGeoAround();

	public void setGeoAround(byte[][] geo);

	public boolean isConcrete();
}
