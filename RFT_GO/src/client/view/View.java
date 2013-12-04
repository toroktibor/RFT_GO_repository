package client.view;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class View {

	private JFrame frame;

	public View() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	
	
	
	/**
	 * Megjeleníti az alap ablakot ez így egyben kell kb
	 * 
	 * **/
	public void showView() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					View window = new View();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/***
	 * Bekér a felhasználótól egy ablakban vagy valahol 3 stringet név, host, port 
	 * ki, melyik hostal rendelkezõ szerverre, milyen porton akar csatlakozni, le okézza
	 * ezt listában adja vissza. ez induláskor a controllerbõl meghívódik.
	 **/
	public List<String> getLoginInfos() {
		/* itt történik a bekérés valahogy */
			/*TODO*/
		
		/*példa visszatérési értékek, ezek ugye a bekértek lesznek*/
			List<String> l=new ArrayList<String>();
			l.add("Jani");
			l.add("localhost");
			l.add("7777");
			return l;
		/*--*/
	}
	
	
	
	
	

}
