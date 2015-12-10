package xz42_bb26.game.controller;

import gov.nasa.worldwind.layers.IconLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import xz42_bb26.game.model.Team;

public interface IViewAdapter {

	void update();

	public RenderableLayer getBoxLayer();

	void gameBegin();

	void aTeamWins(Team team);

	public IconLayer getIconLayer();

	void gameOver();

}
