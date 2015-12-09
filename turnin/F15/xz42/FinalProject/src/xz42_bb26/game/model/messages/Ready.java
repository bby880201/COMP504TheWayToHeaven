package xz42_bb26.game.model.messages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import common.IChatUser;
import common.message.ARequest;
import common.message.IChatMessage;
import gov.nasa.worldwind.geom.Position;
import provided.datapacket.DataPacket;
import xz42_bb26.game.model.Depot;

public class Ready extends ARequest implements IChatMessage {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4402431958967360485L;
	
	private HashSet<IChatUser> users;
	private HashMap<Position, Depot> depots;
	
	public Ready(HashSet<IChatUser> users, HashMap<Position, Depot> depots) {
		this.users = users;
		this.depots = depots;
	}
	
	public Set<IChatUser> getUsers(){
		return this.users;
	}

	public HashMap<Position, Depot> getDepots() {
		return depots;
	}
	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<Ready>(Ready.class, this);
	}

}
