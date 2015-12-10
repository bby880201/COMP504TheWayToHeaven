package xz42_bb26.server.controller;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import javax.swing.JFrame;

import common.IChatUser;
import common.IChatroom;
import common.IInitUser;
import xz42_bb26.server.model.chatroom.TeamRoom;
import xz42_bb26.server.model.ServerModel;
import xz42_bb26.server.model.IModel2ViewAdapter;
import xz42_bb26.server.model.chatroom.ServerRoom;
import xz42_bb26.server.model.chatroom.IServerRoom2WorldAdapter;
import xz42_bb26.server.model.user.ChatUserEntity;
import xz42_bb26.server.view.IView2ModelAdapter;
import xz42_bb26.server.view.ServerGUI;
import xz42_bb26.server.view.chatwindow.ServerWindow;
import xz42_bb26.server.view.chatwindow.IServerWindow2World;


/**
 * MVC Controller for the system, which oversees the model and view of the system
 * @author bb26, xc7
 */
public class ServerController {
	// field representing the view of the system
	private ServerGUI<IChatroom, IInitUser, ChatUserEntity,TeamRoom> view;
	// field representing the model of the system
	private ServerModel model;

	/**
	 * Controller constructor builds the system
	 */
	public ServerController() {
		// set the view field
		view = new ServerGUI<IChatroom, IInitUser,ChatUserEntity, TeamRoom>(new IView2ModelAdapter<IChatroom, IInitUser, ChatUserEntity>() {
			/**
			 * Quits the current connection and closes the application.   
			 * Causes the model to stop and thus end the application. 
			 */
			@Override
			public void quit() {
				model.stop();
			}

			/**
			 * Connect to the RMI Registry at the given remote host, and grab the 
			 * stub from that registry. Then create a local chatroom with the remote 
			 * user represented by the given IP address, and inform the remote user 
			 * to create a local chatroom.
			 * 
			 * @param ip The remote IP address to connect to.
			 */
			@Override
			public void chatWith(String ip) {
				model.chatWith(ip);
			}

			@Override
			public void getListRooms(String ip) {
				model.getFriendChatrooms(ip);
			}

			@Override
			/**
			 * Upon given a chatroom to join, create a local chatroom and add existing 
			 * users in the given chatroom into the local chatroom. Then broadcast 
			 * to users in remote chatroom to add my stub into their local chatrooms.
			 * 
			 * @param rm the chatroom to join
			 */
			public void joinChatroom(IChatroom rm) {
				if (rm != null) {
					model.joinRoom(rm);
				}
			}
		});

		// set the model field
		model = new ServerModel(new IModel2ViewAdapter<IInitUser,IChatUser,IChatroom,ChatUserEntity,TeamRoom>() {

			@Override
			/**
			 * Creates a chatroom, which is a MINI-MVC structure, by using factory methods
			 * 
			 * @param chatRoom the mini-model given as a parameter
			 * @return the mini-model2view adapter, which will be installed into the mini-model
			 */
			public IServerRoom2WorldAdapter<ChatUserEntity, TeamRoom> makeChatRoom(ServerRoom serverRoom) {
				/**
				 * Factory method makes a new mini-view and installs the 
				 * mini-View2Model adapter in it.
				 */
				ServerWindow<ChatUserEntity,TeamRoom> cw = view.makeChatRoom(new IServerWindow2World<ChatUserEntity>() {

					@Override
					/**
					 * Get name of this chatroom
					 * @return a string which is the name of this chatroom
					 */
					public String getName() {
						return serverRoom.getName();
					}

					@Override
					/**
					 * Broadcast to every current member in the chatroom to remove 
					 * my stub from their local chatrooms. 
					 * Then delete the local chatroom in my model. 
					 */
					public void quit() {
						serverRoom.quit();
					}

					@Override
					/**
					 * Send a message to every user in the chatroom.
					 * @param text the message to send over
					 */
					public void sendMsg(String text) {
						serverRoom.sendMsg(text);
					}

					@Override
					/**
					 * Invite the user with the remote IP address to join the chatroom. 
					 * The remote user will create a local copy of the chatroom and 
					 * broadcast to existing users in the chatroom to add the remote 
					 * user's stub into their local chatrooms. 
					 * 
					 * @param ip the remote IP address of the remote user
					 */
					public void invite(String ip) {
						IInitUser friend = model.connectTo(ip);
						if (null != friend)
							serverRoom.invite(friend);
					}

					@SuppressWarnings({ "rawtypes" })
					@Override
					/**
					 * Delete the mini-view (the chat window of this chatroom) 
					 * from the main-view (the window of the chatapp)
					 * 
					 * @param cw the view of the chatroom
					 */
					public void deleteWindow(ServerWindow cw) {
						view.deleteChatWindow(cw);
					}

					/**
					 * Speak to the given user in this chatroom. This is done by 
					 * calling the chatWith method with the user's IP address.
					 * 
					 * @param user the specific user to speak to in this chatroom
					 */
					@Override
					public void speakTo(ChatUserEntity user) {
						serverRoom.speakTo(user);
					}
					
					@Override
					public void installGame() {
						serverRoom.installGame();
					}

					@Override
					public void createTeam(ChatUserEntity member) {
						serverRoom.setUserInvisible(member.getChatUser());
						serverRoom.addTeam(model.creatTeam(serverRoom.getMe(),member));
					}

					@Override
					public void rejectConnection() {
						serverRoom.rejectConnection();
						
					}

					@Override
					public void begin() {
						serverRoom.begin();
					}

					@Override
					public void kick(ChatUserEntity usr) {
						serverRoom.kick(usr);
					}
				});

				// return the mini-model2world adapter
				return new IServerRoom2WorldAdapter<ChatUserEntity,TeamRoom>() {

					@Override
					/**
					 * Add the data to the specific chatroom's chat window.
					 * @param data the data to be added to GUI panel
					 */
					public void append(String data) {
						cw.append(data);
					}

					@Override
					/**
					 * Refresh the member list on the chatroom 
					 * @param users the list of users to show on chatroom member list panel
					 */
					public void refreshList(Collection<ChatUserEntity> users) {
						cw.refreshList(users);
					}

					@Override
					/**
					 * Delete the certain chat window from the main GUI panel
					 */
					public void deleteWindow() {
						model.deleteChatroom(serverRoom.getID());
						cw.deleteWindow();
					}

					@Override
					/**
					 * Delete the chatroom associated with the specific id from local system
					 * @param id the id of a specific chatroom
					 */
					public void deleteModel(UUID id) {
						model.deleteChatroom(id);
					}

					@Override
					/**
					 * Give a Container (e.g. JPanel) as the accessible part of the local system 
					 * to the unknown command
					 * @return a container that is on the chatroom window's GUI 
					 */
					public Container Scrollable() {
						return cw.Scrollable();
					}

					/**
					 * Display a container to view
					 * @param containerSupplier a supplier contains a container
					 */
					@Override
					public void display(Supplier<Component> containerSupplier) {
						cw.display(containerSupplier);
					}

					@Override
					public IInitUser getInitUser() {
						return model.getInitUser();
					}

					@Override
					public String getName() {
						return model.getName();
					}

					@Override
					public String getIp() {
						return model.getIp();
					}

					@Override
					public void speakTo(String ip) {
						model.chatWith(ip);
					}

					@Override
					public void popUp(Supplier<JFrame> frameFac) {
						EventQueue.invokeLater(new Runnable() {
							public void run() {
								try {
									JFrame frame = frameFac.get();
									frame.setVisible(true);
									frame.revalidate();
									frame.repaint();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}

					@Override
					public void refreshTeam(Collection<TeamRoom> collection) {
						cw.refreshTeam(collection);
					}
				};
			}
			
			/**
			 * display chat rooms
			 */
			@Override
			public void refreshRoomList(Set<IChatroom> rooms) {
				view.refreshRoomList(rooms);
			}
		});
	}

	/**
	 * Start the system
	 */
	private void start() {
		model.start();
		view.start();
	}

	/**
	 * Launch the application.
	 * @param args a list of stings 
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerController ctl = new ServerController();
					ctl.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
