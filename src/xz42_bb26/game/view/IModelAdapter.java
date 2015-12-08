package xz42_bb26.game.view;

import gov.nasa.worldwind.geom.Position;
import xz42_bb26.game.model.Depot;

public interface IModelAdapter {

	boolean isNavigator();

	void moveTo(Position p);

	void buySupply(Depot depot);

}
