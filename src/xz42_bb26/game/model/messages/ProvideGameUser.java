package xz42_bb26.game.model.messages;

import common.IChatUser;
import common.message.ARequest;
import common.message.IChatMessage;
import provided.datapacket.DataPacket;

public class ProvideGameUser extends ARequest implements IChatMessage {
	
	IChatUser gameUser;
	
	public ProvideGameUser(IChatUser _gameUser) {
		this.gameUser = _gameUser;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5986649333523402862L;

	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<ProvideGameUser>(ProvideGameUser.class, this);
	}

	public IChatUser getStub() {
		return gameUser;
	}

}
