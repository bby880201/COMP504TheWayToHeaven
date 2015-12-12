package xz42_bb26.game.model.messages;

import java.util.Set;

import common.IChatUser;
import common.message.ARequest;
import common.message.IChatMessage;
import provided.datapacket.DataPacket;
import xz42_bb26.game.model.Depot;
/**
 * This is a ready message server sends to clients.
 * Providing all game users and depots information
 * @author xz42
 *
 */
public class Ready extends ARequest implements IChatMessage {

	
	/**
	 * Gernerated serial version id.
	 */
	private static final long serialVersionUID = 4402431958967360485L;
	
	/**
	 * User list of all users from the server to the game client.
	 */
	private Set<IChatUser> users;
	
	/**
	 * The information of the init depots.
	 */
	private Set<Depot> depots;
	
	/**
	 * Constructor
	 * @param users All user list
	 * @param depots Init information of depots
	 */
	public Ready(Set<IChatUser> users, Set<Depot> depots) {
		this.users = users;
		this.depots = depots;
	}
	
	/**
	 * Getter of users
	 * @return
	 */
	public Set<IChatUser> getUsers(){
		return this.users;
	}
	
	/**
	 * Getter of depots
	 * @return
	 */
	public Set<Depot> getDepots() {
		return depots;
	}
	
	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<Ready>(Ready.class, this);
	}

}
