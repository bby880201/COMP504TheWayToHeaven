package xz42_bb26.client.model.messages;

import java.util.UUID;

import common.message.IChatMessage;
import provided.datapacket.DataPacket;

public class StartGameMessage implements IChatMessage{

	/**
	 * Auto-generated UID. 
	 */
	private static final long serialVersionUID = -7408699688711641243L;
	/**
	 * ID of this message. 
	 */
	private final UUID msgID;

	/**
	 * Constructs a new message containing the specified user to be removed. 
	 * UUID for the message is auto-generated.
	 * @param user - user to be removed
	 */
	public StartGameMessage() {
		this.msgID = UUID.randomUUID();
	}


	public UUID getID() {
		return msgID;
	}

	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<StartGameMessage>(StartGameMessage.class, this);
	}

}