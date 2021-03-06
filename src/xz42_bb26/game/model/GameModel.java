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
import common.demo.message.chat.RemoveMe;
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
import xz42_bb26.game.controller.IViewAdapter;
import xz42_bb26.game.model.messages.ProvideGameUser;
import xz42_bb26.game.model.messages.TeamComsumeDepot;
import xz42_bb26.game.model.messages.TeamOut;
import xz42_bb26.game.model.messages.TeamWins;

/**
 * This is the main model of the game
 * @author xz42, bb26
 *
 */
public class GameModel {

	/**
	 * World wind model, used to get elevations. 
	 */
	private Model worldModel = (Model) WorldWind
			.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);

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
	private BufferedImage circleYellow = createBitmap(
			PatternFactory.PATTERN_CIRCLE, Color.YELLOW);
	/**
	 * My team
	 */
	private Team team;
	/**
	 * Teams
	 */
	private HashMap<UUID, Team> teams = new HashMap<UUID, Team>();
	/**
	 * Current username like teamA_Navigator
	 */
	private String userName;
	/**
	 * The information of the depots
	 */
	public HashMap<UUID, Depot> depots = new HashMap<UUID, Depot>();
	/**
	 * The game status
	 */
	private boolean inGame;
	/**
	 * The list of the team boxes
	 */
	private HashMap<UUID, TeamBox> boxList = new HashMap<UUID, TeamBox>();
	/**
	 * THIS team box
	 */
	private TeamBox myBox;
	/**
	 * The point of the destination
	 */
	PulsingIcon desIcon;
	/**
	 * The points of the depots
	 */
	private HashMap<UUID, PulsingIcon> depotsIcons;

	/**
	 * Constructor of the game model.
	 * @param view A model to view adapter.
	 */
	public GameModel(IViewAdapter iViewAdapter, UUID teamUUID,
			IChatUser server, String _teamName, boolean _isNavigator) {
		view = iViewAdapter;
		this.server = server;
		this.inGame = false;
		team = new Team();
		team.setModel(this);
		team.uuid = teamUUID;
		team.supply = 10000;
		team.myLatitude = 63;
		team.myLongtitude = -151;
		team.isNavigator = _isNavigator;
		team.name = _teamName;
		team.cash = 100;

		if (team.isNavigator) {
			userName = _teamName + "_Navigator";
		} else {
			userName = _teamName + "_ResourceMaster";
		}

	}

	/**
	 * Move my box to a position
	 * @param pos
	 */
	public void moveTo(Position pos) {
		if (inGame) {

			myBox.move(pos.getLatitude().getDegrees(), pos.getLongitude()
					.getDegrees());
		}
	}

	/**
	 * Move teams' boxes according to given params.
	 * @param uuid UUID of the box.
	 * @param desLat Destination latitude.
	 * @param desLon Destination longitude.
	 * @param onBoard If the player is on board.
	 */
	public void moveBox(UUID uuid, double desLat, double desLon) {
		TeamBox box = boxList.get(uuid);
		box.move(desLat, desLon);
	}

	/**
	 * Init the boxes to the view
	 */
	public void initBoxes() {

		//Rice 29 -95
		//
		System.out.println("initBoxes");
		myBox = makeTeamBox(UUID.randomUUID(), Angle.fromDegrees(63),
				Angle.fromDegrees(-151), Material.GREEN, "Your team");
		view.getBoxLayer().addRenderable(myBox);
		for (Team team : teams.values()) {
			TeamBox newBox = makeTeamBox(team.uuid, Angle.fromDegrees(63),
					Angle.fromDegrees(-151), Material.BLUE, team.name);
			boxList.put(team.uuid, newBox);
			view.getBoxLayer().addRenderable(newBox);
		}
	}

	/**
	 * Random integer generator.
	 * @param min Minimum
	 * @param max Maximum
	 * @return A random integer in the range between min and max.
	 */
	public int randomInt(int min, int max) {
		return (int) Math.floor((Math.random() * (1 + max - min)) + min);
	}

	/**
	 * Generate the boxes
	 * @param uuid the id of the team
	 * @param lat latitude
	 * @param lon longitude
	 * @param color color of the box
	 * @param teamName team name
	 * @return a generated team box
	 */
	public TeamBox makeTeamBox(UUID uuid, Angle lat, Angle lon, Material color,
			String teamName) {

		ShapeAttributes attrs = new BasicShapeAttributes();
		attrs.setDrawOutline(false);
		attrs.setInteriorMaterial(color);
		attrs.setEnableLighting(true);

		TeamBox box4 = new TeamBox(Position.fromDegrees(lat.degrees,
				lon.degrees, 50000), 50000, 50000, 50000, null);
		box4.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
		box4.setAttributes(attrs);
		box4.setVisible(true);
		box4.setValue(AVKey.DISPLAY_NAME, teamName);
		return box4;
	}

	/**
	 * Model start
	 * @throws RemoteException remote excepotions
	 */
	public void start() throws RemoteException {
		try {

			globalChatroom = new Chatroom(userName);
			globalChatroom
					.setChatroom2ModelAdapter(new IChatroom2ModelAdapter() {

						@Override
						public void updateTeamInfo(Team _team) {
							if (_team.uuid.equals(team.uuid)) {
								if (!isNavigator()) {
									System.out.println("your team is moving"
											+ _team.toString());
									myBox.move(_team.myLatitude,
											_team.myLongtitude);
									_team.myLocation = Position.fromDegrees(
											_team.myLatitude,
											_team.myLongtitude);
									team.myLatitude = _team.myLatitude;
									team.myLongtitude = _team.myLongtitude;
									team.cash = _team.cash;
									team.supply = _team.supply;
								}

							} else {
								System.out.println("other team is moving"
										+ _team.toString());
								System.out.println(team.toString());
								TeamBox aBox = boxList.get(_team.uuid);
								if (aBox == null) {
									aBox = makeTeamBox(
											_team.uuid,
											Angle.fromDegrees(_team.myLatitude),
											Angle.fromDegrees(_team.myLongtitude),
											Material.BLUE, _team.name);
									view.getBoxLayer().addRenderable(aBox);
									boxList.put(_team.uuid, aBox);
								}
								teams.put(_team.uuid, _team);
								_team.myLocation = Position.fromDegrees(
										_team.myLatitude, _team.myLongtitude);
								_team.setModel(GameModel.this);
								aBox.move(_team.myLatitude, _team.myLongtitude);
								teams.put(_team.uuid, _team);
							}

						}

						@Override
						public void gameBigin() {
							inGame = true;
							view.gameBegin();
						}

						@Override
						public void aTeamOut(UUID id) {
							if (id != team.uuid) {
								if (boxList.get(id) != null) {
									boxList.get(id)
											.getAttributes()
											.setInteriorMaterial(Material.BLACK);
									boxList.get(id).stop();
								}

							} else {
								inGame = false;
								view.gameOver();
								myBox.stop();
							}

						}

						@Override
						public void aTeamWins(UUID id) {
							inGame = false;
							if (id.equals(team.uuid)) {
								myBox.stop();
								view.aTeamWins(team);
							} else {
								for (TeamBox aBox : boxList.values()) {
									aBox.stop();
								}
								view.aTeamWins(teams.get(id));
							}

						}

						@Override
						public void setDepots(Set<Depot> _depots) {
							for (Depot depot : _depots) {
								depot.position = Position.fromDegrees(
										depot.latitude, depot.longitude);
								depots.put(depot.uuid, depot);
							}
							if (myBox == null) {
								initBoxes();
							}
							renderDepots();
						}

						@Override
						public Team getTeam() {
							return team;
						}

						@Override
						public void teamConsume(UUID id) {
							depots.remove(id);
							depotsIcons.get(id).stop();
							depotsIcons.get(id).setVisible(false);
						}

					});
			ProvideGameUser provideGameUser = new ProvideGameUser(
					globalChatroom.getMe());
			server.receive(globalChatroom.getMe(),
					provideGameUser.getDataPacket());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * render the depots points to the view
	 */
	private void renderDepots() {
		depotsIcons = new HashMap<UUID, PulsingIcon>();
		for (Depot depot : depots.values()) {
			PulsingIcon icon = new PulsingIcon(circleYellow, depot.uuid,
					depot.position, 500);
			icon.setSize(new Dimension(20, 20));
			icon.setToolTipText(depot.price.toString());
			if (!isNavigator()) {
				icon.setVisible(true);
			} else {
				icon.setVisible(false);
			}
			depotsIcons.put(depot.uuid, icon);
			view.getIconLayer().addIcon(icon);
		}

		BufferedImage circleRed = createBitmap(PatternFactory.PATTERN_CIRCLE,
				Color.RED);
		desIcon = new PulsingIcon(circleRed, UUID.randomUUID(),
				Position.fromDegrees(29.71724, -95.40150), 500);
		desIcon.setSize(new Dimension(20, 20));
		if (!isNavigator()) {
			desIcon.setVisible(true);
		} else {
			desIcon.setVisible(false);
		}
		desIcon.setAlwaysOnTop(true);
		view.getIconLayer().addIcon(desIcon);

	}

	/**
	 * Get the job of the player
	 * @return is navigator = true; is manager = false
	 */
	public boolean isNavigator() {
		return team.isNavigator;
	}

	/**
	 * The team box class extend the box in the map apis
	 * @author xz42
	 *
	 */
	private class TeamBox extends Box {
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
		private TeamBox(Position centerPosition, double northSouthRadius,
				double verticalRadius, double eastWestRadius, double[] limit) {
			super(centerPosition, northSouthRadius, verticalRadius,
					eastWestRadius);
			this.oriPos = this.getCenterPosition();
			this.curPos = this.getCenterPosition();
			this.desPos = this.getCenterPosition();
			this.ang = Position.greatCircleDistance(oriPos, desPos);

			if (timer == null) {
				timer = new Timer(500, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//calculate speed: 1.0/5 Nautical mile/200 msec

						if (!inGame)
							return;

						double speed = 0.5 / 2 / ang.degrees;
						n = n + speed;
						curPos = Position.interpolateGreatCircle(n, oriPos,
								desPos);
						prevPos = Position.interpolateGreatCircle(n - speed,
								oriPos, desPos);
						TeamBox.this.setHeading(LatLon.greatCircleAzimuth(
								oriPos, desPos));
						if (worldModel
								.getGlobe()
								.getElevationModel()
								.getElevation(curPos.getLatitude(),
										curPos.getLongitude()) > 5) {
							TeamBox.this.moveTo(curPos);
							if (TeamBox.this.equals(myBox)) {
								team.moveTo(curPos);
								if (isNavigator()) {
									globalChatroom.IMove(team);
								}
							}
							oriPos = curPos;

						} else {
							n = 2;
							desPos = prevPos;
							curPos = prevPos;
						}
						view.update(team);
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
		protected void move(double desLat, double desLon) {
			n = 0;
			desPos = Position.fromDegrees(desLat, desLon, 50000);
			ang = Position.greatCircleDistance(oriPos, desPos);
		}

		/**
		 * Stop the timer.
		 */
		public void stop() {
			timer.stop();
		}

	}

	/**
	 * Pulsing icons for depots and destination.
	 * @author xz42
	 *
	 */
	private class PulsingIcon extends UserFacingIcon {
		/**
		 * Icon path
		 */
		protected final Object bgIconPath;
		/**
		 * Scale factor of the icon image.
		 */
		@SuppressWarnings("unused")
		protected int scaleIndex = 0;
		/**
		 * Scale steps. which is not used for bad perforomance
		 */
		@SuppressWarnings("unused")
		protected double[] scales = new double[] { 1.25, 1.5, 1.75, 2, 2.25,
				2.5, 2.75, 3, 3.25, 3.5, 3.25, 3, 2.75, 2.5, 2.25, 2, 1.75, 1.5 };
		/**
		 * Timer for pulsing.
		 */
		protected Timer timer;

		private UUID uuid;

		/**
		 * Constructor of PulsingIcon.
		 * @param imageSource Source of image.
		 * @param pos Position of the icon.
		 * @param frequency Pulsing frequency.
		 * @param uuid UUID of the icon.
		 */
		private PulsingIcon(Object imageSource, UUID id, Position pos,
				int frequency) {

			super(imageSource, pos);
			this.bgIconPath = imageSource;
			this.uuid = id;

			if (timer == null) {
				timer = new Timer(frequency, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//						PulsingIcon.this.setBackgroundScale(scales[++scaleIndex % scales.length]);
						if (Position.greatCircleDistance(
								myBox.getCenterPosition(),
								PulsingIcon.this.getPosition()).degrees < 0.8) {
							System.out.println("getIntoRangeOfAIcon");
							timer.stop();
							if (PulsingIcon.this.uuid.equals(desIcon.uuid)) {
								System.out.println("getIntoRangeOfDestination");
								globalChatroom.send(globalChatroom.getMe(),
										new TeamWins(team.uuid));
								view.aTeamWins(team);
							} else {
								team.buySupply(depots
										.get(PulsingIcon.this.uuid));
								globalChatroom.send(globalChatroom.getMe(),
										new TeamComsumeDepot(uuid));
								depots.remove(depots.get(PulsingIcon.this
										.getPosition()));
								PulsingIcon.this.setVisible(false);
								view.update(team);
							}
						} else {
							if (this.equals(desIcon)) {
								System.out.println("distance to the destination"
										+ Position.greatCircleDistance(
												myBox.getCenterPosition(),
												desIcon.getPosition()).degrees);
							}

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
		@SuppressWarnings("unused")
		private void starTimer() {
			timer.start();
		}

		public void stop() {
			timer.stop();
		}
	}

	/**
	 * Create a blurred pattern bitmap for PulsingIcon
	 * @param pattern Pattern of the image.
	 * @param color Color of the image.
	 * @return A buffered image.
	 */
	private BufferedImage createBitmap(String pattern, Color color) {
		// Create bitmap with pattern
		BufferedImage image = PatternFactory.createPattern(pattern,
				new Dimension(128, 128), 0.7f, color, new Color(color.getRed(),
						color.getGreen(), color.getBlue(), 0));
		// Blur a lot to get a fuzzy edge
		image = PatternFactory.blur(image, 13);
		image = PatternFactory.blur(image, 13);
		image = PatternFactory.blur(image, 13);
		image = PatternFactory.blur(image, 13);
		return image;
	}

	/**
	 * Tell someone game is over
	 */
	public void sendGameOver() {
		TeamOut aTeamOutMessage = new TeamOut(team.uuid);
		globalChatroom.send(globalChatroom.getMe(), aTeamOutMessage);
		this.inGame = false;
		view.gameOver();
	}

	/**
	 * Calculate the cash and supply after get to a depot
	 * @param depot
	 */
	public void buySupply(Depot depot) {
		team.buySupply(depot);

	}

	/**
	 * Getter of my team
	 * @return my team
	 */
	public Team getTeam() {
		return team;
	}

	/**
	 * Tell everyone I leave the game
	 */
	public void quit() {
		globalChatroom.send(globalChatroom.getMe(),
				new RemoveMe(globalChatroom.getMe()));
	}

}
