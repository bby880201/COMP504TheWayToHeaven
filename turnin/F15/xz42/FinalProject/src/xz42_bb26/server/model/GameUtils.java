package xz42_bb26.server.model;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import xz42_bb26.game.model.Depot;

/**
 * Some Utils for the game
 * @author xz42
 *
 */
public class GameUtils {
	/**
	 * A singleton of this class
	 */
	static public GameUtils singleton = new GameUtils();

	private GameUtils() {
	};

	/**
	 * Generate some random depots
	 * @param number the number of the depots
	 * @param left the smaller number of the latitude
	 * @param right the bigger number of the latitude
	 * @param top the smaller number of the longitude
	 * @param bottom the bigger number of the longtitude
	 * @param min the min cost
	 * @param max the max cost
	 * @return the set of the depots
	 */
	public Set<Depot> generateRandomDepots(int number, double left,
			double right, double top, double bottom, int min, int max) {
		Set<Depot> result = new HashSet<Depot>();
		for (int i = 0; i < number; i++) {
			Depot aDepot = new Depot();
			aDepot.latitude = randomDouble(left, right);
			aDepot.longitude = randomDouble(top, bottom);
			aDepot.price = randomInt(min, max);
			result.add(aDepot);
		}
		return result;
	}

	/**
	 * Generate a random double number in range
	 * @param from from
	 * @param to to
	 * @return the random double number 
	 */
	public double randomDouble(double from, double to) {
		Random r = new Random();
		double randomValue = from + (to - from) * r.nextDouble();
		return randomValue;
	}

	/**
	 * Generate a random integer in range
	 * @param from from
	 * @param to to
	 * @return the random integer
	 */
	public Integer randomInt(int from, int to) {
		Random generator = new Random();
		int i = generator.nextInt(to - from) + from;
		return i;
	}
}
