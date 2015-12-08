/**
 * 
 */
package xz42_bb26.server.model.messages;

import provided.datapacket.DataPacket;
import common.IChatUser;
import common.IInitUser;

/**
 * A simple wrapper class just for caching unknown data packet.
 * @author bb26
 *
 */
public class UnknownTypeData {
	
	private final DataPacket<Object> dp;
	private final IChatUser sender;
	private final IInitUser initSender;

	public UnknownTypeData(DataPacket<Object> host, IChatUser remote) {
		dp = host;
		sender = remote;
		initSender = null;
	}
	
	public UnknownTypeData(DataPacket<Object> host, IInitUser remote) {
		dp = host;
		sender = null;
		initSender = remote;
	}
	
	/**
	 * @return the dp
	 */
	public DataPacket<Object> getDataPacket() {
		return dp;
	}

	/**
	 * @return the sender
	 */
	public IChatUser getSender() {
		return sender;
	}

	/**
	 * @return the initSender
	 */
	public IInitUser getInitSender() {
		return initSender;
	}
}
