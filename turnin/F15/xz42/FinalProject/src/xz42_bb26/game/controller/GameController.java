package xz42_bb26.game.controller;

import java.awt.EventQueue;

import common.IChatroom;
import xz42_bb26.client.controller.ChatAppController;
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

			@Override
			public void update() {
				view.update();
			}
			
		});
		view = new GameView(new IModelAdapter() {

			@Override
			public boolean isNavigator() {
				return model.isNavigator();
			}
			
		});
	}
	
	public void start() {	
		view.start();
		model.start();
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameController ctl = new GameController();
					ctl.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
