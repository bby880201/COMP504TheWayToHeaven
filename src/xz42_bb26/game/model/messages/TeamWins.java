package xz42_bb26.game.model.messages;

import java.util.UUID;

import common.message.ARequest;
import common.message.IChatMessage;
import provided.datapacket.DataPacket;

/**
 * This is a message class for a team to claim it wins
 * @author xz42
 *
 */
public class TeamWins extends ARequest implements IChatMessage {

	
	private UUID teamID;
	/**
	 * 
	 */
	private static final long serialVersionUID = 7303350744402421839L;

	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<TeamWins>(TeamWins.class, this);
	}

	/**
	 * @return the teamID
	 */
	public UUID getTeamID() {
		return teamID;
	}

	/**
	 * @param teamID the teamID to set
	 */
	public void setTeamID(UUID teamID) {
		this.teamID = teamID;
	}

}
