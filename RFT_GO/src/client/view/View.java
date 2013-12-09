package client.view;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import client.StateOfPlayer;

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
	 * ki, melyik hostal rendelkező szerverre, milyen porton akar csatlakozni, le okézza
	 * ezt listában adja vissza. ez induláskor a controllerből meghívódik.
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
	
	
	
	/**
	 * lényege hogy egy ablakban megkérdezi a játékost hogy meg akarja e vásárolni
	 * a paraméterként kapott dolgot (vagy autó lesz vagy ház, egy szöveg lesz)
	 * hitelre, kp-ra vagy nem akarja megvenni
	 * felugró ablak közepén valami szöveg pl: akarsz-e @p venni? stb stb
	 * 
	 * ha hitelre akarja 1
	 * ha kp-ra akarja 2
	 * ha nem akarja 0
	 * értékkel tér vissza a metódus
	 * 
	 * **/
	public int getBuyingInfos(String p){
		
			return 0;
	}
	
	
	/**
	 * Lényege hogy felugrik az ablak vagy valami megkérdi hogy akar-e autó,lakás biztosítást kötni
	 * 
	 * visszatérés: 
	 * 0- nem akar kötni
	 * 1- autó biztosítás
	 * 2- ház biztosítás
	 * 3- mind kettö
	 * 
	 * **/
	public int getInsurances(){
		
		return 0;
	}
	
	
	/**
	 * Lényege hogy felugrik egy ablak megkérdi vesze olyan berendezést
	 * amelyet a string tartalmaz
	 * 
	 * visszatérés:
	 * 0 nem
	 * 1 igen
	 * 
	 * **/
	public int getFurnitureOptions(String furniture){
		return 0;
	}

	/**
	 * Felugró ablak sima üzenettel amit leokézhat
	 * lényeg hogy látja amit közölni akar a szerver. (message paraméterbe jön)
	 * 
	 * 
	 * **/
	public void simpleMessage(String message) {
		
	}
	

	public void refreshView(StateOfPlayer sop) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
