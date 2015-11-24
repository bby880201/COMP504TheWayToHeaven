package xz42_bb26.client.model.messages;

import java.io.Serializable;

/**
 * This class wraps around a String. Served as testing Unknown Data Type purpose
 * @author bb26, xc7
 */
public class AcknowledgeMessage implements Serializable {
	
	/**
	 * declare a static final serialVersionUID of type long to fix the warning
	 */
	private static final long serialVersionUID = -7898658335679753053L;

	private String ack;

	/**
	 * Constructor that takes a string message
	 * @param str A String message
	 */
	public AcknowledgeMessage(String str) {
		setAck(str);
	}

	/**
	 * Returns the string message
	 * @return the string message
	 */
	public String getAck() {
		return ack;
	}

	/**
	 * Set the string message
	 * @param msg The string message to set
	 */
	public void setAck(String msg) {
		this.ack = msg;
	}
}
