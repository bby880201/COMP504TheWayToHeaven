package xz42_bb26.server.model.chatroom;

import java.awt.Color;
import java.awt.Component;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JLabel;

import xz42_bb26.game.controller.GameController;
import xz42_bb26.server.model.messages.InstallGameMessage;
import xz42_bb26.server.model.messages.StringMessage;
import xz42_bb26.server.model.messages.UnknownTypeData;
import xz42_bb26.server.model.user.ChatUser;
import xz42_bb26.server.model.user.ChatUserEntity;
import xz42_bb26.server.model.user.IChatUser2ModelAdapter;
import provided.datapacket.ADataPacketAlgoCmd;
import provided.datapacket.DataPacket;
import provided.datapacket.DataPacketAlgo;
import provided.mixedData.IMixedDataDictionary;
import provided.mixedData.MixedDataDictionary;
import provided.mixedData.MixedDataKey;
import common.IChatUser;
import common.IChatroom;
import common.ICmd2ModelAdapter;
import common.IInitUser;
import common.demo.message.chat.ChatUserInfoRequest;
import common.demo.message.chat.ChatUserInfoResponse;
import common.demo.message.chat.CommandRequest;
import common.demo.message.chat.CommandResponse;
import common.demo.message.chat.InitUserRequest;
import common.demo.message.chat.InitUserResponse;
import common.demo.message.chat.RemoveMe;
import common.message.IChatMessage;
import common.message.IInitMessage;
import common.message.chat.AAddMe;
import common.message.chat.AChatUserInfoRequest;
import common.message.chat.AChatUserInfoResponse;
import common.message.chat.ACommandResponse;
import common.message.chat.AInitUserRequest;
import common.message.chat.AInitUserResponse;
import common.message.chat.ARemoveMe;
import common.message.chat.ACommandRequest;
import common.message.chat.ATextMessage;
import common.message.init.AInvitation2Chatroom;

/**
 * This class implements the IChatroom interface. 
 * @author bb26, xc7
 */
public class ServerRoom implements IChatroom {
	/**
	 * declare a static final serialVersionUID of type long to fix the warning
	 */
	private static final long serialVersionUID = -1842717037685994672L;

	@SuppressWarnings("unchecked")
	private transient IServerRoom2WorldAdapter<ChatUserEntity, TeamRoom> serverAdapter = IServerRoom2WorldAdapter.NULL_OBJECT;
	
	// default command to model adapter that provides unknown command limited 
	// access to local system
	// mark as "transient" to prevent it from being serialized during any 
	// transport process
	private transient ICmd2ModelAdapter _cmd2ModelAdpt;
	
	// name of the local user	
	private IChatUser me;
	
	private IInitUser initMe;

	// private UUID id;
	private UUID id;
	
	// name of the chatroom
	private String displayName;

	private HashMap<IChatUser,ChatUserEntity> users = new HashMap<IChatUser,ChatUserEntity>();
	
	private transient HashMap<UUID, ChatUserEntity> userInfo = new HashMap<UUID, ChatUserEntity>();
	
	private transient BlockingQueue<IInitUser> initUserBq = new ArrayBlockingQueue<IInitUser>(1);
	
	private transient HashMap<UUID, UnknownTypeData> unknownDataCache = new HashMap<UUID, UnknownTypeData>();
	
	private transient IMixedDataDictionary mixDict = new MixedDataDictionary();

	private DataPacketAlgo<String, IChatUser> msgAlgo;
	
	private IChatroom thisRoom = this;
	
	private transient HashMap<UUID,TeamRoom> teamList = new HashMap<UUID,TeamRoom>();

