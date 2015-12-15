/**
 * 
 */
package xz42_bb26.server.model.user;

import java.io.Serializable;

import common.IChatUser;

/**
 * @author bb26
 *
 */
public class GameUser implements IGameUser, Serializable {

	/**
	 * declare a static final serialVersionUID of type long to fix the warning
	 */
	private static final long serialVersionUID = 6220233930285348477L;

	/**
	 * Indicating if this player is navigator
	 */
	private final boolean isNavig;

	/**
	 * Player's chat user stub
	 */
	private final IChatUser stub;

	/**
	 * Player's name
	 */
	private final String name;

	/**
	 * Constructor of this class
	 * @param memb player's information
	 * @param isNav if this player is navigator
	 */
	public GameUser(ChatUserEntity memb, boolean isNav) {
		isNavig = isNav;
		stub = memb.getChatUser();
		name = memb.getName();
	}

	/**
	 * Return player's chat user stub 
	 * @return player's chat user stub
	 */
	@Override
	public IChatUser getChatUser() {
		return stub;
	}

	/**
	 * @return the name
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * @return the isNavig
	 */
	public boolean isNavig() {
		return isNavig;
	}
}
