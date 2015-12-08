package xz42_bb26.game.model;

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

public class Team {
	public UUID uuid;
	
	public double supply;
	
	public double cash;
	
	public Position myPosition;
	
	public HashMap<Position,Depot> depots;
	
	public boolean isNavigator= true;
	
	public Position destination;

	public String name;

	private GameModel model;
	
	public void buySupply(Depot depot){
		cash -= depot.price;
		if(cash < 0){
			model.sendGameOver();
		}
	}
	
	public Team(GameModel _model) {
		model = _model;
	}
	
	public void moveTo(Position aPos){
		ArrayList<Position> positions = new ArrayList<Position>();
		positions.add(myPosition);
		positions.add(aPos);
		LengthMeasurer measurer = new LengthMeasurer(positions);
		Model worldModel = (Model)WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
		measurer.setFollowTerrain(true);
		consume(measurer.getLength(worldModel.getGlobe()));
		if(supply > 0){
			myPosition = aPos;
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
