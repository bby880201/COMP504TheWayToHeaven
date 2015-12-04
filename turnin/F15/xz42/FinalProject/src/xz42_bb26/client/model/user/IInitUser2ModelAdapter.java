/**
 * 
 */
package xz42_bb26.client.model.user;

import provided.datapacket.DataPacket;
import common.IInitUser;
import common.message.IInitMessage;

/**
 * This adapter is installed into IInitUser and provides access of the local system
 * to the remote user.
 * 
 * @author bb26
 */

public interface IInitUser2ModelAdapter {

	/**
	 * Receives an ADataPacket from the remote user
	 * @param remote The remove IInitUser, the sender of the ADataPacket
	 * @param dp An instance of ADatPacket class which wraps the data being sent
	 */
	public <T> void receive(IInitUser remote, DataPacket<? extends IInitMessage> dp);
}
