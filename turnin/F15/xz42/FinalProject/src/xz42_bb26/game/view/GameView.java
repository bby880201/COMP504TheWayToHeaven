package xz42_bb26.game.view;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.layers.IconLayer;
import gov.nasa.worldwind.layers.RenderableLayer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import xz42_bb26.game.model.Depot;
import xz42_bb26.game.model.Team;

import javax.swing.border.TitledBorder;

import java.awt.Color;

import javax.swing.JTextArea;

import map.IRightClickAction;
import map.MapPanel;

import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;

import java.awt.FlowLayout;

public class GameView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8909380491432619237L;
	
	private final int maxHeight;
	private final int maxWidth;
	
	private JPanel contentPane;
	private JPanel stPanel;
	private MapPanel map;
		
	private JTextArea taGamePhase;
	private JTextArea taGameInstr;
	private JTextArea taOtherInfo;

	private RenderableLayer sLayer;
	private IconLayer iconLayer;

	private IModelAdapter toModel = IModelAdapter.NULL_OBJECT;

	private JLabel lblRole;

	private JLabel lblCashNum;

	private JLabel lblSupplyNum;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameView frame = new GameView();
					frame.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GameView() {
		maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		maxHeight =	Toolkit.getDefaultToolkit().getScreenSize().height;
	}
	
	/**
	 * Create the frame.
	 */
	public GameView(IModelAdapter iModelAdapter) {
		toModel = iModelAdapter;
		maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		maxHeight =	Toolkit.getDefaultToolkit().getScreenSize().height;
	}
	
	public void initialize() {
		iconLayer = new IconLayer();
		sLayer = new RenderableLayer();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		stPanel = new JPanel();
		stPanel.setForeground(Color.BLACK);
		stPanel.setBackground(Color.WHITE);
		stPanel.setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		stPanel.setPreferredSize(new Dimension(maxWidth, 50));
		contentPane.add(stPanel, BorderLayout.NORTH);
		stPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel instrPanel = new JPanel();
		instrPanel.setBackground(Color.WHITE);
		instrPanel.setBorder(null);
		instrPanel.setPreferredSize(new Dimension(200, maxHeight));
		contentPane.add(instrPanel, BorderLayout.EAST);
		instrPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel instrPanel_1 = new JPanel();
		instrPanel_1.setBackground(Color.WHITE);
		instrPanel_1.setBorder(new TitledBorder(null, "Game Phase", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		instrPanel_1.setPreferredSize(new Dimension(200, maxHeight/5));
		instrPanel.add(instrPanel_1, BorderLayout.NORTH);

		JPanel instrPanel_2 = new JPanel();
		instrPanel_2.setBackground(Color.WHITE);
		instrPanel_2.setBorder(new TitledBorder(null, "Game Instruction", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		instrPanel_2.setPreferredSize(new Dimension(200, 3*maxHeight/5));
		instrPanel.add(instrPanel_2, BorderLayout.CENTER);
		
		JPanel instrPanel_3 = new JPanel();
		instrPanel_3.setBackground(Color.WHITE);
		instrPanel_3.setBorder(new TitledBorder(null, "Other Info", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		instrPanel_3.setPreferredSize(new Dimension(200, maxHeight/5));
		instrPanel.add(instrPanel_3, BorderLayout.SOUTH);
		instrPanel_2.setLayout(new BorderLayout(0, 0));
		instrPanel_1.setLayout(new BorderLayout(0, 0));

		taGamePhase = new JTextArea();
		taGamePhase.setLineWrap(true);
		taGamePhase.setWrapStyleWord(true);
		taGamePhase.setEditable(false);
		JScrollPane scrollPane_1 = new JScrollPane(taGamePhase);
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		instrPanel_1.add(scrollPane_1);
		
		taGameInstr = new JTextArea();
		taGameInstr.setLineWrap(true);
		taGameInstr.setWrapStyleWord(true);
		taGameInstr.setEditable(false);
		JScrollPane scrollPane_2 = new JScrollPane(taGameInstr);
		scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		instrPanel_2.add(scrollPane_2, BorderLayout.CENTER);
		instrPanel_3.setLayout(new BorderLayout(0, 0));
		
		taOtherInfo = new JTextArea();
		taOtherInfo.setLineWrap(true);
		taOtherInfo.setWrapStyleWord(true);
		taOtherInfo.setEditable(false);
		JScrollPane scrollPane_3 = new JScrollPane(taOtherInfo);
		scrollPane_3.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		instrPanel_3.add(scrollPane_3);
		
		JPanel mapPanel = new JPanel();
		mapPanel.setForeground(Color.GREEN);
		mapPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(0, 255, 0), new Color(64, 64, 64)), "Game Map", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 255, 0)));
		mapPanel.setBackground(Color.DARK_GRAY);
		mapPanel.setPreferredSize(new Dimension(maxWidth - 200, maxHeight - 50));
		contentPane.add(mapPanel, BorderLayout.CENTER);
		
		map = new MapPanel(Earth.class);
		map.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		mapPanel.add(map, BorderLayout.CENTER);
		map.setPreferredSize(new Dimension(maxWidth-250, maxHeight-150));
		
		pack();
	}

	public void start(){
		
		this.setVisible(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initialize();
				setBounds(0, 0, maxWidth, maxHeight);
				map.start();

//				if(!model.isNavigator()){
				map.addLayer(iconLayer);
//				}
				map.addLayer(sLayer);
				setLocationRelativeTo(null);
			    setVisible(true);
			    
			    if(toModel.isNavigator()){
					initNavig();
				}
				else{
					initManag();
				}			
			}
		});
	}
	
	public void initNavig(){
		map.addRightClickAction(new IRightClickAction(){
			@Override
			public void apply(Position p) {
				toModel.moveTo(p);
			}
		});
		
		taGameInstr.append("You are the driver!!\n\nYou can control your team to move to wherever you want,"
				+ " but don't forget you need supply to live! Communicate to your partener to find a path to your destiny!");

		JLabel lblNewLabel_1 = new JLabel("Player Role:");
		lblNewLabel_1.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		stPanel.add(lblNewLabel_1);
		
		lblRole = new JLabel("Driver");
		stPanel.add(lblRole);
	}
	
	public void initManag(){
		taGameInstr.append("You are the resource manager!!\n\nYou need to communicate with your partener about your team's"
				+ " cash, supply, nearest supply depots, and the way to your destiny!");
		
		JLabel lblNewLabel_1 = new JLabel("Player Role:");
		lblNewLabel_1.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		stPanel.add(lblNewLabel_1);
		
		lblRole = new JLabel("Resource Manager");
		stPanel.add(lblRole);
		
		JLabel label_3 = new JLabel("                        ");
		stPanel.add(label_3);
		
		JLabel lblCash = new JLabel("Cash:");
		lblCash.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		stPanel.add(lblCash);
		
		JLabel label_2 = new JLabel("$");
		stPanel.add(label_2);
		
		lblCashNum = new JLabel("100");
		stPanel.add(lblCashNum);
		
		JLabel lblEmpty = new JLabel("                        ");
		stPanel.add(lblEmpty);
		
		JLabel lblSupply = new JLabel("Supply:");
		lblSupply.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		stPanel.add(lblSupply);
		
		lblSupplyNum = new JLabel("10000");
		stPanel.add(lblSupplyNum);
		
		JLabel lblMiles = new JLabel("Miles");
		stPanel.add(lblMiles);	
	}
	
	public void update(Team team){
		sLayer.firePropertyChange(AVKey.LAYER, null, null);
		if(! toModel.isNavigator()){
			lblCashNum.setText(Double.toString(team.cash));
			lblSupplyNum.setText(Double.toString(team.supply));
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
            toModel.buySupply(depot);
        }
	}

	public void gameBegin() {
		taGamePhase.setText("Game is began! Hurry up go to your destiny!!");
		JOptionPane.showMessageDialog(this, "Game Start!!!");
		map.setPosition(Position.fromDegrees(63, -151,3000000), true);
	}

	public void aTeamWins(Team team) {
		if(team.uuid.equals(toModel.getTeam().uuid)){
			taGamePhase.setText("Game Over:"+"You win!!!!");
			JOptionPane.showMessageDialog(this, "CONGRATULATIONS!!! YOU WIN!!!");
		}
		else{
			taGamePhase.setText("Game Over: "+"Team "+team.name+" wins.");
			JOptionPane.showMessageDialog(this, "Team "+team.name+" wins. You lose.");
		}
		
	}

	public void gameOver() {
		taGamePhase.setText("Game Over: Sorry, you ran out of supply or money!");
		JOptionPane.showMessageDialog(this, "Game Over! You lose for out of supply.");
	}
	private void quit() {
		System.out.println("ChatGUI: Server is quitting...");
		toModel.quit();
	}
	
	
}
