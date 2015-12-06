package xz42_bb26.server.model;

import xz42_bb26.client.model.chatroom.ChatroomWithAdapter;
import xz42_bb26.client.model.chatroom.IChatRoom2WorldAdapter;

/**
 * Adapter that enables the model to talk to the view.
 * 
 * @author bb26, xc7
 */
public interface IModel2ViewAdapter<T1,T2> {

	/**
	 * Creates a chatroom, which is a MINI-MVC structure, by using factory methods
	 * 
	 * @param chatRoom the mini-model given as a parameter
	 * @return the mini-model2view adapter, which will be installed into the mini-model
	 */
	public IChatRoom2WorldAdapter<T2> makeChatRoom(ChatroomWithAdapter chatRoom);
}
