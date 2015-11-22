package xz42_bb26.model.user;

import java.net.InetAddress;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import common.IConnect;
import common.IUser;

/**
 * This class contains the fields associated with the User.
 * 
 * @author xc7, bb26
 */
public class User implements IUser {

	/**
	 * declare a static final serialVersionUID of type long to fix the warning
	 */
	private static final long serialVersionUID = -119130136862198169L;
	// fields represents the IConnect stub associated with this user
	private IConnect myConnect;
	// the name of this user
	private String name;
	// the IP address of this user
	private InetAddress IP;
	// for the purpose of distinguishing multiple stubs representing the same user
	private long time;

	/**
	 * The constructor method
	 * @param myConnect An instance of IConnect stub
	 * @param name The user name
	 * @param IP The user IP address
	 */
	public User(IConnect myConnect, String name, InetAddress IP) {
		this.myConnect = myConnect;
		this.name = name;
		this.IP = IP;
		this.time = System.currentTimeMillis();
	}

	/**
	 * Get IConnect associated with this user
	 */
	public IConnect getConnect() {
		return myConnect;
	}

	/**
	 * Set IConnect associated with this user
	 * @param stub An instance of IConnect associated with this user
	 */
	public void setConnect(IConnect stub) {
		myConnect = stub;
	}

	/**
	 * Get user name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set user name
	 * @param name A string which represents the name of this user
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** 
	 * Get IP address from this user
	 */
	public InetAddress getIP() {
		return IP;
	}

	/** 
	 * Set IP address for this user
	 * @param newIP An instance of INetAddress 
	 */
	public void setIP(InetAddress newIP) {
		this.IP = newIP;
	}

	@Override
	/**
	 * Overriden equals() method to simply compare hashCodes.
	 * @param other The other object to compare with
	 * @return  Equal if the hashCodes are the same.  False otherwise.
	 */
	public boolean equals(Object other) {
		return hashCode() == other.hashCode();
	}

	@Override
	/**
	 * Override hashCode() method to create a hashcode from all the accessible values in IUser.
	 * @return a hashCode tied to the values of this IUser.
	 */
	public int hashCode() {
		// using IP, name, and time to calculate hashCode.
		int hash = 1;
		hash = hash * 17 + IP.hashCode();
		hash = hash * 31 + name.hashCode();
		hash = hash * 7 + Long.valueOf(time).hashCode();
		return hash;
	}

	@Override
	/**
	 * Override the toString method of this class
	 */
	public String toString() {
		Date date = new Date(time);
		Format format = new SimpleDateFormat("HH:mm:ss.SSS");
		return name + " " + format.format(date) + "@" + IP.getCanonicalHostName();
	}
}