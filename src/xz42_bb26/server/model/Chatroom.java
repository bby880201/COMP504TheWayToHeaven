package xz42_bb26.server.model;

import java.awt.Component;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import javax.swing.JLabel;

import xz42_bb26.client.model.messages.StartGameMessage;
import xz42_bb26.client.model.messages.StringMessage;
import xz42_bb26.client.model.user.ChatUser;
import xz42_bb26.client.model.user.IChatUser2ModelAdapter;
import xz42_bb26.game.controller.GameController;
import provided.datapacket.ADataPacketAlgoCmd;
import provided.datapacket.DataPacket;
import provided.datapacket.DataPacketAlgo;
import provided.mixedData.MixedDataKey;
import common.IChatUser;
import common.IChatroom;
import common.ICmd2ModelAdapter;
import common.IInitUser;
import common.message.IChatMessage;
import common.message.chat.AddMe;
import common.message.chat.RemoveMe;
import common.message.chat.CommandRequest;
import common.message.init.Invitation2Chatroom;

/**
 * This class implements the IChatroom interface. 
 * @author bb26, xc7
 */
public class Chatroom implements IChatroom {

	/**
	 * declare a static final serialVersionUID of type long to fix the warning
	 */
	private static final long serialVersionUID = 4342241825756246744L;

	@SuppressWarnings("unchecked")
	// name of the local user
	private IChatUser me;

//	private UUID id;
	private UUID id;
	// name of the chatroom
	private String displayName;

	private Set<IChatUser> users = new HashSet<IChatUser>();

	private DataPacketAlgo<String, IChatUser> msgAlgo;

	// default command to model adapter that provides unknown command limited 
	// access to local system
	// mark as "transient" to prevent it from being serialized during any 
	// transport process
	private transient ICmd2ModelAdapter _cmd2ModelAdpt;

	private IChatroom thisRoom = this;

	private IInitUser initMe;



	/**
	 * Constructor that takes in a user name as parameter
	 * @param name The local user name
	 * @throws UnknownHostException Throw exception if host is unknown
	 * @throws RemoteException Throw exception if remote connection failed
	 */
	public Chatroom(String name, IInitUser init) throws UnknownHostException, RemoteException {

		initAlgo();
		me = new ChatUser(name, new IChatUser2ModelAdapter(){

			@Override
			public <T> void receive(IChatUser remote, IChatMessage message) {
				String str = message.getDataPacket().execute(msgAlgo, remote);
				System.out.println(str);
			}
		});
		
		IChatUser stub = (IChatUser) UnicastRemoteObject.exportObject(me, IInitUser.BOUND_PORT + 1);
		
		id = UUID.randomUUID();
		users.add(stub);
		initMe = init;
	}

	public Chatroom(String userName, IInitUser me2, UUID id2) throws UnknownHostException, RemoteException {
		this(userName, me2);
		id = id2;
	}

