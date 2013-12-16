//01:48
package client.view;

import java.awt.BorderLayout;

import client.view.GazdOkGUIConf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import client.Controller;
import client.StateOfPlayer;

public class View extends JFrame implements IView {

	private static final long serialVersionUID = 1L;
	private static final String ENDL = "\n";
	// private static int noHistoryEntries = 10;
	private JComponent pnlContentPane;
	private JComponent pnlGameTable;

	private JComponent pnlHistory;
	private JTextArea historyText;
	private JTabbedPane pnlStatus;

	public View() {
		initialize();
		// foo();
		simpleMessage("A játék inicializálása véget ért.");

		simpleMessage("A játék elkezdődött.\tLegyen Ön is multi-csilliárdos!");
		// getLoginInfos();
//		System.out.println(getBuyingInfos("kaka", true));
//		System.out.println(getInsurance("Lakásbizti"));
//		System.out.println(getFurnitureOptions("Macskabőr zokni"));
		// getFurnitureOptions("bautor");
		
		refreshView();

		// simpleMessage(java.awt.Toolkit.getDefaultToolkit().getScreenSize().toString());
	}

	private void initialize() {
		initPnlGameTable();
		initPnlHistory();
		initPnlStatus();
		initPnlContentpane();
		initFrame();
		pack();
	}

