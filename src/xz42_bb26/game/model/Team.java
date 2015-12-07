package xz42_bb26.game.model;

import java.util.HashSet;
import java.util.UUID;

import gov.nasa.worldwind.geom.Position;

public class Team {
	public UUID uuid;
	
	public Integer supply;
	
	public Integer cash;
	
	public Position myPosition;
	
	public HashSet<Depot> depots;
	
	public boolean isNavigator= true;
	
	public Position destination;

	public void buySupply(){

	}
	
	public void moveTo(Position aPos){
		
	}
	
	public void checkWin(){
		
	}
}
