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
	/** Ez a metódus ellenőrzi, hogy rendelkezésre áll-e az aktuális játékos egyenlegén a megfelelő összeg.
	 * 
	 * @param amount a kívánt pénzösszeg
	 * @return igaz, ha rendelkezésre áll, és hamis, ha nem.
	 */
	public Boolean checkBalance(int amount) {
		if(actualPlayer.getBalance() >= amount) {
			System.out.println("###Egyenleg ellenőrizve - IGEN, végrehajtható a tranzakció###");
			return true;
		}
		else {
			System.out.println("###Egyenleg ellenőrizve - NEM végrehajtható a tranzakció###");
			return false;
		}		
	}
	/** Ez a metódus az aktuális játékos egyenlegéről levonja a megfelelő összeget.
		 * 
		 * @param amount a levonandó pénzösszeg
		 * @return
		 */
	public Boolean deductMoney(int amount) {
		int originalBalance = actualPlayer.getBalance();
		System.out.println("###Egyenleg levonás előtt: " + originalBalance + " Euro ###");
		if(checkBalance(amount) == true) {
			actualPlayer.setBalance(originalBalance-amount);
			System.out.println("###Sikeres pénzlevonási tranzakció###");
			System.out.println("###Egyenleg levonás után: " + actualPlayer.getBalance() + " Euro ###");
			return true;
		}
		else {
			System.out.println("###Sikertelen pénzlevonási tranzakció - nem elegendő az egyenleg###");
			
			return false;
		}
	}
	/** Ez a metódus minden kör végén hívódik meg, és ha van az aktuális játékosnak háza, illetve autója
	 * akkor levonja az esetleges hátralévő tartozásból a körönként kötelezően törlesztendő 500 eurót.
	 * Ha nem tudja levonni, a játékos kiesett, az isActiva változó értékét ennek megfelelően átállítja, 
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
	 * a @param goalMethodsName nevű metódust, és visszaadja az indexét, ha megtalálta.
	 * 
	 * @param allMethodsNameList a lista, amiben keressük a metódust
	 * @param goalMethodsName a keresendő metódus neve
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
		
			return;
		}
	
	
	public void initLuckyCards() {
		deck=p.parseLuckyCards("LuckyCards.xml");
		return;
		}
	
	
	
	/** Ez a metódus végzi el a kockával való dobást.
	 *  A metódusban ellenőrzésre kerül, hogy nincs-e 1-6 büntetésben a játékos, mert ha igen,
	 *  akkor csak megfelelő értékű dobás esetén hívódik meg a {@code moveWithQuantity()} metódus.
	 *  Minden esetben tájékozhatjuk a játékost szöveges üzenet formájában az eredményről.
	 */
	public void dice() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Random generator = new Random();
		int result = generator.nextInt(5);
		result+=1;
		if( 0 < actualPlayer.get_1_6Penalty()) {
			if((result==1) || (result ==6)) {
			actualPlayer.set_1_6Penalty(0);
			sendMessageForRead("Büntetésben voltál, mely szerint csak 1-es vagy 6-os dobással léphetsz tovább," +
								" de mivel dobásod értéke " + result + ", így lépj előre ennyi mezőt!");
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
			sendMessageForRead("Dobásod értéke " + result + ". Lépj előre ennyi mezőt!" );
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
