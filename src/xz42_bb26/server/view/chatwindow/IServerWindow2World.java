package xz42_bb26.server.view.chatwindow;

/**
 * Mini-View2Model adapter that enables the mini-view (chat window) to communicate 
 * to the model.
 * 
 * @author bb26, xz42
 */
public interface IServerWindow2World<T> {

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
	public void deleteWindow(ServerWindow cw);

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
	public static final IServerWindow2World<?> NULL_OBJECT = new IServerWindow2World<Object>() {

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
		public void deleteWindow(ServerWindow cw) {
			// this method will be override in the controller
		}

		@Override
		public void speakTo(Object user) {
			// this method will be override in the controller
		}

		@Override
		public void installGame() {
			// TODO Auto-generated method stub

		}

		@Override
		public void createTeam(Object member) {
			// TODO Auto-generated method stub

		}

		@Override
		public void rejectConnection() {
			// TODO Auto-generated method stub

		}

		@Override
		public void begin() {
			// TODO Auto-generated method stub

		}

		@Override
		public void kick(Object usr) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * Install game to the clients
	 */
	public void installGame();

	/**
	 * Create a team with a team member
	 * @param member the team member
	 */
	public void createTeam(T member);

	/**
	 * Reject connection when game started
	 */
	public void rejectConnection();

	/**
	 * Game start
	 */
	public void begin();

	/**
	 * kick a user out
	 * @param usr the user to be kicked out
	 */
	public void kick(T usr);
}
