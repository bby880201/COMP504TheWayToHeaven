package xz42_bb26.game.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Box;
import map.MapPanel;
import map.IRightClickAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GameView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2733797230967669361L;
	private JPanel contentPane;
	
	private RenderableLayer sLayer = new RenderableLayer();

	MapPanel mapPanel;
	IModelAdapter model;
	/**
	 * Start the game view.
	 */
	public void start(){
		this.setVisible(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mapPanel.start();
				mapPanel.addLayer(sLayer);
			    setVisible(true);
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GameView(IModelAdapter iModelAdapter) {
		this.model = iModelAdapter;
		initGUI();
	}
	
	private void initGUI(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		
		mapPanel = new MapPanel();
		mapPanel.setPreferredSize(new java.awt.Dimension(600, 400));
		panel.add(mapPanel, BorderLayout.CENTER);
		
		mapPanel.addRightClickAction(new IRightClickAction(){

			@Override
			public void apply(Position p) {
				System.out.println(p);
			}});
		
		
		
		JPanel infoPanel = new JPanel();
		contentPane.add(infoPanel, BorderLayout.EAST);
		
		GridBagLayout gbl_infoPanel = new GridBagLayout();
		gbl_infoPanel.columnWidths = new int[]{145, 0};
		gbl_infoPanel.rowHeights = new int[]{29, 0, 0, 0};
		gbl_infoPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_infoPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		infoPanel.setLayout(gbl_infoPanel);
		JButton btnBeTheNavigator = new JButton("Be the navigator");
		GridBagConstraints gbc_btnBeTheNavigator = new GridBagConstraints();
		gbc_btnBeTheNavigator.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnBeTheNavigator.insets = new Insets(0, 0, 5, 0);
		gbc_btnBeTheNavigator.gridx = 0;
		gbc_btnBeTheNavigator.gridy = 0;
		infoPanel.add(btnBeTheNavigator, gbc_btnBeTheNavigator);
//		if(model.isNavigator()){
//			
//		}
//		else{
//			JLabel cashLabel = new JLabel("Current Cash: $100");
//			GridBagConstraints gbc_cashLabel = new GridBagConstraints();
//			gbc_cashLabel.insets = new Insets(0, 0, 5, 0);
//			gbc_cashLabel.anchor = GridBagConstraints.WEST;
//			gbc_cashLabel.gridx = 0;
//			gbc_cashLabel.gridy = 1;
//			infoPanel.add(cashLabel, gbc_cashLabel);
//			
//			JLabel supplyLabel = new JLabel("Current Supply: 100miles");
//			GridBagConstraints gbc_supplyLabel = new GridBagConstraints();
//			gbc_supplyLabel.gridx = 0;
//			gbc_supplyLabel.gridy = 2;
//			infoPanel.add(supplyLabel, gbc_supplyLabel);
//		}
		
		/**
		 * Update the layer.
		 */

		
		
	}
	public void update(){
		sLayer.firePropertyChange(AVKey.LAYER, null, null);
	}

}
