package xz42_bb26.client.view;

/**
 * Adapter that the view uses to communicate to the model.
 * @author bb26, xc7
 */
public interface IView2ModelAdapter<Room, User, ChatUser> {

	/**
	 * Quits the applications and gracefully shuts down the RMI-related resources.
	 */
	public void quit();

	/**
	 * Connect to the RMI Registry at the given remote host, and grab the 
	 * stub from that registry. Then create a local chatroom with the remote 
	 * user represented by the given IP address, and inform the remote user 
	 * to create a local chatroom
	 * 
	 * @param ip The remote IP address to connect to.
	 */
	public void chatWith(String ip);

	/**
	 * Upon given a chatroom to join, create a local chatroom and add existing 
	 * users in the given chatroom into the local chatroom. Then broadcast 
	 * to users in remote chatroom to add my stub into their local chatrooms.
	 * 
	 * @param rm the chatroom to join
	 */
	public void joinChatroom(Room rm);

	/**
	 * Get list of rooms the remote user is involved with
	 * 
	 * @param ip The remote user's IP address
	 * @return A set of Room
	 */
	public void getListRooms(String ip);

	@SuppressWarnings("rawtypes")
	public static final IView2ModelAdapter NULL_OBJECT = new IView2ModelAdapter() {
		@Override
		public void quit() {
			// this method will be override in the controller
		}

		@Override
		public void chatWith(String text) {
			// this method will be override in the controller
		}

		@Override
		public void getListRooms(String ip) {
			// this method will be override in the controller
		}

		@Override
		public void joinChatroom(Object rm) {
			// this method will be override in the controller
		}
	};
}
