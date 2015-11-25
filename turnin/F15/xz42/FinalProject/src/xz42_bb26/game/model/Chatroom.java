package xz42_bb26.game.model;

import java.util.Set;
import java.util.UUID;

import common.IChatUser;
import common.IChatroom;
import common.message.IChatMessage;

public class Chatroom implements IChatroom {

	@Override
	public UUID getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<IChatUser> getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addUser(IChatUser user) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void send(IChatUser sender, IChatMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean removeUser(IChatUser user) {
		// TODO Auto-generated method stub
		return false;
	}

}
