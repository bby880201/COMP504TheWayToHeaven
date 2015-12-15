/**
 * 
 */
package xz42_bb26.client.model.messages;

import provided.datapacket.DataPacket;
import common.IChatUser;
import common.IInitUser;

/**
 * A simple wrapper class just for caching unknown data packet.
 * @author bb26
 *
 */
public class UnknownTypeData {

	/**
	 * Data packet need to be cached
	 */
	private final DataPacket<Object> dp;

	/**
	 * Sender's chat user stub
	 */
	private final IChatUser sender;

	/**
	 * Sender's init user stub
	 */
	private final IInitUser initSender;

	public UnknownTypeData(DataPacket<Object> host, IChatUser remote) {
		dp = host;
		sender = remote;
		initSender = null;
	}

	/**
	 * Return the data packet of this message
	 * @return the data packet of this message
	 */
	public UnknownTypeData(DataPacket<Object> host, IInitUser remote) {
		dp = host;
		sender = null;
		initSender = remote;
	}

	/**
	 * @return the data packet
	 */
	public DataPacket<Object> getDataPacket() {
		return dp;
	}

	/**
	 * @return the sender stub
	 */
	public IChatUser getSender() {
		return sender;
	}

	/**
	 * @return the initSender stub
	 */
	public IInitUser getInitSender() {
		return initSender;
	}
}
