package xz42_bb26.game.controller;

import java.rmi.RemoteException;

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
	public GameController(IChatroom globalGameRoom, String teamName, Boolean isNavigator) {
		model = new GameModel(new IViewAdapter() {

			@Override
			public void update() {
				view.update();
			}
			
		},globalGameRoom,teamName,isNavigator);
		view = new GameView(new IModelAdapter() {

			@Override
			public boolean isNavigator() {
				return model.isNavigator();
			}
			
		});
	}
	public void start() throws RemoteException {	
		view.start();
		model.start();
	}
	
}
