package xz42_bb26.game.model.messages;

import java.util.UUID;

import common.message.ARequest;
import common.message.IChatMessage;
import gov.nasa.worldwind.geom.Position;
import javafx.geometry.Pos;
import provided.datapacket.DataPacket;

public class TeamComsumeDepot extends ARequest implements IChatMessage {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6625931077082454728L;
	/**
	 * 
	 */
	private Position aDepot;

	public TeamComsumeDepot(Position aPos) {
		this.setaDepot(aPos);
	}

	@Override
	public DataPacket<? extends IChatMessage> getDataPacket() {
		return new DataPacket<TeamComsumeDepot>(TeamComsumeDepot.class, this);
	}

	/**
	 * @return the aDepot
	 */
	public Position getaDepot() {
		return aDepot;
	}

	/**
	 * @param aDepot the aDepot to set
	 */
	public void setaDepot(Position aDepot) {
		this.aDepot = aDepot;
	}


}