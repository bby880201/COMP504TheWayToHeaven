package xz42_bb26.game.model;

import java.awt.Component;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JLabel;

import common.IChatUser;
import common.IChatroom;
import common.ICmd2ModelAdapter;
import common.IInitUser;
import common.demo.message.chat.CommandRequest;
import common.message.IChatMessage;
import jogamp.common.util.locks.RecursiveThreadGroupLockImpl01Unfairish;
import provided.datapacket.ADataPacketAlgoCmd;
import provided.datapacket.DataPacket;
import provided.datapacket.DataPacketAlgo;
import provided.mixedData.MixedDataKey;
import xz42_bb26.client.model.messages.StartGameMessage;
import xz42_bb26.client.model.messages.StringMessage;
import xz42_bb26.client.model.user.IChatUser2ModelAdapter;
import xz42_bb26.game.controller.GameController;

public class Chatroom implements IChatroom {
	/**
	 * The IChatUser
	 */
	private IChatUser me;
	/**
	 * Message algorithms
	 */
	private DataPacketAlgo<String, IChatUser> msgAlgo;
	
	/**
	 * The set of users
	 */
	private Set<IChatUser> users = new HashSet<IChatUser>();
	/**
	 * serial id
	 */
	private static final long serialVersionUID = -3786521131110581109L;
	
	/**
	 * The UUID of the chatroom
	 */
	private UUID id;

	/**
	 * The command to model adapter
	 */
	private transient ICmd2ModelAdapter _cmd2ModelAdpt;
	/**
	 * Constructor
	 * @param userName the name of the user
	 * @throws RemoteException 
	 */
	public Chatroom(String userName) throws RemoteException {
		
		initAlgo();
		me = new ChatUser(userName,new IChatUser2ModelAdapter() {
			
			@Override
			public <T> void receive(IChatUser remote, DataPacket<? extends IChatMessage> dp) {
				String string = dp.execute(msgAlgo, remote);
				System.out.println(string);
			}
		});
		IChatUser stub = (IChatUser) UnicastRemoteObject.exportObject(me, IInitUser.BOUND_PORT_CLIENT);
		id = UUID.randomUUID();
		users.add(stub);
	}

	/**
	 * Get the id of the Chatroom
	 * @return id of the Chatroom
	 */
	@Override
	public UUID getID() {
		return id;
	}

	/**
	 * get the name of the chatroom
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "The global game chatroom";
	}

	/**
	 * set the display name
	 */
	@Override
	public void setName(String name) {
	}

	@Override
	/**
	 * Get a list of IUser in this chatroom
	 * @return A list of IUser in this chatroom
	 */
	public Set<IChatUser> getUsers() {
		return new HashSet<IChatUser>(users);
	}
	/**
	 * Add a user to this chatroom
	 */
	@Override
	public boolean addUser(IChatUser user) {
		return users.add(user);
	}


	@Override
	public void send(IChatUser sender, IChatMessage message) {
		(new Thread() {
			@Override
			public void run() {
				for (IChatUser user : users) {
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
	
	/**
	 * Delete a user from this chatroom
	 * @param user An instance of IUser to be deleted to this chatroom
	 * @return A boolean value which indicates whether the user was 
	 * 			successfully deleted to the chatroom
	 */
	@Override
	public boolean removeUser(IChatUser user) {
		return users.remove(user);
	}
	
	
	private void initAlgo() {
		// initialize the cmd2model adapter, which grant the unknown command access
		// to limited local GUI 
		_cmd2ModelAdpt = new ICmd2ModelAdapter() {

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
			public String getUserName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void sendToChatroom(IChatMessage message) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void addToScrollable(Supplier<Component> componentFac) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void updateUpdatable(Supplier<Component> componentFac) {			
			}

			@Override
			public void createNewWindow(Supplier<JFrame> frameFac) {
				// TODO Auto-generated method stub
				
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
					remote.receive(me, reqForAlgo.getDataPacket());
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

	}

}
