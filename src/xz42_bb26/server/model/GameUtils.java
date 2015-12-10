package xz42_bb26.server.model;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import gov.nasa.worldwind.geom.Position;
import xz42_bb26.game.model.Depot;

public class GameUtils {
	static public GameUtils singleton = new GameUtils();
	
	private GameUtils(){};
	
	public Set<Depot> generateRandomDepots(int number, double left, double right, double top, double bottom, int min, int max){
		Set<Depot> result = new HashSet<Depot>();
		for(int i=0;i<number;i++){
			Depot aDepot = new Depot();
			aDepot.latitude = randomDouble(left, right);
			aDepot.longitude = randomDouble(top, bottom);
			aDepot.price = randomInt(min, max);
			result.add(aDepot);
		}
		return result;
	}
	
	public double randomDouble(double from, double to){
		Random r = new Random();
		double randomValue = from + (to - from) * r.nextDouble();
		return randomValue;
	}
	public Integer randomInt(int from, int to){
		Random generator = new Random(); 
		int i = generator.nextInt(to-from) + from;
		return i;
	}
}
