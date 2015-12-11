package xz42_bb26.game.view;

import gov.nasa.worldwind.geom.Position;
import xz42_bb26.game.model.Depot;
import xz42_bb26.game.model.Team;

public interface IModelAdapter {
	
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

	boolean isNavigator();

	void moveTo(Position p);

	void buySupply(Depot depot);

	Team getTeam();

	void quit();

}