	/**
	 * Constructor that takes in user name and user id as parameter
	 * @param name The local user name
	 * @param uuid The local user id
	 * @throws UnknownHostException Throw exception if host is unknown
	 * @throws RemoteException Throw exception if remote connection failed
	 */
	public ServerRoom(UUID uuid) throws UnknownHostException, RemoteException {

		initAlgo();
		IChatUser prestub = new ChatUser("", new IChatUser2ModelAdapter(){
			
			@Override
			public <T> void receive(IChatUser remote,
					DataPacket<? extends IChatMessage> dp) {
				String str = dp.execute(msgAlgo, remote);
				System.out.println(str);				
			}
			
		});
		
		me = (IChatUser) UnicastRemoteObject.exportObject(prestub, IInitUser.BOUND_PORT_SERVER);

		users.put(me,null);
		initMe = null;
		
		id = uuid;
	}

	/**
	 * Constructor that takes in a user name as parameter
	 * @param name The local user name
	 * @throws UnknownHostException Throw exception if host is unknown
	 * @throws RemoteException Throw exception if remote connection failed
	 */
	public ServerRoom() throws UnknownHostException, RemoteException {
		this(UUID.randomUUID());
	}

	private IInitUser getInitUser() {
		if (serverAdapter != IServerRoom2WorldAdapter.NULL_OBJECT) {
			initMe = serverAdapter.getInitUser();
		}
		return initMe;
	}

