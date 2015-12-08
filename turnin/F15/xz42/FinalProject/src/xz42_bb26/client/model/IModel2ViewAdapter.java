package xz42_bb26.client.model;

import java.util.Set;

import xz42_bb26.client.model.chatroom.ChatroomWithAdapter;
import xz42_bb26.client.model.chatroom.IChatRoom2WorldAdapter;

/**
 * Adapter that enables the model to talk to the view.
 * 
 * @author bb26, xc7
 */
public interface IModel2ViewAdapter<T1,T2,T3,T4> {

	/**
	 * Creates a chatroom, which is a MINI-MVC structure, by using factory methods
	 * 
	 * @param chatRoom the mini-model given as a parameter
	 * @return the mini-model2view adapter, which will be installed into the mini-model
	 */
	public IChatRoom2WorldAdapter<T4> makeChatRoom(ChatroomWithAdapter chatRoom);
	
	/**
	 * Let GUI refresh chat rooms obtained from remote user
	 * @param rooms chat rooms
	 */
	public void refreshRoomList(Set<T3> rooms);
}
