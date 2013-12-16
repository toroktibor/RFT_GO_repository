package client;

/**
 * Interfész a Controller osztály számára. 
 * Azon metódusokat tartalmazza, melyet az osztály minimálisan meg kell valósítson.
 * 
 * @author Ölveti József
 */
public interface IController {
	/**
	 * A kliens csatlakozását megvalósító metódus.
	 */
	public void login();
	/**
	 * Metódus a házvásárláshoz.
	 *  
	 * @param b logikai érték, mely azt jelöli hogy a játékos vehet-e készpénzre házat
	 */
	public void buyHouse(boolean b);
	/**
	 * Metódus az autóvásárláshoz.
	 * 
	 * @param b logikai érték, mely azt jelöli hogy a játékos vehet-e készpénzre autót
	 */
	public void buyCar(boolean b);
	/** 
	 * Metódus a berendezések vásárlásához.
	 */
	public void buyFurnitures();
	/**
	 * Metódus a biztosítások megkötéséhez.
	 */
	public void makeInsurance();
	/**
	 * Üzenet fogadó metódus a szervertől érkező előüzenetek számára.
	 */
	public void getInitialMessage();
	/**
	 * Üzenet fogadása a szervertől, és továbbítása a GUI-nak.
	 */
	public void getMessageForRead();
	/**
	 * Állapot frissítő üzenetek fogadására szolgáló metódus.
	 */
	public void getGameStateMessage();
}
