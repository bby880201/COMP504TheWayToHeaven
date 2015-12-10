package xz42_bb26.game.model;

import java.util.Set;
import java.util.UUID;

public interface IChatroom2ModelAdapter {

	void updateTeamInfo(Team team);

	void gameBigin();

	void aTeamOut(UUID id);

	void aTeamWins(UUID id);

	void setDepots(Set<Depot> depots);

	Team getTeam();

	void teamConsume(UUID id);

}
