package xz42_bb26.game.model.messages;

import common.message.ARequest;
import common.message.IChatMessage;
import provided.datapacket.DataPacket;
/**
 * This is a message that broadcast the team infomation to all users
 * @author xz42, bb26
 *
 */
import xz42_bb26.game.model.Team;
public class TeamInfoUpdate extends ARequest implements IChatMessage {

	
	/**
	 * Generated id
	 */
	private static final long serialVersionUID = 4402431958967360485L;
	/**
	 * The team info to be broadcasted
	 */
	private Team theTeam;
	
	/**
	 * The consturctor
	 * @param _theTeam The team info to be broadcasted
	 */
	public TeamInfoUpdate(Team _theTeam) {
		this.theTeam = _theTeam;
	}
	
	/**
	 * Getter of the team
	 * @return the team
	 */
	public Team getTeam(){
		return theTeam;
	}

	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<TeamInfoUpdate>(TeamInfoUpdate.class, this);
	}

}
