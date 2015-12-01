package xz42_bb26.client.view.chatwindow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.function.Supplier;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;

/**
 * The mini-view GUI panel for the chat window
 * 
 * @author bb26, xc7
 *
 * @param <Usr>
 */
public class ChattingWindow<Usr> extends JSplitPane {

	/**
	 * declare a static final serialVersionUID of type long to fix the warning
	 */
	private static final long serialVersionUID = -8432378916828183998L;

	private IChatWindow2Model<Usr> toChatroomAdapt;

	private ChattingWindow<Usr> thisWindow = this;
	private JEditorPane edMsg;
	private JList<Usr> lsMember;
	private JTextField tfInvite;
	private JScrollPane scDisplay;
	private JPanel plDisplay;
	private JButton btnSpeakTo;
	private JButton btnSend;
	private JButton btnLeave;
	private JButton btnInvite;
	private JButton btnStartGame;
	private JPanel panel;

	/**
	 * Constructor that takes an instance of IChatWindow2Model
	 * @param mv2mmAdapt An instance of IChatWindow2Model
	 */
	public ChattingWindow(IChatWindow2Model<Usr> mv2mmAdapt) {

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
		scDisplay.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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
		gbl_panel_4.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_panel_4.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_4.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_4.setLayout(gbl_panel_4);
		panel_4.setMinimumSize(new Dimension(150, 200));
		panel_4.setMaximumSize(new Dimension(150, 1000));
		
		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Invite Friend", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridheight = 3;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		panel_4.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{134, 0};
		gbl_panel.rowHeights = new int[]{28, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
				tfInvite = new JTextField();
				GridBagConstraints gbc_tfInvite = new GridBagConstraints();
				gbc_tfInvite.insets = new Insets(0, 0, 5, 0);
				gbc_tfInvite.fill = GridBagConstraints.BOTH;
				gbc_tfInvite.gridx = 0;
				gbc_tfInvite.gridy = 0;
				panel.add(tfInvite, gbc_tfInvite);
				tfInvite.setToolTipText("Enter the IP address of the remote user you want to invite to the current chatroom.");
				tfInvite.setColumns(10);

		btnInvite = new JButton("Invite");
		GridBagConstraints gbc_btnInvite = new GridBagConstraints();
		gbc_btnInvite.fill = GridBagConstraints.BOTH;
		gbc_btnInvite.gridx = 0;
		gbc_btnInvite.gridy = 1;
		panel.add(btnInvite, gbc_btnInvite);
		btnInvite.setToolTipText("Invite the remote user as specified by the IP address to the current chatroom.");

		JPanel panel_6 = new JPanel();
		GridBagConstraints gbc_panel_6 = new GridBagConstraints();
		gbc_panel_6.insets = new Insets(0, 0, 5, 0);
		gbc_panel_6.fill = GridBagConstraints.BOTH;
		gbc_panel_6.gridx = 0;
		gbc_panel_6.gridy = 3;
		panel_4.add(panel_6, gbc_panel_6);
		panel_6.setLayout(new BorderLayout(0, 0));

		lsMember = new JList<Usr>();
		lsMember.setToolTipText("Display the current members in the chatroom.");
		lsMember.setBorder(new TitledBorder(null, "Member List", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		lsMember.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel_6.add(new JScrollPane(lsMember), BorderLayout.CENTER);

		btnSpeakTo = new JButton("Speak To");
		btnSpeakTo.setToolTipText("Speak to a chosen member from the member list of this chatroom.");

		GridBagConstraints gbc_btnSpeakTo = new GridBagConstraints();
		gbc_btnSpeakTo.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSpeakTo.insets = new Insets(0, 0, 5, 0);
		gbc_btnSpeakTo.gridx = 0;
		gbc_btnSpeakTo.gridy = 4;
		panel_4.add(btnSpeakTo, gbc_btnSpeakTo);
		
		btnStartGame = new JButton("Start Game");
		btnStartGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mv2mmAdapt.startGame();
			}
		});
		GridBagConstraints gbc_btnStartGame = new GridBagConstraints();
		gbc_btnStartGame.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnStartGame.insets = new Insets(0, 0, 5, 0);
		gbc_btnStartGame.gridx = 0;
		gbc_btnStartGame.gridy = 5;
		panel_4.add(btnStartGame, gbc_btnStartGame);

		btnLeave = new JButton("Leave");
		btnLeave.setToolTipText("Leave the current chatroom.");

		GridBagConstraints gbc_btnLeave = new GridBagConstraints();
		gbc_btnLeave.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLeave.gridx = 0;
		gbc_btnLeave.gridy = 6;
		panel_4.add(btnLeave, gbc_btnLeave);

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
		btnSpeakTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toChatroomAdapt.speakTo(lsMember.getSelectedValue());
			}
		});
		/**
		 * Send the message.
		 */
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = edMsg.getText();

				append("YOU says:\n" + str + "\n");

				toChatroomAdapt.sendMsg(str);
				edMsg.setText("");
			}
		});
		/**
		 * Leave the current chatroom.
		 */
		btnLeave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toChatroomAdapt.quit();
				toChatroomAdapt.deleteWindow(thisWindow);
			}
		});
		/**
		 * Invite the remote user as specified by the IP address to the current chatroom.
		 */
		btnInvite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toChatroomAdapt.invite(tfInvite.getText());
			}
		});
	}

	/**
	 * Add the data to the specific chatroom's chat window.
	 * @param data the data to be added to GUI panel
	 */
	public void append(String data) {
		JLabel content = new JLabel(data);

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
	public void refreshList(Set<Usr> users) {
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
	
	
}
