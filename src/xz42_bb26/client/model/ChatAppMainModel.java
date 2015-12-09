package xz42_bb26.client.model;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import xz42_bb26.client.model.chatroom.ChatroomWithAdapter;
import xz42_bb26.client.model.user.ChatUserEntity;
import xz42_bb26.client.model.user.IInitUser2ModelAdapter;
import xz42_bb26.client.model.user.InitUser;
import common.IChatUser;
import common.IChatroom;
import common.ICmd2ModelAdapter;
import common.IInitUser;
import common.demo.message.init.ChatroomListRequest;
import common.demo.message.init.ChatroomListResponse;
import common.demo.message.init.InitUserInfoResponse;
import common.demo.message.init.Invitation2Chatroom;
import common.message.IInitMessage;
import common.message.init.AChatroomListRequest;
import common.message.init.AChatroomListResponse;
import common.message.init.AInitUserInfoRequest;
import common.message.init.AInitUserInfoResponse;
import common.message.init.AInvitation2Chatroom;
import provided.datapacket.ADataPacketAlgoCmd;
import provided.datapacket.DataPacket;
import provided.datapacket.DataPacketAlgo;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIUtils;
import provided.util.IVoidLambda;

/**
 * The model class, which stores a list of rooms the local user currently joined, 
 * the stub of the local user on the registry, etc
 * 
 * @author bb26, xc7
 */
public class ChatAppMainModel {

	/**
	 * The RMI Registry
	 */
	private Registry registry;

	/**
	 * Utility object used to get the Registry
	 */
	private IRMIUtils rmiUtils;

	private IInitUser me = null;
	// field stores a list of room 
	private HashMap<UUID, IChatroom> rooms;
	// instance of model2view adapter
	private IModel2ViewAdapter<IInitUser,IChatUser,IChatroom,ChatUserEntity> toView;

	private DataPacketAlgo<String, IInitUser> msgAlgo;

	private String userName = "xz42_bb26";
	
	private String ip;

	/**
	 * Constructor that takes an instance of IModel2ViewAdapter
	 * @param toViewAdapter An instance of IModel2ViewAdapter
	 */
	public ChatAppMainModel(IModel2ViewAdapter<IInitUser,IChatUser,IChatroom,ChatUserEntity> toViewAdapter) {

		toView = toViewAdapter;
		// initialize an empty set of rooms
		rooms = new HashMap<UUID, IChatroom>();

		msgAlgo = new DataPacketAlgo<String, IInitUser>(new ADataPacketAlgoCmd<String, Object, IInitUser>() {
			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */
			private static final long serialVersionUID = -4329950671092819917L;

			
			/**
			 * default cmd
			 */
			@Override
			public String apply(Class<?> index, DataPacket<Object> host,
					IInitUser... params) {
				return "Stub on registry: Unknow data type!";
			}
			
			@Override
			/**
			 * Set the ICmd2ModelAdapter of this command
			 * @param cmd2ModelAdpt An instance of ICmd2ModelAdapter
			 */
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
			}			
		});