	/**
	 * Initialize the algo command
	 */
	private void initAlgo() {
		// initialize the cmd2model adapter, which grant the unknown command access
		// to limited local GUI 
		_cmd2ModelAdpt = new ICmd2ModelAdapter() {

			@Override
			public void updateOldGUI(Supplier<Component> componentFac) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void createNewGUI(Supplier<Component> componentFac) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public <T> T getMixedDataDictEntry(MixedDataKey<T> key) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> void setMixedDataDictEntry(MixedDataKey<T> key, T value) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void sendToChatroom(IChatMessage message) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getUserName() {
				// TODO Auto-generated method stub
				return null;
			}


		};
		// install ADataPacketAlgoCmd into DataPacketAlgo
		msgAlgo = new DataPacketAlgo<String, IChatUser>(new ADataPacketAlgoCmd<String, Object, IChatUser>() {

			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */
			private static final long serialVersionUID = -1139989943264094599L;

			@Override
			/**
			 * install default command to handle unknown command type
			 */
			public String apply(Class<?> index, DataPacket<Object> host,
					IChatUser... params) {
				IChatUser remote = params[0];
				// class type of the unknown command
				Class<?> newCmdType = host.getData().getClass();
				
				CommandRequest reqForAlgo = new CommandRequest(newCmdType);
				
				//TODO handle unknown data type
				// request the AlgoCmd from the remote user
				ADataPacketAlgoCmd<String, ?, IChatUser> cmd = null;
				
				try {
					remote.receive(me, reqForAlgo);
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
		msgAlgo.setCmd(StringMessage.class, new ADataPacketAlgoCmd<String, StringMessage, IChatUser>() {
			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */
			private static final long serialVersionUID = 2210559989023917346L;

			@Override
			/**
			 * Set the ICmd2ModelAdapter of this command
			 * @param cmd2ModelAdpt An instance of ICmd2ModelAdapter
			 */
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;
			}

			@Override
			public String apply(Class<?> index, DataPacket<StringMessage> host,
					IChatUser... params) {

				IChatUser remote = params[0];

				JLabel content = new JLabel(remote.toString() + " says:\n" + host.getData().getMsg() + "\n");
				
				_cmd2ModelAdpt.updateOldGUI(new Supplier<Component>(){

					@Override
					public Component get() {
						return content;
					}
					
				});

				// return status information
				return "String Message received from: " + remote;

				//				IUser remote = (IUser) params[0];
				//				chatWindowAdapter.append(remote.getName() + " says:");
				//				chatWindowAdapter.append(host.getData());
				//				// return status information
				//				return "String received from " + remote;
			}
		});
		
		// handle addMe type cmd as known cmd type 
		msgAlgo.setCmd(AddMe.class, new ADataPacketAlgoCmd<String, AddMe, IChatUser>() {

			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */
			private static final long serialVersionUID = -189336880905492572L;				

			@Override
			/**
			 * Set the ICmd2ModelAdapter of this command
			 * @param cmd2ModelAdpt An instance of ICmd2ModelAdapter
			 */
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;
			}

			@Override
			public String apply(Class<?> index, DataPacket<AddMe> host,
					IChatUser... params) {
				addUser(host.getData().getUser());
				return "User joined: " + host.getData().getUser();
			}
		});
		
		
		// handle startGame type cmd as known cmd type 
		msgAlgo.setCmd(StartGameMessage.class, new ADataPacketAlgoCmd<String, StartGameMessage, IChatUser>() {
			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */	
			private static final long serialVersionUID = 2964027427383796628L;

		

			@Override
			/**
			 * Set the ICmd2ModelAdapter of this command
			 * @param cmd2ModelAdpt An instance of ICmd2ModelAdapter
			 */
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;
			}

			@Override
			public String apply(Class<?> index, DataPacket<StartGameMessage> host,
					IChatUser... params) {
				GameController gameController = new GameController();
				gameController.start();
				return "Start Game";
			}
		});
		
		// handle RemoveMe type cmd as known cmd type 
		msgAlgo.setCmd(RemoveMe.class, new ADataPacketAlgoCmd<String, RemoveMe, IChatUser>() {

			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */
			private static final long serialVersionUID = 3679441143550851362L;

			@Override
			/**
			 * Set the ICmd2ModelAdapter of this command
			 * @param cmd2ModelAdpt An instance of ICmd2ModelAdapter
			 */
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;
			}

			@Override
			public String apply(Class<?> index, DataPacket<RemoveMe> host,
					IChatUser... params) {
				removeUser(host.getData().getUser());
				return "User left: " + host.getData().getUser();
			}
		});
	}



	/**
	 * Quit from the chatroom
	 */
	public void quit() {
		// first remove myself from the chatroom, then broadcast to everyone else
		// to remove me from their local chatroom
		removeMe();
	}

	/**
	 * Get an instance of IUser 
	 * @return An instance of IUser which represents the local user
	 */
	public IChatUser getMe() {
		return me;
	}

