package xz42_bb26.game.model.messages;

import java.util.HashMap;
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
	
	private Set<IChatUser> users;
	
	private Set<Depot> depots;
	
	public Ready(Set<IChatUser> users, Set<Depot> depots) {
		this.users = users;
		this.depots = depots;
	}
	
	public Set<IChatUser> getUsers(){
		return this.users;
	}

	public Set<Depot> getDepots() {
		return depots;
	}
	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<Ready>(Ready.class, this);
	}

}
