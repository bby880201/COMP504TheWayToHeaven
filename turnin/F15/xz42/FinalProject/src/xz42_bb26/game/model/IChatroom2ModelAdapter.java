package xz42_bb26.game.model;

import java.util.HashMap;
import java.util.UUID;

import gov.nasa.worldwind.geom.Position;

public interface IChatroom2ModelAdapter {

	void updateTeamInfo(Team team);

	void gameBigin();

	void aTeamOut(UUID id);

	void aTeamWins(UUID id);

	void setDepots(HashMap<Position, Depot> depots);

	Team getTeam();

}