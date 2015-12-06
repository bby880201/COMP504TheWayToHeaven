package xz42_bb26.server.model;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import xz42_bb26.server.model.ChatroomWithAdapter;
import xz42_bb26.client.model.user.IInitUser2ModelAdapter;
import xz42_bb26.client.model.user.InitUser;
import xz42_bb26.game.model.Team;
import common.IChatUser;
import common.IChatroom;
import common.ICmd2ModelAdapter;
import common.IInitUser;
import common.message.IInitMessage;
import common.message.chat.InitUserRequest;
import common.message.init.ChatroomListRequest;
import common.message.init.Invitation2Chatroom;
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
	
	private HashSet<IChatroom> teams;
	private HashSet<IInitUser> users;
	// instance of model2view adapter
	private IModel2ViewAdapter<IInitUser,IChatUser> toView;

	private DataPacketAlgo<String, IInitUser> msgAlgo;

	private String userName = "server";
	
	ChatroomWithAdapter globalChatroom;

	/**
	 * Constructor that takes an instance of IModel2ViewAdapter
	 * @param toViewAdapter An instance of IModel2ViewAdapter
	 * @throws RemoteException 
	 * @throws UnknownHostException 
	 */
	public ChatAppMainModel(IModel2ViewAdapter<IInitUser,IChatUser> toViewAdapter) throws UnknownHostException, RemoteException {

		toView = toViewAdapter;
		// initialize an empty set of rooms
		rooms = new HashMap<UUID, IChatroom>();
		teams = new HashSet<IChatroom>();
		users = new HashSet<IInitUser>();
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
					if (rooms.containsKey(remoteRoom.getID())) {
						throw new IllegalArgumentException(
								"Got invitation to join a chatroom in which user already exists.");
					}
					
					// creates a new local copy of the chatroom 
					ChatroomWithAdapter room = new ChatroomWithAdapter(userName, me, remoteRoom.getID());

					for (IChatUser user : remoteRoom.getUsers()) {
						room.addUser(user);
						InitUserRequest aMessage = new InitUserRequest();
						user.receive(room.getMe(), aMessage);
					}
					room.addMe();

					rooms.put(room.getID(), (IChatroom) room);
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
			InitUser preStub = new InitUser(userName,rmiUtils.getLocalAddress(), new IInitUser2ModelAdapter(){

				@Override
				public <T> void receive(IInitUser remote, IInitMessage message) {
					String str = message.getDataPacket().execute(msgAlgo, remote);
					System.out.println(str);
				}
			});
			IInitUser stub = (IInitUser) UnicastRemoteObject.exportObject(preStub, IInitUser.BOUND_PORT);
			me = stub;

			globalChatroom = new ChatroomWithAdapter(userName, me);
			
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
						ChatroomWithAdapter chatRoom = new ChatroomWithAdapter(userName,me);
						chatRoom.setChatWindowAdapter(toView.makeChatRoom(chatRoom));
						// invite the remote user to join the chatroom
						Invitation2Chatroom invite = new Invitation2Chatroom((IChatroom) chatRoom, false);
						friend.receive(me, invite);

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
			ChatroomWithAdapter chatRoom = new ChatroomWithAdapter(userName, me, rm.getID());

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
	 * Return a list of chatrooms from the remote user
	 * @param ip The IP address of the remote user
	 * @return A list of chatrooms the remote user has
	 */
	public HashSet<IChatroom> getFriendChatrooms(String ip) {
		IInitUser friend = connectTo(ip);
		ChatroomListRequest rmList = new ChatroomListRequest();
		try {
			if (null != friend)
				friend.receive(me, rmList);
			//TODO block and unblock request
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}


	public void speakTo(IChatUser user) {
		//TODO get inituser via ichatuser
	}
	
	public void makeTeam(IInitUser user1, IInitUser user2) throws UnknownHostException, RemoteException{
		ChatroomWithAdapter aTeam = new ChatroomWithAdapter(userName, me);
		aTeam.invite(user1);
		aTeam.invite(user2);
		teams.add(aTeam);
	}
}
