package xz42_bb26.game.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.layers.IconLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import map.MapPanel;
import xz42_bb26.game.model.Depot;
import xz42_bb26.game.model.Team;
import map.IRightClickAction;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;

public class GameViewOld extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2733797230967669361L;
	private JPanel contentPane;
	
	private RenderableLayer sLayer;
	private IconLayer iconLayer;
	private JLabel statusLabel;
	private JLabel supplyLabel;
	private JLabel cashLabel;
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

//				if(!model.isNavigator()){
					mapPanel.addLayer(iconLayer);
//				}
				mapPanel.addLayer(sLayer);
				setLocationRelativeTo(null);
			    setVisible(true);
			   
//				
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GameViewOld(IModelAdapter iModelAdapter) {
		this.model = iModelAdapter;
		
	}
	
	private void initGUI(){
		iconLayer = new IconLayer();
		sLayer = new RenderableLayer();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1024, 768);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		
		mapPanel = new MapPanel(Earth.class);
		mapPanel.setPreferredSize(new java.awt.Dimension(800,600));
		panel.add(mapPanel, BorderLayout.CENTER);
		
		
		
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(5,1 , 0, 0));
		contentPane.add(infoPanel, BorderLayout.EAST);
		statusLabel = new JLabel();
		statusLabel.setText("Waiting server to start.");
		infoPanel.add(statusLabel);
		
		JLabel dutyLabel = new JLabel();

		infoPanel.add(dutyLabel);
		if(model.isNavigator()){
			dutyLabel.setText("You are the navigator");
			JLabel destLabel = new JLabel("Your destination is Rice ");
			GridBagConstraints gbc_destLabel = new GridBagConstraints();
			infoPanel.add(destLabel, gbc_destLabel);
			mapPanel.addRightClickAction(new IRightClickAction(){

				@Override
				public void apply(Position p) {
					model.moveTo(p);
				}
			});
		}
		else{

			dutyLabel.setText("You are the resource manager");
			cashLabel = new JLabel("Current Cash: $100");
			infoPanel.add(cashLabel);
			supplyLabel = new JLabel("Current Supply: 100miles");
			infoPanel.add(supplyLabel);
		}
		
		/**
		 * Update the layer.
		 */

		
		
	}
	public void update(Team team){
		sLayer.firePropertyChange(AVKey.LAYER, null, null);
		if(!model.isNavigator()){
			cashLabel.setText("Current Cash: $"+team.cash);
			supplyLabel.setText("Current Supply: "+team.supply+"meters");
		}
		
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
		statusLabel.setText("In game.");
		JOptionPane.showMessageDialog(this, "Game Start!!!");
		mapPanel.setPosition(Position.fromDegrees(63, -151,3000000), true);
	}

	public void aTeamWins(Team team) {
		if(team.uuid.equals(model.getTeam().uuid)){
			statusLabel.setText("Game Over:"+"You win.");
			JOptionPane.showMessageDialog(this, "YOU WIN");
		}
		else{
			statusLabel.setText("Game Over:"+"Team "+team.name+" wins.");
			JOptionPane.showMessageDialog(this, "Team "+team.name+" wins. You lose.");
		}
		
	}

	public void gameOver() {
		statusLabel.setText("You ran out of recources or money");
		JOptionPane.showMessageDialog(this, "Game Over! You lose for out of resources.");
	}

}