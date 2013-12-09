package client.view;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class View implements IView{

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
	
	
	
	/**
	 * l�nyege hogy egy ablakban megk�rdezi a j�t�kost hogy meg akarja e v�s�rolni
	 * a param�terk�nt kapott dolgot (vagy aut� lesz vagy h�z, egy sz�veg lesz)
	 * hitelre, kp-ra vagy nem akarja megvenni
	 * felugr� ablak k�zep�n valami sz�veg pl: akarsz-e @p venni? stb stb de ez m�g kider�l milyen lesz
	 * 
	 * ha hitelre akarja 1
	 * ha kp-ra akarja 2
	 * ha nem akarja 0
	 * �rt�kkel t�r vissza a met�dus
	 * 
	 * **/
	public int getBuyingInfos(String p){
		
			return 0;
	}
	
	
	/**
	 * L�nyege hogy felugrik az ablak vagy valami megk�rdi hogy akar-e aut�,lak�s biztos�t�st k�tni
	 * 
	 * visszat�r�s: 
	 * 0- nem akar k�tni
	 * 1- aut� biztos�t�s
	 * 2- h�z biztos�t�s
	 * 3- mind kett�
	 * 
	 * **/
	public int getInsurances(){
		
		return 0;
	}
	
	
	/**
	 * L�nyege hogy felugrik egy ablak megk�rdi vesze olyan berendez�st
	 * amelyet a string tartalmaz
	 * 
	 * visszat�r�s:
	 * 0 nem
	 * 1 igen
	 * 
	 * **/
	public int getFurnitureOptions(String furniture){
		return 0;
	}

	/**
	 * Felugr� ablak sima �zenettel amit leok�zhat
	 * l�nyeg hogy l�tja amit k�z�lni akar a szerver. (message param�terbe j�n)
	 * 
	 * 
	 * **/
	public void simpleMessage(String message) {
		
	}
	
	public void refreshView(StateOfPlayer sop) {
                
        }
	
	
	/* tov�bbi met�dusok lesznek
	 * biztos kell m�g egy friss�t� met�dus
	 * */
	
}
