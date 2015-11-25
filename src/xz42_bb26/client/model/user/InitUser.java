package xz42_bb26.client.model.user;

import java.rmi.RemoteException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import common.IInitUser;
import common.message.IInitMessage;

public class InitUser implements IInitUser {
	
	/**
	 * declare a static final serialVersionUID of type long to fix the warning
	 */
	// the name of this user
	private String name;
	// the IP address of this user
	private String IP;
	// for the purpose of distinguishing multiple stubs representing the same user
	private long time;
	// an adapter communicate to model
	private IInitUser2ModelAdapter toModelAdap;

	/**
	 * The constructor method
	 * @param myConnect An instance of IConnect stub
	 * @param name The user name
	 * @param IP The user IP address
	 */
	public InitUser(String name, String IP, IInitUser2ModelAdapter toModel) {
		this.toModelAdap = toModel;
		this.name = name;
		this.IP = IP;
		this.time = System.currentTimeMillis();
	}

	@Override
	public void receive(IInitUser sender, IInitMessage message)
			throws RemoteException {
		toModelAdap.receive(this, message);
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
	
	/**
	 * Override the toString method of this class
	 */
	@Override
	public String toString() {
		Date date = new Date(time);
		Format format = new SimpleDateFormat("HH:mm:ss.SSS");
		return name + " " + format.format(date) + "@" + IP;
	}

}