		/**
		 * Handle InviteToChatroom command
		 */
		msgAlgo.setCmd(AInvitation2Chatroom.class, new ADataPacketAlgoCmd<String, AInvitation2Chatroom, IInitUser>() {

			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */
			private static final long serialVersionUID = 6397860207466953790L;

			@Override
			/**
			 * Set the ICmd2ModelAdapter of this command
			 * @param cmd2ModelAdpt An instance of ICmd2ModelAdapter
			 */
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				// empty method
			}

			@Override
			public String apply(Class<?> index,
					DataPacket<AInvitation2Chatroom> host, IInitUser... params) {

				try {
					IChatroom remoteRoom = host.getData().getChatroom();
					if (rooms.containsKey(remoteRoom.getID())) {
						throw new IllegalArgumentException(
								"Got invitation to join a chatroom in which user already exists.");
					}
					
					// creates a new local copy of the chatroom 
					ChatroomWithAdapter room = new ChatroomWithAdapter(remoteRoom.getID());
					boolean adptAdded = room.setChatWindowAdapter(toView.makeChatRoom(room));

					// add user to chatroom after adapter is installed
					if (adptAdded) {
						for (IChatUser user : remoteRoom.getUsers()) {
							room.addUser(user);
						}
						room.addMe();

						rooms.put(room.getID(), (IChatroom) room);
					}
				} catch (Exception e) {
					System.out.println("create room failed: " + e + "\n");
					e.printStackTrace();
				}
				return "Invitation from: " + (IInitUser) params[0];
			}
		});
		
		msgAlgo.setCmd(AChatroomListRequest.class, new ADataPacketAlgoCmd<String, AChatroomListRequest, IInitUser>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 4197659744867046587L;

			@Override
			public String apply(Class<?> index,
					DataPacket<AChatroomListRequest> host, IInitUser... params) {
				Set<IChatroom> rms = new HashSet<IChatroom>(rooms.values());
				AChatroomListResponse response = new ChatroomListResponse(host.getData(), rms);
				(new Thread(){
					@Override
					public void run() {
						try {
							params[0].receive(me, response.getDataPacket());
						} catch (RemoteException e) {
							System.err.println("Sending room list response failed:");
							e.printStackTrace();
						}
					}
				}).start();
				
				return "Chat room list sended to: " + (IInitUser) params[0];
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				// no need to set
			}
			
		});
		
		
		msgAlgo.setCmd(AChatroomListResponse.class, new ADataPacketAlgoCmd<String, AChatroomListResponse, IInitUser>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 4197659744867046587L;

			@Override
			public String apply(Class<?> index,
					DataPacket<AChatroomListResponse> host, IInitUser... params) {
				toView.refreshRoomList(host.getData().getChatrooms());
				return "Get chat room list from: " + (IInitUser) params[0];
			}

			//well-known packet, no need for adapter
			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
			}
			
		});
		
		msgAlgo.setCmd(AInitUserInfoRequest.class , new ADataPacketAlgoCmd<String, AInitUserInfoRequest, IInitUser>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -991849208865071242L;

			@Override
			public String apply(Class<?> index,
					DataPacket<AInitUserInfoRequest> host, IInitUser... params) {
				@SuppressWarnings("unused")
				IInitUser sender = params[0];
				(new Thread(){
					@Override
					public void run(){
						try{
							AInitUserInfoResponse response = new InitUserInfoResponse(host.getData(), userName, rmiUtils.getLocalAddress());
							params[0].receive(me, response.getDataPacket());
						} catch (Exception e) {
							System.err.println("Sending init user info response failed:");
							e.printStackTrace();
						}
					}
				}).start();

				return "Init user info sended to: " + params[0];
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {				
			}
			
		}); 
		
		msgAlgo.setCmd(AInitUserInfoResponse.class, new ADataPacketAlgoCmd<String, AInitUserInfoResponse, IInitUser>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3790264469150587510L;

			@Override
			public String apply(Class<?> index,
					DataPacket<AInitUserInfoResponse> host, IInitUser... params) {
				
				return null;
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				
			}
			
		});
	}


	/**
	 * Start the model
	 */
	public void start() {

		rmiUtils = new RMIUtils(new IVoidLambda<String>() {

			@Override
			public void apply(String... params) {
				for (String s : params) {
					System.out.println(s);
				}
			}
		});

		rmiUtils.startRMI(IRMI_Defs.CLASS_SERVER_PORT_CLIENT);
		try {
			IInitUser preStub = new InitUser(userName,rmiUtils.getLocalAddress(), new IInitUser2ModelAdapter(){

				@Override
				public <T> void receive(IInitUser remote,
						DataPacket<? extends IInitMessage> dp) {
					String str = dp.execute(msgAlgo, remote);
					System.out.println(str);					
				}
			});
			IInitUser stub = (IInitUser) UnicastRemoteObject.exportObject(preStub, IInitUser.BOUND_PORT_CLIENT);
			me = stub;

			registry = rmiUtils.getLocalRegistry();
			// put the user's stub onto the registry
			registry.rebind(IInitUser.BOUND_NAME, stub);
			
			ip = rmiUtils.getLocalAddress();

			System.out.println("Waiting..." + "\n");
		} catch (Exception e) {
			System.err.println("Connect exception:" + "\n");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Get remove user's stub from the remove user's registry, and wrap the stub
	 * with an IUser object
	 * @param ip The IP address of the remove user to connect with
	 * @return An instance of IUser which represents the remote user
	 */
	public IInitUser connectTo(final String ip) {

		IInitUser friend = null;
		try {
			Registry registry = rmiUtils.getRemoteRegistry(ip);
			System.out.println("Found registry: " + registry + "\n");
			friend = (IInitUser) registry.lookup(IInitUser.BOUND_NAME);
			System.out.println("Found remote IInitUser object: " + friend + " from " + ip + "\n");
		} catch (Exception e) {
			System.out.println("Establish connect failed!\n Exception connecting to " + ip + ": " + e + "\n");
		}
		return friend;
	}

	/**
	 * Quit from all current chatrooms and stop the system
	 */
	public void stop() {
		try {
			// quit from all current chatrooms
			for (IChatroom rm : rooms.values()) {
				((ChatroomWithAdapter) rm).removeMe();
			}
			registry.unbind(IInitUser.BOUND_NAME);
			System.out.println("Chat App Model Registry: " + IInitUser.BOUND_NAME + " has been unbound.");

			rmiUtils.stopRMI();
			System.exit(0);
		} catch (Exception e) {
			System.err.println("Chat App Model Registry: Error unbinding " + IInitUser.BOUND_NAME + ":\n" + e);
			System.exit(-1);
		}
	}

	/**
	 * Delete the room associated with certain ID from the list of rooms
	 * @param id2 The id associated with a room to delete from local system
	 */
	public void deleteChatroom(UUID id2) {
		rooms.remove(id2);
	}

	/**
	 * Connect to the RMI Registry at the given remote host, and grab the 
	 * stub from that registry. Then create a local chatroom with the remote 
	 * user represented by the given IP address, and inform the remote user 
	 * to create a local chatroom
	 * 
	 * @param ip The remote IP address to connect to.
	 */
	public void chatWith(final String ip) {

		IInitUser friend = connectTo(ip);

		if (null != friend) {
			createNewRoom(friend);
		}
	}

	/**
	 * Locally create a new chatroom and invite the remove user to join
	 * @param friend The remote user
	 */
	public void createNewRoom(IInitUser friend) {
		(new Thread() {
			@Override
			public void run() {
				if (null != friend) {
					try {
						// create a local chatroom which contains the local user's stub
						ChatroomWithAdapter chatRoom = new ChatroomWithAdapter();
						chatRoom.setChatWindowAdapter(toView.makeChatRoom(chatRoom));
						// invite the remote user to join the chatroom
						AInvitation2Chatroom invite = new Invitation2Chatroom((IChatroom) chatRoom, false);
						
						friend.receive(me, invite.getDataPacket());

						rooms.put(chatRoom.getID(), (IChatroom) chatRoom);

					} catch (Exception e) {
						System.out.println("Create room failed: " + e + "\n");
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	/**
	 * Upon given a chatroom to join, create a local chatroom and add existing 
	 * users in the given chatroom into the local chatroom. Then broadcast 
	 * to users in remote chatroom to add my stub into their local chatrooms.
	 * 
	 * @param rm the remote chatroom to join
	 */
	public void joinRoom(IChatroom rm) {

		try {
			// check whether my stub already exists in the chatroom to join
			if (rooms.containsKey(rm.getID())) {
				ChatroomWithAdapter chatroom = (ChatroomWithAdapter) rooms.get(rm.getID());
				chatroom.display("You are already in this chatroom!");
				throw new IllegalArgumentException("User joining a chatroom in which the user already exists.");
			}
			// create a local chatroom with same ID as the remove chatroom
			ChatroomWithAdapter chatRoom = new ChatroomWithAdapter(rm.getID());

			chatRoom.setChatWindowAdapter(toView.makeChatRoom(chatRoom));
			// add members in the remote chatroom to the created local chatroom
			for (IChatUser user : rm.getUsers()) {
				chatRoom.addUser(user);
			}
			// broadcast addMe command to all the members in the chatroom
			chatRoom.addMe();

			rooms.put(rm.getID(), chatRoom);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send a room list request to remote user 
	 * @param ip The IP address of the remote user
	 */
	public void getFriendChatrooms(String ip) {
		IInitUser friend = connectTo(ip);
		AChatroomListRequest rmList = new ChatroomListRequest();

		(new Thread(){
			@Override
			public void run(){
				try {
					if (null != friend)
						friend.receive(me, rmList.getDataPacket());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public IInitUser getInitUser() {
		return me;
	}


	public String getName() {
		return userName;
	}


	public String getIp() {
		return ip;
	}
}
