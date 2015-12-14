package xz42_bb26.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.util.measure.LengthMeasurer;
/**
 * The team information structure
 * @author xz42, bb26
 *
 */
public class Team implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5192671148676572316L;
	/**
	 * team id
	 */
	public UUID uuid;
	/**
	 * supply left
	 */
	public double supply;
	/**
	 * cash left
	 */
	public double cash;
	/**
	 * Current position
	 */
	public transient Position myLocation;
	/**
	 * latitude of the position
	 */
	public double myLatitude;
	/**
	 * longitude of the position
	 */
	public double myLongtitude;
	/**
	 * whether is the navigator or the manager
	 */
	public boolean isNavigator= true;
	/**
	 * team name
	 */
	public String name;
	/**
	 * An adapter of the model
	 */
	private transient GameModel model;
	/**
	 * The behavior when get to a depot
	 * @param depot the depot ge to
	 */
	public void buySupply(Depot depot){
		cash -= depot.price;
		supply +=3000;
		if(cash < 0){
			System.out.println("run out of money with "+cash);
			model.sendGameOver();
		}
	}
	/**
	 * The setter of the model adapter
	 * @param _model the model adapter
	 */
	public void setModel(GameModel _model){
		model = _model;
	}
	/**
	 * Move to a position
	 * @param aPos the position
	 */
	public void moveTo(Position aPos){
		ArrayList<Position> positions = new ArrayList<Position>();
		myLocation = Position.fromDegrees(myLatitude, myLongtitude);
		positions.add(myLocation);
		positions.add(aPos);
		LengthMeasurer measurer = new LengthMeasurer(positions);
		Model worldModel = (Model)WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
		measurer.setFollowTerrain(true);
		consume(measurer.getLength(worldModel.getGlobe()));
		if(supply > 0){
			myLocation = aPos;
			myLatitude = aPos.getLatitude().getDegrees();
			myLongtitude = aPos.getLongitude().getDegrees();
		}
		else{
			System.out.println("run out of supply with "+supply);
			model.sendGameOver();
		}
	}
	/**
	 * Cost some supply when moving
	 * @param length the length moved
	 */
	private void consume(double length){
		this.supply -= Math.abs(0.003*length);
	}
	
	/**
	 * To string
	 */
	public String toString(){
		return uuid+"team: "+name+"is now on "+myLatitude+","+myLongtitude+"with cash "+cash+"and supply "+supply; 
	}
}
