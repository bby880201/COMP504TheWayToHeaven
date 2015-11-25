package xz42_bb26.client.model;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import xz42_bb26.client.model.chatroom.ChatroomWithAdapter;
import xz42_bb26.client.model.user.Connect;
import xz42_bb26.client.model.user.IConnectToWorldAdapter;
import xz42_bb26.client.model.user.IInitUser2ModelAdapter;
import xz42_bb26.client.model.user.InitUser;
import xz42_bb26.client.model.user.User;
import common.IChatUser;
import common.IChatroom;
import common.ICmd2ModelAdapter;
import common.IInitUser;
import common.message.IInitMessage;
import common.message.init.Invitation2Chatroom;
import provided.datapacket.ADataPacket;
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
	private ArrayList<IChatroom> rooms;
	// instance of model2view adapter
	private IModel2ViewAdapter<IInitUser> toView;

	private DataPacketAlgo<String, IInitUser> msgAlgo;

	private String userName = "xz42_bb26";

	/**
	 * Constructor that takes an instance of IModel2ViewAdapter
	 * @param toViewAdapter An instance of IModel2ViewAdapter
	 */
	public ChatAppMainModel(IModel2ViewAdapter<IInitUser> toViewAdapter) {

		toView = toViewAdapter;
		// initialize an empty set of rooms
		rooms = new ArrayList<IChatroom>();

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
				return "Stub on registry: Unknow data type asking for command!";
			}
			
			@Override
			/**
			 * Set the ICmd2ModelAdapter of this command
			 * @param cmd2ModelAdpt An instance of ICmd2ModelAdapter
			 */
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				// empty method
			}

			
		});

		/**
		 * Handle InviteToChatroom command
		 */
		msgAlgo.setCmd(Invitation2Chatroom.class, new ADataPacketAlgoCmd<String, Invitation2Chatroom, IInitUser>() {

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
					DataPacket<Invitation2Chatroom> host, IInitUser... params) {

				try {
					IChatroom remoteRoom = host.getData().getChatroom();
					for (IChatroom rm: rooms){
						if (rm.getName() == remoteRoom.getName()){
							throw new IllegalArgumentException(
									"Got invitation to join a chatroom in which user already exists.");

						}
					}
					
					// creates a new local copy of the chatroom 
					ChatroomWithAdapter room = new ChatroomWithAdapter(userName, remoteRoom.getName());
					boolean adptAdded = room.setChatWindowAdapter(toView.makeChatRoom(room));

					// add user to chatroom after adapter is installed
					if (adptAdded) {
						for (IChatUser user : remoteRoom.getUsers()) {
							room.addUser(user);
						}
						room.addMe();

						rooms.add((IChatroom) room);
					}
				} catch (Exception e) {
					System.out.println("create room failed: " + e + "\n");
					e.printStackTrace();
				}
				return "Invitation from: " + (IInitUser) params[0];
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

		rmiUtils.startRMI(IRMI_Defs.CLASS_SERVER_PORT_SERVER);
		try {
			me = new InitUser(userName,rmiUtils.getLocalAddress(), new IInitUser2ModelAdapter(){

				@Override
				public <T> void receive(IInitUser remote, IInitMessage message) {
					String str = message.getDataPacket().execute(msgAlgo, remote);
					System.out.println(str);
				}
			});
			IInitUser stub = (IInitUser) UnicastRemoteObject.exportObject(me, IInitUser.BOUND_PORT);

			registry = rmiUtils.getLocalRegistry();
			// put the user's stub onto the registry
			registry.rebind(IInitUser.BOUND_NAME, stub);

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
	public IUser connectTo(final String ip) {

		IUser friend = null;
		try {
			Registry registry = rmiUtils.getRemoteRegistry(ip);
			System.out.println("Found registry: " + registry + "\n");
			IConnect connect = (IConnect) registry.lookup(IConnect.BOUND_NAME);
			System.out.println("Found remote Connect object: " + connect + "\n");
			friend = connect.getUser(connect);
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
			registry.unbind(IConnect.BOUND_NAME);
			System.out.println("Chat App Model Registry: " + IConnect.BOUND_NAME + " has been unbound.");

			rmiUtils.stopRMI();
			System.exit(0);
		} catch (Exception e) {
			System.err.println("Chat App Model Registry: Error unbinding " + IConnect.BOUND_NAME + ":\n" + e);
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

		IUser friend = connectTo(ip);

		if (null != friend) {
			createNewRoom(friend);
		}
	}

	/**
	 * Locally create a new chatroom and invite the remove user to join
	 * @param friend The remote user
	 */
	public void createNewRoom(IUser friend) {
		(new Thread() {
			@Override
			public void run() {
				if (null != friend) {
					try {
						// create a local chatroom which contains the local user's stub
						ChatroomWithAdapter chatRoom = new ChatroomWithAdapter(userName);
						chatRoom.setChatWindowAdapter(toView.makeChatRoom(chatRoom));
						// invite the remote user to join the chatroom
						InviteToChatroom invite = new InviteToChatroom((IChatroom) chatRoom);
						friend.getConnect().sendReceive(chatRoom.getMe(),
								new DataPacket<InviteToChatroom>(InviteToChatroom.class, invite));

						rooms.put(chatRoom.id(), (IChatroom) chatRoom);

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
			if (rooms.containsKey(rm.id())) {
				ChatroomWithAdapter chatroom = (ChatroomWithAdapter) rooms.get(rm.id());
				chatroom.display("You are already in this chatroom!");
				throw new IllegalArgumentException("User joining a chatroom in which the user already exists.");
			}
			// create a local chatroom with same ID as the remove chatroom
			ChatroomWithAdapter chatRoom = new ChatroomWithAdapter(userName, rm.id());

			chatRoom.setChatWindowAdapter(toView.makeChatRoom(chatRoom));
			// add members in the remote chatroom to the created local chatroom
			for (IUser user : rm.getUsers()) {
				chatRoom.addUser(user);
			}
			// broadcast addMe command to all the members in the chatroom
			chatRoom.addMe();

			rooms.put(rm.id(), chatRoom);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return a list of chatrooms from the remote user
	 * @param ip The IP address of the remote user
	 * @return A list of chatrooms the remote user has
	 */
	public HashSet<IChatroom> getFriendChatrooms(String ip) {
		IUser friend = connectTo(ip);

		try {
			if (null != friend)
				return friend.getConnect().getChatrooms();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
}
