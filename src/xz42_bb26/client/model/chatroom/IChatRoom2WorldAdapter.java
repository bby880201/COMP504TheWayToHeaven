package xz42_bb26.client.model.chatroom;

import java.awt.Container;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Mini-Model2World adapter that enables the mini-model (chat room) to communicate 
 * to the local system.
 * 
 * @author xc7, bb26 
 * @param <T> The type of the data being held.
 */
public interface IChatRoom2WorldAdapter<T> {

	/**
	 * Add the data to the specific chatroom's chat window.
	 * @param data the data to be added to GUI panel
	 */
	public void append(String data);

	/**
	 * Delete the certain chat window from the main GUI panel
	 */
	public void deleteWindow();

	/**
	 * Delete the chatroom associated with the specific id from local system
	 * @param id the id of a specific chatroom
	 */
	public void deleteModel(UUID id);

	/**
	 * Refresh the member list on the chatroom 
	 * @param users the list of users to show on chatroom member list panel
	 */
	void refreshList(Set<T> users);

	/**
	 * Give a Container (e.g. JPanel) as the accessible part of the local system 
	 * to the unknown command
	 * @return a container that is on the chatroom window's GUI 
	 */
	public Container Scrollable();

	@SuppressWarnings("rawtypes")
	/**
	 * No-op "null" adapter
	 */
	public final static IChatRoom2WorldAdapter NULL_OBJECT = new IChatRoom2WorldAdapter() {

		@Override
		public void append(String data) {
			// this method will be override in the controller
		}

		@Override
		public void deleteWindow() {
			// this method will be override in the controller
		}

		@Override
		public void deleteModel(UUID id) {
			// this method will be override in the controller
		}

		@Override
		public void refreshList(Set users) {
			// this method will be override in the controller
		}

		@Override
		public Container Scrollable() {
			// this method will be override in the controller
			return null;
		}

		@Override
		public void display(Supplier containerSupplier) {
			// TODO Auto-generated method stub
			
		}
	};

	public void display(Supplier<Container> containerSupplier);
}
