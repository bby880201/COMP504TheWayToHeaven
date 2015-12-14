/**
 * 
 */
package xz42_bb26.server.model.user;

import java.io.Serializable;

import common.IChatUser;

/**
 * A class wrapped most of information need for a user stub 
 * @author bb26
 *
 */
public class ChatUserEntity implements Serializable{
	
	/**
	 * declare a static final serialVersionUID of type long to fix the warning
	 */
	private static final long serialVersionUID = -4734953263106665787L;

	/**
	 * User's name
	 */
	private String name;
	
	/**
	 * User's IP address
	 */
	private String ip;
	
	/**
	 * User's chat user stub
	 */
	private IChatUser me;
	
	/**
	 * Constructor of this class
	 * @param chatUser User's chat user stub
	 * @param name User's name
	 * @param ip User's IP address
	 */
	public ChatUserEntity(IChatUser chatUser, String name, String ip) {
		setName(name);
		setIp(ip);
		setChatUser(chatUser);
	}
	
	/**
	 * Constructor of this class, for anonymous user 
	 * @param chatUser User's chat user stub
	 */
	public ChatUserEntity(IChatUser chatUser) {
		this(chatUser, "Anonymous", "0.0.0.0");
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the ChatUser
	 */
	public IChatUser getChatUser() {
		return me;
	}

	/**
	 * @param me the ChatUser to set
	 */
	public void setChatUser(IChatUser me) {
		this.me = me;
	}
	
	
	/**
	 * Override hashCode() method to create a hashcode from all the accessible values in IUser.
	 * @return a hashCode tied to the values of this IUser.
	 */
	@Override
	public int hashCode() {
		// using IP, name, and time to calculate hashCode.
		int hash = 1;
		hash = hash * 17 + ip.hashCode();
		hash = hash * 31 + name.hashCode();
		return hash;
	}
	
	/**
	 * Override the toString method of this class
	 */
	@Override
	public String toString() {
		return name;
	}
	
}
