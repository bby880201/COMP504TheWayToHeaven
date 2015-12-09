package xz42_bb26.server.view.chatwindow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EtchedBorder;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import java.awt.Color;
import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;

/**
 * The mini-view GUI panel for the chat window
 * 
 * @author bb26
 *
 * @param <Usr>
 */
public class ServerWindow<Usr,TmRm> extends JSplitPane {

	/**
	 * declare a static final serialVersionUID of type long to fix the warning
	 */
	private static final long serialVersionUID = -8432378916828183998L;

	private IServerWindow2World<Usr> toChatroomAdapt;

	private ServerWindow<Usr,TmRm> thisWindow = this;
	private JEditorPane edMsg;
	private JList<Usr> lsMember;
	private JScrollPane scDisplay;
	private JPanel plDisplay;
	private JButton btnCreateTeam;
	private JButton btnSend;
	private JButton btnKick;
	private JButton btnInstallGame;
	private JList<TmRm> lsTeam;

	/**
	 * Constructor that takes an instance of IChatWindow2Model
	 * @param mv2mmAdapt An instance of IChatWindow2Model
	 */
	public ServerWindow(IServerWindow2World<Usr> mv2mmAdapt) {

		super();

		toChatroomAdapt = mv2mmAdapt;

		this.setResizeWeight(0.7);
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JPanel panel_2 = new JPanel();
		this.setLeftComponent(panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 0, 0, 0 };
		gbl_panel_2.rowHeights = new int[] { 0, 0 };
		gbl_panel_2.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		scDisplay = new JScrollPane();
		GridBagConstraints gbc_scDisplay = new GridBagConstraints();
		gbc_scDisplay.insets = new Insets(0, 0, 0, 5);
		gbc_scDisplay.fill = GridBagConstraints.BOTH;
		gbc_scDisplay.gridx = 0;
		gbc_scDisplay.gridy = 0;
		panel_2.add(scDisplay, gbc_scDisplay);

		plDisplay = new JPanel();
		plDisplay.setToolTipText("The message display panel.");
		scDisplay.setViewportView(plDisplay);
		plDisplay.setBackground(Color.WHITE);
		plDisplay.setLayout(new BoxLayout(plDisplay, BoxLayout.Y_AXIS));

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 1;
		gbc_panel_4.gridy = 0;
		panel_2.add(panel_4, gbc_panel_4);
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[] { 0, 0 };
		gbl_panel_4.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_4.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_4.rowWeights = new double[] { 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_4.setLayout(gbl_panel_4);
		panel_4.setMinimumSize(new Dimension(150, 200));
		panel_4.setMaximumSize(new Dimension(150, 1000));

		JPanel panel_6 = new JPanel();
		GridBagConstraints gbc_panel_6 = new GridBagConstraints();
		gbc_panel_6.insets = new Insets(0, 0, 5, 0);
		gbc_panel_6.fill = GridBagConstraints.BOTH;
		gbc_panel_6.gridx = 0;
		gbc_panel_6.gridy = 0;
		panel_4.add(panel_6, gbc_panel_6);
		panel_6.setLayout(new BorderLayout(0, 0));

		lsMember = new JList<Usr>();
		lsMember.setToolTipText("Display the current members in the chatroom.");
		lsMember.setBorder(new TitledBorder(null, "Member List", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_6.add(new JScrollPane(lsMember), BorderLayout.CENTER);

		btnCreateTeam = new JButton("Create Team");
		btnCreateTeam.setToolTipText("Select two members and create a team.");

		GridBagConstraints gbc_btnCreateTeam = new GridBagConstraints();
		gbc_btnCreateTeam.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnCreateTeam.insets = new Insets(0, 0, 5, 0);
		gbc_btnCreateTeam.gridx = 0;
		gbc_btnCreateTeam.gridy = 1;
		panel_4.add(btnCreateTeam, gbc_btnCreateTeam);
		
		btnKick = new JButton("Kick Out");
		btnKick.setToolTipText("Kick selected user");
				
		GridBagConstraints gbc_btnKick = new GridBagConstraints();
		gbc_btnKick.insets = new Insets(0, 0, 5, 0);
		gbc_btnKick.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnKick.gridx = 0;
		gbc_btnKick.gridy = 2;
		panel_4.add(btnKick, gbc_btnKick);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 3;
		panel_4.add(panel, gbc_panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		lsTeam = new JList<TmRm>();		
		lsTeam.setToolTipText("Display the current teams in the room.");
		lsTeam.setBorder(new TitledBorder(null, "Team List", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(new JScrollPane(lsTeam), BorderLayout.CENTER);

		btnInstallGame = new JButton("Install Game");
		btnInstallGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lsMember.getModel().getSize() == 0) {
					mv2mmAdapt.installGame();
				}
				else {
					append("Some players haven't been assigned to a team yet!");
				}
			}
		});
		GridBagConstraints gbc_btnStartGame = new GridBagConstraints();
		gbc_btnStartGame.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnStartGame.gridx = 0;
		gbc_btnStartGame.gridy = 5;
		panel_4.add(btnInstallGame, gbc_btnStartGame);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		this.setRightComponent(panel_3);

		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] { 717, -62, 0 };
		gbl_panel_3.rowHeights = new int[] { 0, 0 };
		gbl_panel_3.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel_3.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_3.setLayout(gbl_panel_3);

		btnSend = new JButton("Send");
		btnSend.setToolTipText("Send the message.");

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		panel_3.add(scrollPane, gbc_scrollPane);

		edMsg = new JEditorPane();
		edMsg.setToolTipText("Enter the message to send over.");
		scrollPane.setViewportView(edMsg);
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.fill = GridBagConstraints.BOTH;
		gbc_btnSend.gridx = 1;
		gbc_btnSend.gridy = 0;
		panel_3.add(btnSend, gbc_btnSend);

		btnActionListner();
	}

	/**
	 * This method specifies the ActionListener for each buttons
	 */
	public void btnActionListner() {
		/**
		 * Speak to a chosen member from the member list of this chatroom.
		 */
		btnCreateTeam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<Usr> teamMems = lsMember.getSelectedValuesList();
				if (teamMems.size() == 2) {
					toChatroomAdapt.createTeam(teamMems);
				}
				else {
					append("Please select two members to create a new team!\n");
				}
			}
		});
		/**
		 * Send the message.
		 */
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = edMsg.getText();

				append("Server:\n" + str + "\n");

				toChatroomAdapt.sendMsg(str);
				edMsg.setText("");
			}
		});
		/**
		 * Leave the current chatroom.
		 */
		btnKick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toChatroomAdapt.quit();
				toChatroomAdapt.deleteWindow(thisWindow);
				//TODO change function
			}
		});
	}

	/**
	 * Add the data to the specific chatroom's chat window.
	 * @param data the data to be added to GUI panel
	 */
	public void append(String data) {
		JLabel content = new JLabel("Server:\n"+data);
		content.setForeground(Color.RED);
		plDisplay.add(content);
		
		plDisplay.revalidate();
		plDisplay.repaint();
	}

	/**
	 * Give a Container (e.g. JPanel) as the accessible part of the local system 
	 * to the unknown command
	 * @return a container that is on the chatroom window's GUI 
	 */
	public Container Scrollable() {
		return plDisplay;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Refresh the member list on the chatroom 
	 * @param users the list of users to show on chatroom member list panel
	 */
	public void refreshList(Collection<Usr> users) {
		lsMember.setListData((Usr[]) users.toArray());
	}

	/**
	 * Delete the certain chat window from the main GUI panel
	 */
	public void deleteWindow() {
		toChatroomAdapt.deleteWindow(thisWindow);
	}
	
	/**
	 * Display a container
	 * @param containerSupplier the container needs to be displayed
	 */
	public void display(Supplier<Component> containerSupplier) {
		
		plDisplay.add(containerSupplier.get());
		plDisplay.revalidate();
		plDisplay.repaint();
	}

	public void refreshRoomName(String displayName) {
		//TODO need to be done!
	}

	@SuppressWarnings("unchecked")
	public void refreshTeam(Collection<TmRm> teamList) {
		lsTeam.setListData((TmRm[]) teamList.toArray());
	}
	
	
}
