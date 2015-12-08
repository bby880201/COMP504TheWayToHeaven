/**
 * 
 */
package xz42_bb26.client.model.user;

import java.rmi.RemoteException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import provided.datapacket.DataPacket;
import common.IChatUser;
import common.message.IChatMessage;

/**
 * This class contains the fields and methods associated with the ChatUser.
 * @author bb26
 *
 */
public class ChatUser implements IChatUser {
		
	private IChatUser2ModelAdapter toModelAdap;
	
	private String name;
		
	private long time;
	
	
	
	public ChatUser(String name, IChatUser2ModelAdapter toModel) {
		this.toModelAdap = toModel;
		this.name = name;
		this.time = System.currentTimeMillis();

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

	@Override
	/**
	 * Override hashCode() method to create a hashcode from all the accessible values in IUser.
	 * @return a hashCode tied to the values of this IUser.
	 */
	public int hashCode() {
		// using IP, name, and time to calculate hashCode.
		int hash = 1;
//		hash = hash * 17 + IP.hashCode();
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
		return name + " " + format.format(date);	
	}

	@Override
	public void receive(IChatUser sender, DataPacket<? extends IChatMessage> dp)
			throws RemoteException {
		toModelAdap.receive(sender,dp);		
	}
}
