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
	 * Megjelen�ti az alap ablakot ez �gy egyben kell kb
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
	 * Bek�r a felhaszn�l�t�l egy ablakban vagy valahol 3 stringet n�v, host, port 
	 * ki, melyik hostal rendelkez� szerverre, milyen porton akar csatlakozni, le ok�zza
	 * ezt list�ban adja vissza. ez indul�skor a controllerb�l megh�v�dik.
	 **/
	public List<String> getLoginInfos() {
		/* itt t�rt�nik a bek�r�s valahogy */
			/*TODO*/
		
		/*p�lda visszat�r�si �rt�kek, ezek ugye a bek�rtek lesznek*/
			List<String> l=new ArrayList<String>();
			l.add("Jani");
			l.add("localhost");
			l.add("7777");
			return l;
		/*--*/
	}
	
	
	
	
	

}
