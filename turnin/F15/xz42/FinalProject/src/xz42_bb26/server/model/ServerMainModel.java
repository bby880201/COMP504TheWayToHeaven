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
public class ServerMainModel {

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
	
	Chatroom globalChatroom;

	/**
	 * Constructor that takes an instance of IModel2ViewAdapter
	 * @param toViewAdapter An instance of IModel2ViewAdapter
	 * @throws RemoteException 
	 * @throws UnknownHostException 
	 */
	public ServerMainModel(IModel2ViewAdapter<IInitUser,IChatUser> toViewAdapter) throws UnknownHostException, RemoteException {

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

			globalChatroom = new Chatroom(userName, me);
			
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
}
