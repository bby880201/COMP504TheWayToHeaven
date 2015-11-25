package xz42_bb26.game.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import map.MapPanel;
import javax.swing.JButton;
import javax.swing.JLayeredPane;

public class GameView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2733797230967669361L;
	private JPanel contentPane;
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
		
		
		
		JPanel infoPanel = new JPanel();
		contentPane.add(infoPanel, BorderLayout.EAST);
		
		JButton btnBeTheNavigator = new JButton("Be the navigator");
		infoPanel.add(btnBeTheNavigator);
		
		JLayeredPane layeredPane = new JLayeredPane();
		infoPanel.add(layeredPane);
	}


}
