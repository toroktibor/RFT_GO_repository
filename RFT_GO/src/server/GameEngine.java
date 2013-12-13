package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class GameEngine implements ICashier, IGamePlay {
	private Player actualPlayer;
	private List<Player> allPlayers = new ArrayList<Player>();
	private List<Field> board = new ArrayList<Field>();			//the board contains the list of all 42 fields
	private List<LuckyCard> deck = new ArrayList<LuckyCard>();	//the deck contains the list of all lucky cards
	private int luckyCardIndex = -1;		//index of the following lucky card in the row
	private int playerIndex = 0;		//index of the respective actual player
	
	private XMLParser p=new XMLParser();	//the parser used for initialize the lucky cards of the deck and the fields of the board
	ServerSocket serverSocket = null;	//the server socket, that handle client connections
	DataInputStream in;		//the input stream of the respective actual player's socket 
	DataOutputStream out;	//the output stream of the respective actual player's socket 
	
	
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
	public void getLuckyCardByIndex(int index) {
		
	}
	
	//IMPLEMENTATION OF THE METHODS OF ICASHIER INTERFACE
	/** This method add the required amount of money to the actual player's balance.
	 * 
	 * @param amount the required amount of money
	 * @throws IOException 
	 */
	//DONE
	public void addMoney(int amount) throws IOException {
		int originalBalance = actualPlayer.getBalance();
		actualPlayer.setBalance(originalBalance+amount);
		System.out.println("###Money added. Original -> New balance:" 
							+ originalBalance + "->" + actualPlayer.getBalance() + " ###");
		sendGameState("BALANCE");
		return;
	}
	
	/* DONE
	 * NEED REVIEW */
	public void addPercentage(int percentage) throws IOException {
		int originalBalance = actualPlayer.getBalance();
		double result = originalBalance * ((double)percentage / 100);
		addMoney((int)result);
		System.out.println("###Percentage added. Original -> New balance: " 
							+ originalBalance + "->" + actualPlayer.getBalance() + " ###");
		return;
	}	
	
	/** This method check if the balance of the actual player is greater or equal to the required amount.
	 * 
	 * @param amount the required amount of money
	 * @return 
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
	
	/** This method deduct the required amount of money from the balance of the actual player.
		 * 
		 * @param amount the amount of money to deduct
		 * @return
	 * @throws IOException 
		 */
	public void deductMoney(int amount) throws IOException {
		int originalBalance = actualPlayer.getBalance();
		if(checkBalance(amount) == true) {
			actualPlayer.setBalance(originalBalance - amount);
			sendGameState("BALANCE");
			System.out.println("###Money deducted. Original -> New balance: " 
					+ originalBalance + "->" + actualPlayer.getBalance() + " ###");
		}
		else {
			System.out.println("###Money deduction unsuccessful. Balance vs. Required: "
									+ originalBalance + " vs. " + amount +" ###");
		}
	}
	
	/* DONE - This method deduct 500 euros for each debts in the end of every round. If it is not possible, the player lose the game. */
	public void handleDebits() throws IOException{
		if(actualPlayer.getHouse() != null) {
			int houseDebit = actualPlayer.getHouse().getDebit();
			if(houseDebit != 0) {
				if(checkBalance(500) == false) {
					sendMessageForRead("Vesztettél! Nem tudod kifizetni adósságodat!");
					actualPlayer.setIsActive(false);
					return;
				}
				else {
					sendMessageForRead("Levontuk a kötelező törlesztőrészletet (500 euro) a házad hiteléből.");
					actualPlayer.getHouse().setDebit(houseDebit-500);
					sendGameState("HOUSE");
					deductMoney(500); 
				}
			}
		}
		if(actualPlayer.getCar() != null) {
			int carDebit = actualPlayer.getCar().getDebit();
			if(carDebit != 0) {
				if(checkBalance(500) == false) {
					sendMessageForRead("Vesztettél! Nem tudod kifizetni adósságodat!");
					actualPlayer.setIsActive(false);
				}
				else {
					sendMessageForRead("Levontuk a kötelező törlesztőrészletet (500 euro) az autód hiteléből.");
					actualPlayer.getCar().setDebit(carDebit-500);
					sendGameState("CAR");
					deductMoney(500);
				}
			}	
		}
	}
	
	
	//IMPLEMENTATION OF THE METHODS OF IGAMEPLAY INTERFACE
	
	/** This method search in the allMethosdNameList methodlist the given method named by
	 * @param goalMethodsName parameter, and give back the index of it.
	 * 
	 * @param allMethodsNameList the list to search in it
	 * @param goalMethodsName the name of the searched method
	 * @return the index of the result method.
	 */
	private int giveIndexOfSearchedMethod(Method[] allMethodsNameList, String goalMethodsName ) {
		int indexOfSearchedMethod = -1;
		for(int i = 0; i<allMethodsNameList.length; ++i) {
			if(allMethodsNameList[i].toString().contains(goalMethodsName)) {
				indexOfSearchedMethod = i;
			}
		}
		return indexOfSearchedMethod;
	}
	
	/* DONE - This method initialize the fields of the board, and the lucky cards of the deck. */
	public void init() {
		board=p.parseFields("Fields.xml");
		deck=p.parseLuckyCards("LuckyCards.xml");
		return;
	}
	
	/** This method execute the dice using a random number generator in a range from 1 to 6.
	 *  If the player has the so called 1-6 Penalty, it is not possible to move away while the result of the dice is not 1 or 6.
	 *  @throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException 
	 */
	public void dice() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		Random generator = new Random();
		int result = generator.nextInt(5);
		result+=1;
		if( 0 < actualPlayer.get_1_6Penalty()) {
			if((result==1) || (result ==6)) {
			actualPlayer.set_1_6Penalty(0);
			/*FINOMÍTÁSRA SZORUL*/
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
	
	public void executeFieldCommand() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		// Az aktuális játékos mezőjének Command adattagja, a parancsszavakat tartalmazó 
		// string feldarabolása '#' karakterek mentén, eredmény a commandWords String tömb.
		int commandWordIterator = 0;
		int methodIterator;
		String[] commandWords = actualPlayer.getLocation().getCommand().split("#");
		String executableMethodsName;
		// A commandWords String tömb első eleme a végrehajtandó metódusok száma.
		int numberOfExecutableMethods = Integer.parseInt(commandWords[commandWordIterator++]);
		System.out.println("Végrehajtandó metódusok száma: " + numberOfExecutableMethods);
		// Ennek megfelelő számú metódust kell meghívni. (ez egyéként max. 2 lesz.)
		// Lekérjük az osztálytól a metódusok listáját, hogy majd ezek közül egyet meghívhassunk.
		Method[] methods = GameEngine.class.getDeclaredMethods();
		for(methodIterator = 0; methodIterator<numberOfExecutableMethods; ++methodIterator) {
			// Az executableMethodsName változóban rögzítem a végrehajtandó metódus nevét.
			executableMethodsName = commandWords[commandWordIterator++];
			System.out.println("###végrehajtandó metódus: " + executableMethodsName + " ###");
			// Az actMet metódusban rögzítem a végrehajtandó metódus objektumot.
			Method actMet = methods[giveIndexOfSearchedMethod(methods, executableMethodsName)];
			// Megvizsgálom a metódus neve alapján, hogy hány paramétere lesz, azokat rögzítem, és meghívom a met�dust.
			
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
	
	/* FAULTY!!!!!!!!!!!!!!!! */
	public void executeLuckyCardCommand() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Az aktuális szerencsekártya Command adattagja, a parancsszavakat tartalmazó 
		// string feldarabolása '#' karakterek mentén, eredmény a commandWords String tömb.
		int commandWordIterator = 0;
		int methodIterator;
		String[] commandWords = deck.get(luckyCardIndex).getCommand().split("#");
		String executableMethodsName;
		// A commandWords String tömb első eleme a végrehajtandó metódusok száma.
		int numberOfExecutableMethods = Integer.parseInt(commandWords[commandWordIterator++]);
		System.out.println("Végrehajtandó metódusok száma: " + numberOfExecutableMethods);
		// Ennek megfelelő számú metódust kell meghívni. (ez egyéként max. 2 lesz.)
		// Lekérjük az osztálytól a metódusok listáját, hogy majd ezek közül egyet meghívhassunk.
		Method[] methods = GameEngine.class.getDeclaredMethods();
		for(methodIterator = 0; methodIterator<numberOfExecutableMethods; ++methodIterator) {
			// Az executableMethodsName változóban rögzítem a végrehajtandó metódus nevét.
			executableMethodsName = commandWords[commandWordIterator++];
			System.out.println("###végrehajtandó metódus: " + executableMethodsName + " ###");
			// Az actMet metódusban rögzítem a végrehajtandó metódus objektumot.
			Method actMet = methods[giveIndexOfSearchedMethod(methods, executableMethodsName)];
			// Megvizsgálom a metódus neve alapján, hogy hány paramétere lesz, azokat rögzítem, és meghívom a met�dust.
			
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
	
	/** In this method we wait for the given number players. There is a time limit, after which we start the game.
	 */
	/* DONE 
	 * NEED REVIEW*/
	public void waitForPlayers(int maxNumberOfPlayers) throws IOException { //EZ EGéSZEN MáS LESZ....
		/* azt mondjuk várakozunk */
		boolean wait=true;
		Calendar time = Calendar.getInstance() ;
		Calendar time2 = Calendar.getInstance() ;
		int wtime=20000;
		while((allPlayers.size() < maxNumberOfPlayers) && wait ) {
			try {
				/* várakozunk a kliensig ha megjön új játékos a listára felveszük stb
				 * mind ezt 2x az első 2 kliensig
				 * */
				System.out.println("Kliensre várakozunk!");
				Socket oneClient=serverSocket.accept();
				in=new DataInputStream(oneClient.getInputStream());
				out=new DataOutputStream(oneClient.getOutputStream());
				String pname=in.readUTF();
				System.out.println(pname+" csatlakozott!");
				actualPlayer = new Player(pname, oneClient, board.get(0));
				allPlayers.add(actualPlayer);
				out.writeUTF("SETID#"+String.valueOf(allPlayers.indexOf(actualPlayer)));
				sendGameState("NEWPLAYER");
				/*Megjött 2 kliens innentől maximum x időig (jelen esetben 120sec) várunk a további kliensekre
				 * ha letelt kivételt dob, elkapjuk a kivételt
				 * 
				 * alap esetben úgy működne hogy minden kliens érkezésére várunk x időt és ha nem jön meg 
				 * kivétel dobás, ha megjön újra indul a várakozás ezzel az idővel
				 * na de, most csökkentjük azzal az idővel ami a 2. játékos érkezése óta és a legutóbbi
				 * játékos érkezése óta telt el, így az újabb játékosoknak egyre kevesebb ideje marad
				 * magyarul: a 2. játékoshoz viszonyítjuk az időt.
				 * */
				if (2==allPlayers.size()){
					time2 = Calendar.getInstance() ;
					System.out.println("Már van 2 kliens, ezentúl maximum "+wtime/1000+" másodpercig várunk!");
					serverSocket.setSoTimeout(wtime);
				}
				if (2<allPlayers.size()){
					time = Calendar.getInstance() ;
					wtime=wtime-(int) (time.getTimeInMillis()-time2.getTimeInMillis());
					System.out.println("Már van "+allPlayers.size()+" játékos, hátralévő idő "+wtime/1000+" másodperc!");
					serverSocket.setSoTimeout(wtime);
				}
			}
			
			/*
			 * itt kapjuk el aztán azt mondjuk nem várunk tovább, 
			 * mivel elkaptuk elindul a következő ciklus lépés, de a feltétel miatt már nem
			 * fut le, azaz nem várunk tovább kliensre
			 */
			catch ( SocketTimeoutException e ) {  
				System.out.println("Az idő letelt! Indulhat a játék!");
				wait=false;
	        }  
		}
	}
	
	public void sendGameState(String option) throws IOException {
		int originalActualPlayersIndex = allPlayers.indexOf(actualPlayer);
		String s = String.valueOf(originalActualPlayersIndex);
		if(option.equals("HOUSE")) {
			s=s.concat("#1#SETHOUSE#" + actualPlayer.getHouse().toString());	
		}
		else if(option.equals("CAR")) {
			s=s.concat("#1#SETCAR#" + actualPlayer.getCar().toString());
		}
		else if(option.equals("NAME")) {
			s=s.concat("#1#SETNAME#" + actualPlayer.getName().toString());
		}
		else if(option.equals("BALANCE")) {
			s=s.concat("#1#SETBALANCE#" + actualPlayer.getBalance());
		}		
		else if(option.equals("LOCATION")) {
			s=s.concat("#1#SETLOCATION#" + actualPlayer.getLocation().toString());
		}		
		else if(option.equals("NEWPLAYER")) {
			s=s.concat("#5");
			s=s.concat("#SETHOUSE#FALSE:FALSE:FALSE:FALSE:FALSE:FALSE:FALSE:FALSE:0");	
			s=s.concat("#SETCAR#FALSE:FALSE:0");
			s=s.concat("#SETNAME#" + actualPlayer.getName().toString());
			s=s.concat("#SETBALANCE#" + actualPlayer.getBalance());
			s=s.concat("#SETLOCATION#" + actualPlayer.getLocation().toString());
		}
		for(int i = 0; i<allPlayers.size()-1; ++i) {
			changeActualPlayerByIndex(i);
			out.writeUTF("GETGAMESTATE");
			out.writeUTF(s);
		}
		changeActualPlayerByIndex(originalActualPlayersIndex);		
	}
	
	/* DONE - This method change the value of the actualPlayer to a player of allPlayers list given by the index */
	public void changeActualPlayerByIndex(int index) throws IOException {
		actualPlayer = allPlayers.get(index);
		in = new DataInputStream( actualPlayer.getSocket().getInputStream());
		out =new DataOutputStream( actualPlayer.getSocket().getOutputStream());
	}
	
	/** DONE - In this method we call initialization method, and then the waitForPlayers() method, in which we wait for maximum 6 players.
	 * After that, we start the game, so we decide who is the next to dice.
	 */
	public void startGame() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		int iterator = 0;
		init();
		serverSocket=new ServerSocket(6000);
		waitForPlayers(6);
		do {
			changeActualPlayerByIndex(iterator);
			if(actualPlayer.getIsActive() == true) {
				if( actualPlayer.getExclusions() == 0) {
					dice();
					while( actualPlayer.getGiftDices() != 0 ) {
						dice();
						actualPlayer.setGiftDices(actualPlayer.getGiftDices()-1);
					}
				}
				else if(actualPlayer.getExclusions() > 0) {
					actualPlayer.setExclusions(actualPlayer.getExclusions()-1);
				}
			}
			++iterator;
			if (iterator==allPlayers.size())
				iterator = 0;
		} while (actualPlayer.isWinner() == false);
		
		String winnersName = actualPlayer.getName();
		for(int i = 0; i < allPlayers.size()-1; ++i) {
			changeActualPlayerByIndex(i);
			
			if(actualPlayer.isWinner() == false) {
				sendMessageForRead("Önnek most nem volt szerencséje, " + winnersName + " nyerte meg a játékot.");
			}
			else if(actualPlayer.isWinner() == true) {
				sendMessageForRead("Gratulálunk, " + winnersName + "! Szép játék volt, Ön nyert!");
			}
			actualPlayer.getSocket().close();
		}
		serverSocket.close();
	}

	//CALLABLE METHODS OF FIELD AND LUCKYCARD COMMANDS
	private void drawNextLuckyCard() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		luckyCardIndex++;
		luckyCardIndex %= 36;
		sendMessageForRead("Ezt a szerencsekártyát húztad: " + deck.get(luckyCardIndex).getDescription());
		executeLuckyCardCommand();
	}
	
	/* DONE
	 * NEED REVIEW */
	private void loseCar() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(actualPlayer.getCar() != null) {
			
			int moneyToGiveBack = 0;
			if(actualPlayer.getCar().getDebit() > 0)
				moneyToGiveBack = 12000 - actualPlayer.getCar().getDebit();
			else if(actualPlayer.getCar().getDebit() == 0) {
				moneyToGiveBack = 10000;
			}
			if(actualPlayer.getCar().getIsInsured() == true) {
				addMoney(moneyToGiveBack);
			}
			else if(actualPlayer.getCar().getIsInsured() == false) {
				moveToField(9);
			}
		actualPlayer.setCar(null);
		sendGameState("CAR");
		}
	}
	
	/* DONE
	 * NEED REVIEW */
	private void loseFurnitures() throws IOException {
		if(actualPlayer.getHouse().getIsInsured() == true) {
			int moneyToGiveBack = 0;
			if(actualPlayer.getHouse().getHasCooker() == true)
				moneyToGiveBack += 200;
			if(actualPlayer.getHouse().getHasDishwasher() == true)
				moneyToGiveBack += 300;
			if(actualPlayer.getHouse().getHasFrigo() == true)
				moneyToGiveBack += 200;
			if(actualPlayer.getHouse().getHasKitchen() == true)
				moneyToGiveBack += 1000;
			if(actualPlayer.getHouse().getHasRoomFurniture() == true)
				moneyToGiveBack += 3000;
			if(actualPlayer.getHouse().getHasWashMachine() == true)
				moneyToGiveBack += 300;
			addMoney(moneyToGiveBack);
		}
		actualPlayer.getHouse().setHasCooker(false);
		actualPlayer.getHouse().setHasDishwasher(false);
		actualPlayer.getHouse().setHasFrigo(false);
		actualPlayer.getHouse().setHasKitchen(false);
		actualPlayer.getHouse().setHasRoomFurniture(false);
		actualPlayer.getHouse().setHasWashMachine(false);
		sendGameState("HOUSE");
	}

	/* MAYBE DONE
	 * NEED REVIEW  */
	private void moveWithQuantity(int amount) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		int newPositionNumber = actualPlayer.getLocationNumber() + amount;
		actualPlayer.setLocation(board.get(newPositionNumber % 42));
		System.out.println(actualPlayer.getLocationNumber());
		sendGameState("LOCATION");
		if(newPositionNumber > 42) { //it means that round finished, and we step over start field
			handleDebits();	//if we can handle debits, so actual player is not in a looser state
			executeFieldCommand();	//then execute the field command
		}
		else if(newPositionNumber == 42) {	//it means that round finished, and we are on start field
			executeFieldCommand();			//execute field command ( so add 4000 euros )
			handleDebits();					//and then we handle the debits.
		}
		else								//it means a simple step, so the round is not finished
			executeFieldCommand();			//we execute field command
		return;
	}
	
	/* MAYBE DONE
	 * NEED REVIEW  */
	private void moveToField(int goalFieldsNumber) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		int originalPositionNumber = actualPlayer.getLocationNumber();
		actualPlayer.setLocation(board.get(goalFieldsNumber));
		sendGameState("LOCATION");
		if( ( goalFieldsNumber < originalPositionNumber ) && ( goalFieldsNumber == 0 ) ) {
			executeFieldCommand();
			handleDebits();
		}
		else if(( goalFieldsNumber < originalPositionNumber ) && ( goalFieldsNumber > 0 ) ) {
			handleDebits();
			executeFieldCommand();
		}
		else {
			executeFieldCommand();
		}
	}
	
	/* MAYBE DONE!
	 * NEED REVIEW */
	private void offerBuyCar() throws IOException {
		if (actualPlayer.getCar()==null){
			out.flush();
			out.writeUTF("BUYCAR");
			System.out.println("###Buying of Car Offered.###");
			String incomingMessage = in.readUTF();
			if((incomingMessage.equals("BUYFORCASH")) && (checkBalance(10000) == true)) {
				actualPlayer.setCar(new Car());
				actualPlayer.getCar().setDebit(0);
				out.flush();
				out.writeUTF("SUCCESS");
				deductMoney(10000);
				sendGameState("CAR");
			}
			else if((incomingMessage.equals("BUYFORCREDIT")) && (checkBalance(2000) == true)) {
				actualPlayer.setCar(new Car());
				actualPlayer.getCar().setDebit(10000);
				out.flush();
				out.writeUTF("SUCCESS");
				deductMoney(15000);
				sendGameState("CAR");
			}
			else if(incomingMessage.equals("DONTBUY")) {
				out.flush();
				out.writeUTF("UNSUCCESS");
			}
		}
		
	}
	
	private void offerBuyFurniture2(String string) throws IOException {
		String incomingMessage = null;
		if(actualPlayer.getHouse() == null )
			sendMessageForRead("Még nem tudsz bútorokat vásárolni, mert nincs házad.");
		else if(actualPlayer.getHouse() != null) {
			if(incomingMessage.equals("COOKER")) {
			if(checkBalance(200) == false) {
				sendMessageForRead("Nincs elegendő pénzed a tűzhely megvásárlásához.");
			}
			else if(checkBalance(200) == true) 
				if(actualPlayer.getHouse().getHasCooker() == true)
					sendMessageForRead("Már van tűzhelyed, és csak egyet vehetsz.");
		/* CONTINUE HERE MADAFUCKA*/
			}
		}
	}
	
	
	/* MAYBE DONE!
	 * NEED REVIEW !*/
	private void offerBuyFurniture(String string) throws IOException {
		String incomingMessage;
		if (actualPlayer.getHouse()!=null) {								//we offer buying furnitures only if the player has a house
			if(string.equals("COOKER")) {								//if on the actual field the "COOKER" is buyable
				if(actualPlayer.getHouse().getHasCooker() == false) { 	//and if the player does not have it
					if( checkBalance(200) == true ) {					//and if the player has the required money to buy it
						out.flush();									//we offer business
						out.writeUTF("BUYFURNITURE");					
						System.out.println("###Buying of Furniture Offered.###");
						out.flush();
						out.writeUTF(string);
						incomingMessage = in.readUTF();
						if(incomingMessage.equals("BUYCOOKER")) {			//if the player want to buy the cooker
							actualPlayer.getHouse().setHasCooker(true);		//we give it to the player
							deductMoney(200);								//deduct the price of it
							sendGameState("HOUSE");							//and send gamestate infos about the house
							sendGameState("BALANCE");						//and the balance
						}
						else if(incomingMessage.equals("DONTBUYCOOKER")) {	//but if the player don't want to buy it
																			//we don't do anything
						}
					}
					else { 												//but if the player does not have the required money
						out.flush();
						out.writeUTF("NOTENOUGHMONEY");
					}		
				}
				else {
					out.flush();
					out.writeUTF("ALREADYHAVETHIS");
				}
			}
			else if( string.equals("DISHWASHER") ) {								//if on the actual field the "COOKER" is buyable
				if(actualPlayer.getHouse().getHasDishwasher() == false) { 	//and if the player does not have it
					if( checkBalance(300) == true ) {					//and if the player has the required money to buy it
						out.flush();									//we offer business
						out.writeUTF("BUYFURNITURE");					
						System.out.println("###Buying of Furniture Offered.###");
						out.flush();
						out.writeUTF(string);
						incomingMessage = in.readUTF();
						if(incomingMessage.equals("BUYDISHWASHER")) {			//if the player want to buy the cooker
							actualPlayer.getHouse().setHasDishwasher(true);		//we give it to the player
							deductMoney(300);								//deduct the price of it
							sendGameState("HOUSE");							//and send gamestate infos about the house
							sendGameState("BALANCE");						//and the balance
						}
						else if(incomingMessage.equals("DONTBUYDISHWASHER")) {	//but if the player don't want to buy it
																				//we don't do anything
						}
					}
					else { 												//but if the player does not have the required money
						out.flush();
						out.writeUTF("NOTENOUGHMONEY");
					}		
				}
				else {
					out.flush();
					out.writeUTF("ALREADYHAVETHIS");
				}
			}
			else if( string.equals("FRIGO") ) {								//if on the actual field the "COOKER" is buyable
				if(actualPlayer.getHouse().getHasFrigo() == false) { 	//and if the player does not have it
					if( checkBalance(200) == true ) {					//and if the player has the required money to buy it
						out.flush();									//we offer business
						out.writeUTF("BUYFURNITURE");					
						System.out.println("###Buying of Furniture Offered.###");
						out.flush();
						out.writeUTF(string);
						incomingMessage = in.readUTF();
						if(incomingMessage.equals("BUYFRIGO")) {			//if the player want to buy the cooker
							actualPlayer.getHouse().setHasFrigo(true);		//we give it to the player
							deductMoney(200);								//deduct the price of it
							sendGameState("HOUSE");							//and send gamestate infos about the house
							sendGameState("BALANCE");						//and the balance
						}
						else if(incomingMessage.equals("DONTBUYFRIGO")) {	//but if the player don't want to buy it
																			//we don't do anything
						}
					}
					else { 												//but if the player does not have the required money
						out.flush();
						out.writeUTF("NOTENOUGHMONEY");
					}		
				}
				else {
					out.flush();
					out.writeUTF("ALREADYHAVETHIS");
				}
			}
			else if( string.equals("KITCHENFURNITURE") ) {								//if on the actual field the "COOKER" is buyable
				if(actualPlayer.getHouse().getHasKitchen() == false) { 	//and if the player does not have it
					if( checkBalance(1000) == true ) {					//and if the player has the required money to buy it
						out.flush();									//we offer business
						out.writeUTF("BUYFURNITURE");					
						System.out.println("###Buying of Furniture Offered.###");
						out.flush();
						out.writeUTF(string);
						incomingMessage = in.readUTF();
						if(incomingMessage.equals("BUYKITCHENFURNITURE")) {			//if the player want to buy the cooker
							actualPlayer.getHouse().setHasKitchen(true);		//we give it to the player
							deductMoney(1000);								//deduct the price of it
							sendGameState("HOUSE");							//and send gamestate infos about the house
							sendGameState("BALANCE");						//and the balance
						}
						else if(incomingMessage.equals("DONTBUYKITCHENFURNITURE")) {	//but if the player don't want to buy it
																						//we don't do anything
						}
					}
					else { 												//but if the player does not have the required money
						out.flush();
						out.writeUTF("NOTENOUGHMONEY");
					}		
				}
				else {
					out.flush();
					out.writeUTF("ALREADYHAVETHIS");
				}
			}
			else if( string.equals("ROOMFURNITURE") ) {								//if on the actual field the "COOKER" is buyable
				if(actualPlayer.getHouse().getHasRoomFurniture() == false) { 	//and if the player does not have it
					if( checkBalance(3000) == true ) {					//and if the player has the required money to buy it
						out.flush();									//we offer business
						out.writeUTF("BUYFURNITURE");					
						System.out.println("###Buying of Furniture Offered.###");
						out.flush();
						out.writeUTF(string);
						incomingMessage = in.readUTF();
						if(incomingMessage.equals("BUYROOMFURNITURE")) {			//if the player want to buy the cooker
							actualPlayer.getHouse().setHasRoomFurniture(true);		//we give it to the player
							deductMoney(3000);								//deduct the price of it
							sendGameState("HOUSE");							//and send gamestate infos about the house
							sendGameState("BALANCE");						//and the balance
						}
						else if(incomingMessage.equals("DONTBUYROOMFURNITURE")) {	//but if the player don't want to buy it
																					//we don't do anything
						}
					}
					else { 												//but if the player does not have the required money
						out.flush();
						out.writeUTF("NOTENOUGHMONEY");
					}		
				}
				else {
					out.flush();
					out.writeUTF("ALREADYHAVETHIS");
				}
			}
			else if( string.equals("WASHMACHINE") ) {								//if on the actual field the "COOKER" is buyable
				if(actualPlayer.getHouse().getHasWashMachine() == false) { 	//and if the player does not have it
					if( checkBalance(300) == true ) {					//and if the player has the required money to buy it
						out.flush();									//we offer business
						out.writeUTF("BUYFURNITURE");					
						System.out.println("###Buying of Furniture Offered.###");
						out.flush();
						out.writeUTF(string);
						incomingMessage = in.readUTF();
						if(incomingMessage.equals("BUYWASHMACHINE")) {			//if the player want to buy the cooker
							actualPlayer.getHouse().setHasWashMachine(true);		//we give it to the player
							deductMoney(300);								//deduct the price of it
							sendGameState("HOUSE");							//and send gamestate infos about the house
							sendGameState("BALANCE");						//and the balance
						}
						else if(incomingMessage.equals("DONTBUYWASHMACHINE")) {	//but if the player don't want to buy it
																				//we don't do anything
						}
					}
					else { 												//but if the player does not have the required money
						out.flush();
						out.writeUTF("NOTENOUGHMONEY");
					}		
				}
				else {
					out.flush();
					out.writeUTF("ALREADYHAVETHIS");
				}
			}
		}
	}
	
	/* MAYBE DONE!
	 * NEED REVIEW! */
	private void offerBuyHouse() throws IOException {
		if (actualPlayer.getHouse()==null) {	//if player does not have a house, we offer to buy it
			out.flush();
			out.writeUTF("BUYHOUSE");
			System.out.println("###Buying of House Offered.###");
			String incomingMessage = in.readUTF();
			if((incomingMessage.equals("BUYFORCASH")) && (checkBalance(30000) == true)) {
				actualPlayer.setHouse(new House());
				actualPlayer.getHouse().setDebit(0);
				out.flush();
				out.writeUTF("SUCCESS");
				deductMoney(30000);
				sendGameState("HOUSE");
			}
			else if((incomingMessage.equals("BUYFORCREDIT")) && (checkBalance(15000) == true)) {
				actualPlayer.setHouse(new House());
				System.out.println(actualPlayer.getHouse());
				actualPlayer.getHouse().setDebit(15000);
				out.flush();
				out.writeUTF("SUCCESS");
				deductMoney(15000);
				sendGameState("HOUSE");
			}
			else if(incomingMessage.equals("DONTBUY")) {
				out.flush();
				out.writeUTF("UNSUCCESS");
			}
		}		
	}
	
	
	/* FAULTY!!!!!!!!!!!!!!!! */
	private void offerMakeInsurances() throws IOException {
		if (actualPlayer.getHouse()!=null && actualPlayer.getHouse() !=null){
		out.flush();
		out.writeUTF("MAKEINSURANCES");
		System.out.println("###Making of Insurances Offered.###");
		
		String incomingMessage = in.readUTF();
		if((incomingMessage.equals("MAKEONLYHOUSEINSURANCE")) && (checkBalance(100) == true)) {
			actualPlayer.getHouse().setIsInsured(true);
			out.flush();
			out.writeUTF("SUCCESS");
			deductMoney(100);
			sendGameState("HOUSE");
		}
		else if((incomingMessage.equals("MAKEONLYCARINSURANCE")) && (checkBalance(100) == true)) {
			actualPlayer.getCar().setIsInsured(true);
			out.flush();
			out.writeUTF("SUCCESS");
			deductMoney(100);
			sendGameState("CAR");
		}
		else if((incomingMessage.equals("MAKEBOTHINSURANCES")) && (checkBalance(200) == true)) {
			actualPlayer.getCar().setIsInsured(true);
			actualPlayer.getHouse().setIsInsured(true);
			out.flush();
			out.writeUTF("SUCCESS");
			deductMoney(200);
			sendGameState("CAR");
			sendGameState("HOUSE");
		}
		else if(incomingMessage.equals("DONTMAKEANYINSURANCES")) {
			out.flush();
			out.writeUTF("UNSUCCESS");
		}
		}
	}
	
	
	/* DONE 
	 * NEED REVIEW */
	private void sendMessageForRead(String description) throws IOException {
		out.writeUTF("MESSAGEFORREAD");
		out.writeUTF(description);
	}	
	
	
	/* DONE 
	 * NEED REVIEW */
	private void set_1_6Penalty(int amount) {
		actualPlayer.set_1_6Penalty(amount);
		return;
	}
	
	
	/* DONE 
	 * DON'T NEED REVIEW */
	private void setExclusions(int amount) {
		actualPlayer.setExclusions(amount);
		return;
	}
	
	
	/* DONE 
	 * DON'T NEED REVIEW  */
	private void setGiftDices(int amount) {
		actualPlayer.setGiftDices(amount);
		return;
	}
	
	
	/* DONE 
	 * DON'T NEED REVIEW */
	private void wonFurniture(String furnitureName) throws IOException {
		if(furnitureName.equals("DISHWASHER")) {
			if( (actualPlayer.getHouse() != null) && (actualPlayer.getHouse().getHasDishwasher() == false) ) {
				actualPlayer.getHouse().setHasDishwasher(true);
				sendGameState("HOUSE");
				
			}	
			else {
				addMoney(300);
			}
		}
		else if(furnitureName.equals("WASHMACHINE")) {
			if( (actualPlayer.getHouse() != null) && (actualPlayer.getHouse().getHasWashMachine() == false) ) {
				actualPlayer.getHouse().setHasWashMachine(true);
				sendGameState("HOUSE");
			}	
			else {
				addMoney(300);
			}
		}
		else if(furnitureName.equals("ROOMFURNITURE")) {
			if( (actualPlayer.getHouse() != null) && (actualPlayer.getHouse().getHasRoomFurniture() == false) ) {
				actualPlayer.getHouse().setHasRoomFurniture(true);
				sendGameState("HOUSE");
			}	
			else {
				addMoney(3000);
			}
		}
	return;
	}
}
