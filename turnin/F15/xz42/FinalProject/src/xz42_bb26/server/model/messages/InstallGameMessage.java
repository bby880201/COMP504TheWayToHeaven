package xz42_bb26.server.model.messages;

import java.util.UUID;

import common.message.IChatMessage;
import provided.datapacket.DataPacket;

public class InstallGameMessage implements IChatMessage{

	/**
	 * Auto-generated UID. 
	 */
	private static final long serialVersionUID = -7408699688711641243L;
	/**
	 * ID of this message. 
	 */
	private final UUID teamID;
	
	/**
	 * Represent if this player is navigator
	 */
	private final boolean isNavigator;
	
	/**
	 * Team name
	 */
	private final String teamName;

	/**
	 * Constructs a new message containing the specified user to be removed. 
	 * UUID for the message is auto-generated.
	 * @param user - user to be removed
	 */
	public InstallGameMessage(UUID uuid,String teamname,Boolean isNavigator) {
		this.teamID = uuid;
		this.isNavigator = isNavigator;
		this.teamName = teamname;
	}


	/**
	 * Return UUID
	 * @return teamID team's UUID
	 */
	public UUID getID() {
		return teamID;
	}

	/**
	 * Return data packet of this message
	 * @return data packet of this message
	 */
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<InstallGameMessage>(InstallGameMessage.class, this);
	}


	/**
	 * @return the isNavigator
	 */
	public boolean isNavigator() {
		return isNavigator;
	}


	/**
	 * @return the teamName
	 */
	public String getTeamName() {
		return teamName;
	}

}