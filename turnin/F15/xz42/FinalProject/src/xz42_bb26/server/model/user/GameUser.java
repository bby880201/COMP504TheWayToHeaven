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
public class GameUser implements IGameUser,Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6220233930285348477L;

	private final boolean isNavig;
	
	private final IChatUser stub;
	
	private final String name;
	
	public GameUser(ChatUserEntity memb, boolean isNav) {
		isNavig = isNav;
		stub = memb.getChatUser();
		name = memb.getName();
	}

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
