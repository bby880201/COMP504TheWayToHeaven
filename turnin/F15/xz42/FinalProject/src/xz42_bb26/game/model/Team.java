package xz42_bb26.game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Resource;

import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil.ToStringAdapter;

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

	private transient GameModel model;
	
	public void buySupply(Depot depot){
		cash -= depot.price;
		supply +=10000;
		if(cash < 0){
			System.out.println("run out of money with "+cash);
			model.sendGameOver();
		}
	}
	
	public void setModel(GameModel _model){
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
			myLatitude = aPos.getLatitude().getDegrees();
			myLongtitude = aPos.getLongitude().getDegrees();
		}
		else{
			System.out.println("run out of supply with "+supply);
			model.sendGameOver();
		}
	}
	
	private void consume(double length){
		this.supply -= 0.001*length;
	}
	
	public void checkWin(){	
	}
	
	public String toString(){
		return uuid+"team: "+name+"is now on "+myLatitude+","+myLongtitude+"with cash "+cash+"and supply "+supply; 
	}
}
