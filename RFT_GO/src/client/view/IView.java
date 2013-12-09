package client.view;

import java.util.List;

public interface IView {
	/***
	 * Bek�r a felhaszn�l�t�l valahol 3 stringet n�v, host, port 
	 * ki, melyik hostal rendelkez� szerverre, milyen porton akar csatlakozni
	 * ezt list�ban adja vissza. ez indul�skor a controllerb�l megh�v�dik.
	 **/
	public List<String> getLoginInfos();
	
	/**
	 * l�nyege hogy megk�rdezi a j�t�kost hogy meg akarja e v�s�rolni
	 * a param�terk�nt kapott dolgot (vagy aut� lesz vagy h�z, egy sz�veg lesz)
	 * hitelre, kp-ra vagy nem akarja megvenni
	 * 
	 * 
	 * ha hitelre akarja 1
	 * ha kp-ra akarja 2
	 * ha nem akarja 0
	 * �rt�kkel t�r vissza a met�dus
	 * 
	 * **/
	public int getBuyingInfos(String p);
	
	/**
	 * L�nyege hogy megk�rdi hogy akar-e aut�,lak�s biztos�t�st k�tni
	 * 
	 * visszat�r�s: 
	 * 0- nem akar k�tni
	 * 1- aut� biztos�t�s
	 * 2- h�z biztos�t�s
	 * 3- mind kett�
	 * 
	 * **/
	public int getInsurances();
	
	/**
	 * L�nyege hogy megk�rdi vesze olyan berendez�st
	 * amelyet a string tartalmaz
	 * 
	 * visszat�r�s:
	 * 0 nem
	 * 1 igen
	 * 
	 * **/
	public int getFurnitureOptions(String furniture);
	
	/**
	 * sima �zenet amit leok�zhat
	 * l�nyeg hogy l�tja amit k�z�lni akar a szerver. (message param�terbe j�n)
	 * 
	 * 
	 * **/
	public void simpleMessage(String message);
	
	/**
	 * GUI hoz kell mindenk�pp megjelen�ti az alap ablakot.
	 * **/
	public void showView();
	
	/**
	 * Frissíti a Gui-t, paraméterként megkap egy játékost szimbolizáló állapot objektumot.
	 **/
	public void refreshView(StateOfPlayer sop);
}
