package xz42_bb26.game.model.messages;

import java.util.UUID;

import common.message.ARequest;
import common.message.IChatMessage;
import provided.datapacket.DataPacket;

/**
 * This is a message to claim a team is out.
 * @author xz42, bb26
 *
 */
public class TeamOut extends ARequest implements IChatMessage {

	/**
	 * Generated id
	 */
	private static final long serialVersionUID = 8353675427415511872L;

	/**
	 * The UUID of the team which is out
	 */
	private UUID teamID;

	/**
	 * Constructor
	 * @param teamID The UUID of the team which is out
	 */
	public TeamOut(UUID teamID) {
		this.setTeamID(teamID);
	}

	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<TeamOut>(TeamOut.class, this);
	}

	/**
	 * Getter of teamID
	 * @return the teamID
	 */
	public UUID getTeamID() {
		return teamID;
	}

	/**
	 * Setter of teamID
	 * @param teamID the teamID to set
	 */
	public void setTeamID(UUID teamID) {
		this.teamID = teamID;
	}

}
