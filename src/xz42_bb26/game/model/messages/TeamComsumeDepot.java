package xz42_bb26.game.model.messages;

import java.util.UUID;

import common.message.ARequest;
import common.message.IChatMessage;
import provided.datapacket.DataPacket;

public class TeamComsumeDepot extends ARequest implements IChatMessage {

	
	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = -6625931077082454728L;

	/**
	 * The UUID of the depot which is cosumed
	 */
	private UUID aUuid;

	/**
	 * Constructor
	 * @param id The UUID of the depot which is cosumed
	 */
	public TeamComsumeDepot(UUID id) {
		this.aUuid = id;
	}

	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<TeamComsumeDepot>(TeamComsumeDepot.class, this);
	}

	/**
	 * The getter of the id
	 * @return the aDepot
	 */
	public UUID getaDepot() {
		return aUuid;
	}


}
