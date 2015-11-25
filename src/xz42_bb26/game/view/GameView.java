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

	/**
	 * Start the game view.
	 */
	public void start(){
//		this.setVisible(true);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			    setVisible(true);
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GameView(IModelAdapter iModelAdapter) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		
		MapPanel mapPanel = new MapPanel();
		panel.add(mapPanel, BorderLayout.CENTER);
		
		JPanel infoPanel = new JPanel();
		contentPane.add(infoPanel, BorderLayout.EAST);
		
		JButton btnBeTheNavigator = new JButton("Be the navigator");
		infoPanel.add(btnBeTheNavigator);
		
		JLayeredPane layeredPane = new JLayeredPane();
		infoPanel.add(layeredPane);
		
		
	}


}
