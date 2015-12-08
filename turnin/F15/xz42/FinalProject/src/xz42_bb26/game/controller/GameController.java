package xz42_bb26.game.controller;

import java.awt.EventQueue;
import java.rmi.RemoteException;
import java.util.UUID;

import common.IChatUser;
import common.IChatroom;
import common.demo.Chatroom;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import xz42_bb26.client.controller.ChatAppController;
import xz42_bb26.game.model.GameModel;
import xz42_bb26.game.model.GameUser;
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
	public GameController(UUID teamID, IChatUser server, String teamName, Boolean isNavigator) {
		model = new GameModel(new IViewAdapter() {

			@Override
			public void update() {
				view.update();
			}

			@Override
			public RenderableLayer getBoxLayer() {
				// TODO Auto-generated method stub
				return view.getBoxLayer();
			}
			
		},teamID, server,teamName,isNavigator);
		view = new GameView(new IModelAdapter() {

			@Override
			public boolean isNavigator() {
				return model.isNavigator();
			}

			@Override
			public void moveTo(Position p) {
				model.moveTo(p);
			}
			
		});
	}
	public void start() throws RemoteException {	
		view.start();
		model.start();
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
