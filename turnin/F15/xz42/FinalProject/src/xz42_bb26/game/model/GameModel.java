package xz42_bb26.game.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.Timer;

import common.IChatUser;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Box;
import xz42_bb26.game.controller.IViewAdapter;

public class GameModel {
	/**
	 * Model to view adapter.
	 */
	/**
	 * World wind model, used to get elevations. 
	 */
	private Model worldModel = (Model)WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
	
	private IViewAdapter view;
	
	private Chatroom globalChatroom;
	
	private HashMap<IChatUser,Position> teams;
	
	private Team team;
	
	
	/**
	 * Constructor of the game model.
	 * @param view A model to view adapter.
	 */
	public GameModel(IViewAdapter iViewAdapter) {
		view = iViewAdapter;
	}
	
	public void updateStatus(){
		
	}
	
	public void moveTo(Position pos){
		team.moveTo(pos);
		
	}

	public void start() {
		
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
						double speed = 1.0/2/ang.degrees;
						n=n+speed;
						curPos = Position.interpolateGreatCircle(n, oriPos, desPos);
						prevPos = Position.interpolateGreatCircle(n-speed, oriPos, desPos);
						TeamBox.this.setHeading(LatLon.greatCircleAzimuth(oriPos, desPos));
						if (worldModel.getGlobe().getElevationModel().getElevation(curPos.getLatitude(), curPos.getLongitude())>5) {
							TeamBox.this.moveTo(curPos);

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
		protected void move(double oriLat, double oriLon, double desLat, double desLon){
			n = 0;
			oriPos = Position.fromDegrees(oriLat, oriLon, 50000);
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
	


}
