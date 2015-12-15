package xz42_bb26.game.controller;

import gov.nasa.worldwind.layers.IconLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import xz42_bb26.game.model.Team;

/**
 * The model to view adapter
 * @author xz42, bb26
 *
 */
public interface IViewAdapter {
	/**
	 * Update team information on the panels
	 * @param team
	 */
	void update(Team team);

	/**
	 * Get the layer of team moving boxes.
	 * @return the layer
	 */
	public RenderableLayer getBoxLayer();

	/**
	 * Set game playable.
	 */
	void gameBegin();

	/**
	 * Tell the view a team wins and other teams are over
	 * @param team
	 */
	void aTeamWins(Team team);

	/**
	 * Get the layer of the depot points.
	 * @return the layer
	 */
	public IconLayer getIconLayer();

	/**
	 * Show game over message.
	 */
	void gameOver();

}
