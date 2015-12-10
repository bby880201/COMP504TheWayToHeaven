package xz42_bb26.game.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import javax.swing.Timer;


import common.IChatUser;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Box;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.PatternFactory;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.UserFacingIcon;
import gov.nasa.worldwindx.applications.sar.actions.DeletePositionsAction;
import javafx.geometry.Pos;
import provided.datapacket.DataPacket;
import xz42_bb26.game.controller.IViewAdapter;
import xz42_bb26.game.model.messages.ProvideGameUser;
import xz42_bb26.game.model.messages.TeamComsumeDepot;
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
	 * Image for pulsing icon.
	 */
	private BufferedImage circleYellow = createBitmap(PatternFactory.PATTERN_CIRCLE, Color.YELLOW);
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
	
	public HashMap<Position,Depot> depots= new HashMap<>();
	
	private boolean inGame;
	
	private HashMap<UUID, TeamBox> boxList= new HashMap<UUID, TeamBox>();
	
	private TeamBox myBox;
	
	PulsingIcon desIcon;
	
	private HashMap<Position, PulsingIcon> depotsIcons;
	/**
	 * Constructor of the game model.
	 * @param view A model to view adapter.
	 */
	public GameModel(IViewAdapter iViewAdapter,UUID teamUUID, IChatUser server, String _teamName, boolean _isNavigator) {
		view = iViewAdapter;
		this.server =server;
		this.inGame = false;
		team = new Team();
		team.setModel(this);
		team.uuid = teamUUID;
		team.supply = 2000000000;
		team.isNavigator = _isNavigator;
		team.name = _teamName;
		team.cash = 10000000;
		if(team.isNavigator){
			userName = _teamName + "_Navigator";
		}
		else{
			userName = _teamName + "_ResourceMaster";
		}

		
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
	
	
	public void initBoxes(){
		
		//Rice 29 -95
		//
		myBox = makeTeamBox(UUID.randomUUID(), 
				Angle.fromDegrees(63), 
				Angle.fromDegrees(-151), 
				Material.GREEN, "Your team");
		view.getBoxLayer().addRenderable(myBox);
		for (Team team : teams.values()) {
			boxList.put(team.uuid, makeTeamBox(team.uuid,Angle.fromDegrees(63), 
					Angle.fromDegrees(-151), 
					Material.BLUE, team.name));
		}
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
	
	public TeamBox makeTeamBox(UUID uuid, Angle lat, Angle lon, Material color, String teamName) {

		ShapeAttributes attrs = new BasicShapeAttributes();
		attrs.setDrawOutline(false);
		attrs.setInteriorMaterial(color);
		attrs.setEnableLighting(true);		

		TeamBox box4 = new TeamBox(Position.fromDegrees(lat.degrees, lon.degrees, 50000), 50000, 50000, 50000, null);
		box4.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
		box4.setAttributes(attrs);
		box4.setVisible(true);
		box4.setValue(AVKey.DISPLAY_NAME, teamName);
		return box4;
	}
	
	public void start() throws RemoteException {
		try {
			
			globalChatroom = new Chatroom(userName);
			globalChatroom.setChatroom2ModelAdapter(new IChatroom2ModelAdapter() {

				@Override
				public void updateTeamInfo(Team team) {
					TeamBox aBox = boxList.get(team.uuid);
					if(aBox==null){
						aBox= makeTeamBox(team.uuid,Angle.fromDegrees(63), 
								Angle.fromDegrees(-151), 
								Material.BLUE, team.name);
						boxList.put(team.uuid, aBox);
					}
					team.myLocation = Position.fromDegrees(team.myLatitude, team.myLongtitude);
					aBox.move(team.myLocation.getLatitude().getDegrees(), team.myLocation.getLongitude().getDegrees());
					teams.put(team.uuid, team);
				}

				@Override
				public void gameBigin() {
					inGame = true;
					view.gameBegin();
				}

				@Override
				public void aTeamOut(UUID id) {
					if(id!=team.uuid){
						boxList.get(id).getAttributes().setInteriorMaterial(Material.BLACK);
					}
					
				}

				@Override
				public void aTeamWins(UUID id) {
					inGame = false;
					view.aTeamWins(teams.get(id));
				}

				@Override
				public void setDepots(Set<Depot> _depots) {
					for (Depot depot : _depots) {
						depot.position = Position.fromDegrees(depot.latitude, depot.longitude);
						depots.put(depot.position, depot);
					}
					initBoxes();
					renderDepots();
				}

				@Override
				public Team getTeam() {
					return team;
				}

				@Override
				public void teamConsume(Position getaDepot) {
					depots.remove(getaDepot);
					depotsIcons.get(getaDepot).stop();
					depotsIcons.get(getaDepot).setVisible(false);
					
				}


			});
			ProvideGameUser provideGameUser = new ProvideGameUser(globalChatroom.getMe());
			server.receive(globalChatroom.getMe(), provideGameUser.getDataPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void renderDepots() {
		depotsIcons = new HashMap<>();
		for (Depot depot : depots.values()) {
			PulsingIcon icon = new PulsingIcon(circleYellow, depot.position, 100);
			icon.setSize(new Dimension(20, 20));
			icon.setToolTipText(depot.price.toString());
			icon.setVisible(true);
			depotsIcons.put(icon.getPosition(), icon);
			view.getIconLayer().addIcon(icon);
		}

		BufferedImage circleRed = createBitmap(PatternFactory.PATTERN_CIRCLE, Color.RED);
		desIcon = new PulsingIcon(circleRed, Position.fromDegrees(29, -95), 20);
		desIcon.setVisible(false);
		desIcon.setSize(new Dimension(10,10));
		desIcon.setAlwaysOnTop(true);
		view.getIconLayer().setRegionCulling(false);
		view.getIconLayer().addIcon(desIcon);
		

		
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
		public void stop(){
			timer.stop();
		}
		
	}

	
	/**
	 * Pulsing icon represents alarms.
	 * @author xz42
	 *
	 */
	private class PulsingIcon extends UserFacingIcon{
		/**
		 * Icon path
		 */
		protected final Object bgIconPath;
		/**
		 * Scale factor of the icon image.
		 */
		protected int scaleIndex = 0;
		/**
		 * Scale steps.
		 */
		protected double[] scales = new double[] {1.25, 1.5, 1.75, 2, 2.25, 2.5, 2.75, 3, 3.25, 3.5, 3.25, 3,
				2.75, 2.5, 2.25, 2, 1.75, 1.5};
		/**
		 * Timer for pulsing.
		 */
		protected Timer timer;

		/**
		 * Constructor of PulsingIcon.
		 * @param imageSource Source of image.
		 * @param pos Position of the icon.
		 * @param frequency Pulsing frequency.
		 * @param uuid UUID of the icon.
		 */
		private PulsingIcon(Object imageSource, Position pos, int frequency){

			super(imageSource, pos);
			this.bgIconPath = imageSource;
	
			if (timer == null) {
				timer = new Timer(frequency, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PulsingIcon.this.setBackgroundScale(scales[++scaleIndex % scales.length]);
						view.update();
							if (Position.greatCircleDistance(myBox.getCenterPosition(), PulsingIcon.this.getPosition()).degrees<0.01){
								timer.stop();
								team.buySupply(depots.get(PulsingIcon.this.getPosition()));
								globalChatroom.send(globalChatroom.getMe(), new TeamComsumeDepot(PulsingIcon.this.getPosition()));
								depots.remove(depots.get(PulsingIcon.this.getPosition()));
								//TO DO send message to tell everyone the depot is comsumed
								PulsingIcon.this.setVisible(false);
						}
					}
				});
			}
			this.setBackgroundImage(bgIconPath);
			scaleIndex = 0;

			timer.start();
		}

		/**
		 * Star the pulsing timer.
		 */
		private void starTimer(){
			timer.start();
		}
		public void stop(){
			timer.stop();
		}
	}
	
	/**
	 * Create a blurred pattern bitmap for PulsingIcon
	 * @param pattern Pattern of the image.
	 * @param color Color of the image.
	 * @return A buffered image.
	 */
	private BufferedImage createBitmap(String pattern, Color color){
		// Create bitmap with pattern
		BufferedImage image = PatternFactory.createPattern(pattern, new Dimension(128, 128), 0.7f,
				color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
		// Blur a lot to get a fuzzy edge
		image = PatternFactory.blur(image, 13);
		image = PatternFactory.blur(image, 13);
		image = PatternFactory.blur(image, 13);
		image = PatternFactory.blur(image, 13);
		return image;
	}
	public void sendGameOver() {
		TeamOut aTeamOutMessage = new TeamOut(team.uuid);
		globalChatroom.send(globalChatroom.getMe(), aTeamOutMessage);
		this.inGame = false;
		view.gameOver();
	}

	public void buySupply(Depot depot) {
		team.buySupply(depot);
		
	}
	


}
