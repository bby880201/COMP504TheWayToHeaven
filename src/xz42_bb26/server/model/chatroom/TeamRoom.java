/**
 * 
 */
package xz42_bb26.server.model.chatroom;

import java.rmi.RemoteException;
import java.util.UUID;

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
	 * declare a static final serialVersionUID of type long to fix the warning
	 */
	private static final long serialVersionUID = 8191388183040152624L;

	/**
	 * Chat user stub of server
	 */
	private final IChatUser server;

	/**
	 * Navigator role player
	 */
	private IGameUser navig;

	/**
	 * Manager role player
	 */
	private IGameUser manag;

	/**
	 * Team name
	 */
	private String teamName;

	/**
	 * Constructor of this class
	 * @param name team name
	 * @param srv server's stub
	 * @throws Exception
	 */
	public TeamRoom(String name, IChatUser srv) throws Exception {
		super();
		teamName = name;
		setName(name);

		server = srv;
		navig = null;
		manag = null;

		msgAlgo.setCmd(
				AChatUserInfoResponse.class,
				new ADataPacketAlgoCmd<String, AChatUserInfoResponse, IChatUser>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 5105304030772119307L;

					@Override
					public String apply(Class<?> index,
							DataPacket<AChatUserInfoResponse> host,
							IChatUser... params) {

						ChatUserEntity info = userInfo.get(host.getData()
								.getID());
						info.setIp(host.getData().getIP());
						info.setName(host.getData().getName());

						users.put(params[0], info);
						userInfo.remove(host.getData().getID());

						if (null == navig) {
							navig = new GameUser(info, true);
							return "Player: " + info + "act as navigator";
						} else {
							manag = new GameUser(info, false);
							return "Player: " + info + "act as manager";
						}

					}

					@Override
					public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
					}
				});
	}

	/**
	 * Override toString method to just return the team name
	 */
	@Override
	public String toString() {
		return teamName;
	}

	/**
	 * Install games to team members
	 */
	public void installGame() {
		//		(new Thread() {
		//			@Override
		//			public void run() {
		try {
			UUID uuid = UUID.randomUUID();
			InstallGameMessage instGameNavigator = new InstallGameMessage(uuid,
					teamName, true);
			navig.getChatUser().receive(server,
					instGameNavigator.getDataPacket());
			InstallGameMessage instGameManager = new InstallGameMessage(uuid,
					teamName, false);
			manag.getChatUser()
					.receive(server, instGameManager.getDataPacket());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		//			}
		//		}).start();		
	}
}
