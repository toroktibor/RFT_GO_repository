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

import server.xml.XMLParser;


public class GameEngine implements ICashier, IGamePlay {
	private Player actualPlayer;
	private List<Player> allPlayers = new ArrayList<Player>();
	private List<Field> board = new ArrayList<Field>();			//the board contains the list of all 42 fields
	private List<LuckyCard> deck = new ArrayList<LuckyCard>();	//the deck contains the list of all lucky cards
	private int luckyCardIndex = -1;		//index of the following lucky card in the row
	private int playerIndex = 0;		//index of the respective actual player
	
	private XMLParser parser=new XMLParser();	//the parser used for initialize the lucky cards of the deck and the fields of the board
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
	public void addMoney(int amount) throws IOException {
		int originalBalance = actualPlayer.getBalance();
		actualPlayer.setBalance(originalBalance+amount);
		System.out.println("###Money added. Original -> New balance:" 
							+ originalBalance + "->" + actualPlayer.getBalance() + " ###");
		sendGameState("BALANCE");
		return;
	}
	
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
	
	/** DONE - This method deduct 500 euros for each debts in the end of every round. 
	 * If it is not possible, the player lose the game. 
	 * */
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
	
	/** This method initialize the fields of the board, and the lucky cards of the deck. */
	public void init() {
		board=parser.parseFields("Fields.xml");
		deck=parser.parseLuckyCards("LuckyCards.xml");
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
				set_1_6Penalty(actualPlayer.get_1_6Penalty()-1);
				sendMessageForRead("Büntetésben vagy, mely szerint csak 1-es vagy 6-os dobással léphetsz tovább.\n" +
									"Most itt maradsz, mert dobásod értéke " + result + ".\n" +
									actualPlayer.get_1_6Penalty() + " kör múlva bármilyen dobással tovább léphetsz.");
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
		System.out.println("CommandWordIterator :" + commandWordIterator);
		System.out.println("Végrehajtandó metódusok száma: " + numberOfExecutableMethods);
		// Ennek megfelelő számú metódust kell meghívni. (ez egyéként max. 2 lesz.)
		// Lekérjük az osztálytól a metódusok listáját, hogy majd ezek közül egyet meghívhassunk.
		Method[] methods = GameEngine.class.getDeclaredMethods();
		for(methodIterator = 0; methodIterator<numberOfExecutableMethods; ++methodIterator) {
			// Az executableMethodsName változóban rögzítem a végrehajtandó metódus nevét.
			executableMethodsName = commandWords[commandWordIterator++];
			// Az actMet metódusban rögzítem a végrehajtandó metódus objektumot.
			Method actMet = methods[giveIndexOfSearchedMethod(methods, executableMethodsName)];
			// Megvizsgálom a metódus neve alapján, hogy hány paramétere lesz, azokat rögzítem, és meghívom a met�dust.
			
			if(	executableMethodsName.equals("addMoney") || executableMethodsName.equals("deductMoney") || 
				executableMethodsName.equals("moveToField") || executableMethodsName.equals("moveWithQuantity") ||
				executableMethodsName.equals("set_1_6Penalty") || executableMethodsName.equals("setGiftDices") ||
				executableMethodsName.equals("setExclusion")) {
				
				int param1 = Integer.parseInt(commandWords[commandWordIterator++]);
				System.out.println("###végrehajtandó metódus: " + executableMethodsName + "(" + param1 + ")###");
				System.out.println("###Ami tényleg végre lesz hajtva: " + actMet.getName() + "###");
				actMet.invoke(this, param1);
			}
			else if(executableMethodsName.equals("offerBuyFurniture") || executableMethodsName.equals("sendMessageForRead") ||
					executableMethodsName.equals("offerMakeInsurances")) {
				String param1 = commandWords[commandWordIterator++];
				System.out.println("###végrehajtandó metódus: " + executableMethodsName + "(" + param1 + ")###");
				System.out.println("###Ami tényleg végre lesz hajtva: " + actMet.getName());
				actMet.invoke(this, param1);
			}
			else if(executableMethodsName.equals("offerBuyHouse") || executableMethodsName.equals("offerBuyCar") 
					 || executableMethodsName.equals("drawNextLuckyCard")) {
				System.out.println("###végrehajtandó metódus: " + executableMethodsName + "###");
				System.out.println("###Ami tényleg végre lesz hajtva: " + actMet.getName() + "###");
				actMet.invoke(this);
			}
			System.out.println("CommandWordIterator :" + commandWordIterator);
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
			
			if(	executableMethodsName.equals("addMoney") || executableMethodsName.equals("deductMoney") ||
				executableMethodsName.equals("addPercentage") || executableMethodsName.equals("set_1_6Penalty") || 
				executableMethodsName.equals("setGiftDices")  ||executableMethodsName.equals("setExclusion") ||
				executableMethodsName.equals("moveToField") ||	executableMethodsName.equals("moveWithQuantity")) {
				
				int param1 = Integer.parseInt(commandWords[commandWordIterator++]);
				actMet.invoke(this, param1);
			}
			else if(executableMethodsName.equals("wonFurniture")) {
				String param1 = commandWords[commandWordIterator++];
				actMet.invoke(this, param1);
			}
			else if(	(executableMethodsName.equals("loseFurnitures")) ||
					(executableMethodsName.equals("loseCar"))) {
				actMet.invoke(this);
			}
		}
		
		return;
	}
	
