package xz42_bb26.server.model.user;

import java.rmi.RemoteException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import provided.datapacket.DataPacket;
import xz42_bb26.server.model.user.IInitUser2ModelAdapter;
import common.IInitUser;
import common.message.IInitMessage;

/**
 * This class implements the IInitUser interface, which could be convert to stub 
 * @author bb26
 *
 */
public class InitUser implements IInitUser {
	
	/**
	 * the name of this user
	 */
	private String name;
	
	/**
	 *  the IP address of this user
	 */
	private String IP;
	
	/**
	 *  for the purpose of distinguishing multiple stubs representing the same user
	 */
	private long time;
	
	/**
	 *  an adapter communicate to model
	 */
	private transient IInitUser2ModelAdapter toModelAdap;

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

	/**
	 * Implement the method in interface
	 * @param sender sender stub
	 * @param dp data packet need to be transmitted
	 */
	@Override
	public void receive(IInitUser sender, DataPacket<? extends IInitMessage> dp)
			throws RemoteException {
		toModelAdap.receive(sender, dp);		
	}

}
