package xz42_bb26.game.controller;

import common.IChatroom;
import xz42_bb26.game.model.GameModel;
import xz42_bb26.game.view.GameView;
import xz42_bb26.game.view.IModelAdapter;

public class GameController {
	/**
	 * Model of the MVC.
	 */
	private GameModel model;
	/**
	 * View of the MVC
	 */
	private GameView view;
	
	/**
	 * Constructor of the controller.
	 * @param team The team.
	 */
	public GameController() {
		model = new GameModel(new IViewAdapter() {
			
		});
		view = new GameView(new IModelAdapter() {
			
		});
	}
}
