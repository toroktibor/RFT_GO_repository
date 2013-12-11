package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameEngine implements ICashier, IGamePlay {
	private Player actualPlayer;
	private List<Player> allPlayers = new ArrayList<Player>();
	private List<Field> board = new ArrayList<Field>();
	private List<LuckyCard> deck = new ArrayList<LuckyCard>();
	private int luckyCardIndex = 0;
	private XMLParser p=new XMLParser();
	ServerSocket serverSocket = null;
	DataInputStream in;
	DataOutputStream out;
	
	
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
	 * @param amount az átutlanandó pénzösszeg
	 */
	public void addMoney(int amount) {
		int originalBalance = actualPlayer.getBalance();
		actualPlayer.setBalance(originalBalance+amount);
		System.out.println("###MONEY ADDED SUCCESSFULLY###");
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
			System.out.println("###BALANCE CHECKED - YES, TRANSACTION IS EXECUTABLE###");
			return true;
		}
		else {
			System.out.println("###BALANCE CHECKED - NO, TRANSACTION IS NOT EXECUTABLE###");
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
	
	public void init() {
		board=p.parseFields("Fields.xml");
		deck=p.parseLuckyCards("LuckyCards.xml");
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
		// Ennek megfelelõ számÃº metódust kell meghívni. (ez egyébként max. 2 lesz.)
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
	 * amely a játékos neve lesz, ily módon az Ãºj játékost hozzáadjuk a játékosok listájához.
	 * 2 játékos csatlakozása után 2 perces (azaz 120000 ms) türelmi idõ van, amíg további játékosokra várakozunk.
	 * Ezt követõen elindul a játék a startGame() metódusba történõ visszatéréssel.
	 * SZERKESZTéS ALATT! MéG NEM TESZTELVE! VáRJA A KRITIKáKAT! :)
	 */
	public void waitForPlayers(int maxNumberOfPlayers) throws IOException {
		serverSocket = new ServerSocket(5005);
		Socket actualSocket = new Socket();
		Integer actualPlayersIndex;
		while(allPlayers.size() < maxNumberOfPlayers) {
			actualSocket = serverSocket.accept();
			if(actualSocket.isConnected()) {
				in = (DataInputStream) actualSocket.getInputStream();
				out = (DataOutputStream) actualSocket.getOutputStream();
				allPlayers.add(new Player(in.readUTF(), actualSocket, board.get(0)));
				out.flush();
				actualPlayersIndex = (allPlayers.size()-1);
				out.writeUTF(actualPlayersIndex.toString());
				actualSocket.close(); //kérdés nem lesz-e ez gáz???
			}
			if( 2 <= allPlayers.size() ) {
				serverSocket.setSoTimeout(120000);
			}
		}
		return;
	}
	public void sendGameState() {
		// TODO Auto-generated method stub //majd itt egyeztessünk mert az üzenet váltás érdekes :)
		
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
				sendMessageForRead("Gratulálunk, " + winnersName + "! Szép játék volt, ön nyert!");
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
