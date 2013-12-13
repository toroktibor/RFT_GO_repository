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
	 * a paraméterként kapott dolgot (egy kiírandó szöveg lesz)
	 * hitelre, kp-ra vagy nem akarja megvenni
	 * 
	 * 
	 * ha hitelre akarja 1
	 * ha kp-ra akarja 2
	 * ha nem akarja 0
	 * értékkel tér vissza a metódus
	 * 
	 * **/
	public int getBuyingInfos(String s,boolean c);
	
	/**
	 * Lényege hogy megkérdi hogy akar-e autó,lakás biztosítást kötni, paraméterbe jön a szöveg
	 * 
	 * visszatérés: 
	 * 0- nem akar kötni
	 * 1- autó biztosítás
	 * 2- ház biztosítás	 
	 * 3- mind kettő
	 * 
	 * **/
	public int getInsurances(String s);
	
	/**
	 * Lényege hogy megkérdi vesz-e xyz dolgot, a string-et kiíratjuk
	 * 
	 * visszatérés:
	 * 1 nem
	 * 0 igen
	 * 
	 * **/
	public int getFurnitureOptions(String s);
	
	/**
	 * sima üzenet amit leokézhat
	 * lényeg hogylátja amit közölni akar a szerver. (string paraméterbe jön)
	 * @return 
	 * 
	 * 
	 * **/
	public String simpleMessage(String s);
	
	/**
	 * GUI hoz kell mindenképp megjeleníti az alap ablakot.
	 * **/
	public void showView();
	
	/**
	 * Frissíti a Gui-t, az állapotok alapján.
	 **/
	public void refreshView();
}
