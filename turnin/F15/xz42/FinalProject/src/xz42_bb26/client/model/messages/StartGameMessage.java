package xz42_bb26.client.model.messages;

import java.util.UUID;

import common.message.IChatMessage;
import provided.datapacket.DataPacket;

/**
 * A message used to start the game
 * @author bb26
 *
 */
public class StartGameMessage implements IChatMessage {

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

	/**
	 * Return the UUID of this date packet
	 * @return the UUID of this date packet
	 */
	public UUID getID() {
		return msgID;
	}

	/**
	 * Return the data packet of this message
	 * @return the data packet of this message
	 */
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<StartGameMessage>(StartGameMessage.class, this);
	}

}