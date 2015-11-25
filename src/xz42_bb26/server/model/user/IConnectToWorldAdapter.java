package xz42_bb26.client.model.user;

import java.util.HashSet;
import provided.datapacket.ADataPacket;
import provided.datapacket.ADataPacketAlgoCmd;
import common.IChatroom;
import common.IConnect;
import common.IUser;
import common.messages.RequestForAlgo;

/**
 * This adapter is installed into IConnect and provides access of the local system
 * to the remote user.
 * 
 * @author bb26, xc7
 */
public interface IConnectToWorldAdapter {

	/**
	 * Get a list of IChatroom the local user currently joined
	 * @return a list of IChatroom holds by the local system 
	 */
	public HashSet<IChatroom> getChatrooms();

	/**
	 * Request the AlgoCmd with type specified by the RequestForAlgo from the remote user 
	 * @param request A wrapper which wraps around the class type of the algo
	 * @return an instance of ADataPacketAlgoCmd
	 */
	public <T> ADataPacketAlgoCmd<String, ?, IUser> getCommand(RequestForAlgo request);

	/**
	 * Returns an IUser wrapper class which wraps around the stub
	 * @param stub The stub puts on the registry
	 */
	public IUser getUser(IConnect stub);

	/**
	 * Receives an ADataPacket from the remove user
	 * @param remote The remove IUser, the sender of the ADataPacket
	 * @param data An instance of ADatPacket class which wraps the data being sent
	 */
	public <T> void receive(IUser remote, ADataPacket data);
}
