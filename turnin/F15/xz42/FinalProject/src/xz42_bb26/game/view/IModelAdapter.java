package xz42_bb26.game.view;

import gov.nasa.worldwind.geom.Position;
import xz42_bb26.game.model.Depot;
import xz42_bb26.game.model.Team;

/**
 * The adapter from view to model
 * @author xz42, bb26
 *
 */
public interface IModelAdapter {
	/**
	 * NULL_OBJECT for adapter
	 */
	public static IModelAdapter NULL_OBJECT = new IModelAdapter() {

		@Override
		public void moveTo(Position p) {
		}

		@Override
		public boolean isNavigator() {
			return false;
		}

		@Override
		public Team getTeam() {
			return null;
		}

		@Override
		public void buySupply(Depot depot) {
		}

		@Override
		public void quit() {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * The role of the player
	 * @return true= is the navigator. false = is the manager
	 */
	boolean isNavigator();

	/**
	 * Move to a position 
	 * @param p the position to move to
	 */
	void moveTo(Position p);

	/**
	 * Get supply from a depot
	 * @param depot
	 */
	void buySupply(Depot depot);

	/**
	 * The getter of the team information
	 * @return my team
	 */
	Team getTeam();

	/**
	 * exit the game
	 */
	void quit();

}
