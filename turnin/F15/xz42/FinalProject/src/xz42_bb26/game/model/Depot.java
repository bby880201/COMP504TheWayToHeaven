package xz42_bb26.game.model;

import java.io.Serializable;
import java.util.UUID;

import gov.nasa.worldwind.geom.Position;

/**
 * This is a depot structure
 * @author xz42, bb26
 *
 */
public class Depot implements Serializable {
	/**
	 * Generated id
	 */
	private static final long serialVersionUID = -3713811666017363399L;

	/**
	 * The price will cost
	 */
	public Integer price;
	/**
	 * latitude
	 */
	public double latitude;
	/**
	 * longitude
	 */
	public double longitude;
	/**
	 * Position by the lat and lon above
	 */
	public transient Position position;
	/**
	 * UUID
	 */
	public UUID uuid = UUID.randomUUID();
}
