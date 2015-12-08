/**
 * 
 */
package xz42_bb26.client.model.user;

import java.io.Serializable;

import common.IChatUser;

/**
 * @author bb26
 *
 */
public class ChatUserEntity implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4734953263106665787L;

	private String name;
	
	private String ip;
	
	private IChatUser me;
		
	public ChatUserEntity(IChatUser chatUser, String name, String ip) {
		setName(name);
		setIp(ip);
		setChatUser(chatUser);
	}
	
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
