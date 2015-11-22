package xz42_bb26.model.chatroom;

import java.awt.Container;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import javax.swing.JLabel;
import xz42_bb26.model.messages.StringMessage;
import xz42_bb26.model.user.Connect;
import xz42_bb26.model.user.IConnectToWorldAdapter;
import xz42_bb26.model.user.User;
import provided.datapacket.ADataPacket;
import provided.datapacket.ADataPacketAlgoCmd;
import provided.datapacket.DataPacket;
import provided.datapacket.DataPacketAlgo;
import provided.mixedData.MixedDataDictionary;
import common.IChatroom;
import common.ICmd2ModelAdapter;
import common.IConnect;
import common.IUser;
import common.messages.AddMe;
import common.messages.InviteToChatroom;
import common.messages.RemoveMe;
import common.messages.RequestForAlgo;

/**
 * This class implements the IChatroom interface. 
 * @author bb26, xc7
 */
public class ChatroomWithAdapter implements IChatroom {

	/**
	 * declare a static final serialVersionUID of type long to fix the warning
	 */
	private static final long serialVersionUID = 4342241825756246744L;

	@SuppressWarnings("unchecked")
	private transient IChatRoom2WorldAdapter<IUser> chatWindowAdapter = IChatRoom2WorldAdapter.NULL_OBJECT;
	// name of the local user
	private IUser me;

	private UUID id;
	// name of the chatroom
	private String displayName;

	private ArrayList<IUser> users = new ArrayList<IUser>();

	private DataPacketAlgo<String, IUser> msgAlgo;

	// default command to model adapter that provides unknown command limited 
	// access to local system
	// mark as "transient" to prevent it from being serialized during any 
	// transport process
	private transient ICmd2ModelAdapter _cmd2ModelAdpt;

	private IChatroom thisRoom = this;

	/**
	 * Constructor that takes in user name and user id as parameter
	 * @param name The local user name
	 * @param uuid The local user id
	 * @throws UnknownHostException Throw exception if host is unknown
	 * @throws RemoteException Throw exception if remote connection failed
	 */
	public ChatroomWithAdapter(String name, UUID uuid) throws UnknownHostException, RemoteException {
		this(name);
		id = uuid;
	}

	/**
	 * Constructor that takes in a user name as parameter
	 * @param name The local user name
	 * @throws UnknownHostException Throw exception if host is unknown
	 * @throws RemoteException Throw exception if remote connection failed
	 */
	public ChatroomWithAdapter(String name) throws UnknownHostException, RemoteException {

		initAlgo();

		Connect connect = new Connect(new IConnectToWorldAdapter() {

			@Override
			/**
			 * Get a list of IChatroom the local user currently joined
			 * @return a list of IChatroom holds by the local system 
			 */
			public HashSet<IChatroom> getChatrooms() {
				return new HashSet<IChatroom>();
			}

			@SuppressWarnings("unchecked")
			@Override
			/**
			 * Request the AlgoCmd with type specified by the RequestForAlgo from the remote user 
			 * @param request A wrapper which wraps around the class type of the algo
			 * @return an instance of ADataPacketAlgoCmd
			 */
			public <T> ADataPacketAlgoCmd<String, ?, IUser> getCommand(RequestForAlgo request) {

				Class<?> newCmdType = request.unknownType();

				// downcast the return value of getCmd to AdataPacketAlgoCmd		
				return (ADataPacketAlgoCmd<String, ?, IUser>) msgAlgo.getCmd(newCmdType);
			}

			@Override
			/**
			 * Returns an IUser wrapper class which wraps around the stub
			 * @param stub The stub puts on the registry
			 */
			public IUser getUser(IConnect stub) {
				return me;
			}

			@Override
			/**
			 * Receives an ADataPacket from the remove user
			 * @param remote The remove IUser, the sender of the ADataPacket
			 * @param data An instance of ADatPacket class which wraps the data being sent
			 */
			public <T> void receive(IUser remote, ADataPacket data) {
				String str = data.execute(msgAlgo, remote);
				System.out.println(str);
			}
		});

		IConnect stub = (IConnect) UnicastRemoteObject.exportObject(connect, IConnect.BOUND_PORT);

		me = new User(stub, name, InetAddress.getLocalHost());

		id = UUID.randomUUID();
		users.add(me);
	}

