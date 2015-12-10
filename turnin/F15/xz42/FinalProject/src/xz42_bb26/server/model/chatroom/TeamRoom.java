/**
 * 
 */
package xz42_bb26.server.model.chatroom;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import provided.datapacket.DataPacket;
import xz42_bb26.server.model.messages.InstallGameMessage;
import xz42_bb26.server.model.user.ChatUser;
import xz42_bb26.server.model.user.ChatUserEntity;
import xz42_bb26.server.model.user.GameUser;
import xz42_bb26.server.model.user.IChatUser2ModelAdapter;
import xz42_bb26.server.model.user.IGameUser;
import common.IChatUser;
import common.IChatroom;
import common.message.IChatMessage;

/**
 * @author bb26
 *
 */
public class TeamRoom implements IChatroom {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8191388183040152624L;
	
	private final IChatUser server;
	
	private final IGameUser navig;
	
	private final IGameUser manag;
	
	private final Set<IChatUser> stubList;
	
	private final UUID id;
	
	private String teamName;
	
	private IChatUser prestub;
	
	private IChatUser serverInRm;
	
	public TeamRoom(String name, IChatUser srv, ChatUserEntity memb1, ChatUserEntity memb2) {
		server = srv;
		navig = new GameUser(memb1, true);
		manag = new GameUser(memb2, false);
		teamName = name;
		stubList = new HashSet<IChatUser>();
		id = UUID.randomUUID();
		
		try {
			prestub = new ChatUser("serverInRoom", new IChatUser2ModelAdapter(){
				
				@Override
				public <T> void receive(IChatUser remote,
						DataPacket<? extends IChatMessage> dp) {
					System.out.println(dp);				
				}
			});
			
			serverInRm = (IChatUser) UnicastRemoteObject.exportObject(prestub, IChatUser.BOUND_PORT_SERVER);
			stubList.add(serverInRm);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see common.IChatroom#getID()
	 */
	@Override
	public UUID getID() {
		return id;
	}

	/* (non-Javadoc)
	 * @see common.IChatroom#getName()
	 */
	@Override
	public String getName() {
		return teamName;
	}

	/* (non-Javadoc)
	 * @see common.IChatroom#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		teamName = name;
	}

	/* (non-Javadoc)
	 * @see common.IChatroom#getUsers()
	 */
	@Override
	public Set<IChatUser> getUsers() {
		return stubList;
	}

	/* (non-Javadoc)
	 * @see common.IChatroom#addUser(common.IChatUser)
	 */
	@Override
	public boolean addUser(IChatUser user) {
		//users are fixed, this method is disallowed
		return false;
	}

	/* (non-Javadoc)
	 * @see common.IChatroom#removeUser(common.IChatUser)
	 */
	@Override
	public boolean removeUser(IChatUser user) {
		//users are fixed, this method is disallowed
		return false;
	}

	/* (non-Javadoc)
	 * @see common.IChatroom#send(common.IChatUser, common.message.IChatMessage)
	 */
	@Override
	public void send(IChatUser sender, IChatMessage message) {
		(new Thread() {
			@Override
			public void run() {
				try {
					for (IChatUser stub:stubList){
						if (stub!=sender){
							stub.receive(sender, message.getDataPacket());
						}
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	@Override
	public String toString(){
		return teamName;
	}

	public void installGame() {
		(new Thread() {
			@Override
			public void run() {
				try {
					InstallGameMessage instGameNavigator = new InstallGameMessage(teamName,true);
					navig.getChatUser().receive(server, instGameNavigator.getDataPacket());
					InstallGameMessage instGameManager = new InstallGameMessage(teamName,false);
					manag.getChatUser().receive(server, instGameManager.getDataPacket());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}).start();		
	}
}
