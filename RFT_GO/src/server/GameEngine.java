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
	/** Ez a metódus az aktuális játékos egyenlegén jóváírja a megfelelõ összeget.
	 * 
	 * @param amount az átulanandó pénzösszeg
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
	/** Ez a metódus ellenõrzi, hogy rendelkezésre áll-e az aktuális játékos egyenlegén a megfelelõ összeg.
	 * 
	 * @param amount a kívánt pénzösszeg
	 * @return igaz, ha rendelkezésre áll, és hamis, ha nem.
	 */
	public Boolean checkBalance(int amount) {
		if(actualPlayer.getBalance() >= amount) {
			System.out.println("###Egyenleg ellenõrizve - IGEN, végrehajtható a tranzakció###");
			return true;
		}
		else {
			System.out.println("###Egyenleg ellenõrizve - NEM végrehajtható a tranzakció###");
			return false;
		}		
	}
	/** Ez a metódus az aktuális játékos egyenlegérõl levonja a megfelelõ összeget.
		 * 
		 * @param amount a levonandó pénzösszeg
		 * @return
		 */
	public Boolean deductMoney(int amount) {
		int originalBalance = actualPlayer.getBalance();
		System.out.println("###Egyenleg levonás elõtt: " + originalBalance + " Euro ###");
		if(checkBalance(amount) == true) {
			actualPlayer.setBalance(originalBalance-amount);
			System.out.println("###Sikeres pénzlevonási tranzakció###");
			System.out.println("###Egyenleg levonás után: " + actualPlayer.getBalance() + " Euro ###");
			return true;
		}
		else {
			System.out.println("###Sikertelen pénzlevonási tranzakció - nem elegendõ az egyenleg###");
			
			return false;
		}
	}
	/** Ez a metódus minden kör végén hívódik meg, és ha van az aktuális játékosnak háza, illetve autója
	 * akkor levonja az esetleges hátralévõ tartozásból a körönként kötelezõen törlesztendõ 500 eurót.
	 * Ha nem tudja levonni, a játékos kiesett, az isActiva változó értékét ennek megfelelõen átállítja, 
	 * és hamis értékkel tér vissza, ha pedig sikerülnek a tranzakciók, akkor igaz visszatérési értékkel.
	 */
	public Boolean handleDebits() {
		if(actualPlayer.getHouse() != null) {
			int houseDebit = actualPlayer.getHouse().getDebit();
			if(houseDebit != 0) {
				if(checkBalance(500) == false) {
					System.out.println("Vesztettél, mert nem tudsz törleszteni.");
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
					System.out.println("Vesztettél, mert nem tudsz törleszteni.");
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
	/** Ez a metódus a @param allMethosdNameList metódus listában keresi
	 * a @param goalMethodsName nevû metódust, és visszaadja az indexét, ha megtalálta.
	 * 
	 * @param allMethodsNameList a lista, amiben keressük a metódust
	 * @param goalMethodsName a keresendõ metódus neve
	 * @return a keresett metódus indexe a listában.
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
			board.add( new Field( 0, "START mezõ! Új kör kezdetekor, ha erre a mezõre lépsz, kapsz 4000 eurót, ha áthaladsz rajta, " +
										"akkor 2000 eurót.", 
										"1#addMoney#4000"));
			board.add( new Field( 1, "Lakásodat széppé varázsolhatod, ha a Diego boltban vásárolt szõnyeggel díszíted. " +
										"A vásárlásért fizess 200 eurót.", 
										"1#deductMoney#200"));
			board.add( new Field( 2, "Vásárolj BKV éves bérletet 200 euróért!", 
										"1#deductMoney#200"));
			board.add( new Field( 3, "Húzz egy SZERENCSEKERÉK kártyát, és kövesd annak utasításait !", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 4, "Tölts el egy kellemes estét a Zila Kávéház- és Étteremben! Fizess 40 eurót!", 
										"1#deductMoney#40"));
			board.add( new Field( 5, "Ha nem szeretsz gyalog járni, vagy ha ügyeid intézéséhez autóra van szükséged, most választhatsz egy " +
									"Citroene C4-es autót. Kp. vásárlás esetén: 10.000 euró, részletre történõ vásárlás " +
									"esetén kezdõ befizetés: 2.000 euró, minimális törlesztés körönként: 500 euró, végösszeg 12.000 euró", 
										"1#offerBuyCar"));
			board.add( new Field( 6, "Ügyesen vásároltál a Regió Játékkereskedésben! Jutalmad 50 euró!", 
										"1#addMoney#50"));
			board.add( new Field( 7, "Ételeid megromlását elkerülheted, ha hûtõben tárolod õket! Vásárolj hûtõgépet 200 euróért!", 
										"1#offerBuyFurniture#FRIGO"));
			board.add( new Field( 8, "A családi ebédek elkészítéséhez vásárolj modern tûzhelyet 200 eurtóért!", 
										"1#offerBuyFurniture#COOKER"));
			board.add( new Field( 9, "Megkötheted autó biztosításodat és lakásbiztosításodat 100-100 euróért az Allianz Hungária Biztosítónál!", 
										"1#offerMakeInsurances"));
			board.add( new Field( 10, "Húzz egy SZERENCSEKERÉK kártyát, és kövesd annak utasításait !", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 11, "Ha van pénzed és lakásod, vásárolj szép konyhabútort 1.000 euróért!", 
										"1#offerBuyFurniture#KITCHENFURNITURE"));
			board.add( new Field( 12, "Meglátogattad fõvárosunk kedvenc állatait az Állat- és Növénykertben.", 
										"1#addMoney#200"));
			board.add( new Field( 13, "Betegséged idejére kórházba kerülsz, és csak akkor léphetsz tovább, ha l-est, vagy 6-ost dobsz. " +
										"A 3. sikertelen dobás után bármilyen dobással kiléphetsz.", 
										"1#set_1_6Penalty#3"));
			board.add( new Field( 14, "Tönkrement a cipõd, vásárolnod kell egy újat a cipõboltból. Fizess 80 eurót.", 
										"1#deductMoney#80"));
			board.add( new Field( 15, "Gondoltál a környezetvédelemre, és a közösségi közlekedést választottad utazásodhoz. " +
										"Ezért jutalmul még kétszer dobhatsz.", 
										"1#setGiftDices#2"));
			board.add( new Field( 16, "Húzz egy SZERENCSEKERÉK kártyát, és kövesd annak utasításait !", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 17, "A film tanít, szórakoztat. Nézd meg a legújabb sikerfilmet barátaiddal a " +
										"Corvin filmpalotában, és fizess 20 eurót.", 
										"1#deductMoney#20"));
			board.add( new Field( 18, "Minden könyvújdonságot megtalálsz, és kedvedre böngészhetsz az Alexandra Könyváruházban! " +
										"A vásárolt könyvekért fizess 60 eurót.", 
										"1#deductMoney#60"));
			board.add( new Field( 19, "Takarékoskodj, mert így szép lakáshoz juthatsz. Ha már van pénzed, fizess be 30.000 eurót " +
										"a pénztárba és megkapod lakásod. Amennyiben részletfizetésre van csak lehetõséged, " +
										"fizess 15.000 eurót az OTP Bank pénztárába, a fennmaradó 20.000 eurót " +
										"pedig 500 eurós részletekben törlesztheted!", 
										"1#offerBuyHouse"));
			board.add( new Field( 20, "Mobiltelefont vettél, és beléptél a GSM hálózatba. Fizess 100 eurót.", 
										"1#deductMoney#100"));
			board.add( new Field( 21, "Pihenés? Feltöltõdés? Kikapcsolódás? Választ a Klub Tihanyt! Fizess 280 eurót! Még egyszer dobhatsz!", 
										"2#deductMoney#280#setGiftDices#1"));
			board.add( new Field( 22, "Megtekintetted a Nemzeti Galéria gyönyörû kiállítását.", "0"));
			board.add( new Field( 23, "Húzz egyet a SZERENCSEKERÉK kártyából, és kövesd annak utasításait!", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 24, "Visegrádi hajókiránduláson veszel részt barátaiddal, amelyért 40 eurót kell fizetned.", 
										"1#deductMoney#40	"));
			board.add( new Field( 25, "Ma a Kakas Étteremben vacsorázol családoddal, fizess 40 eurót.", 
										"1#deductMoney#40"));
			board.add( new Field( 26, "Kellemes séta közben tekintsd meg a Margitsziget nevezetességeit !", "0"));
			board.add( new Field( 27, "Vásárolj mosógépet a Whirpool Márkakereskedésben, fizess 300 eurót!", 
										"1#offerBuyFurniture#WASHMACHINE	"));
			board.add( new Field( 28, "Húzz egyet a SZERENCSEKERÉK kártyából, és kövesd annak utasításait !", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 29, "Sétálj a Halászbástyán. Innen gyönyörû kilátás nyílik Budapestre.", "0"));
			board.add( new Field( 30, "A Vista Utazási irodákban elintézhetsz az utazásoddal kapcsolatban mindent kényelmesen és gyorsan! " +
										"Fizess 300 eurót!", 
										"1#deductMoney#300"));
			board.add( new Field( 31, "Beléptél a PICK Márkaáruházba. 20 eurót fizetsz.", 
										"1#deductMoney#20"));
			board.add( new Field( 32, "Húzz egyet a SZERENCSEKERÉK kártyából, és kövesd annak utasításait!", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 33, "Ha van pénzed, vásárolj Electrolux mosogatógépet, melynek ára 300 euró!", 
										"1#offerBuyFurniture#DISHWASHER"));
			board.add( new Field( 34, "A SkyEurope olcsó és gyors utazást biztosít Európa nagyvárosaiba, mindezt csak 300 euróért!", 
										"1#deductMoney#300"));
			board.add( new Field( 35, "Vásároltál a regiojatek.hu webáruházban, ezért fizess 20 eurót!", 		
										"1#deductMoney#20"));
			board.add( new Field( 36, "Ma a barátaiddal a Mammut Mozi elõadását nézted meg, fizess 20 eurót.",
										"1#deductMoney#20"));
			board.add( new Field( 37, "Húzz egyet a SZERENCSEKERÉK kártyából, és kövesd annak utasításait!", 
										"1#drawNextLuckyCard"));
			board.add( new Field( 38, "Ha már van lakásod, most vásárolhatsz bele szobabútort, mindössze 3.000 euróért!", 
										"1#offerBuyFurniture#ROOMFURNITURE"));
			board.add( new Field( 39, "Takarékoskodj, mert így szép lakáshoz juthatsz. Ha már van pénzed, fizess be 30.000 eurót a pénztárba és megkapod lakásod. " +
					"					Amennyiben részletfizetésre van csak lehetõséged, fizess 15.000 eurót az OTP Bank pénztárába, a fennmaradó 20.000 eurót " +
					"					pedig 500 eurós részletekben törlesztheted!", 
										"1#offerBuyHouse"));
			board.add( new Field( 40, "Az Invitelnél kifizetted a havi telefon- és internetszámládat, melynek összege 20 euró.", 
										"1#deductMoney#20"));
			board.add( new Field( 41, "Beléptél az IKEA áruházba! Konyhafelszerelésért fizess 100 eurót!", 
										"1#deductMoney#100"));
										
										*/
			return;
		}
	public void initLuckyCards() {
		deck=p.parseLuckyCards("LuckyCards.xml");
		/*deck.add(new LuckyCard( 1 ,"Fizesd ki gáz- és villanyszámládat a folyószámlán keresztül, melynek összege 40 euró!","1#deductMoney#40" ));						
		deck.add(new LuckyCard( 2 ,"A Budapesti Nemzetközi Vásár sorsjátékán mosógépet nyertél!","1#wonWashMachine" ));						
		deck.add(new LuckyCard( 3 ,"Újításért 2.500 eurót kapsz, melyet a pénztár fizet ki!","1#addMoney#2500" ));						
		deck.add(new LuckyCard( 4 ,"Jó munkádért 1.000 euró jutalomban részesülsz, vedd fel a pénztárból!","1#addMoney#1000" ));						
		deck.add(new LuckyCard( 5 ,"Hármas találatod volt a lottón, 800 eurót kapsz a pénztártól!","1#addMoney#800" ));						
		deck.add(new LuckyCard( 6 ,"A lottó 10. játékhetén 10.000 eurót nyertél, amelyet a pénztár fizet ki!"," 1#addMoney#10000" ));						
		deck.add(new LuckyCard( 7 ,"A lottó nyereménysorsolásán mosogatógépet nyertél!","1#wonDishWasher" ));						
		deck.add(new LuckyCard( 8 ,"A totón 12-es találattal 6.000 eurót nyertél, amelyet a pénztár fizet ki!","1#addMoney#6000" ));						
		deck.add(new LuckyCard( 9 ,"A totón 10-es találattal 400 eurót nyertél, amelyet a pénztár fizet ki!","1#addMoney#400" ));						
		deck.add(new LuckyCard( 10 ,"A totón 11-es találattal 2.000 eurót nyertél, amelyet a pénztár fizet ki!","1#addMoney#2000" ));						
		deck.add(new LuckyCard( 11 ,"Sorsjátékon szobabútort nyertél! Ha nincs lakásod, 3.000 eurót fizet ki a pénztár!","1#wonRoomFurniture" ));						
		deck.add(new LuckyCard( 12 ,"Takarékoskodj! A megtakarított pénzed után 7% azonnali kamatot kapsz!","1#addPercentage#7" ));						
		deck.add(new LuckyCard( 13 ,"Jól takarékoskodtál, ezért az OTP Bank Nyrt. a folyószámládon elhelyezett pénzed után 15% kamatot fizet!","1#addPercentage#15" ));						
		deck.add(new LuckyCard( 14 ,"Errõl a mezõrõl csak akkor léphetsz tovább, ha 1-est, vagy 6-ost dobsz!","1#set_1_6Penalty#3" ));						
		deck.add(new LuckyCard( 15 ,"Lépj elõre 1 mezõt!","1#moveWithQuantity#1" ));						
		deck.add(new LuckyCard( 16 ,"Lépj vissza 3 mezõt!","1#moveWithQuantity#-3" ));						
		deck.add(new LuckyCard( 17 ,"Egyszer kimaradsz a dobásból!","1#setExclusions#1" ));						
		deck.add(new LuckyCard( 18 ,"Kivetted éves szabadságod! Kétszer kimaradsz a dobásból!","1#setExclusions#2" ));						
		deck.add(new LuckyCard( 19 ,"Menj a Start mezõre, így kapsz a pénztártól 4.000 eurót!"," 1#moveToField#0" ));						
		deck.add(new LuckyCard( 20 ,"Lakásod díszítsd iráni és afgán kézi csomózású perzsa szõnyeggel! Lépj az 1-es mezõre!","1#moveToField#1" ));						
		deck.add(new LuckyCard( 21 ,"Most vedd meg álmaid autóját! Lépj az 5-ös mezõre, és élvezd az új autódat!","1#moveToField#5" ));						
		deck.add(new LuckyCard( 22 ,"Ellopták az autódat. Ha van autó biztosításod, a bank kifizeti a károdat. Ha még nincs, lépj a 9-es mezõre, a biztosítónál megkötheted!","2#loseCar#moveToField#9" ));						
		deck.add(new LuckyCard( 23 ,"Kiégett a lakásod, így elvesztetted az összes berendezési tárgyadat! Ha van lakásbiztosításod, a biztosító megtéríti a károdat. Ha még nincs, lépj a 9-es mezõre, a biztosítónál megkötheted!","2#loseFurnitures#moveToField#9" ));						
		deck.add(new LuckyCard( 24 ,"Lépj a 11-es mezõre, ahol konyhabútort vásárolhatsz, ha már van lakásod!","1#moveToField#11" ));						
		deck.add(new LuckyCard( 25 ,"Térj be cipõt vásárolni a Deichmann valamelyik üzletébe! Kényelmes cipõben lépj a 14-es mezõre!","1#moveToField#14" ));						
		deck.add(new LuckyCard( 26 ,"Látogass el az Alexandra Könyváruházakba. Egy szórólappal kapott 20 eurós kupont felhasználhatsz, amit a végösszegbõl levonnak. Lépj a 18-as mezõre.","2#addMoney#20#moveToField#18" ));						
		deck.add(new LuckyCard( 27 ,"Pihenés? Feltöltõdés? Kikapcsolódás? Választ a Klub Tihanyt! Lépj a 21-es mezõre, és fizess 280 eurót! Még egyszer dobhatsz!","1#moveToField#21" ));						
		deck.add(new LuckyCard( 28 ,"Ajándékba kaptál egy vouchert egy visegrádi hajókirándulásra. Belépéskor érvényesítve ingyen kirándulhatsz. Induláshoz lépj a 24-es mezõre!","2#addMoney#40#moveToField#24" ));						
		deck.add(new LuckyCard( 29 ,"Ebédelj barátaiddal étteremben! Csendes, békés, mediterrán hangulatú helyen, ahová mindig szívesen fogsz visszatérni! Lépj a 25-ös mezõre!","2#addMoney#40#moveToField#25" ));						
		deck.add(new LuckyCard( 30 ,"Menj ki a Margitszigetre autóbusszal, lépj a 26-os mezõre!","1#moveToField#26" ));						
		deck.add(new LuckyCard( 31 ,"Vásárolj mosógépet a Hajdú Márkakereskedésben! Lépj a 27-es mezõre!","1#moveToField#27" ));						
		deck.add(new LuckyCard( 32 ,"A Vista Utazási Irodák nyereménysorsolásán utazást nyertél! Kipihenten folytathatod utadat! Lépj a 30-as mezõre!","2#addMoney#300#moveToField#30" ));						
		deck.add(new LuckyCard( 33 ,"Jól választottál! 20 euróért telepakoltad a kosaradat mindenféle PICK finomsággal! Lépj a 31-es mezõre!","1#moveToField#31" ));						
		deck.add(new LuckyCard( 34 ,"Háztartásodat mosogatógéppel szerelheted fel, amely megkönnyíti hétköznapjaidat. Lépj a 33-as mezõre!","1#moveToField#33" ));						
		deck.add(new LuckyCard( 35 ,"Jól választottál, a SkyEurope gyorsan és olcsón elrepít Európa nagyvárosaiba. Fizesd ki repülõjegyed árát, amely 300 euró, ezután lépj a 34-es mezõre!","1#moveToField#34" ));						
		deck.add(new LuckyCard( 36 ,"Az Invitelnél 2in1 csomagra szerzõdtél, mely a telefon mellett az Internet hozzáférés díját is tartalmazza, így ez csak 20 euróba kerül! Fizetés után lépj a 40-es mezõre!","1#moveToField#40" ));
	*/
		return;
		}
	/** Ez a metódus végzi el a kockával való dobást.
	 *  A metódusban ellenõrzésre kerül, hogy nincs-e 1-6 büntetésben a játékos, mert ha igen,
	 *  akkor csak megfelelõ értékû dobás esetén hívódik meg a {@code moveWithQuantity()} metódus.
	 *  Minden esetben tájékozhatjuk a játékost szöveges üzenet formájában az eredményrõl.
	 */
	public void dice() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Random generator = new Random();
		int result = generator.nextInt(5);
		result+=1;
		if( 0 < actualPlayer.get_1_6Penalty()) {
			if((result==1) || (result ==6)) {
			actualPlayer.set_1_6Penalty(0);
			sendMessageForRead("Büntetésben voltál, mely szerint csak 1-es vagy 6-os dobással léphetsz tovább," +
								" de mivel dobásod értéke " + result + ", így lépj elõre ennyi mezõt!");
			moveWithQuantity(result);
			return;
			}
			else {
				sendMessageForRead("Büntetésben vagy, mely szerint csak 1-es vagy 6-os dobással léphetsz tovább. " +
									"Most itt maradsz, mert dobásod értéke " + result + ".");
				return;
			}
		}
		else {
			sendMessageForRead("Dobásod értéke " + result + ". Lépj elõre ennyi mezõt!" );
			moveWithQuantity(result);
			return;
		}
	}
	public void executeFieldCommand() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		System.out.println(actualPlayer.getLocation().getDescription()); //le lesz cserélve a következõ sorra...
		//sendMessageForRead(actualPlayer.getLocation().getDescription());
		// Az aktuális játékos mezõjének Command adattagja, a parancsszavakat tartalmazó 
		// string feldarabolása '#' karakterek mentén, eredmény a commandWords String tömb.
		int commandWordIterator = 0;
		int methodIterator;
		String[] commandWords = actualPlayer.getLocation().getCommand().split("#");
		String executableMethodsName;
		// A commandWords String tömb elsõ eleme a végrehajtandó metódusok száma.
		int numberOfExecutableMethods = Integer.parseInt(commandWords[commandWordIterator++]);
		System.out.println("Végrehajtandó metódusok száma: " + numberOfExecutableMethods);
		// Ennek megfelelõ számú metódust kell meghívni. (ez egyébként max. 2 lesz.)
		// Lekérjük az osztálytól a metódusok listáját, hogy majd ezek közül egyet meghívhassunk.
		Method[] methods = GameEngine.class.getDeclaredMethods();
		/*System.out.println("Az osztály metódusai, ezek között keresünk");
		for(int i=0; i<methods.length; ++i) {
			System.out.println(methods[i].getName());
		}
		*/
		for(methodIterator = 0; methodIterator<numberOfExecutableMethods; ++methodIterator) {
			// Az executableMethodsName változóban rögzítem a végrehajtandó metódus nevét.
			executableMethodsName = commandWords[commandWordIterator++];
			System.out.println("###Végrehajtandó metódus: " + executableMethodsName + " ###");
			// Az actMet metódusban rögzítem a végrehajtandó metódus objektumot.
			Method actMet = methods[giveIndexOfSearchedMethod(methods, executableMethodsName)];
			// Megvizsgálom a metódus neve alapján, hogy hány paramétere lesz, azokat rögzítem, és meghívom a metódust.
			
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
	/** Ebben a metódusban különbözõ játékosokhoz különbözõ socketeket rendelünk.
	 * Amint érkezik egy kapcsolódási kérelem, az adott socket inputStream-jébõl kinyerjük a kliens üzenetét, 
	 * amely a játékos neve lesz, ily módon az új játékost hozzáadjuk a játékosok listájához.
	 * 2 játékos csatlakozása után 2 perces (azaz 120000 ms) türelmi idõ van, amíg további játékosokra várakozunk.
	 * Ezt követõen elindul a játék a startGame() metódusba történõ visszatéréssel.
	 * SZERKESZTÉS ALATT! MÉG NEM TESZTELVE! VÁRJA A KRITIKÁKAT! :)
	 */
	public void waitForPlayers(int maxNumberOfPlayers) throws IOException { //EZ EGÉSZEN MÁS LESZ....
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
		// TODO Auto-generated method stub
		
	}
	/**Ebben a metódusban elõször meghívjuk a waitForPlayers() metódust, amelyben 6 becsatlakozó játékosra várunk.
	 * Amennyiben a metódustól a vezérlést visszakapjuk, elindítjuk a tényleges játékot, azaz
	 * sorra eldöntjük, hogy ki következik dobni, és aki következik az dobhat-e, vagy éppen kimarad, illetve, ha
	 * többször is dobhat, akkor több lehetõséget kap a szabályoknak megfelelõen.
	 * Ez egészen addig megy, míg egy játékos meg nem nyerte a játékot.
	 * Nyerés esetén a gyõztest, és a többieket is értesítjük.
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
				sendMessageForRead("Önnek most nem volt szerencséje, " + winnersName + " nyerte meg a játékot.");
			}
			else if(actualPlayer.isWinner() == true) {
				sendMessageForRead("Gratulálunk, " + winnersName + "! Szép játék volt, Ön nyert!");
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
			System.out.println("###Körnek vége. A Start mezõn áthaladtál, ezért kapsz 2000 eurót, majd " + 
								"levonásra kerülnek kötelezõ törlesztõrészleteid. Ha nincs arra elég pénzed, vesztettél.###");
			if(handleDebits()==true)
				executeFieldCommand();
			return;
		}
		else if(newPositionNumber == 42) {
			System.out.println("###Körnek vége. A Start mezõre léptél, ezért kapsz 4000 eurót, majd " + 
					"levonásra kerülnek kötelezõ törlesztõrészleteid. Ha nincs arra elég pénzed, vesztettél.###");
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
