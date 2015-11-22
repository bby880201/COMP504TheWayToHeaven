package xz42_bb26.game.model;

import xz42_bb26.game.controller.IViewAdapter;

public class GameModel {
	/**
	 * Model to view adapter.
	 */
	private IViewAdapter view;
	
	/**
	 * Constructor of the game model.
	 * @param view A model to view adapter.
	 */
	public GameModel(IViewAdapter iViewAdapter) {
		// TODO Auto-generated constructor stub
		view = iViewAdapter;
	}

}
