package xz42_bb26.game.model.messages;

import common.message.ARequest;
import common.message.IChatMessage;
import provided.datapacket.DataPacket;

public class Begin extends ARequest implements IChatMessage {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -705360032856665714L;

	/**
	 * 
	 */


	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<Begin>(Begin.class, this);
	}


}