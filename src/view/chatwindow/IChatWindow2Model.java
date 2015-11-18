package view.chatwindow;

/**
 * Mini-View2Model adapter that enables the mini-view (chat window) to communicate 
 * to the model.
 * 
 * @author bb26, xc7
 */
public interface IChatWindow2Model<T> {

	/**
	 * Get name of this chatroom
	 * @return a string which is the name of this chatroom
	 */
	public String getName();

	/**
	 * Broadcast to every current member in the chatroom to remove my stub from their
	 * local chatrooms. Then delete the local chatroom in my model. 
	 */
	public void quit();

	/**
	 * Send a message to every user in the chatroom.
	 * @param text the message to send over
	 */
	public void sendMsg(String text);

	/**
	 * Invite the user with the remote IP address to join the chatroom. The remote 
	 * user will create a local copy of the chatroom and broadcast to existing users 
	 * in the chatroom to add the remote user's stub into their local chatrooms. 
	 * 
	 * @param ip the remote IP address of the remote user
	 */
	public void invite(String ip);

	@SuppressWarnings("rawtypes")
	/**
	 * Delete the mini-view (the chat window of this chatroom) 
	 * from the main-view (the window of the chatapp)
	 * 
	 * @param cw the view of the chatroom
	 */
	public void deleteWindow(ChattingWindow cw);

	/**
	 * Speak to the given user in this chatroom. This is done by calling the 
	 * chatWith method with the user's IP address.
	 * 
	 * @param user the specific user to speak to in this chatroom
	 */
	public void speakTo(T user);

	/**
	 * No-op "null" adapter
	 */
	public static final IChatWindow2Model<?> NULL_OBJECT = new IChatWindow2Model<Object>() {

		@Override
		public String getName() {
			// this method will be override in the controller
			return null;
		}

		@Override
		public void quit() {
			// this method will be override in the controller
		}

		@Override
		public void sendMsg(String text) {
			// this method will be override in the controller
		}

		@Override
		public void invite(String text) {
			// this method will be override in the controller	
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void deleteWindow(ChattingWindow cw) {
			// this method will be override in the controller
		}

		@Override
		public void speakTo(Object user) {
			// this method will be override in the controller
		}
	};
}
