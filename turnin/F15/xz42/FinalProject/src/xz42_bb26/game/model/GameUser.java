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
 * @author xz42, bb26
 *
 */
public class GameUser implements IChatUser {
	/**
	 * The adapter of model
	 */
	private IChatUser2ModelAdapter toModelAdapter;
	/**
	 * The client's name
	 */
	private String name;
	/**
	 * The time user generated
	 */
	private long time;

	/**
	 * Constructor
	 * @param name the client's name
	 * @param toModel the adapter
	 */
	public GameUser(String name, IChatUser2ModelAdapter toModel) {
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

	/**
	 * Receive messages
	 */
	@Override
	public void receive(IChatUser sender, DataPacket<? extends IChatMessage> dp)
			throws RemoteException {
		toModelAdapter.receive(sender, dp);
	}

}
