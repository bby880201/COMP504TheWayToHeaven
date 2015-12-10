package xz42_bb26.game.model;

import java.awt.Component;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import javax.swing.JFrame;
import common.IChatUser;
import common.IChatroom;
import common.ICmd2ModelAdapter;
import common.IInitUser;
import common.message.IChatMessage;
import provided.datapacket.ADataPacketAlgoCmd;
import provided.datapacket.DataPacket;
import provided.datapacket.DataPacketAlgo;
import provided.mixedData.MixedDataKey;
import xz42_bb26.client.model.user.IChatUser2ModelAdapter;
import xz42_bb26.game.model.messages.Begin;
import xz42_bb26.game.model.messages.Ready;
import xz42_bb26.game.model.messages.TeamComsumeDepot;
import xz42_bb26.game.model.messages.TeamInfoUpdate;
import xz42_bb26.game.model.messages.TeamOut;
import xz42_bb26.game.model.messages.TeamWins;

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
	 * Current Info of teams
	 */
	private HashMap<IChatUser,Team> teams;
	
	private IChatroom2ModelAdapter model;
	
	public void setChatroom2ModelAdapter(IChatroom2ModelAdapter model){
		this.model = model;
	}
	
	/**
	 * Constructor
	 * 
	 * @param userName
	 *            the name of the user
	 * @throws RemoteException
	 */
	public Chatroom(String userName) throws RemoteException {

		initAlgo();
		me = new GameUser(userName, new IChatUser2ModelAdapter() {

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
	 * 
	 * @return id of the Chatroom
	 */
	@Override
	public UUID getID() {
		return id;
	}

	/**
	 * Get current user stub in the chatroom
	 * 
	 * @return
	 */
	public IChatUser getMe() {
		return me;
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
	 * 
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
	 * 
	 * @param user
	 *            An instance of IUser to be deleted to this chatroom
	 * @return A boolean value which indicates whether the user was successfully
	 *         deleted to the chatroom
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
				return null;
			}

			@Override
			public <T> void setMixedDataDictEntry(MixedDataKey<T> key, T value) {
			}

			@Override
			public String getUserName() {
				return null;
			}

			@Override
			public void sendToChatroom(IChatMessage message) {
				// TODO Auto-generated method stub	
			}

			@Override
			public void addToScrollable(Supplier<Component> componentFac) {
			}

			@Override
			public void updateUpdatable(Supplier<Component> componentFac) {				
			}

			@Override
			public void createNewWindow(Supplier<JFrame> frameFac) {
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
				
				return null;
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
		
		// Update Team Info when get info from other teams
		msgAlgo.setCmd(TeamInfoUpdate.class, new ADataPacketAlgoCmd<String, TeamInfoUpdate, IChatUser>() {
			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */
			private static final long serialVersionUID = 2210559989023917346L;


			@Override
			public String apply(Class<?> index, DataPacket<TeamInfoUpdate> host, IChatUser... params) {
				if(params[0]!= me){
					model.updateTeamInfo(host.getData().getTeam());
				}
				return host.getData().getTeam().toString();
			}


			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				
			}
		});
		
		// Update chat users when ready
		msgAlgo.setCmd(Ready.class, new ADataPacketAlgoCmd<String, Ready, IChatUser>() {

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
			}

			@Override
			public String apply(Class<?> index, DataPacket<Ready> host,
					IChatUser... params) {
				
				users = host.getData().getUsers();
				for (IChatUser user : users) {
					if(user!=me){
						TeamInfoUpdate aInfoUpdateMessage = new TeamInfoUpdate(model.getTeam());
						try {
							user.receive(me, aInfoUpdateMessage.getDataPacket());
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
				model.setDepots(host.getData().getDepots());
				return "Users list updated";
			}
		});
		
		// command for chat user info request
		msgAlgo.setCmd(TeamOut.class, new ADataPacketAlgoCmd<String, TeamOut, IChatUser>() {

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
					DataPacket<TeamOut> host, IChatUser... params) {
				model.aTeamOut(host.getData().getID());
				return "a Team is out";
			}
		});
		
		// command for chat user info response
		msgAlgo.setCmd(TeamWins.class, new ADataPacketAlgoCmd<String, TeamWins, IChatUser>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 5105304030772119307L;

			@Override
			public String apply(Class<?> index,
					DataPacket<TeamWins> host, IChatUser... params) {
				model.aTeamWins(host.getData().getID());
				
				return "A team wins";
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				_cmd2ModelAdpt = cmd2ModelAdpt;
			}
			
		});
		
		// command for chat user info request
		msgAlgo.setCmd(TeamComsumeDepot.class, new ADataPacketAlgoCmd<String, TeamComsumeDepot, IChatUser>() {

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
					DataPacket<TeamComsumeDepot> host, IChatUser... params) {
				model.teamConsume(host.getData().getaDepot());
				return "a Team is out";
			}
		});
		msgAlgo.setCmd(Begin.class, new ADataPacketAlgoCmd<String, Begin, IChatUser>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -6681106016892170401L;

			/**
			 * declare a static final serialVersionUID of type long to fix the warning
			 */

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
					DataPacket<Begin> host, IChatUser... params) {
				model.gameBigin();
				return "a Team is out";
			}
		});
	}

	public void IMove(Team team) {
		TeamInfoUpdate aMessage = new TeamInfoUpdate(team);
		send(me, aMessage);
	}


}
