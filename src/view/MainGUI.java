package view;

import java.awt.Toolkit;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import view.chatwindow.ChattingWindow;
import view.chatwindow.IChatWindow2Model;
import javax.swing.JComboBox;

/**
 * The main GUI frame of the ChatApp program.
 * 
 * @author xc7, bb26
 *
 * @param <Room> Generic type for chatroom
 * @param <Usr> Generic type for user
 */
public class MainGUI<Room, Usr> {

	@SuppressWarnings({ "unchecked" })
	private IView2ModelAdapter<Room, Usr> toModelAdapter = IView2ModelAdapter.NULL_OBJECT;
	private JFrame frame;
	private JTextField tfIPInput;
	private JTabbedPane tabbedPane;
	private JButton btnGetChatrooms;
	private JButton btnChatWith;
	private JButton btnQuit;
	private JButton btnJoinChatroom;
	private JComboBox<Room> cbRooms;
	private JLabel lbRoomList;

	/**
	 * Create the application.
	 * @param modelAdapter An instance of the IView2ModelAdapter
	 */
	public MainGUI(IView2ModelAdapter<Room, Usr> modelAdapter) {
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
		gbl_panel.rowHeights = new int[] { 0, 28, 0, 0, 0, 0, 0, 0, 540, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JLabel lblConnectTo = new JLabel("Connect To:");
		GridBagConstraints gbc_lblConnectTo = new GridBagConstraints();
		gbc_lblConnectTo.fill = GridBagConstraints.BOTH;
		gbc_lblConnectTo.insets = new Insets(0, 0, 5, 0);
		gbc_lblConnectTo.gridx = 0;
		gbc_lblConnectTo.gridy = 0;
		panel.add(lblConnectTo, gbc_lblConnectTo);

		tfIPInput = new JTextField();
		tfIPInput.setToolTipText("Enter the IP address of the remote user you want to connect to.");
		GridBagConstraints gbc_tfIPInput = new GridBagConstraints();
		gbc_tfIPInput.fill = GridBagConstraints.BOTH;
		gbc_tfIPInput.insets = new Insets(0, 0, 5, 0);
		gbc_tfIPInput.gridx = 0;
		gbc_tfIPInput.gridy = 1;
		panel.add(tfIPInput, gbc_tfIPInput);
		tfIPInput.setColumns(10);

		btnChatWith = new JButton("Chat With");
		btnChatWith.setToolTipText("Build connection with the remove user specified by the IP address.");

		GridBagConstraints gbc_btnChatWith = new GridBagConstraints();
		gbc_btnChatWith.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnChatWith.insets = new Insets(0, 0, 5, 0);
		gbc_btnChatWith.gridx = 0;
		gbc_btnChatWith.gridy = 2;
		panel.add(btnChatWith, gbc_btnChatWith);

		btnGetChatrooms = new JButton("Get Chatrooms");
		btnGetChatrooms
				.setToolTipText("Get the list of chatrooms from the remote user as specified by the IP address.");
		GridBagConstraints gbc_btnGetChatrooms = new GridBagConstraints();
		gbc_btnGetChatrooms.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnGetChatrooms.insets = new Insets(0, 0, 5, 0);
		gbc_btnGetChatrooms.gridx = 0;
		gbc_btnGetChatrooms.gridy = 3;
		panel.add(btnGetChatrooms, gbc_btnGetChatrooms);

		lbRoomList = new JLabel("Chat Room List:");
		GridBagConstraints gbc_lbRoomList = new GridBagConstraints();
		gbc_lbRoomList.fill = GridBagConstraints.HORIZONTAL;
		gbc_lbRoomList.insets = new Insets(0, 0, 5, 0);
		gbc_lbRoomList.gridx = 0;
		gbc_lbRoomList.gridy = 5;
		panel.add(lbRoomList, gbc_lbRoomList);

		cbRooms = new JComboBox<Room>();
		cbRooms.setToolTipText("Choose a chatroom from the list of chatrooms the remote user is currently in.");
		GridBagConstraints gbc_cbRooms = new GridBagConstraints();
		gbc_cbRooms.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbRooms.insets = new Insets(0, 0, 5, 0);
		gbc_cbRooms.gridx = 0;
		gbc_cbRooms.gridy = 6;
		panel.add(cbRooms, gbc_cbRooms);

		btnJoinChatroom = new JButton("Join Chatroom");
		btnJoinChatroom.setToolTipText("Join the chosen chatroom.");

		GridBagConstraints gbc_btnJoinChatroom = new GridBagConstraints();
		gbc_btnJoinChatroom.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnJoinChatroom.insets = new Insets(0, 0, 5, 0);
		gbc_btnJoinChatroom.gridx = 0;
		gbc_btnJoinChatroom.gridy = 7;
		panel.add(btnJoinChatroom, gbc_btnJoinChatroom);

		btnQuit = new JButton("Quit");
		btnQuit.setToolTipText("Quit the program.");

		GridBagConstraints gbc_btnQuit = new GridBagConstraints();
		gbc_btnQuit.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnQuit.gridx = 0;
		gbc_btnQuit.gridy = 10;
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
				HashSet<Room> rooms = toModelAdapter.getListRooms(tfIPInput.getText());

				for (Room rm : rooms) {
					cbRooms.addItem(rm);
				}
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
	public ChattingWindow<Usr> makeChatRoom(IChatWindow2Model<Usr> mv2mmAdapt) {

		ChattingWindow<Usr> cw = new ChattingWindow<Usr>(mv2mmAdapt);
		tabbedPane.addTab(mv2mmAdapt.getName(), null, cw, null);

		return cw;
	}
}