	/**
	 * Initialize the algo command
	 */
	private void initAlgo() {
		// initialize the cmd2model adapter, which grant the unknown command access
		// to limited local GUI 
		_cmd2ModelAdpt = new ICmd2ModelAdapter() {

			@Override
			public <T> T getMixedDataDictEntry(MixedDataKey<T> key) {
				return mixDict.get(key);
			}

			@Override
			public <T> void setMixedDataDictEntry(MixedDataKey<T> key, T value) {
				mixDict.put(key, value);
			}

			@Override
			public String getUserName() {
				return serverAdapter.getName();
			}

			@Override
			public void sendToChatroom(IChatMessage message) {
				// TODO Auto-generated method stub	
			}

			@Override
			public void addToScrollable(Supplier<Component> componentFac) {
				serverAdapter.display(componentFac);
			}

			@Override
			public void updateUpdatable(Supplier<Component> componentFac) {
				componentFac.get().setForeground(Color.ORANGE);;
				serverAdapter.display(componentFac);				
			}

			@Override
			public void createNewWindow(Supplier<JFrame> frameFac) {
				serverAdapter.popUp(frameFac);
			}

			@Override
			public void sendMsgTo(IChatMessage msg, IChatUser chatUser) {
				// TODO Auto-generated method stub
				
			}

		};
		// install ADataPacketAlgoCmd into DataPacketAlgo
		msgAlgo = new DataPacketAlgo<String, IChatUser>(new ADataPacketAlgoCmd<String, Object, IChatUser>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -6547790461171634944L;

			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */

			@Override
			/**
			 * install default command to handle unknown command type
			 */
			public String apply(Class<?> index, DataPacket<Object> host,
					IChatUser... params) {
				
				IChatUser remote = params[0];
				// class type of the unknown command
				Class<?> newCmdType = host.getData().getClass();
				
				ACommandRequest reqForAlgo = new CommandRequest(newCmdType);
				unknownDataCache.put(reqForAlgo.getID(), new UnknownTypeData(host, remote));
				
				(new Thread(){
					@Override
					public void run() {
						try {
							remote.receive(me, reqForAlgo.getDataPacket());
						} catch (RemoteException e) {
							System.out.println("Unknown data type command request failed:");
							e.printStackTrace();
						}
					}
				}).start();
				
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
		msgAlgo.setCmd(ATextMessage.class, new ADataPacketAlgoCmd<String, ATextMessage, IChatUser>() {
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
			public String apply(Class<?> index, DataPacket<ATextMessage> host,
					IChatUser... params) {

				IChatUser remote = params[0];

				JLabel content = new JLabel(users.get(remote).getName() + " says:\n" + host.getData().getText() + "\n");
				
				_cmd2ModelAdpt.addToScrollable(new Supplier<Component>(){

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
		msgAlgo.setCmd(AAddMe.class, new ADataPacketAlgoCmd<String, AAddMe, IChatUser>() {

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
			public String apply(Class<?> index, DataPacket<AAddMe> host,
					IChatUser... params) {
				
				IChatUser remote = host.getData().getUser();
				
				infoRequest(remote);
				return "User joined: " + users.get(params[0]);
			}
		});
		
		// command for chat user info request
		msgAlgo.setCmd(AChatUserInfoRequest.class, new ADataPacketAlgoCmd<String, AChatUserInfoRequest, IChatUser>() {

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
			public String apply(Class<?> index,
					DataPacket<AChatUserInfoRequest> host, IChatUser... params) {
				AChatUserInfoResponse infoResp = new ChatUserInfoResponse(host.getData(),serverAdapter.getName(),serverAdapter.getIp());
				(new Thread() {
					@Override
					public void run() {
						try {
							params[0].receive(me, infoResp.getDataPacket());
						} catch (RemoteException e) {
							System.out.println("Chat user info response sending failed:");
							e.printStackTrace();
						}
					}
				}).start();
				
				return "Sending chat user info to: " + users.get(params[0]);
			}
		});
		
		// command for chat user info response
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
				refreshList();
				
				userInfo.remove(host.getData().getID());
				
				return "User info updated from: " + users.get(params[0]);
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;
			}
			
		});
		
		msgAlgo.setCmd(AInitUserRequest.class, new ADataPacketAlgoCmd<String, AInitUserRequest, IChatUser>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -3791322058946183906L;

			@Override
			public String apply(Class<?> index,
					DataPacket<AInitUserRequest> host, IChatUser... params) {
				AInitUserResponse initResp = new InitUserResponse(host.getData(), initMe);
				(new Thread() {
					@Override
					public void run() {
						try {
							params[0].receive(me, initResp.getDataPacket());
						} catch (RemoteException e) {
							System.out.println("Init user stub response sending failed:");
							e.printStackTrace();
						}
					}
				}).start();
				
				return "Sending init user stub response to: " + users.get(params[0]);
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;
			}
		});

		msgAlgo.setCmd(AInitUserResponse.class, new ADataPacketAlgoCmd<String, AInitUserResponse, IChatUser>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1282191495265041594L;

			@Override
			public String apply(Class<?> index,
					DataPacket<AInitUserResponse> host, IChatUser... params) {
				
				(new Thread(){
					@Override
					public void run() {
						try {
							initUserBq.offer(host.getData().getUser(),10,TimeUnit.SECONDS);
						} catch (Exception e) {
							System.out.println("Exception happened when trying to put init user to blocking queue:");
							e.printStackTrace();
						} 
					}
				}).start();
				
				return null;
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;				
			}
			
		});
		
		msgAlgo.setCmd(ACommandRequest.class, new ADataPacketAlgoCmd<String, ACommandRequest, IChatUser>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1064401054222898150L;

			@Override
			@SuppressWarnings("unchecked")
			public String apply(Class<?> index,
					DataPacket<ACommandRequest> host, IChatUser... params) {
				
				Class<?> unknow = host.getData().getUnknownType();
				ADataPacketAlgoCmd<String,?,IChatUser> cmd = (ADataPacketAlgoCmd<String, ?, IChatUser>) msgAlgo.getCmd(unknow);
				ACommandResponse cmdResp = new CommandResponse(host.getData(), unknow, cmd);
				
				(new Thread(){
					@Override
					public void run() {
						try {
							params[0].receive(me, cmdResp.getDataPacket());
						} catch (RemoteException e) {
							System.out.println("Command response failed:");
							e.printStackTrace();
						}
					}
				}).start();
				return "Responsing command to :" + users.get(params[0]);
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;					
			}
			
		});

		msgAlgo.setCmd(ACommandResponse.class, new ADataPacketAlgoCmd<String, ACommandResponse, IChatUser>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7984109318009129478L;

			@Override
			public String apply(Class<?> index,
					DataPacket<ACommandResponse> host, IChatUser... params) {
				
				ADataPacketAlgoCmd<String, ?, IChatUser> cmd = host.getData().getCommand();
				cmd.setCmd2ModelAdpt(_cmd2ModelAdpt);
				
				msgAlgo.setCmd(host.getData().getUnknownType(), cmd);
				UnknownTypeData data = unknownDataCache.remove(host.getData().getID());
				
				if (null!= data.getSender()) {
					String str = data.getDataPacket().execute(msgAlgo, data.getSender());
					System.out.println(str);
					
					return "Installed unknown data type: " + host.getData().getUnknownType() + " from: "
					+ users.get(data.getSender());
				}
				else {
					return "Unknown data handling failed, wrong data cache";
				}
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;					
			}
			
		});
		
		// handle startGame type cmd as known cmd type 
		msgAlgo.setCmd(InstallGameMessage.class, new ADataPacketAlgoCmd<String, InstallGameMessage, IChatUser>() {
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
			public String apply(Class<?> index, DataPacket<InstallGameMessage> host,
					IChatUser... params) {
				UUID uuid = host.getData().getID();
				
				GameController gameController = new GameController(uuid, me, teamList.get(uuid).getName(), host.getData().isNavigator());
				try {
					gameController.start();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				return "Start Game";
			}
		});
		
		// handle RemoveMe type cmd as known cmd type 
		msgAlgo.setCmd(ARemoveMe.class, new ADataPacketAlgoCmd<String, ARemoveMe, IChatUser>() {

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
			public String apply(Class<?> index, DataPacket<ARemoveMe> host,
					IChatUser... params) {
				ChatUserEntity user = users.get(params[0]);
				serverAdapter.append("User left: " + user);
				removeUser(host.getData().getUser());
				return "User left: " + user;
			}
		});
	}
	
	/**
	 * Get the IChatRoom2WorldAdapter associated with this chatroom
	 * @return An instance of IChatRoom2WorldAdapter
	 */
	public IServerRoom2WorldAdapter<ChatUserEntity,TeamRoom> getChatWindowAdapter() {
		return serverAdapter;
	}

	/**
	 * Set the IChatRoom2WorldAdapter
	 * 
	 * @param chatWindowAdapter An instance of IChatRoom2WorldAdapter
	 * @return return boolean value for synchronize purpose
	 */
	public boolean setChatWindowAdapter(IServerRoom2WorldAdapter<ChatUserEntity, TeamRoom> chatWindowAdapter) {
		this.serverAdapter = chatWindowAdapter;
		
		ChatUserEntity meInfo = new ChatUserEntity(me, chatWindowAdapter.getName(), chatWindowAdapter.getIp());
		users.put(me, meInfo);
		
		initMe = getInitUser();
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
		serverAdapter.deleteWindow();
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
		AInvitation2Chatroom invite = new AInvitation2Chatroom(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 375311101703022871L;

			@Override
			public DataPacket<? extends IInitMessage> getDataPacket() {
				return new DataPacket<AInvitation2Chatroom>(AInvitation2Chatroom.class, this);
			}

			@Override
			public IChatroom getChatroom() {
				return thisRoom;
			}

			@Override
			public boolean mustAccept() {
				return false;
			}
			
		};

		(new Thread() {
			@Override
			public void run() {
				try {
					// send friend an InviteToChatroom message
					friend.receive(initMe, invite.getDataPacket());
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
			displayName = "Empty room";
		}
		return displayName;
	}

	@Override
	/**
	 * Get a list of IUser in this chatroom
	 * @return A list of IUser in this chatroom
	 */
	public HashSet<IChatUser> getUsers() {
		return new HashSet<IChatUser>(users.keySet());
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
		ATextMessage txtMsg = new StringMessage(text);
		send(me, txtMsg);
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
		serverAdapter.append(data);
	}

	/**
	 * Refresh the member list to display in the GUI panel
	 */
	private void refreshList() {
		if (!(null == serverAdapter)) {
			HashMap<IChatUser, ChatUserEntity> mbList = users;
			mbList.remove(me);
			serverAdapter.refreshList(mbList.values());
		}
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
		AAddMe addMe = new AAddMe(){

			/**
			 * 
			 */
			private static final long serialVersionUID = -1950093697393528989L;

			@Override
			public DataPacket<? extends IChatMessage> getDataPacket() {
				return new DataPacket<AAddMe>(AAddMe.class, this);
			}

			@Override
			public IChatUser getUser() {
				return me;
			}
			
		};
		(new Thread() {
			@Override
			public void run() {
				for (IChatUser user : users.keySet()) {
					try {
						// send addMe message to users in the chatroom other than myself
						if (!user.equals(me)) {
							user.receive(me, addMe.getDataPacket());
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
		ARemoveMe rmMe = new RemoveMe(me);
		for (IChatUser user : users.keySet()) {
			try {
				// send removeMe message to users in the chatroom other than myself
				if (!user.equals(me)) {
					user.receive(me, rmMe.getDataPacket());
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
		boolean added = users.put(user,new ChatUserEntity(user)) != null;
		// Refresh the member list to display in the GUI panel
		infoRequest(user);
		return added;
	}
	
	public void infoRequest(IChatUser user) {
		ChatUserEntity newEntity = new ChatUserEntity(user);
		AChatUserInfoRequest infoReq = new ChatUserInfoRequest();
		
		userInfo.put(infoReq.getID(), newEntity);
		users.put(user,newEntity);
		refreshList();
		
		serverAdapter.append("User joined: " + newEntity);
		
		(new Thread(){
			@Override
			public void run(){
				try {
					user.receive(me, infoReq.getDataPacket());
				} catch (RemoteException e) {
					System.out.println("Chat user info request sending failed:");
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * Delete a user from this chatroom
	 * @param user An instance of IUser to be deleted to this chatroom
	 * @return A boolean value which indicates whether the user was 
	 * 			successfully deleted to the chatroom
	 */
	@Override
	public boolean removeUser(IChatUser user) {
		boolean removed = users.remove(user) != null;
		refreshList();
		if (users.size() == 0) {
			serverAdapter.deleteWindow();
			serverAdapter.deleteModel(id);
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
				for (IChatUser user : users.keySet()) {
					// send message to users other than myself
					if (!user.equals(me)) {
						try {
							user.receive(me, message.getDataPacket());
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

	public void installGame() {
		for (TeamRoom team : teamList.values()) {
			(new Thread(){
				@Override
				public void run(){
					team.installGame();
				}
			}).start();
		}
//		GameController gameController = new GameController();
//		gameController.start();
		
	}


	public void speakTo(ChatUserEntity user) {
		
		(new Thread(){
			@Override
			public void run(){
				if (null!=user){
					serverAdapter.speakTo(user.getIp());
				}
			}
		}).start();

	}
	
	public void getRemoteInitUser(IChatUser user) {
		
		AInitUserRequest initUsrReq = new InitUserRequest();
		(new Thread(){
			@Override
			public void run(){
				try {
					user.receive(me, initUsrReq.getDataPacket());
					@SuppressWarnings("unused")
					IInitUser initUser = initUserBq.poll(10, TimeUnit.SECONDS);
				} catch (Exception e) {
					System.out.println("Getting remote init user failed:");
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void addTeam(TeamRoom team) {
		teamList.put(team.getID(),team);
		refreshTeam();		
	}
	
	private void refreshTeam() {
		serverAdapter.refreshTeam(teamList.values());
	}

}