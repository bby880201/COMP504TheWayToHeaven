package xz42_bb26.game.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import xz42_bb26.client.view.IView2ModelAdapter;

import javax.swing.border.TitledBorder;
import javax.swing.JEditorPane;
import java.awt.Color;
import javax.swing.JTextArea;

public class NewGameGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8909380491432619237L;
	private JPanel contentPane;
	private IModelAdapter toModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NewGameGUI frame = new NewGameGUI(null);
					frame.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public NewGameGUI(IModelAdapter iModelAdapter) {
		toModel = iModelAdapter;
		initialize();

		
	}
	
	public void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel stPanel = new JPanel();
		stPanel.setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(stPanel, BorderLayout.NORTH);
		
		JPanel mapPanel = new JPanel();
		mapPanel.setBorder(new TitledBorder(null, "Game Map", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(mapPanel, BorderLayout.CENTER);
		
		JPanel istPanel = new JPanel();
		istPanel.setBackground(Color.WHITE);
		istPanel.setBorder(new TitledBorder(null, "Instructions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(istPanel, BorderLayout.EAST);
		istPanel.setLayout(new BorderLayout(0, 0));
		
		JTextArea istTa = new JTextArea();
		istTa.setWrapStyleWord(true);
		istTa.setEditable(false);
		istPanel.add(istTa, BorderLayout.NORTH);
	}

	public void start(){
		this.setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width,
				Toolkit.getDefaultToolkit().getScreenSize().height);
		this.setVisible(true);
	}
}
