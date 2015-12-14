/**
 * 
 */
package xz42_bb26.server.model.user;

import java.rmi.Remote;

import common.IChatUser;

/**
 * An interface for game player
 * @author bb26
 *
 */
public interface IGameUser extends Remote {

	/**
	 * Return player's chat user stub 
	 * @return player's chat user stub
	 */
	public IChatUser getChatUser();

}
