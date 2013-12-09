package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class GameEngine implements ICashier, IGamePlay {
	private Player actualPlayer;
	private List<Player> allPlayers = new ArrayList<Player>();
	private List<Field> board = new ArrayList<Field>();
	private List<LuckyCard> deck = new ArrayList<LuckyCard>();
	private int luckyCardIndex = 0;
	private XMLParser p=new XMLParser();
	
	
	//GETTERS AND SETTERS
	public Player getActualPlayer() {
		return actualPlayer;
	}
	public void setActualPlayer(Player actualPlayer) {
		this.actualPlayer = actualPlayer;
	}
	public List<LuckyCard> getDeck() {
		return deck;
	}
	public void setDeck(List<LuckyCard> deck) {
		this.deck = deck;
	}
	public List<Field> getBoard() {
		return board;
	}
	public void setBoard(List<Field> board) {
		this.board = board;
	}
	public List<Player> getAllPlayers() {
		return allPlayers;
	}
	public void setAllPlayers(List<Player> allPlayers) {
		this.allPlayers = allPlayers;
	}
	
	//IMPLEMENTATION OF THE METHODS OF ICASHIER INTERFACE
	/** Ez a metódus az aktuális játékos egyenlegén jóváírja a megfelelő összeget.
	 * 
	 * @param amount az átutlanandó pénzösszeg
	 */
	public void addMoney(int amount) {
		int originalBalance = actualPlayer.getBalance();
		actualPlayer.setBalance(originalBalance+amount);
		System.out.println("###Sikeres pénzhozzáadási tranzakció###");
		return;
	}
	public void addPercentage(int percentage) {
		int originalBalance = actualPlayer.getBalance();
		double result = originalBalance * ((double)percentage / 100);
		addMoney((int)result);
		return;
	}	
	/** Ez a met�dus ellen�rzi, hogy rendelkez�sre �ll-e az aktu�lis j�t�kos egyenleg�n a megfelel� �sszeg.
	 * 
	 * @param amount a k�v�nt p�nz�sszeg
	 * @return igaz, ha rendelkez�sre �ll, �s hamis, ha nem.
	 */
	public Boolean checkBalance(int amount) {
		if(actualPlayer.getBalance() >= amount) {
			System.out.println("###Egyenleg ellen�rizve - IGEN, v�grehajthat� a tranzakci�###");
			return true;
		}
		else {
			System.out.println("###Egyenleg ellen�rizve - NEM v�grehajthat� a tranzakci�###");
			return false;
		}		
	}
	/** Ez a met�dus az aktu�lis j�t�kos egyenleg�r�l levonja a megfelel� �sszeget.
		 * 
		 * @param amount a levonand� p�nz�sszeg
		 * @return
		 */
	public Boolean deductMoney(int amount) {
		int originalBalance = actualPlayer.getBalance();
		System.out.println("###Egyenleg levon�s el�tt: " + originalBalance + " Euro ###");
		if(checkBalance(amount) == true) {
			actualPlayer.setBalance(originalBalance-amount);
			System.out.println("###Sikeres p�nzlevon�si tranzakci�###");
			System.out.println("###Egyenleg levon�s ut�n: " + actualPlayer.getBalance() + " Euro ###");
			return true;
		}
		else {
			System.out.println("###Sikertelen p�nzlevon�si tranzakci� - nem elegend� az egyenleg###");
			
			return false;
		}
	}
	/** Ez a met�dus minden k�r v�g�n h�v�dik meg, �s ha van az aktu�lis j�t�kosnak h�za, illetve aut�ja
	 * akkor levonja az esetleges h�tral�v� tartoz�sb�l a k�r�nk�nt k�telez�en t�rlesztend� 500 eur�t.
	 * Ha nem tudja levonni, a j�t�kos kiesett, az isActiva v�ltoz� �rt�k�t ennek megfelel�en �t�ll�tja, 
	 * �s hamis �rt�kkel t�r vissza, ha pedig siker�lnek a tranzakci�k, akkor igaz visszat�r�si �rt�kkel.
	 */
	public Boolean handleDebits() {
		if(actualPlayer.getHouse() != null) {
			int houseDebit = actualPlayer.getHouse().getDebit();
			if(houseDebit != 0) {
				if(checkBalance(500) == false) {
					System.out.println("Vesztett�l, mert nem tudsz t�rleszteni.");
					actualPlayer.setIsActive(false);
					return false;
				}
				else {
					actualPlayer.getHouse().setDebit(houseDebit-500);
				}
			}
		}
		if(actualPlayer.getCar() != null) {
			int carDebit = actualPlayer.getCar().getDebit();
			if(carDebit != 0) {
				if(checkBalance(500) == false) {
					System.out.println("Vesztett�l, mert nem tudsz t�rleszteni.");
					actualPlayer.setIsActive(false);
					return false;
				}
				else {
					actualPlayer.getCar().setDebit(carDebit-500);
				}
			}	
		}
	return true;
	}
	
	
	//IMPLEMENTATION OF THE METHODS OF IGAMEPLAY INTERFACE
	/** Ez a met�dus a @param allMethosdNameList met�dus list�ban keresi
	 * a @param goalMethodsName nev� met�dust, �s visszaadja az index�t, ha megtal�lta.
	 * 
	 * @param allMethodsNameList a lista, amiben keress�k a met�dust
	 * @param goalMethodsName a keresend� met�dus neve
	 * @return a keresett met�dus indexe a list�ban.
	 */
	private int giveIndexOfSearchedMethod(Method[] allMethodsNameList, String goalMethodsName ) {
		int indexOfSearchedMethod = -1;
		for(int i = 0; i<allMethodsNameList.length; ++i) {
			if(allMethodsNameList[i].toString().contains(goalMethodsName)) {
				indexOfSearchedMethod = i;;
			}
		}
		return indexOfSearchedMethod;
	}
	public void initFields() {
			board=p.parseField("Fields.xml");
		/*
			board.add( new Field( 0, "START mez�! �j k�r kezdetekor, ha erre a mez�re l�psz, kapsz 4000 eur�t, ha �thaladsz rajta, " +
										"akkor 2000 eur�t.", 
										"1#addMoney#4000"));
			board.add( new Field( 1, "Lak�sodat sz�pp� var�zsolhatod, ha a Diego boltban v�s�rolt sz�nyeggel d�sz�ted. " +
										"A v�s�rl�s�rt fizess 200 eur�t.", 
										"1#deductMoney#200"));
			board.add( new Field( 2, "V�s�rolj BKV �ves b�rletet 200 eur��rt!", 
										"1#deductMoney#200"));
			board.add( new Field( 3, "H�zz egy SZERENCSEKER�K k�rty�t, �s k�vesd annak utas�t�sait !", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 4, "T�lts el egy kellemes est�t a Zila K�v�h�z- �s �tteremben! Fizess 40 eur�t!", 
										"1#deductMoney#40"));
			board.add( new Field( 5, "Ha nem szeretsz gyalog j�rni, vagy ha �gyeid int�z�s�hez aut�ra van sz�ks�ged, most v�laszthatsz egy " +
									"Citroene C4-es aut�t. Kp. v�s�rl�s eset�n: 10.000 eur�, r�szletre t�rt�n� v�s�rl�s " +
									"eset�n kezd� befizet�s: 2.000 eur�, minim�lis t�rleszt�s k�r�nk�nt: 500 eur�, v�g�sszeg 12.000 eur�", 
										"1#offerBuyCar"));
			board.add( new Field( 6, "�gyesen v�s�rolt�l a Regi� J�t�kkeresked�sben! Jutalmad 50 eur�!", 
										"1#addMoney#50"));
			board.add( new Field( 7, "�teleid megroml�s�t elker�lheted, ha h�t�ben t�rolod �ket! V�s�rolj h�t�g�pet 200 eur��rt!", 
										"1#offerBuyFurniture#FRIGO"));
			board.add( new Field( 8, "A csal�di eb�dek elk�sz�t�s�hez v�s�rolj modern t�zhelyet 200 eurt��rt!", 
										"1#offerBuyFurniture#COOKER"));
			board.add( new Field( 9, "Megk�theted aut� biztos�t�sodat �s lak�sbiztos�t�sodat 100-100 eur��rt az Allianz Hung�ria Biztos�t�n�l!", 
										"1#offerMakeInsurances"));
			board.add( new Field( 10, "H�zz egy SZERENCSEKER�K k�rty�t, �s k�vesd annak utas�t�sait !", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 11, "Ha van p�nzed �s lak�sod, v�s�rolj sz�p konyhab�tort 1.000 eur��rt!", 
										"1#offerBuyFurniture#KITCHENFURNITURE"));
			board.add( new Field( 12, "Megl�togattad f�v�rosunk kedvenc �llatait az �llat- �s N�v�nykertben.", 
										"1#addMoney#200"));
			board.add( new Field( 13, "Betegs�ged idej�re k�rh�zba ker�lsz, �s csak akkor l�phetsz tov�bb, ha l-est, vagy 6-ost dobsz. " +
										"A 3. sikertelen dob�s ut�n b�rmilyen dob�ssal kil�phetsz.", 
										"1#set_1_6Penalty#3"));
			board.add( new Field( 14, "T�nkrement a cip�d, v�s�rolnod kell egy �jat a cip�boltb�l. Fizess 80 eur�t.", 
										"1#deductMoney#80"));
			board.add( new Field( 15, "Gondolt�l a k�rnyezetv�delemre, �s a k�z�ss�gi k�zleked�st v�lasztottad utaz�sodhoz. " +
										"Ez�rt jutalmul m�g k�tszer dobhatsz.", 
										"1#setGiftDices#2"));
			board.add( new Field( 16, "H�zz egy SZERENCSEKER�K k�rty�t, �s k�vesd annak utas�t�sait !", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 17, "A film tan�t, sz�rakoztat. N�zd meg a leg�jabb sikerfilmet bar�taiddal a " +
										"Corvin filmpalot�ban, �s fizess 20 eur�t.", 
										"1#deductMoney#20"));
			board.add( new Field( 18, "Minden k�nyv�jdons�got megtal�lsz, �s kedvedre b�ng�szhetsz az Alexandra K�nyv�ruh�zban! " +
										"A v�s�rolt k�nyvek�rt fizess 60 eur�t.", 
										"1#deductMoney#60"));
			board.add( new Field( 19, "Takar�koskodj, mert �gy sz�p lak�shoz juthatsz. Ha m�r van p�nzed, fizess be 30.000 eur�t " +
										"a p�nzt�rba �s megkapod lak�sod. Amennyiben r�szletfizet�sre van csak lehet�s�ged, " +
										"fizess 15.000 eur�t az OTP Bank p�nzt�r�ba, a fennmarad� 20.000 eur�t " +
										"pedig 500 eur�s r�szletekben t�rlesztheted!", 
										"1#offerBuyHouse"));
			board.add( new Field( 20, "Mobiltelefont vett�l, �s bel�pt�l a GSM h�l�zatba. Fizess 100 eur�t.", 
										"1#deductMoney#100"));
			board.add( new Field( 21, "Pihen�s? Felt�lt�d�s? Kikapcsol�d�s? V�laszt a Klub Tihanyt! Fizess 280 eur�t! M�g egyszer dobhatsz!", 
										"2#deductMoney#280#setGiftDices#1"));
			board.add( new Field( 22, "Megtekintetted a Nemzeti Gal�ria gy�ny�r� ki�ll�t�s�t.", "0"));
			board.add( new Field( 23, "H�zz egyet a SZERENCSEKER�K k�rty�b�l, �s k�vesd annak utas�t�sait!", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 24, "Visegr�di haj�kir�ndul�son veszel r�szt bar�taiddal, amely�rt 40 eur�t kell fizetned.", 
										"1#deductMoney#40	"));
			board.add( new Field( 25, "Ma a Kakas �tteremben vacsor�zol csal�doddal, fizess 40 eur�t.", 
										"1#deductMoney#40"));
			board.add( new Field( 26, "Kellemes s�ta k�zben tekintsd meg a Margitsziget nevezetess�geit !", "0"));
			board.add( new Field( 27, "V�s�rolj mos�g�pet a Whirpool M�rkakeresked�sben, fizess 300 eur�t!", 
										"1#offerBuyFurniture#WASHMACHINE	"));
			board.add( new Field( 28, "H�zz egyet a SZERENCSEKER�K k�rty�b�l, �s k�vesd annak utas�t�sait !", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 29, "S�t�lj a Hal�szb�sty�n. Innen gy�ny�r� kil�t�s ny�lik Budapestre.", "0"));
			board.add( new Field( 30, "A Vista Utaz�si irod�kban elint�zhetsz az utaz�soddal kapcsolatban mindent k�nyelmesen �s gyorsan! " +
										"Fizess 300 eur�t!", 
										"1#deductMoney#300"));
			board.add( new Field( 31, "Bel�pt�l a PICK M�rka�ruh�zba. 20 eur�t fizetsz.", 
										"1#deductMoney#20"));
			board.add( new Field( 32, "H�zz egyet a SZERENCSEKER�K k�rty�b�l, �s k�vesd annak utas�t�sait!", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 33, "Ha van p�nzed, v�s�rolj Electrolux mosogat�g�pet, melynek �ra 300 eur�!", 
										"1#offerBuyFurniture#DISHWASHER"));
			board.add( new Field( 34, "A SkyEurope olcs� �s gyors utaz�st biztos�t Eur�pa nagyv�rosaiba, mindezt csak 300 eur��rt!", 
										"1#deductMoney#300"));
			board.add( new Field( 35, "V�s�rolt�l a regiojatek.hu web�ruh�zban, ez�rt fizess 20 eur�t!", 		
										"1#deductMoney#20"));
			board.add( new Field( 36, "Ma a bar�taiddal a Mammut Mozi el�ad�s�t n�zted meg, fizess 20 eur�t.",
										"1#deductMoney#20"));
			board.add( new Field( 37, "H�zz egyet a SZERENCSEKER�K k�rty�b�l, �s k�vesd annak utas�t�sait!", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 38, "Ha m�r van lak�sod, most v�s�rolhatsz bele szobab�tort, mind�ssze 3.000 eur��rt!", 
										"1#offerBuyFurniture#ROOMFURNITURE"));
			board.add( new Field( 39, "Takar�koskodj, mert �gy sz�p lak�shoz juthatsz. Ha m�r van p�nzed, fizess be 30.000 eur�t a p�nzt�rba �s megkapod lak�sod. " +
					"					Amennyiben r�szletfizet�sre van csak lehet�s�ged, fizess 15.000 eur�t az OTP Bank p�nzt�r�ba, a fennmarad� 20.000 eur�t " +
					"					pedig 500 eur�s r�szletekben t�rlesztheted!", 
										"1#offerBuyHouse"));
			board.add( new Field( 40, "Az Inviteln�l kifizetted a havi telefon- �s internetsz�ml�dat, melynek �sszege 20 eur�.", 
										"1#deductMoney#20"));
			board.add( new Field( 41, "Bel�pt�l az IKEA �ruh�zba! Konyhafelszerel�s�rt fizess 100 eur�t!", 
										"1#deductMoney#100"));
										
										*/
			return;
		}
	public void initLuckyCards() {
		deck=p.parseLuckyCards("LuckyCards.xml");
		/*deck.add(new LuckyCard( 1 ,"Fizesd ki g�z- �s villanysz�ml�dat a foly�sz�ml�n kereszt�l, melynek �sszege 40 eur�!","1#deductMoney#40" ));						
		deck.add(new LuckyCard( 2 ,"A Budapesti Nemzetk�zi V�s�r sorsj�t�k�n mos�g�pet nyert�l!","1#wonWashMachine" ));						
		deck.add(new LuckyCard( 3 ,"�j�t�s�rt 2.500 eur�t kapsz, melyet a p�nzt�r fizet ki!","1#addMoney#2500" ));						
		deck.add(new LuckyCard( 4 ,"J� munk�d�rt 1.000 eur� jutalomban r�szes�lsz, vedd fel a p�nzt�rb�l!","1#addMoney#1000" ));						
		deck.add(new LuckyCard( 5 ,"H�rmas tal�latod volt a lott�n, 800 eur�t kapsz a p�nzt�rt�l!","1#addMoney#800" ));						
		deck.add(new LuckyCard( 6 ,"A lott� 10. j�t�khet�n 10.000 eur�t nyert�l, amelyet a p�nzt�r fizet ki!"," 1#addMoney#10000" ));						
		deck.add(new LuckyCard( 7 ,"A lott� nyerem�nysorsol�s�n mosogat�g�pet nyert�l!","1#wonDishWasher" ));						
		deck.add(new LuckyCard( 8 ,"A tot�n 12-es tal�lattal 6.000 eur�t nyert�l, amelyet a p�nzt�r fizet ki!","1#addMoney#6000" ));						
		deck.add(new LuckyCard( 9 ,"A tot�n 10-es tal�lattal 400 eur�t nyert�l, amelyet a p�nzt�r fizet ki!","1#addMoney#400" ));						
		deck.add(new LuckyCard( 10 ,"A tot�n 11-es tal�lattal 2.000 eur�t nyert�l, amelyet a p�nzt�r fizet ki!","1#addMoney#2000" ));						
		deck.add(new LuckyCard( 11 ,"Sorsj�t�kon szobab�tort nyert�l! Ha nincs lak�sod, 3.000 eur�t fizet ki a p�nzt�r!","1#wonRoomFurniture" ));						
		deck.add(new LuckyCard( 12 ,"Takar�koskodj! A megtakar�tott p�nzed ut�n 7% azonnali kamatot kapsz!","1#addPercentage#7" ));						
		deck.add(new LuckyCard( 13 ,"J�l takar�koskodt�l, ez�rt az OTP Bank Nyrt. a foly�sz�ml�don elhelyezett p�nzed ut�n 15% kamatot fizet!","1#addPercentage#15" ));						
		deck.add(new LuckyCard( 14 ,"Err�l a mez�r�l csak akkor l�phetsz tov�bb, ha 1-est, vagy 6-ost dobsz!","1#set_1_6Penalty#3" ));						
		deck.add(new LuckyCard( 15 ,"L�pj el�re 1 mez�t!","1#moveWithQuantity#1" ));						
		deck.add(new LuckyCard( 16 ,"L�pj vissza 3 mez�t!","1#moveWithQuantity#-3" ));						
		deck.add(new LuckyCard( 17 ,"Egyszer kimaradsz a dob�sb�l!","1#setExclusions#1" ));						
		deck.add(new LuckyCard( 18 ,"Kivetted �ves szabads�god! K�tszer kimaradsz a dob�sb�l!","1#setExclusions#2" ));						
		deck.add(new LuckyCard( 19 ,"Menj a Start mez�re, �gy kapsz a p�nzt�rt�l 4.000 eur�t!"," 1#moveToField#0" ));						
		deck.add(new LuckyCard( 20 ,"Lak�sod d�sz�tsd ir�ni �s afg�n k�zi csom�z�s� perzsa sz�nyeggel! L�pj az 1-es mez�re!","1#moveToField#1" ));						
		deck.add(new LuckyCard( 21 ,"Most vedd meg �lmaid aut�j�t! L�pj az 5-�s mez�re, �s �lvezd az �j aut�dat!","1#moveToField#5" ));						
		deck.add(new LuckyCard( 22 ,"Ellopt�k az aut�dat. Ha van aut� biztos�t�sod, a bank kifizeti a k�rodat. Ha m�g nincs, l�pj a 9-es mez�re, a biztos�t�n�l megk�theted!","2#loseCar#moveToField#9" ));						
		deck.add(new LuckyCard( 23 ,"Ki�gett a lak�sod, �gy elvesztetted az �sszes berendez�si t�rgyadat! Ha van lak�sbiztos�t�sod, a biztos�t� megt�r�ti a k�rodat. Ha m�g nincs, l�pj a 9-es mez�re, a biztos�t�n�l megk�theted!","2#loseFurnitures#moveToField#9" ));						
		deck.add(new LuckyCard( 24 ,"L�pj a 11-es mez�re, ahol konyhab�tort v�s�rolhatsz, ha m�r van lak�sod!","1#moveToField#11" ));						
		deck.add(new LuckyCard( 25 ,"T�rj be cip�t v�s�rolni a Deichmann valamelyik �zlet�be! K�nyelmes cip�ben l�pj a 14-es mez�re!","1#moveToField#14" ));						
		deck.add(new LuckyCard( 26 ,"L�togass el az Alexandra K�nyv�ruh�zakba. Egy sz�r�lappal kapott 20 eur�s kupont felhaszn�lhatsz, amit a v�g�sszegb�l levonnak. L�pj a 18-as mez�re.","2#addMoney#20#moveToField#18" ));						
		deck.add(new LuckyCard( 27 ,"Pihen�s? Felt�lt�d�s? Kikapcsol�d�s? V�laszt a Klub Tihanyt! L�pj a 21-es mez�re, �s fizess 280 eur�t! M�g egyszer dobhatsz!","1#moveToField#21" ));						
		deck.add(new LuckyCard( 28 ,"Aj�nd�kba kapt�l egy vouchert egy visegr�di haj�kir�ndul�sra. Bel�p�skor �rv�nyes�tve ingyen kir�ndulhatsz. Indul�shoz l�pj a 24-es mez�re!","2#addMoney#40#moveToField#24" ));						
		deck.add(new LuckyCard( 29 ,"Eb�delj bar�taiddal �tteremben! Csendes, b�k�s, mediterr�n hangulat� helyen, ahov� mindig sz�vesen fogsz visszat�rni! L�pj a 25-�s mez�re!","2#addMoney#40#moveToField#25" ));						
		deck.add(new LuckyCard( 30 ,"Menj ki a Margitszigetre aut�busszal, l�pj a 26-os mez�re!","1#moveToField#26" ));						
		deck.add(new LuckyCard( 31 ,"V�s�rolj mos�g�pet a Hajd� M�rkakeresked�sben! L�pj a 27-es mez�re!","1#moveToField#27" ));						
		deck.add(new LuckyCard( 32 ,"A Vista Utaz�si Irod�k nyerem�nysorsol�s�n utaz�st nyert�l! Kipihenten folytathatod utadat! L�pj a 30-as mez�re!","2#addMoney#300#moveToField#30" ));						
		deck.add(new LuckyCard( 33 ,"J�l v�lasztott�l! 20 eur��rt telepakoltad a kosaradat mindenf�le PICK finoms�ggal! L�pj a 31-es mez�re!","1#moveToField#31" ));						
		deck.add(new LuckyCard( 34 ,"H�ztart�sodat mosogat�g�ppel szerelheted fel, amely megk�nny�ti h�tk�znapjaidat. L�pj a 33-as mez�re!","1#moveToField#33" ));						
		deck.add(new LuckyCard( 35 ,"J�l v�lasztott�l, a SkyEurope gyorsan �s olcs�n elrep�t Eur�pa nagyv�rosaiba. Fizesd ki rep�l�jegyed �r�t, amely 300 eur�, ezut�n l�pj a 34-es mez�re!","1#moveToField#34" ));						
		deck.add(new LuckyCard( 36 ,"Az Inviteln�l 2in1 csomagra szerz�dt�l, mely a telefon mellett az Internet hozz�f�r�s d�j�t is tartalmazza, �gy ez csak 20 eur�ba ker�l! Fizet�s ut�n l�pj a 40-es mez�re!","1#moveToField#40" ));
	*/
		return;
		}
	/** Ez a met�dus v�gzi el a kock�val val� dob�st.
	 *  A met�dusban ellen�rz�sre ker�l, hogy nincs-e 1-6 b�ntet�sben a j�t�kos, mert ha igen,
	 *  akkor csak megfelel� �rt�k� dob�s eset�n h�v�dik meg a {@code moveWithQuantity()} met�dus.
	 *  Minden esetben t�j�kozhatjuk a j�t�kost sz�veges �zenet form�j�ban az eredm�nyr�l.
	 */
	public void dice() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Random generator = new Random();
		int result = generator.nextInt(5);
		result+=1;
		if( 0 < actualPlayer.get_1_6Penalty()) {
			if((result==1) || (result ==6)) {
			actualPlayer.set_1_6Penalty(0);
			sendMessageForRead("B�ntet�sben volt�l, mely szerint csak 1-es vagy 6-os dob�ssal l�phetsz tov�bb," +
								" de mivel dob�sod �rt�ke " + result + ", �gy l�pj el�re ennyi mez�t!");
			moveWithQuantity(result);
			return;
			}
			else {
				sendMessageForRead("B�ntet�sben vagy, mely szerint csak 1-es vagy 6-os dob�ssal l�phetsz tov�bb. " +
									"Most itt maradsz, mert dob�sod �rt�ke " + result + ".");
				return;
			}
		}
		else {
			sendMessageForRead("Dob�sod �rt�ke " + result + ". L�pj el�re ennyi mez�t!" );
			moveWithQuantity(result);
			return;
		}
	}
	public void executeFieldCommand() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		System.out.println(actualPlayer.getLocation().getDescription()); //le lesz cser�lve a k�vetkez� sorra...
		//sendMessageForRead(actualPlayer.getLocation().getDescription());
		// Az aktu�lis j�t�kos mez�j�nek Command adattagja, a parancsszavakat tartalmaz� 
		// string feldarabol�sa '#' karakterek ment�n, eredm�ny a commandWords String t�mb.
		int commandWordIterator = 0;
		int methodIterator;
		String[] commandWords = actualPlayer.getLocation().getCommand().split("#");
		String executableMethodsName;
		// A commandWords String t�mb els� eleme a v�grehajtand� met�dusok sz�ma.
		int numberOfExecutableMethods = Integer.parseInt(commandWords[commandWordIterator++]);
		System.out.println("V�grehajtand� met�dusok sz�ma: " + numberOfExecutableMethods);
		// Ennek megfelel� sz�m� met�dust kell megh�vni. (ez egy�bk�nt max. 2 lesz.)
		// Lek�rj�k az oszt�lyt�l a met�dusok list�j�t, hogy majd ezek k�z�l egyet megh�vhassunk.
		Method[] methods = GameEngine.class.getDeclaredMethods();
		/*System.out.println("Az oszt�ly met�dusai, ezek k�z�tt keres�nk");
		for(int i=0; i<methods.length; ++i) {
			System.out.println(methods[i].getName());
		}
		*/
		for(methodIterator = 0; methodIterator<numberOfExecutableMethods; ++methodIterator) {
			// Az executableMethodsName v�ltoz�ban r�gz�tem a v�grehajtand� met�dus nev�t.
			executableMethodsName = commandWords[commandWordIterator++];
			System.out.println("###V�grehajtand� met�dus: " + executableMethodsName + " ###");
			// Az actMet met�dusban r�gz�tem a v�grehajtand� met�dus objektumot.
			Method actMet = methods[giveIndexOfSearchedMethod(methods, executableMethodsName)];
			// Megvizsg�lom a met�dus neve alapj�n, hogy h�ny param�tere lesz, azokat r�gz�tem, �s megh�vom a met�dust.
			
			if(	executableMethodsName.equals("addMoney") || 
				executableMethodsName.equals("deductMoney") || 
				executableMethodsName.equals("actualPlayer.set_1_6Penalty") || 
				executableMethodsName.equals("actualPlayer.setGiftDices")  ||
				executableMethodsName.equals("moveToField") ||
				executableMethodsName.equals("moveWithQuantity") ||
				executableMethodsName.equals("set_1_6Penalty") ||
				executableMethodsName.equals("setGiftDices") ||
				executableMethodsName.equals("setExclusion")) {
				
				int param1 = Integer.parseInt(commandWords[commandWordIterator]);
				actMet.invoke(this, param1);
			}
			else if(executableMethodsName.equals("offerBuyFurniture") || executableMethodsName.equals("sendMessageForRead") ) {
				String param1 = commandWords[commandWordIterator];
				actMet.invoke(this, param1);
			}
			else if(executableMethodsName.equals("offerBuyHouse") || executableMethodsName.equals("offerBuyCar") 
					|| executableMethodsName.equals("offerMakeInsurances") || executableMethodsName.equals("drawNextLuckyCard")) {
				actMet.invoke(this);
			}
			commandWordIterator++;
		}
		
	return;	
	}
	/*
	public void executeFieldCommand() {

		sendMessageForRead(actualPlayer.getLocation().getDescription());
		switch(actualPlayer.getLocationNumber()) {
		case 0	: {	addMoney(4000);	break; }
		case 1	: {	deductMoney(200);	break; }
		case 2	: {	deductMoney(200);	break; }
		case 3	: {	deck.drawNextLuckyCard();	break; }
		case 4	: {	deductMoney(40);	break; }
		case 5	: {	offerBuyCar();	break; }
		case 6	: {	addMoney(50);	break; }
		case 7	: {	offerBuyFurniture("FRIGO");	break;	}
		case 8	: {	offerBuyFurniture("COOKER");	break; }
		case 9	: {	offerMakeInsurances();	break; }
		case 10	: {	deck.drawNextLuckyCard();	break; }
		case 11	: {	offerBuyFurniture("KITCHENFURNITURE");	break; }
		case 12	: {	addMoney(200);	break; }
		case 13	: {	actualPlayer.set_1_6Penalty(3);	break; }
		case 14	: {	deductMoney(80);	break; }
		case 15	: {	actualPlayer.setGiftDices(2);	break; }
		case 16	: {	deck.drawNextLuckyCard();	break; }
		case 17	: {	deductMoney(20);	break; }
		case 18	: {	deductMoney(60);	break; }
		case 19	: {	offerBuyHouse();	break; }
		case 20	: {	deductMoney(100);	break; }
		case 21	: {	deductMoney(280); 
					actualPlayer.setGiftDices(1);
					break; }
		case 22	: {	break; }
		case 23	: {	deck.drawNextLuckyCard(); break; }
		case 24	: {	deductMoney(40); break; }
		case 25	: {	deductMoney(40); break; }
		case 26	: {	break; }
		case 27	: {	offerBuyFurniture("WASHMACHINE"); break; }
		case 28	: {	deck.drawNextLuckyCard(); break; }
		case 29	: { break;	 }
		case 30	: {	deductMoney(300); break; }
		case 31	: {	deductMoney(20); break; }
		case 32	: {	deck.drawNextLuckyCard(); break; }
		case 33	: {	offerBuyFurniture("DISHWASHER"); break; }
		case 34	: {	deductMoney(300); break; }
		case 35	: {	deductMoney(20); break; }
		case 36	: {	deductMoney(20); break; }
		case 37	: {	deck.drawNextLuckyCard(); break; }
		case 38	: {	offerBuyFurniture("ROOMFURNITURE"); break; }
		case 39	: {	offerBuyHouse(); break; }
		case 40	: {	deductMoney(20); break; }
		case 41	: {	deductMoney(100); break; }
		}
		return;
}
*/
	public void executeLuckyCardCommand() {
		return;
	}
	/** Ebben a met�dusban k�l�nb�z� j�t�kosokhoz k�l�nb�z� socketeket rendel�nk.
	 * Amint �rkezik egy kapcsol�d�si k�relem, az adott socket inputStream-j�b�l kinyerj�k a kliens �zenet�t, 
	 * amely a j�t�kos neve lesz, ily m�don az �j j�t�kost hozz�adjuk a j�t�kosok list�j�hoz.
	 * 2 j�t�kos csatlakoz�sa ut�n 2 perces (azaz 120000 ms) t�relmi id� van, am�g tov�bbi j�t�kosokra v�rakozunk.
	 * Ezt k�vet�en elindul a j�t�k a startGame() met�dusba t�rt�n� visszat�r�ssel.
	 * SZERKESZT�S ALATT! M�G NEM TESZTELVE! V�RJA A KRITIK�KAT! :)
	 */
	public void waitForPlayers(int maxNumberOfPlayers) throws IOException { //EZ EG�SZEN M�S LESZ....
		//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//System.out.print("What is your name ? Type here --> ");
		//String playerName = br.readLine();
		//allPlayers.add(new Player(playerName, board.get(0)));	
		//br.close();
		List<Socket> socketList = new ArrayList<Socket>();
		DataInputStream in;
		DataOutputStream out;
		String tmpString;
		Calendar startTime = Calendar.getInstance() ;
		Calendar endTime = Calendar.getInstance();
		while((allPlayers.size() < maxNumberOfPlayers) && (endTime.getTimeInMillis()-startTime.getTimeInMillis() < 120000 ) ) {
			socketList.add(new Socket());
			while(socketList.get(socketList.size()-1).isConnected() == false) {}
			in  = new DataInputStream (socketList.get(socketList.size()-1).getInputStream());
			out = new DataOutputStream(socketList.get(socketList.size()-1).getOutputStream());
			tmpString = in.readUTF();
			allPlayers.add(new Player(tmpString, socketList.get(socketList.size()-1) ,board.get(0)));
			if( 2 <= allPlayers.size() ) {
				endTime = Calendar.getInstance();
			}
		}
		return;
	}
	public void sendGameState() {
		// TODO Auto-generated method stub //majd itt egyeztess�nk mert az �zenet v�lt�s �rdekes :)
		
	}
	/**Ebben a met�dusban el�sz�r megh�vjuk a waitForPlayers() met�dust, amelyben 6 becsatlakoz� j�t�kosra v�runk.
	 * Amennyiben a met�dust�l a vez�rl�st visszakapjuk, elind�tjuk a t�nyleges j�t�kot, azaz
	 * sorra eld�ntj�k, hogy ki k�vetkezik dobni, �s aki k�vetkezik az dobhat-e, vagy �ppen kimarad, illetve, ha
	 * t�bbsz�r is dobhat, akkor t�bb lehet�s�get kap a szab�lyoknak megfelel�en.
	 * Ez eg�szen addig megy, m�g egy j�t�kos meg nem nyerte a j�t�kot.
	 * Nyer�s eset�n a gy�ztest, �s a t�bbieket is �rtes�tj�k.
	 * 
	 */
	public void startGame() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		int iterator = 0;
		waitForPlayers(6);
		do {
			setActualPlayer(allPlayers.get(iterator));
			if( actualPlayer.getExclusions() == 0) {
				dice();
				executeFieldCommand();
				while( actualPlayer.getGiftDices() != 0 ) {
					dice();
					executeFieldCommand();
					actualPlayer.setGiftDices(actualPlayer.getGiftDices()-1);
				}
			}
			else if(actualPlayer.getExclusions() > 0) {
				actualPlayer.setExclusions(actualPlayer.getExclusions()-1);
			}
			iterator += 1;
			iterator = iterator % (allPlayers.size()+1);
		} while (actualPlayer.isWinner() == false);
		
		String winnersName = actualPlayer.getName();
		for(int i = 0; i < allPlayers.size()-1; ++i) {
			setActualPlayer(allPlayers.get(i));
			if(actualPlayer.isWinner() == false) {
				sendMessageForRead("�nnek most nem volt szerencs�je, " + winnersName + " nyerte meg a j�t�kot.");
			}
			else if(actualPlayer.isWinner() == true) {
				sendMessageForRead("Gratul�lunk, " + winnersName + "! Sz�p j�t�k volt, �n nyert!");
			}
		}
	}

	//CALLABLE METHODS OF FIELD AND LUCKYCARD COMMANDS
	private LuckyCard drawNextLuckyCard() {
		int nextIndex = luckyCardIndex++;
		return deck.get(nextIndex%42);
	}
	private void loseCar() {
		
	}
	private void loseFurnitures() {
		
	}
	private void moveWithQuantity(int amount) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		int newPositionNumber = actualPlayer.getLocationNumber() + amount;
		System.out.println("###Player will move from Field No. " + actualPlayer.getLocationNumber() + " with " + amount + " fields.###");
		actualPlayer.setLocation(board.get(newPositionNumber%42));
		System.out.println("###Player moved to Field No. " + actualPlayer.getLocationNumber() + " ###");
		if(newPositionNumber > 42) {
			System.out.println("###K�rnek v�ge. A Start mez�n �thaladt�l, ez�rt kapsz 2000 eur�t, majd " + 
								"levon�sra ker�lnek k�telez� t�rleszt�r�szleteid. Ha nincs arra el�g p�nzed, vesztett�l.###");
			if(handleDebits()==true)
				executeFieldCommand();
			return;
		}
		else if(newPositionNumber == 42) {
			System.out.println("###K�rnek v�ge. A Start mez�re l�pt�l, ez�rt kapsz 4000 eur�t, majd " + 
					"levon�sra ker�lnek k�telez� t�rleszt�r�szleteid. Ha nincs arra el�g p�nzed, vesztett�l.###");
			executeFieldCommand();
			handleDebits();
		}
		else
			executeFieldCommand();
		return;
	}
	private void moveToField(int goalFieldsNumber) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(( -1 < goalFieldsNumber ) && ( goalFieldsNumber < 42 ))
			{
			System.out.println("###Player will move from Field No. " + actualPlayer.getLocationNumber());
			actualPlayer.setLocation(board.get(goalFieldsNumber));
			System.out.println("###Player has benn moved to Field No. " + actualPlayer.getLocationNumber());
			
			}
		else
			System.out.println("###Invalid parameter for moveToField() method, it must be between [0, 41]###");
		executeFieldCommand();
	}
	private void offerBuyCar() {
		System.out.println("###Buying of Car Offered###");
	}
	private void offerBuyFurniture(String string) {
		System.out.println("###Buying of Furniture Offered: " + string + " ###");
	}
	private void offerBuyHouse() {
		System.out.println("###Buying of House Offered###");
	}
	private void offerMakeInsurances() {
		System.out.println("###Making of Insurances Offered###");
	}
	private void sendMessageForRead(String description) {
		System.out.println("###Message For Read Is The Following###");
		System.out.println(description);
		System.out.println("###Message Has Been Sent.");
	}	
	private void set_1_6Penalty(int amount) {
		actualPlayer.set_1_6Penalty(amount);
		return;
	}
	private void setExclusions(int amount) {
		actualPlayer.setExclusions(amount);
		return;
	}
	private void setGiftDices(int amount) {
		actualPlayer.setGiftDices(amount);
		return;
	}
	private void wonFurniture(String furnitureName) {
		if(furnitureName.equals("DISHWASHER")) {
			if( (actualPlayer.getHouse() != null) && (actualPlayer.getHouse().getHasDishwasher() == false) ) {
				actualPlayer.getHouse().setHasDishwasher(true);
			}	
			else {
				addMoney(300);
			}
		}
		else if(furnitureName.equals("WASHMACHINE")) {
			if( (actualPlayer.getHouse() != null) && (actualPlayer.getHouse().getHasWashMachine() == false) ) {
				actualPlayer.getHouse().setHasWashMachine(true);
			}	
			else {
				addMoney(300);
			}
		}
		else if(furnitureName.equals("ROOMFURNITURE")) {
			if( (actualPlayer.getHouse() != null) && (actualPlayer.getHouse().getHasRoomFurniture() == false) ) {
				actualPlayer.getHouse().setHasRoomFurniture(true);
			}	
			else {
				addMoney(3000);
			}
		}
	return;
	}

	
}
