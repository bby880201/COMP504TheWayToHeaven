package xz42_bb26.client.model.messages;

import java.util.UUID;

import provided.datapacket.DataPacket;
import common.message.IChatMessage;
import common.message.chat.ATextMessage;

/**
 * This class wraps around a String. Served as testing Unknown Data Type purpose
 * @author bb26, xc7
 */
public class StringMessage extends ATextMessage {

	/**
	 * declare a static final serialVersionUID of type long to fix the warning
	 */
	private static final long serialVersionUID = -7033804153209704355L;

	/**
	 * Content need to be sent
	 */
	private final String msg;
	
	/**
	 * ID of this message. 
	 */
	private final UUID msgID;

	/**
	 * Constructs a new message containing a string. 
	 * UUID for the message is auto-generated.
	 * @param str A String message
	 */
	public StringMessage(String str) {
		this(UUID.randomUUID(),str);
	}

	/**
	 * Constructs a new message containing a string. 
	 * UUID for the message is given.
	 * @param msgID UUID
	 * @param str A String message
	 */
	public StringMessage(UUID msgID,String str) {
		this.msgID = msgID;
		this.msg = str;
	}

	/**
	 * Returns the string message
	 * @return the string message
	 */
	@Override
	public String getText() {
		return msg;
	}

	@Override
	public UUID getID() {
		return msgID;
	}

	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<ATextMessage>(ATextMessage.class, this);
	}

}
