package xz42_bb26.model.user;

import java.rmi.RemoteException;
import java.util.HashSet;
import provided.datapacket.ADataPacket;
import provided.datapacket.ADataPacketAlgoCmd;
import common.IChatroom;
import common.IConnect;
import common.IUser;
import common.messages.RequestForAlgo;

/**
 * This class wraps around the IConnect interface and provides access of the local 
 * system to the remove user.
 * 
 * @author bb26, xc7
 */
public class Connect implements IConnect {

	private IConnectToWorldAdapter toWorld;

	/**
	 * Constructor that takes an instance of IConnectToWorldAdapter
	 * @param adpt An instance of IConnectToWorldAdapter
	 */
	public Connect(IConnectToWorldAdapter adpt) {
		toWorld = adpt;
	}

	/* (non-Javadoc)
	 * @see common.IConnect#getUser(common.IConnect)
	 */
	@Override
	public IUser getUser(IConnect stub) throws RemoteException {
		return toWorld.getUser(stub);
	}

	/* (non-Javadoc)
	 * @see common.IConnect#getChatrooms()
	 */
	@Override
	public HashSet<IChatroom> getChatrooms() throws RemoteException {
		return toWorld.getChatrooms();
	}

	/* (non-Javadoc)
	 * @see common.IConnect#sendReceive(common.IUser, provided.datapacket.ADataPacket)
	 */
	@Override
	public void sendReceive(IUser me, ADataPacket data) throws RemoteException {
		toWorld.receive(me, data);
	}

	/* (non-Javadoc)
	 * @see common.IConnect#getNewCommand(common.messages.IRequestForAlgo)
	 */
	@Override
	public ADataPacketAlgoCmd<String, ?, IUser> getNewCommand(RequestForAlgo request) throws RemoteException {
		return toWorld.getCommand(request);
	}

}
