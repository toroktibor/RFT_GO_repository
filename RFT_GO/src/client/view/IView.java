package client.view;

import java.util.List;

public interface IView {
	/***
	 * Bekér a felhasználótól valahol 3 stringet név, host, port 
	 * ki, melyik hostal rendelkező szerverre, milyen porton akar csatlakozni
	 * ezt listában adja vissza. ez induláskor a controllerből meghívódik.
	 **/
	public List<String> getLoginInfos();
	
	/**
	 * lényege hogy megkérdezi a játékost hogy meg akarja e vásárolni
	 * a paraméterként kapott dolgot (vagy autó lesz vagy ház, egy szöveg lesz)
	 * hitelre, kp-ra vagy nem akarja megvenni
	 * 
	 * 
	 * ha hitelre akarja 1
	 * ha kp-ra akarja 2
	 * ha nem akarja 0
	 * értékkel tér vissza a metódus
	 * 
	 * **/
	public int getBuyingInfos(String p);
	
	/**
	 * Lényege hogy megkérdi hogy akar-e autó,lakás biztosítást kötni
	 * 
	 * visszatérés: 
	 * 0- nem akar kötni
	 * 1- autó biztosítás
	 * 2- ház biztosítás	 
	 * 3- mind kettő
	 * 
	 * **/
	public int getInsurances();
	
	/**
	 * Lényege hogy megkérdi vesze olyan berendezést
	 * amelyet a string tartalmaz
	 * 
	 * visszatérés:
	 * 0 nem
	 * 1 igen
	 * 
	 * **/
	public int getFurnitureOptions(String furniture);
	
	/**
	 * sima üzenet amit leokézhat
	 * lényeg hogy látja amit közölni akar a szerver. (message paraméterbe jön)
	 * 
	 * 
	 * **/
	public void simpleMessage(String message);
	
	/**
	 * GUI hoz kell mindenképp megjeleníti az alap ablakot.
	 * **/
	public void showView();
	
	/**
	 * Frissíti a Gui-t, paraméterként megkap egy játékost szimbolizáló állapot objektumot.
	 **/
	public void refreshView(StateOfPlayer sop);
}
