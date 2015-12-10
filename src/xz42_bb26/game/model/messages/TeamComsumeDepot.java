package xz42_bb26.game.model.messages;

import java.util.UUID;

import common.message.ARequest;
import common.message.IChatMessage;
import provided.datapacket.DataPacket;

public class TeamComsumeDepot extends ARequest implements IChatMessage {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6625931077082454728L;
	/**
	 * 
	 */
	private UUID aUuid;

	public TeamComsumeDepot(UUID id) {
		this.aUuid = id;
	}

	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<TeamComsumeDepot>(TeamComsumeDepot.class, this);
	}

	/**
	 * @return the aDepot
	 */
	public UUID getaDepot() {
		return aUuid;
	}


}