	private void initPnlGameTable() {
		pnlGameTable = new JPanel(new GridBagLayout());
		pnlGameTable.setPreferredSize(GazdOkGUIConf.GAMETABLE_PREF_DIM);
		pnlGameTable.setBackground(GazdOkGUIConf.GAMETABLE_BGCOLOR);
		pnlGameTable.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.RAISED));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 14;
		gbc.weighty = 9;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;

		/**
		 * Ez valójában felesleges, mert magától berendezi.
		 */
		Dimension buttonPrefSize = new Dimension(45, 45);

		int noField = 0;
		for (int j = 0; j < 13; j++) {
			gbc.gridx = j;
			JButton button = new JButton(Integer.toString(noField++));
			button.setPreferredSize(buttonPrefSize);
			pnlGameTable.add(button, gbc);
		}

		for (int i = 0; i < 9; i++) {
			int j = 13;
			gbc.gridx = j;
			gbc.gridy = i;

			JButton button = new JButton(Integer.toString(noField++));
			button.setPreferredSize(buttonPrefSize);
			pnlGameTable.add(button, gbc);
		}

		for (int j = 12; j > 0; j--) {
			gbc.gridx = j;
			JButton button = new JButton(Integer.toString(noField++));
			button.setPreferredSize(buttonPrefSize);
			pnlGameTable.add(button, gbc);
		}

		for (int i = 8; i > 0; i--) {
			int j = 0;
			gbc.gridx = j;
			gbc.gridy = i;

			JButton button = new JButton(Integer.toString(noField++));
			button.setPreferredSize(buttonPrefSize);
			pnlGameTable.add(button, gbc);
		}

	}

	private void initPnlHistory() {

		pnlHistory = new JPanel(new BorderLayout());
		pnlHistory.setMinimumSize(GazdOkGUIConf.HISTORY_PREF_DIM);
		pnlHistory.setBackground(GazdOkGUIConf.HISTORY_BGCOLOR);

		JLabel labelHistory = new JLabel("-history-");
		labelHistory.setPreferredSize(GazdOkGUIConf.HISTORY_LABEL_DIM);
		pnlHistory.add(labelHistory, BorderLayout.PAGE_START);

		historyText = new JTextArea(20, 30);
		historyText.setEditable(false);
		historyText.setLineWrap(true);
		historyText.setWrapStyleWord(true);
		historyText.setAutoscrolls(true);
		historyText.setBackground(GazdOkGUIConf.HISTORY_TEXT_COLOR);

		JScrollPane historyScrollPane = new JScrollPane(historyText);
		historyScrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		historyScrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		historyScrollPane.setPreferredSize(GazdOkGUIConf.HISTORY_TEXT_DIM);
		pnlHistory.add(historyScrollPane, BorderLayout.CENTER);
	}

	private void initPnlStatus() {
		pnlStatus = new JTabbedPane(JTabbedPane.TOP,
				JTabbedPane.SCROLL_TAB_LAYOUT);
		pnlStatus.setPreferredSize(GazdOkGUIConf.STATUSBAR_PREF_DIM);
		pnlStatus.setBackground(GazdOkGUIConf.STATUSBAR_BGCOLOR);
		pnlStatus.setPreferredSize(GazdOkGUIConf.STATUSBAR_PREF_DIM);

		PnlGazdOkStatus gamer1 = new PnlGazdOkStatus("Maki", 18000);

		PnlGazdOkStatus gamer2 = new PnlGazdOkStatus("Baltazár", 1000);
		PnlGazdOkStatus gamer3 = new PnlGazdOkStatus("Rezső", 8936);
		gamer1.setPreferredSize(new Dimension(400, 300));
		// panel.add(new JButton("Panel 1"));
		gamer1.setCar(true);
		pnlStatus.add(gamer1, 0);
		pnlStatus.setTitleAt(0, gamer1.getName());
		
		gamer2.setHouse(true);
		pnlStatus.add(gamer2, 1);
		pnlStatus.setTitleAt(1, gamer2.getName());
		
		gamer3.setFrigo(true);
		pnlStatus.add(gamer3, 2);
		pnlStatus.setTitleAt(2, gamer3.getName());


	}

	private void initPnlContentpane() {
		pnlContentPane = new JPanel(new GridBagLayout());
		pnlContentPane.setBackground(GazdOkGUIConf.CONTENTPANE_BGCOLOR);
		pnlContentPane.setMinimumSize(GazdOkGUIConf.CONTENTPANE_PREF_DIM);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		// gbc.fill = GridBagConstraints.VERTICAL;
		// gbc.anchor = GridBagConstraints.CENTER;
		pnlContentPane.add(pnlGameTable, gbc);

		// gbc.anchor = GridBagConstraints.SOUTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		// gbc.weightx = 1;
		// gbc.weighty = GazdOkGUIConf.STATUSBAR_PREF_DIM.height /
		// GazdOkGUIConf.GAMETABLE_PREF_DIM.height;
		gbc.gridheight = 1;
		pnlContentPane.add(pnlStatus, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		// gbc.weightx = 0.4;
		// gbc.weighty = 1;
		gbc.gridheight = 2;
		pnlContentPane.add(pnlHistory, gbc);

		pnlContentPane.setVisible(true);
	}

	private void initFrame() {
		setTitle("Gazdálkodj okosan!");
		setLocation(0, 0);
		// setResizable(false);
		setContentPane(pnlContentPane);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
	}

	public void showView() {
		EventQueue.invokeLater(new Runnable() {
			View window = new View();
			public void run() {
				try {
					window.refreshView();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public List<String> getLoginInfos() {
		JPanel dialog = new JPanel();
		dialog.setLayout(new BoxLayout(dialog, BoxLayout.Y_AXIS));

		JTextField username = new JTextField(GazdOkGUIConf.DEFAULT_NAME);
		dialog.add(username);
		JTextField host = new JTextField(GazdOkGUIConf.DEFAULT_HOST);
		dialog.add(host);
		JTextField port = new JTextField(GazdOkGUIConf.DEFAULT_PORT);
		dialog.add(port);

		JOptionPane.showMessageDialog(null, dialog, "Message",
				JOptionPane.PLAIN_MESSAGE);

		List<String> loginfos = new ArrayList<String>();
		loginfos.add(username.getText());
		loginfos.add(host.getText());
		loginfos.add(port.getText());
		return loginfos;
	}

	public int getBuyingInfos(String s, boolean c) {
		Object[] options = { "készpénzre", "hitelre", "nem kérem" };
		int answer = JOptionPane.showOptionDialog(null, "Szeretnél " + s
				+ " -t venni?", "Vásárlás", JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		switch (answer) {
		case 0:
			if (c == true) {
				simpleMessage("Sikeresen megvetted a(z) " + s + " -t.");
				return 2;
			} else
				return 0;
		case 1:
			simpleMessage("Sikeresen megvetted a(z) " + s + " -t.");
			return 1;
		case 3:
			return 0;
			
		default:
			return 0;
		}
	}

	public int getInsurance(String s) {
		Object[] options = { "igen", "nem" };
		int answer = JOptionPane.showOptionDialog(null, "Szeretnél " + s
				+ " -t venni?", "Biztosítás", JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		switch (answer) {
		case 0:
			return 0;
		case 1:
			return 1;
		default:
			return 1;
		}
	}

	public int getFurnitureOptions(String s) {
		Object[] options = { "Megvásárol", "Nem kérem!" };
		int answer = JOptionPane.showOptionDialog(this, s, "Berendezés vásárlás",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, options[0]);
		switch (answer) {
		case 0:
			simpleMessage("Vettél egy " + s + " -t!");
			return 0;
		case 1:
			return 1;

		default:
			return 1;
		}

	}

	public String simpleMessage(String message) {
		historyText.append(new SimpleDateFormat("[HH:mm:ss").format(Calendar
				.getInstance().getTime()) + "] : " + message + ENDL);
		return message;
	}

	public void refreshView() {
		List<StateOfPlayer> gameState = Controller.getGameState();
		int noPlayers = gameState.size();
		for (int i = 0; i < gameState.size(); i++) {
			PnlGazdOkStatus gamer = new PnlGazdOkStatus(gameState.get(i).getName(), gameState.get(i).getBalance());
			gamer.setHouse(gameState.get(i).getHouseStateBinaryFlags().charAt(0) == 1);
			gamer.setKitchen(gameState.get(i).getHouseStateBinaryFlags().charAt(1) == 1);
			gamer.setFurniture(gameState.get(i).getHouseStateBinaryFlags().charAt(2) == 1);
			gamer.setFrigo(gameState.get(i).getHouseStateBinaryFlags().charAt(3) == 1);
			gamer.setCooker(gameState.get(i).getHouseStateBinaryFlags().charAt(4) == 1);
			gamer.setDishwasher(gameState.get(i).getHouseStateBinaryFlags().charAt(5) == 1);
			gamer.setWashingMachine(gameState.get(i).getHouseStateBinaryFlags().charAt(6) == 1);
			gamer.setHouseInsurance(gameState.get(i).getHouseStateBinaryFlags().charAt(7) == 1);
			gamer.setCar(gameState.get(i).getCarStateBinaryFlags().charAt(0) == 1);
			gamer.setCarInsurance(gameState.get(i).getCarStateBinaryFlags().charAt(1) == 1);
//			gamer.setFrigo(gameState.get(i).getCarStateBinaryFlags().charAt(2) == 1);
			pnlStatus.setComponentAt(i, gamer);
//			pnlStatus.add(gamer, i);
			pnlStatus.setTitleAt(i, gameState.get(i).getName());
			
		}

		System.out.println("Number of gamers: " + noPlayers);

	}

}
