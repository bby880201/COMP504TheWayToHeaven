package xz42_bb26.game.model;

import java.util.HashMap;
import java.util.HashSet;

import common.IChatUser;
import gov.nasa.worldwind.geom.Position;
import xz42_bb26.game.controller.IViewAdapter;

public class GameModel {
	/**
	 * Model to view adapter.
	 */
	private IViewAdapter view;
	
	private Chatroom globalChatroom;
	
	private HashMap<IChatUser,Position> teams;
	
	private Team team;

	
	/**
	 * Constructor of the game model.
	 * @param view A model to view adapter.
	 */
	public GameModel(IViewAdapter iViewAdapter) {
		view = iViewAdapter;
	}
	
	public void updateStatus(){
		
	}
	
	public void moveTo(Position pos){
		team.moveTo(pos);;
	}

	public void start() {
		
	}

	public boolean isNavigator() {
		return team.isNavigator;
	}
	


}
