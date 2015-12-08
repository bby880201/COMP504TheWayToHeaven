package xz42_bb26.client.view;

import java.awt.Toolkit;

import javax.swing.JFrame;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.util.Set;
import java.awt.event.ActionEvent;

import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;

import xz42_bb26.client.view.chatwindow.ChattingWindow;
import xz42_bb26.client.view.chatwindow.IChatWindow2Model;

import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

import java.awt.Color;

/**
 * The main GUI frame of the ChatApp program.
 * 
 * @author xc7, bb26
 *
 * @param <Room> Generic type for chatroom
 * @param <Usr> Generic type for user
 */
public class MainGUI<Room, Usr, ChatUsr> {

	@SuppressWarnings({ "unchecked" })
	private IView2ModelAdapter<Room, Usr,ChatUsr> toModelAdapter = IView2ModelAdapter.NULL_OBJECT;
	private JFrame frame;
	private JTextField tfIPInput;
	private JTabbedPane tabbedPane;
	private JButton btnGetChatrooms;
	private JButton btnChatWith;
	private JButton btnQuit;
	private JButton btnJoinChatroom;
	private JComboBox<Room> cbRooms;
	private JPanel panel_1;

	/**
	 * Create the application.
	 * @param modelAdapter An instance of the IView2ModelAdapter
	 */
	public MainGUI(IView2ModelAdapter<Room, Usr,ChatUsr> modelAdapter) {
		initialize();
		toModelAdapter = modelAdapter;
	}

	/**
	 * Start the application.
	 */
	public void start() {
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 80, Toolkit.getDefaultToolkit().getScreenSize().width - 200,
				Toolkit.getDefaultToolkit().getScreenSize().height - 160);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		frame.getContentPane().add(panel, BorderLayout.WEST);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 134, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 28, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);
		panel.setMinimumSize(new Dimension(150,200));
		panel.setMaximumSize(new Dimension(150,2000));
		
		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Connect To", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridheight = 3;
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		panel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{134, 0};
		gbl_panel_1.rowHeights = new int[]{28, 0, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);

		tfIPInput = new JTextField();
		GridBagConstraints gbc_tfIPInput = new GridBagConstraints();
		gbc_tfIPInput.insets = new Insets(0, 0, 5, 0);
		gbc_tfIPInput.fill = GridBagConstraints.BOTH;
		gbc_tfIPInput.gridx = 0;
		gbc_tfIPInput.gridy = 0;
		panel_1.add(tfIPInput, gbc_tfIPInput);
		tfIPInput.setToolTipText("Enter the IP address of the remote user you want to connect to.");
		tfIPInput.setColumns(10);

		btnChatWith = new JButton("Chat With");
		GridBagConstraints gbc_btnChatWith = new GridBagConstraints();
		gbc_btnChatWith.fill = GridBagConstraints.BOTH;
		gbc_btnChatWith.gridx = 0;
		gbc_btnChatWith.gridy = 1;
		panel_1.add(btnChatWith, gbc_btnChatWith);
		btnChatWith.setToolTipText("Build connection with the remove user specified by the IP address.");
		
		btnGetChatrooms = new JButton("Get Chatrooms");
		btnGetChatrooms
				.setToolTipText("Get the list of chatrooms from the remote user as specified by the IP address.");
		GridBagConstraints gbc_btnGetChatrooms = new GridBagConstraints();
		gbc_btnGetChatrooms.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnGetChatrooms.insets = new Insets(0, 0, 5, 0);
		gbc_btnGetChatrooms.gridx = 0;
		gbc_btnGetChatrooms.gridy = 4;
		panel.add(btnGetChatrooms, gbc_btnGetChatrooms);

		cbRooms = new JComboBox<Room>();
		cbRooms.setToolTipText("Choose a chatroom from the list of chatrooms the remote user is currently in.");
		GridBagConstraints gbc_cbRooms = new GridBagConstraints();
		gbc_cbRooms.fill = GridBagConstraints.BOTH;
		gbc_cbRooms.insets = new Insets(0, 0, 5, 0);
		gbc_cbRooms.gridx = 0;
		gbc_cbRooms.gridy = 5;
		panel.add(cbRooms, gbc_cbRooms);

		btnJoinChatroom = new JButton("Join Chatroom");
		btnJoinChatroom.setToolTipText("Join the chosen chatroom.");

		GridBagConstraints gbc_btnJoinChatroom = new GridBagConstraints();
		gbc_btnJoinChatroom.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnJoinChatroom.insets = new Insets(0, 0, 5, 0);
		gbc_btnJoinChatroom.gridx = 0;
		gbc_btnJoinChatroom.gridy = 8;
		panel.add(btnJoinChatroom, gbc_btnJoinChatroom);

		btnQuit = new JButton("Quit");
		btnQuit.setToolTipText("Quit the program.");

		GridBagConstraints gbc_btnQuit = new GridBagConstraints();
		gbc_btnQuit.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnQuit.gridx = 0;
		gbc_btnQuit.gridy = 9;
		panel.add(btnQuit, gbc_btnQuit);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		btnActionListner();
	}

	/**
	 * This method specifies the ActionListener for each buttons
	 */
	public void btnActionListner() {
		/**
		 * Get the list of chatrooms from the remote user as specified by the IP address
		 */
		btnGetChatrooms.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toModelAdapter.getListRooms(tfIPInput.getText());
			}
		});
		/**
		 * Build connection with the remove user specified by the IP address
		 */
		btnChatWith.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toModelAdapter.chatWith(tfIPInput.getText());
			}
		});
		/**
		 * Quit the program
		 */
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toModelAdapter.quit();
			}
		});
		/**
		 * Join the chosen chatroom
		 */
		btnJoinChatroom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toModelAdapter.joinChatroom(cbRooms.getItemAt(cbRooms.getSelectedIndex()));
			}
		});
	}

	/**
	 * Delete the mini-view (the chat window of this chatroom) 
	 * from the main-view (the window of the chatapp)
	 * @param cw the view of the chatroom
	 */
	public void deleteChatWindow(ChattingWindow<Usr> cw) {
		tabbedPane.remove(cw);
	}

	/**
	 * Make a new chat window (mini-view) and add onto main GUI frame
	 * @param mv2mmAdapt An instance of IChatWindow2Model
	 * @return An instance of ChattingWindow
	 */
	public ChattingWindow<ChatUsr> makeChatRoom(IChatWindow2Model<ChatUsr> mv2mmAdapt) {

		ChattingWindow<ChatUsr> cw = new ChattingWindow<ChatUsr>(mv2mmAdapt);
		tabbedPane.addTab(mv2mmAdapt.getName(), null, cw, null);
		return cw;
	}
	
	/**
	 * refresh the room list of remote user
	 * @param rooms available chat rooms
	 */
	public void refreshRoomList(Set<Room> rooms) {
		for (Room rm: rooms) {
			cbRooms.addItem(rm);
		}
	}
}
