package xz42_bb26.game.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.UUID;

import javax.swing.Timer;

import com.sun.javafx.collections.MappingChange.Map;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import common.IChatUser;
import common.IChatroom;
import common.message.IChatMessage;
import common.message.chat.AAddMe;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Box;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import provided.datapacket.DataPacket;
import xz42_bb26.game.controller.IViewAdapter;
import xz42_bb26.game.model.messages.ProvideGameUser;
import xz42_bb26.game.model.messages.TeamOut;

public class GameModel {
	/**
	 * Model to view adapter.
	 */
	/**
	 * World wind model, used to get elevations. 
	 */
	private Model worldModel = (Model)WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
	
	/**
	 * View
	 */
	private IViewAdapter view;
	
	/**
	 * The global chatroom to send message to
	 */
	private Chatroom globalChatroom;
	
	/**
	 * The global chatroom infomation get from server
	 */
	private IChatUser server;
	

	/**
	 * My team
	 */
	private Team team;
	/**
	 * Teams
	 */
	private HashMap<UUID, Team> teams= new HashMap<UUID, Team>();
	/**
	 * Current username like teamA_Navigator
	 */
	private String userName;
	
	
	private boolean inGame;
	
	private HashMap<UUID, TeamBox> boxList= new HashMap<UUID, TeamBox>();
	
	private TeamBox myBox;
	/**
	 * Constructor of the game model.
	 * @param view A model to view adapter.
	 */
	public GameModel(IViewAdapter iViewAdapter,UUID teamUUID, IChatUser server, String _teamName, boolean _isNavigator) {
		view = iViewAdapter;
		this.server =server;
		this.inGame = false;
		team = new Team(this);
		team.uuid = teamUUID;
		team.isNavigator = _isNavigator;
		team.name = _teamName;
		if(team.isNavigator){
			userName = _teamName + "_Navigator";
		}
		else{
			userName = _teamName + "_ResourceMaster";
		}
		initMyBox();
		
	}
	
	public void updateStatus(){
		
	}
	
	public void moveTo(Position pos){
		team.moveTo(pos);
		myBox.move(pos.getLatitude().getDegrees(), pos.getLongitude().getDegrees());
	}
	/**
	 * Move teams' boxes according to given params.
	 * @param uuid UUID of the box.
	 * @param desLat Destination latitude.
	 * @param desLon Destination longitude.
	 * @param onBoard If the player is on board.
	 */
	public void moveBox(UUID uuid, double desLat, double desLon){
		TeamBox box = boxList.get(uuid);
		box.move(desLat, desLon);
	}
	
	
	public void initMyBox(){
		myBox = makeTeamBox(UUID.randomUUID(), 
				Angle.fromDegrees(randomInt(40, 45)), 
				Angle.fromDegrees(randomInt(-94, -100)), 
				Material.GREEN);
	}
	/**
	 * Random integer generator.
	 * @param min Minimum
	 * @param max Maximum
	 * @return A random integer in the range between min and max.
	 */
	public int randomInt(int min, int max) {
		return (int)Math.floor((Math.random()*(1+max-min))+min);
	}
	
	public TeamBox makeTeamBox(UUID uuid, Angle lat, Angle lon, Material color) {

		ShapeAttributes attrs = new BasicShapeAttributes();
		attrs.setDrawOutline(false);
		attrs.setInteriorMaterial(color);
		attrs.setEnableLighting(true);		


		TeamBox box4 = new TeamBox(Position.fromDegrees(lat.degrees, lon.degrees, 50000), 50000, 50000, 50000, null);
		box4.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
		box4.setAttributes(attrs);
		box4.setVisible(true);
		box4.setValue(AVKey.DISPLAY_NAME, "YOU");
		return box4;
	}
	
	public void start() throws RemoteException {
		try {
			view.getBoxLayer().addRenderable(myBox);
			globalChatroom = new Chatroom(userName);
			ProvideGameUser provideGameUser = new ProvideGameUser(globalChatroom.getMe());
			server.receive(globalChatroom.getMe(), provideGameUser.getDataPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public boolean isNavigator() {
		return team.isNavigator;
	}
	
	private class TeamBox extends Box{
		/**
		 * Location interpolated point.
		 */
		private double n = 0;
		/**
		 * Original position.
		 */
		private Position oriPos;
		/**
		 * Current position.
		 */
		private Position curPos;
		/**
		 * Destination position.
		 */
		private Position desPos;
		/**
		 * Angle distance between two positions.
		 */
		private Angle ang;
		/**
		 * Previous position.
		 */
		private Position prevPos;
		/**
		 * Timer for moving.
		 */
		private Timer timer;
		/**
		 * Constructor of TeamBox.
		 * @param uuid UUID of the player.
		 * @param centerPosition Center position of the box.
		 * @param northSouthRadius North to south length of the box.
		 * @param verticalRadius Height of the box.
		 * @param eastWestRadius East to west length of the box.
		 * @param limit Moving limits.
		 */
		private TeamBox(Position centerPosition,
				double northSouthRadius,
				double verticalRadius,
				double eastWestRadius, double[] limit){
			super(centerPosition, northSouthRadius, verticalRadius, eastWestRadius);
			this.oriPos = this.getCenterPosition();
			this.curPos = this.getCenterPosition();
			this.desPos = this.getCenterPosition();
			this.ang = Position.greatCircleDistance(oriPos, desPos);
//			this.uuid = uuid;
//			if (limit!=null)
//				this.limit = limit;

			if (timer==null) {
				timer = new Timer(200, new ActionListener() {
					public void actionPerformed(ActionEvent e) {		
						//calculate speed: 1.0/5 Nautical mile/200 msec
						
						if(!inGame) return;
						
						double speed = 1.0/2/ang.degrees;
						n=n+speed;
						curPos = Position.interpolateGreatCircle(n, oriPos, desPos);
						prevPos = Position.interpolateGreatCircle(n-speed, oriPos, desPos);
						TeamBox.this.setHeading(LatLon.greatCircleAzimuth(oriPos, desPos));
						if (worldModel.getGlobe().getElevationModel().getElevation(curPos.getLatitude(), curPos.getLongitude())>5) {
							TeamBox.this.moveTo(curPos);
							oriPos = curPos;
							team.moveTo(curPos);

						} else {
							n=2;
							desPos = prevPos;
							curPos = prevPos;
						}
						view.update();
					}		 
				});
				timer.start();
			}
		}
		

		/**
		 * Move the TeamBox
		 * @param oriLat Original latitude.
		 * @param oriLon Original longitude.
		 * @param desLat Destination latitude.
		 * @param desLon Destination longitude.
		 */
		protected void move(double desLat, double desLon){
			n = 0;
			desPos = Position.fromDegrees(desLat, desLon, 50000);
			ang = Position.greatCircleDistance(oriPos, desPos);
		}
		
		/**
		 * Stop the timer.
		 */
		protected void stop(){
			timer.stop();
		}
		
	}

	public void sendGameOver() {
		TeamOut aTeamOutMessage = new TeamOut(team.uuid);
		globalChatroom.send(globalChatroom.getMe(), aTeamOutMessage);
		this.inGame = false;
	}

	public void buySupply(Depot depot) {
		team.buySupply(depot);
		
	}
	


}
