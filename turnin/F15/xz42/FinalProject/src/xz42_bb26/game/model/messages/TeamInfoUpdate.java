package xz42_bb26.game.model.messages;

import common.message.ARequest;
import common.message.IChatMessage;
import provided.datapacket.DataPacket;
/**
 * This is a message that broadcast the team infomation to all users
 * @author xz42
 *
 */
import xz42_bb26.game.model.Team;
public class TeamInfoUpdate extends ARequest implements IChatMessage {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4402431958967360485L;
	
	private Team theTeam;
	
	public TeamInfoUpdate(Team _theTeam) {
		this.theTeam = _theTeam;
	}
	
	public Team getTeam(){
		return theTeam;
	}

	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<TeamInfoUpdate>(TeamInfoUpdate.class, this);
	}

}
