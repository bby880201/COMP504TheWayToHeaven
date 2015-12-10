/**
 * 
 */
package xz42_bb26.server.model.chatroom;

import java.rmi.RemoteException;
import provided.datapacket.ADataPacketAlgoCmd;
import provided.datapacket.DataPacket;
import xz42_bb26.server.model.messages.InstallGameMessage;
import xz42_bb26.server.model.user.ChatUserEntity;
import xz42_bb26.server.model.user.GameUser;
import xz42_bb26.server.model.user.IGameUser;
import common.IChatUser;
import common.ICmd2ModelAdapter;
import common.message.chat.AChatUserInfoResponse;

/**
 * @author bb26
 *
 */
public class TeamRoom extends ServerRoom {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8191388183040152624L;
	
	private final IChatUser server;
	
	private IGameUser navig;
	
	private IGameUser manag;
				
	private String teamName;
	
	public TeamRoom(String name, IChatUser srv) throws Exception {
		super();
		teamName = name;
		
		server = srv;
		navig = null;
		manag = null;
		
		msgAlgo.setCmd(AChatUserInfoResponse.class, new ADataPacketAlgoCmd<String, AChatUserInfoResponse, IChatUser>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 5105304030772119307L;

			@Override
			public String apply(Class<?> index,
					DataPacket<AChatUserInfoResponse> host, IChatUser... params) {
				
				ChatUserEntity info = userInfo.get(host.getData().getID());
				info.setIp(host.getData().getIP());
				info.setName(host.getData().getName());
				
				users.put(params[0], info);
				userInfo.remove(host.getData().getID());
				
				if (null==navig){
					navig = new GameUser(info, true);
					return "Player: " + info + "act as navigator";
				}
				else {
					manag = new GameUser(info, false);
					return "Player: " + info + "act as manager";
				}
				
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {				
			}
		});
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