	/** In this method we wait for the given number players. There is a time limit, after which we start the game.
	 */
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
			if(allPlayers.get(originalActualPlayersIndex).getHouse() != null)
				s=s.concat("#1#SETHOUSESTATEBINARYFLAGS#" + actualPlayer.getHouse().toString());	
			else
				s=s.concat("#1#SETHOUSESTATEBINARYFLAGS#FALSE:FALSE:FALSE:FALSE:FALSE:FALSE:FALSE:FALSE:0");
		}
		else if(option.equals("CAR")) {
			if(allPlayers.get(originalActualPlayersIndex).getCar()!= null)
				s=s.concat("#1#SETCARSTATEBINARYFLAGS#" + actualPlayer.getCar().toString());
			else
				s=s.concat("#1#SETCARSTATEBINARYFLAGS#FALSE:FALSE:0");
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
			s=s.concat("#SETHOUSESTATEBINARYFLAGS#FALSE:FALSE:FALSE:FALSE:FALSE:FALSE:FALSE:FALSE:0");	
			s=s.concat("#SETCARSTATEBINARYFLAGS#FALSE:FALSE:0");
			s=s.concat("#SETNAME#" + actualPlayer.getName().toString());
			s=s.concat("#SETBALANCE#" + actualPlayer.getBalance());
			s=s.concat("#SETLOCATION#" + actualPlayer.getLocation().toString());
		}
		for(int i = 0; i<allPlayers.size(); ++i) {
			changeActualPlayerByIndex(i);
			out.flush();
			out.writeUTF("GETGAMESTATE");
			out.flush();
			out.writeUTF(s);
		}
		changeActualPlayerByIndex(originalActualPlayersIndex);		
	}
	
	/* DONE - This method change the value of the actualPlayer to a player of allPlayers list given by the index */
	public void changeActualPlayerByIndex(int index) {
		
		actualPlayer = allPlayers.get(index);
		if(actualPlayer.getIsActive() == true) {
			try {
				in = new DataInputStream( actualPlayer.getSocket().getInputStream());
				out =new DataOutputStream( actualPlayer.getSocket().getOutputStream());
			} catch (IOException e) {
				actualPlayer.setIsActive(false);
				e.printStackTrace();
			}
		}
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
					while( actualPlayer.getGiftDices() > 0 ) {
						dice();
						actualPlayer.setGiftDices(actualPlayer.getGiftDices()-1);
						if(actualPlayer.isWinner() == true)
							break;
						if((actualPlayer.getExclusions() > 0) && (actualPlayer.getGiftDices() > 0)) {
							actualPlayer.setGiftDices(actualPlayer.getGiftDices()-1);
							break;
						}
					}
				}
				else if(actualPlayer.getExclusions() > 0) {
					actualPlayer.setExclusions(actualPlayer.getExclusions()-1);
				}
			}
			++iterator;
			iterator %= allPlayers.size();
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
	
	
	private void loseFurnitures() throws IOException {
		if(actualPlayer.getHouse() != null) {
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
	}

	
	private void moveWithQuantity(int amount) throws IllegalAccessException, IllegalArgumentException, 
								InvocationTargetException, IOException {
		
		int newPositionNumber = actualPlayer.getLocationNumber() + amount;
		actualPlayer.setLocation(board.get(newPositionNumber % 42));
		System.out.println("Ezen a mezőn áll " + actualPlayer.getName() + " : " + actualPlayer.getLocationNumber() + ". mező!");
		sendGameState("LOCATION");
		if(newPositionNumber > 42) { //it means that round finished, and we step over start field
			addPercentage(5);
			addMoney(2000);
			handleDebits();	//if we can handle debits, so actual player is not in a looser state
			executeFieldCommand();	//then execute the field command
		}
		else if(newPositionNumber == 42) {	//it means that round finished, and we are on start field
			addPercentage(5);
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
			addPercentage(5);
			executeFieldCommand();
			handleDebits();
		}
		else if(( goalFieldsNumber < originalPositionNumber ) && ( goalFieldsNumber > 0 ) ) {
			addPercentage(5);
			addMoney(2000);
			handleDebits();
			executeFieldCommand();
		}
		else {
			executeFieldCommand();
		}
	}
	
	private void offerBuyCar() throws IOException {
		String incomingMessage;
		if(actualPlayer.getCar() != null) {
			sendMessageForRead("Már van autód, és csak egyet birtokolhatsz.");
		}
		else if (actualPlayer.getCar()==null){
			if(checkBalance(10000) == true) {
				out.flush();
				out.writeUTF("BUYCARFORCASH");
				System.out.println("###Buying of Car Offered.###");
				incomingMessage = in.readUTF();
				if((incomingMessage.equals("BUYFORCASH"))) {
					actualPlayer.setCar(new Car());
					actualPlayer.getCar().setDebit(0);
					deductMoney(10000);
					sendGameState("CAR");
				}
				else if((incomingMessage.equals("BUYFORCREDIT"))) {
					actualPlayer.setCar(new Car());
					actualPlayer.getCar().setDebit(10000);
					deductMoney(2000);
					sendGameState("CAR");
				}
				else if(incomingMessage.equals("DONTBUY")) {
				}
			}
			else if(checkBalance(2000) == true) {
				out.flush();
				out.writeUTF("BUYCARFORCREDIT");
				System.out.println("###Buying of Car Offered.###");
				incomingMessage = in.readUTF();
				if((incomingMessage.equals("BUYFORCREDIT"))) {
					actualPlayer.setCar(new Car());
					actualPlayer.getCar().setDebit(10000);
					deductMoney(2000);
					sendGameState("CAR");
				}
				else if(incomingMessage.equals("DONTBUY")) {
				}
			}
			else if(checkBalance(2000) == false) {
				sendMessageForRead("Nincs elegendő pénzed autó vásárlásához.\n" + 
						"Térj vissza, ha már van legalább 2.000 euród, hogy hitelre vásárolhass!\n" +
						"Ha egy összegben szeretnéd kifizetni autód árát, 10.000 eurót kell gyűjtened!");
			}
		}
	}
	
	
	private void offerBuyFurniture(String option) throws IOException {
	String incomingMessage = null;
	if(actualPlayer.getHouse() == null )
		sendMessageForRead("Még nem tudsz bútort vásárolni, mert nincs házad.");
	else if(actualPlayer.getHouse() != null) {
		if(option.equals("COOKER")) {
			if(checkBalance(200) == false) {
				sendMessageForRead("Nincs elegendő pénzed a tűzhely megvásárlásához.\nGyere vissza, ha már van 200 euród!");
			}
			else if(checkBalance(200) == true) {
				if(actualPlayer.getHouse().getHasCooker() == true) {
					sendMessageForRead("Már van tűzhelyed, és csak egyet birtokolhatsz.");
				}
				else if(actualPlayer.getHouse().getHasCooker() == false) {
					out.flush();
					out.writeUTF("BUYFURNITURE");					
					System.out.println("###Buying of Furniture Offered.###");
					out.flush();
					out.writeUTF(option);
					incomingMessage = in.readUTF();
					if(incomingMessage.equals("BUYCOOKER")) {
						actualPlayer.getHouse().setHasCooker(true);	
						deductMoney(200);								
						sendGameState("HOUSE");											
					}
				}
			}
		}
		else if(option.equals("DISHWASHER")) {
			if(checkBalance(300) == false) {
				sendMessageForRead("Nincs elegendő pénzed a mosogatógép megvásárlásához.\nGyere vissza, ha már van 300 euród!");
			}
			else if(checkBalance(300) == true) {
				if(actualPlayer.getHouse().getHasDishwasher() == true) {
					sendMessageForRead("Már van mosogatógéped, és csak egyet birtokolhatsz.");
				}
				else if(actualPlayer.getHouse().getHasDishwasher() == false) {
					out.flush();
					out.writeUTF("BUYFURNITURE");					
					System.out.println("###Buying of Furniture Offered.###");
					out.flush();
					out.writeUTF(option);
					incomingMessage = in.readUTF();
					if(incomingMessage.equals("BUYDISHWASHER")) {
						actualPlayer.getHouse().setHasDishwasher(true);	
						deductMoney(300);								
						sendGameState("HOUSE");											
					}
				}
			}
		}
		else if(option.equals("FRIGO")) {
			if(checkBalance(200) == false) {
				sendMessageForRead("Nincs elegendő pénzed a hűtő megvásárlásához.\nGyere vissza, ha már van 200 euród!");
			}
			else if(checkBalance(200) == true) {
				if(actualPlayer.getHouse().getHasFrigo() == true) {
					sendMessageForRead("Már van hűtőd, és csak egyet birtokolhatsz.");
				}
				else if(actualPlayer.getHouse().getHasFrigo() == false) {
					out.flush();
					out.writeUTF("BUYFURNITURE");					
					System.out.println("###Buying of Furniture Offered.###");
					out.flush();
					out.writeUTF(option);
					incomingMessage = in.readUTF();
					if(incomingMessage.equals("BUYFRIGO")) {
						actualPlayer.getHouse().setHasFrigo(true);	
						deductMoney(200);								
						sendGameState("HOUSE");											
					}
				}
			}
		}
		else if(option.equals("KITCHENFURNITURE")) {
			if(checkBalance(1000) == false) {
				sendMessageForRead("Nincs elegendő pénzed a konyhaszekrény megvásárlásához.\nGyere vissza, ha már van 1000 euród!");
			}
			else if(checkBalance(1000) == true) {
				if(actualPlayer.getHouse().getHasKitchen() == true) {
					sendMessageForRead("Már van konyhaszekrényed, és csak egyet birtokolhatsz.");
				}
				else if(actualPlayer.getHouse().getHasKitchen() == false) {
					out.flush();
					out.writeUTF("BUYFURNITURE");					
					System.out.println("###Buying of Furniture Offered.###");
					out.flush();
					out.writeUTF(option);
					incomingMessage = in.readUTF();
					if(incomingMessage.equals("BUYKITCHENFURNITURE")) {
						actualPlayer.getHouse().setHasKitchen(true);	
						deductMoney(1000);								
						sendGameState("HOUSE");											
					}
				}
			}
		}
		else if(option.equals("ROOMFURNITURE")) {
			if(checkBalance(3000) == false) {
				sendMessageForRead("Nincs elegendő pénzed a szobaszekrény megvásárlásához.\nGyere vissza, ha már van 3000 euród!");
			}
			else if(checkBalance(3000) == true) {
				if(actualPlayer.getHouse().getHasRoomFurniture() == true) {
					sendMessageForRead("Már van szobaszekrényed, és csak egyet birtokolhatsz.");
				}
				else if(actualPlayer.getHouse().getHasRoomFurniture() == false) {
					out.flush();
					out.writeUTF("BUYFURNITURE");					
					System.out.println("###Buying of Furniture Offered.###");
					out.flush();
					out.writeUTF(option);
					incomingMessage = in.readUTF();
					if(incomingMessage.equals("BUYROOMFURNITURE")) {
						actualPlayer.getHouse().setHasRoomFurniture(true);	
						deductMoney(3000);								
						sendGameState("HOUSE");											
					}
				}
			}
		}
		else if(option.equals("WASHMACHINE")) {
			if(checkBalance(300) == false) {
				sendMessageForRead("Nincs elegendő pénzed a mosógép megvásárlásához.\nGyere vissza, ha már van 300 euród!");
			}
			else if(checkBalance(300) == true) {
				if(actualPlayer.getHouse().getHasWashMachine() == true) {
					sendMessageForRead("Már van mosógéped, és csak egyet birtokolhatsz.");
				}
				else if(actualPlayer.getHouse().getHasWashMachine() == false) {
					out.flush();
					out.writeUTF("BUYFURNITURE");					
					System.out.println("###Buying of Furniture Offered.###");
					out.flush();
					out.writeUTF(option);
					incomingMessage = in.readUTF();
					if(incomingMessage.equals("BUYWASHMACHINE")) {
						actualPlayer.getHouse().setHasWashMachine(true);	
						deductMoney(300);								
						sendGameState("HOUSE");											
					}
				}
			}
		}
	}
}

	private void offerBuyHouse() throws IOException {
		String incomingMessage;
		if(actualPlayer.getHouse() != null) {
			sendMessageForRead("Már van házad, és csak egyet birtokolhatsz.");
		}
		else if (actualPlayer.getHouse()==null){
			if(checkBalance(30000) == true) {
				out.flush();
				out.writeUTF("BUYHOUSEFORCASH");
				System.out.println("###Buying of House Offered.###");
				incomingMessage = in.readUTF();
				if((incomingMessage.equals("BUYFORCASH"))) {
					actualPlayer.setHouse(new House());
					actualPlayer.getHouse().setDebit(0);
					deductMoney(30000);
					sendGameState("HOUSE");
				}
				else if((incomingMessage.equals("BUYFORCREDIT"))) {
					actualPlayer.setHouse(new House());
					actualPlayer.getHouse().setDebit(15000);
					deductMoney(15000);
					sendGameState("HOUSE");
				}
				else if(incomingMessage.equals("DONTBUY")) {
				}
			}
			else if(checkBalance(15000) == true) {
				out.flush();
				out.writeUTF("BUYHOUSEFORCREDIT");
				System.out.println("###Buying of House Offered.###");
				incomingMessage = in.readUTF();
				if((incomingMessage.equals("BUYFORCREDIT"))) {
					actualPlayer.setHouse(new House());
					actualPlayer.getHouse().setDebit(15000);
					deductMoney(15000);
					sendGameState("HOUSE");
				}
				else if(incomingMessage.equals("DONTBUY")) {
				}
			}
			else if(checkBalance(15000) == false) {
				sendMessageForRead("Nincs elegendő pénzed ház vásárlásához.\n" + 
						"Térj vissza, ha már van legalább 15.000 euród, hogy hitelre vásárolhass!\n" +
						"Ha egy összegben szeretnéd kifizetni házad árát, 30.000 eurót kell gyűjtened!");
			}
		}
	}
	
	
	private void offerMakeInsurances(String option) throws IOException {
		String incomingMessage;
		if(option.equals("CAR")) {
			if(actualPlayer.getCar() == null) {
				sendMessageForRead("Mivel még nincs autód, nem tudsz biztosítást kötni rá.\nTérj vissza, ha már vásároltál autót.");
			}
			else if(actualPlayer.getCar() != null) {
				if(actualPlayer.getCar().getIsInsured() == true) {
					sendMessageForRead("Autódra már van érvényes biztosítás kötve, és egyszerre csak egy biztosítás lehet életben.");
				}
				else if((checkBalance(100) == false)) {
					sendMessageForRead("Nincs elegendő pénzed, hogy autódra biztosítást köss.\nTérj vissza, ha már összegyűjtöttél 100 eurót!");
				}
				else if(checkBalance(100) == true) {
					out.flush();
					out.writeUTF("MAKEINSURANCE");
					System.out.println("###Making Insurances Offered.###");
					out.flush();
					out.writeUTF(option);
					incomingMessage = in.readUTF();
					if(incomingMessage.equals("MAKEINSURANCE")) {
						actualPlayer.getCar().setIsInsured(true);	
						deductMoney(100);								
						sendGameState("CAR");											
					}
					else if(incomingMessage.equals("DONTMAKEINSURANCE")) {
					}
				}
			}
		}
		else if(option.equals("HOUSE")) {
			if(actualPlayer.getHouse() == null) {
				sendMessageForRead("Mivel még nincs házad, nem tudsz biztosítást kötni rá.\nTérj vissza, ha már vásároltál házat.");
			}
			else if(actualPlayer.getHouse() != null) {
				if(actualPlayer.getHouse().getIsInsured() == true) {
					sendMessageForRead("Házadra már van érvényes biztosítás kötve, és egyszerre csak egy biztosítás lehet életben.");
				}
				else if((checkBalance(100) == false)) {
					sendMessageForRead("Nincs elegendő pénzed, hogy házadra biztosítást köss.\nTérj vissza, ha már összegyűjtöttél 100 eurót!");
				}
				else if(checkBalance(100) == true) {
					out.flush();
					out.writeUTF("MAKEINSURANCE");
					System.out.println("###Making Insurances Offered.###");
					out.flush();
					out.writeUTF(option);
					incomingMessage = in.readUTF();
					if(incomingMessage.equals("MAKEINSURANCE")) {
						actualPlayer.getHouse().setIsInsured(true);	
						deductMoney(100);								
						sendGameState("HOUSE");											
					}
					else if(incomingMessage.equals("DONTMAKEINSURANCE")) {
					}
				}
			}
		}
	}
	
	private void sendMessageForRead(String description) throws IOException {
		out.writeUTF("MESSAGEFORREAD");
		out.writeUTF(description);
		in.readUTF();	//WHAT IS THAT JÓZSI? :D
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
