//15:42
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
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import client.Controller;
import client.StateOfPlayer;

public class View extends JFrame implements IView {

	private static final long serialVersionUID = 1L;
	private static final String ENDL = "\n";
	private static int noHistoryEntries = 10;
	private JPanel pnlContentPane;
	private JPanel pnlGameTable;

	private JPanel pnlHistory;
	private JTextArea historyText;
	private List<String> historyEntries = new ArrayList<String>(
			View.noHistoryEntries);
	private JPanel pnlStatus;

	public View() {
		initialize();
//		foo();
	}

	private void initialize() {
		// simpleMessage("Welcome!");
		// getFurnitureOptions("akarjadmá");
		initPnlGameTable();
		initPnlHistory();
		initPnlStatus();

		initPnlContentpane(); 
		initFrame();
		simpleMessage("A játék inicializálása véget ért.");
		simpleMessage("A játék elkezdődött.\tLegyen Ön is milliomos!");
	}

	private void initPnlGameTable() {
		pnlGameTable = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 14;
		gbc.weighty = 9;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;

		/**
		 * Ez valójában felesleges, mert magától berendezi.
		 */
		Dimension buttonPrefSize = new Dimension(45,45);
		
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
		

		
//		pnlGameTable.add(new JLabel("-Game table-"));
		pnlGameTable.setPreferredSize(GazdOkGUIConf.GAMETABLE_PREF_DIM);
		pnlGameTable.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.RAISED));
		pnlGameTable.setBackground(GazdOkGUIConf.GAMETABLE_BGCOLOR);

	}

	private void initPnlHistory() {

		pnlHistory = new JPanel(new GridBagLayout());

		pnlHistory.setPreferredSize(GazdOkGUIConf.HISTORY_PREF_DIM);
		pnlHistory.setBackground(GazdOkGUIConf.HISTORY_BGCOLOR);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 2;
		gbc.fill = GridBagConstraints.BOTH;
		
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 0;

		Dimension labelHistoryDim = new Dimension();
		labelHistoryDim.width = GazdOkGUIConf.HISTORY_PREF_DIM.width;
		labelHistoryDim.height = 20;
		
		JLabel labelHistory = new JLabel("-history-");
		labelHistory.setPreferredSize(labelHistoryDim);
		pnlHistory.add(labelHistory, gbc);
		
		
		historyText = new JTextArea(20, 30);
		JScrollPane historyScrollPane = new JScrollPane(historyText);
		historyScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		historyScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		historyText.setEditable(false);
		historyText.setLineWrap(true);
		historyText.setWrapStyleWord(true);
		historyText.setAutoscrolls(true);

		Dimension historyScrollPaneDim = new Dimension();
		historyScrollPaneDim.width = GazdOkGUIConf.HISTORY_PREF_DIM.width;
		historyScrollPaneDim.height = GazdOkGUIConf.HISTORY_PREF_DIM.height-20;
		historyScrollPane.setPreferredSize(historyScrollPaneDim);

		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.gridy = 1;
		pnlHistory.add(historyScrollPane, gbc);
		
	}

	private void initPnlStatus() {
		pnlStatus = new JPanel();
		pnlStatus.add(new JLabel("-statusbar-"));
		pnlStatus.setPreferredSize(GazdOkGUIConf.STATUSBAR_PREF_DIM);
		pnlStatus.setBackground(GazdOkGUIConf.STATUSBAR_BGCOLOR);
//		pnlStatus.add(new JButton("Statusbar"));
	}

	private void initPnlContentpane() {
		pnlContentPane = new JPanel(new GridBagLayout());
		pnlContentPane.setBackground(GazdOkGUIConf.CONTENTPANE_BGCOLOR);
		pnlContentPane.setVisible(true);
		pnlContentPane.setPreferredSize(GazdOkGUIConf.WINDOW_PREF_DIM);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 2;
		gbc.weighty = 2;
		gbc.fill = GridBagConstraints.BOTH;

		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		pnlContentPane.add(pnlGameTable, gbc);

		gbc.gridx = 1;
		pnlContentPane.add(pnlHistory, gbc);

		gbc.anchor = GridBagConstraints.PAGE_END;

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		pnlContentPane.add(pnlStatus, gbc);
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
			public void run() {
				try {
					View window = new View();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public List<String> getLoginInfos() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Your Name:");
		String name = sc.nextLine();
		System.out.println("Enter Server Host:");
		String host = sc.nextLine();
		System.out.println("Enter Server Port:");
		String port = sc.nextLine();

		/* p�lda visszat�r�si �rt�kek, ezek ugye a bek�rtek lesznek */
		List<String> l = new ArrayList<String>();
		l.add(name);
		l.add(host);
		l.add(port);
		return l;
		/*--*/
	}

	public int getBuyingInfos(String s, boolean c) {
		Scanner sc = new Scanner(System.in);
		System.out.println(s);
		String st = sc.nextLine();
		if (st.equals("h"))
			return 1;
		else if (st.equals("k"))
			return 2;
		else
			return 0;
	}

	public int getInsurance(String s) {
		Scanner sc = new Scanner(System.in);
		System.out.println(s);
		String st = sc.nextLine();
		if (st.equals("a"))
			return 1;
		else if (st.equals("h"))
			return 2;
		else if (st.equals("m"))
			return 3;
		else
			return 0;
	}

	public int getFurnitureOptions(String s) {
		Object[] options = { "Megvásárol", "Nem kérem!" };
		int n = JOptionPane.showOptionDialog(this, s, "Berendezés vásárlás",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, options[0]);
		return n;
	}

	public String simpleMessage(String message) {
		historyText.append(new SimpleDateFormat("[HH:mm:ss").format(Calendar
				.getInstance().getTime()) + "] : " + message + ENDL);
		return message;
	}

	public void refreshView() {

		List<StateOfPlayer> sp = Controller.getGameState();

	}

	private void foo() {
		Object[] possibilities = { "ham", "spam", "yam" };
		String s = (String) JOptionPane.showInputDialog(pnlContentPane,
				"Complete the sentence:\n" + "\"Green eggs and...\"",
				"Customized Dialog", JOptionPane.PLAIN_MESSAGE, null,
				possibilities, "ham");
	}

}
