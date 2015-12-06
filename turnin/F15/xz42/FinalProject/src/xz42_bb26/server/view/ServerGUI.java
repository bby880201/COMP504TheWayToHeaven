package xz42_bb26.server.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.WindowConstants;

import common.IPerson;
import xz42_bb26.client.view.IView2ModelAdapter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import java.awt.Dimension;

import javax.swing.ListSelectionModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Main view of the application.
 * @author xz42,bb26
 *
 */
public class ServerGUI<Room, Usr, ChatUsr> extends JFrame {
	/**
	 * Serial number
	 */
	private static final long serialVersionUID = -2852590443897011594L;


	/**
	 * View to model adapter.
	 */
	private IView2ModelAdapter<Room, Usr,ChatUsr> model = IView2ModelAdapter.NULL_OBJECT;


	private JPanel contentPane;
	private final JPanel controlPnl = new JPanel();
	private final JSplitPane displayPnl = new JSplitPane();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JTextArea displayArea = new JTextArea();
	private final JScrollPane addOnPnl = new JScrollPane();

	private final DefaultListModel<String> listModel = new DefaultListModel<String>();
	private final JButton btnTeam = new JButton("New Team");
	private final JButton btnInvite = new JButton("Invite");
	private final JButton btnStartGame = new JButton("Start Game");
	private final JButton btnStopGame = new JButton("Stop Game");
	private final JSplitPane splitPane = new JSplitPane();
	private final JSplitPane splitPane_1 = new JSplitPane();
	private final JList<String> connectedList = new JList<String>(listModel);
	private final JSplitPane splitPane_2 = new JSplitPane();
	private final JScrollPane scrollPane_1 = new JScrollPane();
	private final JScrollPane scrollPane_2 = new JScrollPane();
	private JList<String> lstTeam = new JList<String>();
	private final JList<String> lstMember = new JList<String>();
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);


	/**
	 * Start the GUI.
	 */
	public void start() {
		setVisible(true);
	}

	/**
	 * Create the frame.
	 * @param model View to model adapter.
	 */
	public ServerGUI(IView2ModelAdapter model) {
		this.model = model;
		lstTeam.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				lstMember.setListData(model.getMembers(lstTeam.getSelectedIndex()));
			}
		});
		lstTeam.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstTeam.setBorder(new TitledBorder(null, "Team List", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.lstTeam.setListData(model.getTeam());

		initGUI();
	}

	/**
	 * Initialize the view.
	 */
	protected void initGUI() {
		//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		setBounds(100, 100, 766, 664);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		contentPane.add(controlPnl, BorderLayout.NORTH);
		btnTeam.setToolTipText("Create a new team");
		btnTeam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.makeChatroom(inputWindow("Team Name: ", "Wayaya Team"));
				btnInvite.setEnabled(true);
			}
		});

		controlPnl.add(btnTeam);
		btnInvite.setToolTipText("Invite selected player(s) to a selected team");
		btnInvite.setEnabled(false);


		btnInvite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ((!connectedList.isSelectionEmpty()) && (!lstTeam.isSelectionEmpty())) {
					Thread requestThread = new Thread( new Runnable() {
						public void run() {
							model.invite(connectedList.getSelectedIndices(), lstTeam.getSelectedIndex());
						}
					});
					requestThread.start(); 
					btnStartGame.setEnabled(true);
				} else {
					JOptionPane.showMessageDialog(null, "Please select a player to invite!", "Error ", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		controlPnl.add(btnInvite);
		btnStartGame.setToolTipText("Start game with all teams");
		btnStartGame.setEnabled(false);
		btnStartGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				for (int i=1; i<lstTeam.getModel().getSize(); i++){
					model.startGame(i);
				}

				btnStopGame.setEnabled(true);
			}
		});

		controlPnl.add(btnStartGame);
		btnStopGame.setToolTipText("Stop the game.");
		btnStopGame.setEnabled(false);


		btnStopGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread stopThread = new Thread( new Runnable() {
					public void run() {
						model.stopGame();
					}
				});
				stopThread.start(); 			
			}
		});

		controlPnl.add(btnStopGame);

		contentPane.add(displayPnl, BorderLayout.CENTER);

		displayPnl.setLeftComponent(scrollPane);
		displayArea.setMinimumSize(new Dimension(100, 24));
		displayArea.setLineWrap(true);
		displayArea.setColumns(30);

		scrollPane.setViewportView(displayArea);
		splitPane.setDividerSize(0);
		splitPane.setEnabled(false);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		displayPnl.setRightComponent(splitPane);

		splitPane.setLeftComponent(splitPane_1);

		splitPane_1.setLeftComponent(addOnPnl);
		splitPane_1.setDividerLocation(160);
		splitPane.setDividerLocation(2000);
		connectedList.setBorder(new TitledBorder(null, "Player List", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		addOnPnl.setViewportView(connectedList);

		splitPane_1.setRightComponent(splitPane_2);

		splitPane_2.setLeftComponent(scrollPane_1);

		scrollPane_1.setViewportView(lstTeam);

		splitPane_2.setRightComponent(scrollPane_2);
		lstMember.setBorder(new TitledBorder(null, "Team Member List", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		scrollPane_2.setViewportView(lstMember);
		splitPane_2.setDividerLocation(160);

		splitPane.setRightComponent(tabbedPane);
		displayPnl.setDividerLocation(230);
	}

	/**
	 * Show the text.
	 * @param s Text to show.
	 */
	public void append(String s) {
		displayArea.append(s);
		displayArea.setCaretPosition(displayArea.getText().length());
	}

	/**
	 * choice window for user to choose
	 * @param choices items for user to choose from
	 * @param message message displayed on the window
	 * @param title title of the window
	 * @return user's choice
	 */
	public String choiceWindow(String[] choices, String message, String title) {
		String input = (String) JOptionPane.showInputDialog(null, message,
				title, JOptionPane.QUESTION_MESSAGE, null, 
				choices, // Array of choices
				choices[0]); // Initial choice
		return input;
	}

	/**
	 * input window for user to input
	 * @param message message displayed on the window
	 * @param defaultValue Default value for the input.
	 * @return user's input
	 */
	public String inputWindow(String message, String defaultValue) {
		String input = JOptionPane.showInputDialog(null, message, defaultValue);
		if (input!=null && input.equals("")) input = defaultValue;
		return input;
	}

	/**
	 * yes or no window for user 
	 * @param message message displayed on the window
	 * @param title title of the window
	 * @return user's choice
	 */
	public int yesNoWindow(String message, String title) {
		int reply = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
		return reply;
	}

	/**
	 * Update the user list.
	 */
	public void updateConnectedList() {
		listModel.clear();
		for (IPerson person: model.getConnection()) {
			try {
				listModel.addElement(person.getName());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}


	/**
	 * Update team list.
	 */
	public void updateTeamList(){
		lstTeam.updateUI();
	}

	/**
	 * Centralized management point for exiting the application.
	 * All calls to exit the system should go through here.
	 * Shuts system down by stopping the model.
	 */
	private void quit() {
		System.out.println("ChatGUI: Server is quitting...");
		model.quit();
	}
}