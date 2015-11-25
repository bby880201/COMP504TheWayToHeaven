package xz42_bb26.game.model;

import java.util.HashSet;

import gov.nasa.worldwind.geom.Position;
import xz42_bb26.game.controller.IViewAdapter;

public class GameModel {
	/**
	 * Model to view adapter.
	 */
	private IViewAdapter view;
	
	private Team myTeam;
	
	private HashSet<Team> teams;
	
	/**
	 * Constructor of the game model.
	 * @param view A model to view adapter.
	 */
	public GameModel(IViewAdapter iViewAdapter) {
		// TODO Auto-generated constructor stub
		view = iViewAdapter;
	}
	
	public void updateStatus(){
		
	}
	
	public void moveTo(Position pos){
		
	}

}
