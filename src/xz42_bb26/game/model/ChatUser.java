package xz42_bb26.game.model;

import java.rmi.RemoteException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import common.IChatUser;
import common.message.IChatMessage;
import provided.datapacket.DataPacket;
import xz42_bb26.client.model.user.IChatUser2ModelAdapter;

/**
 * This class contains the fields and methods associated with the ChatUser.
 * @author xz42
 *
 */
public class ChatUser implements IChatUser {
	
	private IChatUser2ModelAdapter toModelAdapter;
	
	private String name;
	
	private long time;
	
	public ChatUser(String name, IChatUser2ModelAdapter toModel){
		this.toModelAdapter = toModel;
		this.name = name;
		this.time = System.currentTimeMillis();
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
	public void receive(IChatUser sender, DataPacket<? extends IChatMessage> dp) throws RemoteException {
		toModelAdapter.receive(sender, dp);
	}

}
