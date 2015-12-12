package xz42_bb26.game.model;

import java.util.Set;
import java.util.UUID;
/**
 * The adapter from chatroom to model
 * @author xz42
 *
 */
public interface IChatroom2ModelAdapter {
	/**
	 * Update team info
	 * @param team the team info structure
	 */
	void updateTeamInfo(Team team);

	/**
	 * The game started
	 */
	void gameBigin();
	/**
	 * A team is out
	 * @param id the out team id
	 */
	void aTeamOut(UUID id);
	/**
	 * A team wins
	 * @param id the winning team id
	 */
	void aTeamWins(UUID id);
	/**
	 * Update depots information
	 * @param depots depots information
	 */
	void setDepots(Set<Depot> depots);
	/**
	 * Get my team
	 * @return my team
	 */
	Team getTeam();
	/**
	 * A team used the depot
	 * @param id the depot be used
	 */
	void teamConsume(UUID id);

}
