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
	
	
	
	/* tov�bbi met�dusok lesznek
	 * 
	 * getBuyingInfos() l�nyege hogy valaki valamit creditre vagy kp-ra vesz vagy egy�ltal�n nem vesz (3 gomb vagy ilyesmi, a gomb felirat vagy param�ter lesz vagy majd csak a f� sz�veg hogy mit akar venni hitelre kpra vagy nem lesz �tadva param�terk�nt)
	 *
	 * getInsurances() vesz-e aut� vagy lak�s biztos�t�st ( mind2, aut�, lak�s, egyiksem)
	 * 
	 * getFurnitureOptions() megk�rdi hogy megvesz-e valamilyen b�tort, majd param�terk�nt �tj�n hogy milyet, v�lasz igen, nem
	 * 
	 * mind vissza t�r egy eg�sz sz�mmal vagy stringel m�g kital�lom.
	 * 
	 * m�g lehet lesz p�r
	 * */
	
}