	/**
	 * Initialize the algo command
	 */
	private void initAlgo() {
		// initialize the cmd2model adapter, which grant the unknown command access
		// to limited local GUI 
		_cmd2ModelAdpt = new ICmd2ModelAdapter() {

			@Override
			/**
			 * Get a container that is on the GUI.
			 * @return the Container to modify
			 */
			public Container scrollable() {
				return chatWindowAdapter.Scrollable();
			}

			@Override
			/**
			 * Get a container that is on the GUI.
			 * @return the Container to modify
			 */
			public Container updateable() {
				return chatWindowAdapter.Scrollable();
			}

			@Override
			/**
			 * Get a reference to an ADataPacketAlgoCmd, most likely one that has 
			 * some of the application in scope.
			 * @return an ADataPacketAlgoCmd that can perform actions on the local 
			 * 			machine. 
			 */
			public ADataPacketAlgoCmd<String, ?, IUser> other() {
				return null;
			}

			@Override
			/**
			 * This gets the universal MixedDataDictionary that can be used to 
			 * store and restore arbitrary objects
			 * @return the local user's MixedDataDictionary
			 */
			public MixedDataDictionary dictionary() {
				return new MixedDataDictionary();
			}
		};
		// install ADataPacketAlgoCmd into DataPacketAlgo
		msgAlgo = new DataPacketAlgo<String, IUser>(new ADataPacketAlgoCmd<String, Object, IUser>() {

			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */
			private static final long serialVersionUID = -1139989943264094599L;

			@Override
			/**
			 * install default command to handle unknown command type
			 */
			public String apply(Class<?> index, DataPacket<Object> host, IUser... params) {
				IUser remote = params[0];
				// class type of the unknown command
				Class<?> newCmdType = host.getData().getClass();

				RequestForAlgo reqForAlgo = new RequestForAlgo(newCmdType);
				// request the AlgoCmd from the remote user
				ADataPacketAlgoCmd<String, ?, IUser> cmd = null;
				try {
					cmd = remote.getConnect().getNewCommand(reqForAlgo);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				// set local cmd2ModelAdpt into the acquired remote cmd
				cmd.setCmd2ModelAdpt(_cmd2ModelAdpt);
				// install the unknown cmd into local system
				msgAlgo.setCmd(newCmdType, cmd);
				// execute the acquired remote cmd
				cmd.apply(index, host, params);
				// return status information
				return "Unknow data type: \"" + newCmdType + "\", asking for command!";
			}

			@Override
			/**
			 * Set the ICmd2ModelAdapter of this command
			 * @param cmd2ModelAdpt An instance of ICmd2ModelAdapter
			 */
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;
			}
		});
		// handle String type cmd as unknown cmd type 
		msgAlgo.setCmd(String.class, new ADataPacketAlgoCmd<String, String, IUser>() {
			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */
			private static final long serialVersionUID = 2210559989023917346L;

			@Override
			public String apply(Class<?> index, DataPacket<String> host, IUser... params) {

				IUser remote = params[0];

				JLabel content = new JLabel(remote.getName() + " says:\n" + host.getData() + "\n");

				_cmd2ModelAdpt.scrollable().add(content);
				_cmd2ModelAdpt.scrollable().revalidate();
				_cmd2ModelAdpt.scrollable().repaint();

				// return status information
				return "String Message received from: " + remote;

				//				IUser remote = (IUser) params[0];
				//				chatWindowAdapter.append(remote.getName() + " says:");
				//				chatWindowAdapter.append(host.getData());
				//				// return status information
				//				return "String received from " + remote;
			}

			@Override
			/**
			 * Set the ICmd2ModelAdapter of this command
			 * @param cmd2ModelAdpt An instance of ICmd2ModelAdapter
			 */
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;
			}
		});
		// handle addMe type cmd as known cmd type 
		msgAlgo.setCmd(AddMe.class, new ADataPacketAlgoCmd<String, AddMe, IUser>() {

			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */
			private static final long serialVersionUID = -189336880905492572L;

			@Override
			public String apply(Class<?> index, DataPacket<AddMe> host, IUser... params) {
				chatWindowAdapter.append("User joined: " + host.getData().me);
				addUser(host.getData().me);
				return "User joined: " + host.getData().me;
			}

			@Override
			/**
			 * Set the ICmd2ModelAdapter of this command
			 * @param cmd2ModelAdpt An instance of ICmd2ModelAdapter
			 */
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;
			}
		});
		// handle RemoveMe type cmd as known cmd type 
		msgAlgo.setCmd(RemoveMe.class, new ADataPacketAlgoCmd<String, RemoveMe, IUser>() {

			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */
			private static final long serialVersionUID = 3679441143550851362L;

			@Override
			public String apply(Class<?> index, DataPacket<RemoveMe> host, IUser... params) {
				chatWindowAdapter.append("User left: " + host.getData().me);
				removeUser(host.getData().me);
				return "User left: " + host.getData().me;
			}

			@Override
			/**
			 * Set the ICmd2ModelAdapter of this command
			 * @param cmd2ModelAdpt An instance of ICmd2ModelAdapter
			 */
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;
			}
		});

		// handle StringMessage type cmd as unknown cmd type 
		// StringMessage class is for testing Unknown Data Type purpose
		msgAlgo.setCmd(StringMessage.class, new ADataPacketAlgoCmd<String, StringMessage, IUser>() {

			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */
			private static final long serialVersionUID = -7694775719535304909L;

			@Override
			public String apply(Class<?> index, DataPacket<StringMessage> host, IUser... params) {

				IUser remote = params[0];

				JLabel content = new JLabel(remote.getName() + " says:\n" + host.getData().getMsg() + "\n");

				_cmd2ModelAdpt.scrollable().add(content);
				_cmd2ModelAdpt.scrollable().revalidate();
				_cmd2ModelAdpt.scrollable().repaint();

				// return status information
				return "String Message received from: " + remote;
			}

			@Override
			/**
			 * Set the ICmd2ModelAdapter of this command
			 * @param cmd2ModelAdpt An instance of ICmd2ModelAdapter
			 */
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;
			}
		});

		// handle Integer type cmd as unknown cmd type 
		// Integer class is for testing Unknown Data Type purpose
		msgAlgo.setCmd(Integer.class, new ADataPacketAlgoCmd<String, Integer, IUser>() {

			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */
			private static final long serialVersionUID = -7694775719535304909L;

			@Override
			public String apply(Class<?> index, DataPacket<Integer> host, IUser... params) {

				IUser remote = params[0];

				JLabel content = new JLabel(remote.getName() + " says:\n" + host.getData().toString() + "\n");

				_cmd2ModelAdpt.scrollable().add(content);
				_cmd2ModelAdpt.scrollable().revalidate();
				_cmd2ModelAdpt.scrollable().repaint();

				// return status information
				return "Integer received from: " + remote;
			}

			@Override
			/**
			 * Set the ICmd2ModelAdapter of this command
			 * @param cmd2ModelAdpt An instance of ICmd2ModelAdapter
			 */
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;
			}
		});
	}

	/**
	 * Get the IChatRoom2WorldAdapter associated with this chatroom
	 * @return An instance of IChatRoom2WorldAdapter
	 */
	public IChatRoom2WorldAdapter<IUser> getChatWindowAdapter() {
		return chatWindowAdapter;
	}

	/**
	 * Set the IChatRoom2WorldAdapter
	 * 
	 * @param chatWindowAdapter An instance of IChatRoom2WorldAdapter
	 * @return return boolean value for synchronize purpose
	 */
	public boolean setChatWindowAdapter(IChatRoom2WorldAdapter<IUser> chatWindowAdapter) {
		this.chatWindowAdapter = chatWindowAdapter;
		refreshList();
		return true;
	}

	/**
	 * Quit from the chatroom
	 */
	public void quit() {
		// first remove myself from the chatroom, then broadcast to everyone else
		// to remove me from their local chatroom
		removeMe();
		// delete the chat window from the GUI
		chatWindowAdapter.deleteWindow();
	}

	/**
	 * Get an instance of IUser 
	 * @return An instance of IUser which represents the local user
	 */
	public IUser getMe() {
		return me;
	}

	/**
	 * Set the IUser associated with the local user
	 * @param me An instance of IUser
	 */
	public void setMe(IUser me) {
		this.me = me;
	}

	/**
	 * Send the remote user an InviteToChatroom message.
	 * @param friend the remote user to be invited to this chatroom
	 */
	public void invite(IUser friend) {
		InviteToChatroom invite = new InviteToChatroom(thisRoom);

		(new Thread() {
			@Override
			public void run() {
				try {
					// send friend an InviteToChatroom message
					friend.getConnect().sendReceive(me,
							new DataPacket<InviteToChatroom>(InviteToChatroom.class, invite));
				} catch (RemoteException e) {
					System.out.println("Invite " + friend + " to room failed: " + e + "\n");
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	/**
	 * Get the id of this chatroom
	 * @return id of this chatroom
	 */
	public UUID id() {
		return id;
	}

	@Override
	/**
	 * Get a list of IUser in this chatroom
	 * @return A list of IUser in this chatroom
	 */
	public HashSet<IUser> getUsers() {
		return new HashSet<IUser>(users);
	}

	@Override
	/**
	 * Add a remote user into this chatroom
	 * @param user An instance of IUser to be added to this chatroom
	 * @return A boolean value which indicates whether the remote user was 
	 * 			successfully added to the chatroom
	 */
	public boolean addUser(IUser user) {
		boolean added = users.add(user);
		// Refresh the member list to display in the GUI panel
		refreshList();
		return added;
	}

	@Override
	/**
	 * Delete a user from this chatroom
	 * @param user An instance of IUser to be deleted to this chatroom
	 * @return A boolean value which indicates whether the user was 
	 * 			successfully deleted to the chatroom
	 */
	public boolean removeUser(IUser user) {
		boolean removed = users.remove(user);
		if (users.size() == 0) {
			chatWindowAdapter.deleteWindow();
			chatWindowAdapter.deleteModel(id);
		} else {
			refreshList();
		}
		return removed;
	}

	@Override
	/**
	 * Send a message to all the users in the chatroom
	 * @param me An instance of IUser which represents the sender of the message
	 * @param message An instance of ADataPacket which wraps around the message
	 */
	public void send(IUser me, ADataPacket message) {
		(new Thread() {
			@Override
			public void run() {
				for (IUser user : users) {
					// send message to users other than myself
					if (!user.equals(me)) {
						try {
							user.getConnect().sendReceive(me, message);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	@Override
	/**
	 * Return the chatroom name for display
	 */
	public String getName() {
		if (users.size() > 0) {
			IUser[] lstUser = users.toArray(new IUser[users.size()]);
			displayName = "Chatroom with members: " + lstUser[0].getName() + " et, al.";
		} else {
			displayName = "Chatroom with members: " + me.getName() + " et, al.";
		}
		return displayName;
	}

	@Override
	/**
	 * Set the name of the chatroom
	 * @param name The string name to set
	 */
	public void setName(String name) {
		displayName = name;
	}

	/**
	 * Send the string text message to members in the chatroom
	 * @param text The string message to send over
	 */
	public void sendMsg(String text) {
		send(me, new DataPacket<String>(String.class, text));
		/**
		 * The following two lines are for testing purpose
		 */
		// send(me, new DataPacket<Integer>(Integer.class, Integer.valueOf(text)));
		// send(me, new DataPacket<StringMessage>(StringMessage.class, new StringMessage(text)));
	}

	/**
	 * Display the data to the specific chatroom's chat window.
	 * @param data the data to be added to GUI panel
	 */
	public void display(String data) {
		chatWindowAdapter.append(data);
	}

	/**
	 * Refresh the member list to display in the GUI panel
	 */
	private void refreshList() {
		if (!(null == chatWindowAdapter))
			chatWindowAdapter.refreshList(users);
	}

	/**
	 * Override the toString method to display the chatroom
	 */
	public String toString() {
		return displayName;
	}

	/**
	 * Send addMe message to users in the chatroom.
	 * Users in the chatroom upon receiving this message will add local user into
	 * one of their specific chatrooms
	 */
	public void addMe() {
		AddMe addMe = new AddMe(me, id);
		(new Thread() {
			@Override
			public void run() {
				for (IUser user : users) {
					try {
						// send addMe message to users in the chatroom other than myself
						if (!user.equals(me)) {
							user.getConnect().sendReceive(me, new DataPacket<AddMe>(AddMe.class, addMe));
						}
					} catch (RemoteException e) {
						System.out.println("Broadcast to add user failed!\nRemote exception invite " + ": "
								+ user.getIP() + e + "\n");
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	/**
	 * Send removeMe message to users in the chatroom.
	 * Users in the chatroom upon receiving this message will remove local user from
	 * one of their specific chatrooms
	 */
	public void removeMe() {
		RemoveMe rmMe = new RemoveMe(this.me, id);
		for (IUser user : users) {
			try {
				// send removeMe message to users in the chatroom other than myself
				if (!user.equals(me)) {
					user.getConnect().sendReceive(me, new DataPacket<RemoveMe>(RemoveMe.class, rmMe));
				}
			} catch (RemoteException e) {
				System.out.println(
						"Broadcast to remove user failed!\nRemote exception invite " + ": " + user.getIP() + e + "\n");
				e.printStackTrace();
			}
		}
	}
}