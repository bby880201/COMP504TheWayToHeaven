/**
 * 
 */
package xz42_bb26.server.model.user;

import java.rmi.Remote;

import common.IChatUser;

/**
 * @author bb26
 *
 */
public interface IGameUser extends Remote {

	public IChatUser getChatUser();

}
