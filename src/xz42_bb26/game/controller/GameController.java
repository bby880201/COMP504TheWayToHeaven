package xz42_bb26.game.controller;

import java.awt.EventQueue;
import java.rmi.RemoteException;
import java.util.UUID;

import common.IChatUser;
import common.message.IChatMessage;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.IconLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import provided.datapacket.DataPacket;
import xz42_bb26.client.model.user.IChatUser2ModelAdapter;
import xz42_bb26.game.model.Depot;
import xz42_bb26.game.model.GameModel;
import xz42_bb26.game.model.GameUser;
import xz42_bb26.game.model.Team;
import xz42_bb26.game.view.GameView;
import xz42_bb26.game.view.IModelAdapter;

/**
 * This is the game controller
 * @author xz42, bb26
 *
 */
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
	public GameController(UUID teamID, IChatUser server, String teamName,
			Boolean isNavigator) {
		model = new GameModel(new IViewAdapter() {

			@Override
			public void update(Team team) {
				view.update(team);
			}

			@Override
			public RenderableLayer getBoxLayer() {
				return view.getBoxLayer();
			}

			@Override
			public void gameBegin() {
				view.gameBegin();
			}

			@Override
			public void aTeamWins(Team team) {
				view.aTeamWins(team);
			}

			@Override
			public IconLayer getIconLayer() {
				return view.getIconLayer();
			}

			@Override
			public void gameOver() {
				view.gameOver();

			}

		}, teamID, server, teamName, isNavigator);
		view = new GameView(new IModelAdapter() {

			@Override
			public boolean isNavigator() {
				return model.isNavigator();
			}

			@Override
			public void moveTo(Position p) {
				model.moveTo(p);
			}

			@Override
			public void buySupply(Depot depot) {
				model.buySupply(depot);
			}

			@Override
			public Team getTeam() {
				return model.getTeam();
			}

			@Override
			public void quit() {
				model.quit();
			}

		});
	}

	/**
	 * Starting the Game.
	 * @throws RemoteException
	 */
	public void start() throws RemoteException {
		view.start();
		model.start();
	}

	/**
	 * This is the main function for testing the game individually.
	 * Can be removed.
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					IChatUser server = new GameUser("aname",
							new IChatUser2ModelAdapter() {

								@Override
								public <T> void receive(IChatUser remote,
										DataPacket<? extends IChatMessage> dp) {
									// TODO Auto-generated method stub

								}
							});
					GameController controller = new GameController(UUID
							.randomUUID(), server, "a team", true);
					controller.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
