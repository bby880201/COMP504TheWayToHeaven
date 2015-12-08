package xz42_bb26.game.controller;

import org.junit.validator.PublicClassValidator;

import gov.nasa.worldwind.layers.RenderableLayer;

public interface IViewAdapter {

	void update();

	public RenderableLayer getBoxLayer();

}
