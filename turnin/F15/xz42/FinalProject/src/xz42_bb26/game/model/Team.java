package xz42_bb26.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import com.sun.prism.Mesh;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.util.measure.LengthMeasurer;

public class Team implements Serializable {
	public UUID uuid;
	
	public double supply;
	
	public double cash;
	
	public transient Position myLocation;
	
	public double myLatitude;
	
	public double myLongtitude;
	
	public boolean isNavigator= true;

	public String name;

	private GameModel model;
	
	public void buySupply(Depot depot){
		cash -= depot.price;
		supply +=10000;
		if(cash < 0){
			model.sendGameOver();
		}
	}
	
	public Team(GameModel _model) {
		model = _model;
	}
	
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
		}
		else{
			model.sendGameOver();
		}
	}
	
	private void consume(double length){
		this.supply -= length;
	}
	
	public void checkWin(){
		
	}
}
