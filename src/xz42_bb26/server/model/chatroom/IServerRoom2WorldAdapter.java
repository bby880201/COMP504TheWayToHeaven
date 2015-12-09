package xz42_bb26.server.model.chatroom;

import java.awt.Component;
import java.awt.Container;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

import javax.swing.JFrame;

import common.IInitUser;

/**
 * Mini-Model2World adapter that enables the mini-model (chat room) to communicate 
 * to the local system.
 * 
 * @author xc7, bb26 
 * @param <T> The type of the data being held.
 */
public interface IServerRoom2WorldAdapter<T, TmRm> {
	
	/**
	 * Return the initUser from model
	 * @return init client model's initUser 
	 */
	public IInitUser getInitUser();
	
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
	 * @param collection the list of users to show on chatroom member list panel
	 */
	void refreshList(Collection<T> collection);

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
	public final static IServerRoom2WorldAdapter NULL_OBJECT = new IServerRoom2WorldAdapter() {

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
		public Container Scrollable() {
			// this method will be override in the controller
			return null;
		}

		@Override
		public void display(Supplier containerSupplier) {
			// this method will be override in the controller			
		}

		@Override
		public IInitUser getInitUser() {
			// this method will be override in the controller
			return null;
		}

		@Override
		public String getName() {
			// this method will be override in the controller
			return null;
		}

		@Override
		public String getIp() {
			// this method will be override in the controller
			return null;
		}

		@Override
		public void refreshList(Collection collection) {
			// this method will be override in the controller			
		}

		@Override
		public void speakTo(String ip) {
			// this method will be override in the controller			
		}

		@Override
		public void popUp(Supplier frameFac) {
			// TODO Auto-generated method stub
		}

		@Override
		public void refreshTeam(Collection teamList) {
			// TODO Auto-generated method stub
			
		}

	};

	public void display(Supplier<Component> containerSupplier);

	public String getName();

	public String getIp();

	public void speakTo(String ip);

	public void popUp(Supplier<JFrame> frameFac);

	public void refreshTeam(Collection<TmRm> collection);

}
