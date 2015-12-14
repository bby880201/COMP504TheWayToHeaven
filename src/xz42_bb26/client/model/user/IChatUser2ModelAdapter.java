/**
 * 
 */
package xz42_bb26.client.model.user;

import provided.datapacket.DataPacket;
import common.IChatUser;
import common.message.IChatMessage;

/**
 * The adapter interface which may used by chat user stub to communicate to rest parts of the applicaiton
 * @author bb26
 *
 */
public interface IChatUser2ModelAdapter {
	/**
	 * Receives an ADataPacket from the remote user
	 * @param remote The remove IChatUser, the sender of the ADataPacket
	 * @param dp An instance of ADatPacket class which wraps the data being sent
	 */
	public <T> void receive(IChatUser remote, DataPacket<? extends IChatMessage> dp);
	
}
