package model.messages;

import java.io.Serializable;

/**
 * This class wraps around a String. Served as testing Unknown Data Type purpose
 * @author bb26, xc7
 */
public class StringMessage implements Serializable {

	/**
	 * declare a static final serialVersionUID of type long to fix the warning
	 */
	private static final long serialVersionUID = -7033804153209704355L;

	private String msg;

	/**
	 * Constructor that takes a string message
	 * @param str A String message
	 */
	public StringMessage(String str) {
		setMsg(str);
	}

	/**
	 * Returns the string message
	 * @return the string message
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * Set the string message
	 * @param msg The string message to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