	/**
	 * Set the IUser associated with the local user
	 * @param me An instance of IUser
	 */
	public void setMe(IChatUser me) {
		this.me = me;
	}

	/**
	 * Send the remote user an InviteToChatroom message.
	 * @param friend the remote user to be invited to this chatroom
	 */
	public void invite(IInitUser friend) {
		Invitation2Chatroom invite = new Invitation2Chatroom(thisRoom,false);

		(new Thread() {
			@Override
			public void run() {
				try {
					// send friend an InviteToChatroom message
					friend.receive(initMe, invite);
				} catch (RemoteException e) {
					System.out.println("Invite " + friend + " to room failed: " + e + "\n");
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	/**
	 * Return the chatroom name for display
	 * @return display of this chatroom
	 */
	public String getName() {
		if (users.size() > 0) {
			displayName = "Chat with " + Integer.toString(users.size()) + "members";
		} else {
			displayName = "Chat with " + me;
		}
		return displayName;
	}

	@Override
	/**
	 * Get a list of IUser in this chatroom
	 * @return A list of IUser in this chatroom
	 */
	public HashSet<IChatUser> getUsers() {
		return new HashSet<IChatUser>(users);
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
		send(me, new StringMessage(text));
		/**
		 * The following two lines are for testing purpose
		 */
		// send(me, new DataPacket<Integer>(Integer.class, Integer.valueOf(text)));
		// send(me, new DataPacket<StringMessage>(StringMessage.class, new StringMessage(text)));
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
		AddMe addMe = new AddMe(me);
		(new Thread() {
			@Override
			public void run() {
				for (IChatUser user : users) {
					try {
						// send addMe message to users in the chatroom other than myself
						if (!user.equals(me)) {
							user.receive(me, addMe);
						}
					} catch (RemoteException e) {
						System.out.println("Broadcast to add user failed!\nRemote exception invite " + ": "
								+ user + e + "\n");
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
		RemoveMe rmMe = new RemoveMe(me);
		for (IChatUser user : users) {
			try {
				// send removeMe message to users in the chatroom other than myself
				if (!user.equals(me)) {
					user.receive(me, rmMe);
				}
			} catch (RemoteException e) {
				System.out.println(
						"Broadcast to remove user failed!\nRemote exception invite " + ": " + user + e + "\n");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add a remote user into this chatroom
	 * @param user An instance of IUser to be added to this chatroom
	 * @return A boolean value which indicates whether the remote user was 
	 * 			successfully added to the chatroom
	 */
	@Override
	public boolean addUser(IChatUser user) {
		boolean added = users.add(user);
		// Refresh the member list to display in the GUI panel
		return added;
	}

	/**
	 * Delete a user from this chatroom
	 * @param user An instance of IUser to be deleted to this chatroom
	 * @return A boolean value which indicates whether the user was 
	 * 			successfully deleted to the chatroom
	 */
	@Override
	public boolean removeUser(IChatUser user) {
		boolean removed = users.remove(user);
		if (users.size() == 0) {
		} else {
		}
		return removed;
	}

	@Override
	/**
	 * Send a message to all the users in the chatroom
	 * @param me An instance of IUser which represents the sender of the message
	 * @param message An instance of ADataPacket which wraps around the message
	 */
	public void send(IChatUser sender, IChatMessage message) {
		(new Thread() {
			@Override
			public void run() {
				for (IChatUser user : users) {
					// send message to users other than myself
					if (!user.equals(me)) {
						try {
							user.receive(me, message);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
		
	}

	@Override
	public UUID getID() {
		return id;
	}

	public void startGame() {
		StartGameMessage startGame = new StartGameMessage();
		for (IChatUser user : users) {
			try {
				// send startgame message to users in the chatroom other than myself
				user.receive(me, startGame);
			} catch (RemoteException e) {
				System.out.println(
						"Broadcast to start game failed!\nRemote exception invite " + ": " + user + e + "\n");
				e.printStackTrace();
			}
		}
//		GameController gameController = new GameController();
//		gameController.start();
		
	}

}