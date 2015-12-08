package xz42_bb26.game.view;

import gov.nasa.worldwind.geom.Position;

public interface IModelAdapter {

	boolean isNavigator();

	void moveTo(Position p);

}
