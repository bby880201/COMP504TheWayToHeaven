package xz42_bb26.game.model.messages;

import common.IChatUser;
import common.message.ARequest;
import common.message.IChatMessage;
import provided.datapacket.DataPacket;

/**
 * This is a message that the clients send their chatUsers to the server
 * @author xz42, bb26
 *
 */
public class ProvideGameUser extends ARequest implements IChatMessage {
	
	IChatUser gameUser;
	/**
	 * Constructor 
	 * @param _gameUser the game's chatuser
	 */
	public ProvideGameUser(IChatUser _gameUser) {
		this.gameUser = _gameUser;
	}

	/**
	 * Generated serial id
	 */
	private static final long serialVersionUID = -5986649333523402862L;

	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<ProvideGameUser>(ProvideGameUser.class, this);
	}
	/**
	 * Get the game's chat user
	 * @return the chatUser stub
	 */
	public IChatUser getStub() {
		return gameUser;
	}

}
