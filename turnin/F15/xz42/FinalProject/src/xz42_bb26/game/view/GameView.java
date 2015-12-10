package xz42_bb26.game.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.layers.IconLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Box;
import map.MapPanel;
import xz42_bb26.game.model.Depot;
import xz42_bb26.game.model.Team;
import map.IRightClickAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GameView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2733797230967669361L;
	private JPanel contentPane;
	
	private RenderableLayer sLayer;
	private IconLayer iconLayer;
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
				initGUI();
				mapPanel.start();
				if(!model.isNavigator()){
					mapPanel.addLayer(iconLayer);
				}
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

	}
	
	private void initGUI(){
		iconLayer = new IconLayer();
		sLayer = new RenderableLayer();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 768);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		
		mapPanel = new MapPanel(Earth.class);
		mapPanel.setPreferredSize(new java.awt.Dimension(800, 600));
		panel.add(mapPanel, BorderLayout.CENTER);
		
		mapPanel.addRightClickAction(new IRightClickAction(){

			@Override
			public void apply(Position p) {
				model.moveTo(p);
			}});
		
		
		
		JPanel infoPanel = new JPanel();
		contentPane.add(infoPanel, BorderLayout.EAST);
		
		GridBagLayout gbl_infoPanel = new GridBagLayout();
		gbl_infoPanel.columnWidths = new int[]{145, 0};
		gbl_infoPanel.rowHeights = new int[]{29, 0, 0, 0};
		gbl_infoPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_infoPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		infoPanel.setLayout(gbl_infoPanel);
		JLabel dutyLabel = new JLabel();
		GridBagConstraints gbc_dutyLabel = new GridBagConstraints();
		gbc_dutyLabel.insets = new Insets(0, 0, 5, 0);
		gbc_dutyLabel.anchor = GridBagConstraints.WEST;
		gbc_dutyLabel.gridx = 0;
		gbc_dutyLabel.gridy = 1;
		infoPanel.add(dutyLabel, gbc_dutyLabel);
		if(model.isNavigator()){
			dutyLabel.setText("You are the navigator");
			JLabel destLabel = new JLabel("Your destination is (23.332910112240146°, -131.43584349888866°) ");
			GridBagConstraints gbc_destLabel = new GridBagConstraints();
			gbc_destLabel.gridx = 0;
			gbc_destLabel.gridy = 2;
			infoPanel.add(destLabel, gbc_destLabel);
		}
		else{

			dutyLabel.setText("You are the resource manager");
			JLabel cashLabel = new JLabel("Current Cash: $100");
			GridBagConstraints gbc_cashLabel = new GridBagConstraints();

			gbc_cashLabel.gridx = 0;
			gbc_cashLabel.gridy = 2;
			infoPanel.add(cashLabel, gbc_cashLabel);
			
			JLabel supplyLabel = new JLabel("Current Supply: 100miles");
			GridBagConstraints gbc_supplyLabel = new GridBagConstraints();
			gbc_supplyLabel.gridx = 0;
			gbc_supplyLabel.gridy = 3;
			infoPanel.add(supplyLabel, gbc_supplyLabel);
		}
		
		/**
		 * Update the layer.
		 */

		
		
	}
	public void update(){
		sLayer.firePropertyChange(AVKey.LAYER, null, null);
	}

	public RenderableLayer getBoxLayer() {
		return sLayer;
	}
	
	public IconLayer getIconLayer(){
		return iconLayer;
	}
	
	public void checkDepots(Depot depot){
		String ObjButtons[] = {"Yes","No"};
        int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure you want to exit?","Online Examination System",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,ObjButtons,ObjButtons[1]);
        if(PromptResult==JOptionPane.YES_OPTION)
        {
            model.buySupply(depot);
        }
	}

	public void gameBegin() {
		JOptionPane.showMessageDialog(this, "Game Start!!!");
	}

	public void aTeamWins(Team team) {
		JOptionPane.showMessageDialog(this, "Team "+team.name+" wins. You lose.");
	}

}
