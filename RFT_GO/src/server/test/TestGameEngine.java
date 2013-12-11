package server.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import server.Field;
import server.LuckyCard;
import server.XMLParser;

public class TestGameEngine {
	
	private TestPlayer actualPlayer;
	private List<TestPlayer> allPlayers = new ArrayList<TestPlayer>();
	private List<Field> board = new ArrayList<Field>();
	private List<LuckyCard> deck = new ArrayList<LuckyCard>();
	private int luckyCardIndex = 0;
	private XMLParser p=new XMLParser();
	private ServerSocket ss=null;	
	
	//GETTERS AND SETTERS
	public TestPlayer getActualPlayer() {
		return actualPlayer;
	}
	public void setActualPlayer(TestPlayer actualPlayer) {
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
	public List<TestPlayer> getAllPlayers() {
		return allPlayers;
	}
	public void setAllPlayers(List<TestPlayer> allPlayers) {
		this.allPlayers = allPlayers;
	}
	
	
	//IMPLEMENTATION OF THE METHODS OF ICASHIER INTERFACE
	
	public void addMoney(int amount) {
	}
	
	
	public void addPercentage(int percentage) {
	}	

	
	public Boolean checkBalance(int amount) {
		return null;
	}

	
	public Boolean deductMoney(int amount) {
		return null;
	}

	
	public Boolean handleDebits() {
		return true;
	}
	
	
	//IMPLEMENTATION OF THE METHODS OF IGAMEPLAY INTERFACE
	private int giveIndexOfSearchedMethod(Method[] allMethodsNameList, String goalMethodsName ) {
		return luckyCardIndex;
	}
	
	public void initFields() {
		board=p.parseFields("Fields.xml");
	}
	
	
	public void initLuckyCards() {
	}

	public void dice() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	}
	
	public void executeFieldCommand() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	}
	
	
	public void executeLuckyCardCommand() {
	}

	
	public void waitForPlayers(int maxNumberOfPlayers) throws IOException { //EZ EGéSZEN MáS LESZ....
		/* azt mondjuk várakozunk */
		boolean wait=true;
		Calendar time = Calendar.getInstance() ;
		Calendar time2 = Calendar.getInstance() ;
		int wtime=120000;
		while((allPlayers.size() < maxNumberOfPlayers) && wait ) {
			try {
				/* várakozunk a kliensig ha megjön új játékos a listára felveszük stb
				 * mind ezt 2x az első 2 kliensig
				 * */
				System.out.println("Kliensre várakozunk!");
				Socket oneClient=ss.accept();
				DataInputStream in=new DataInputStream(oneClient.getInputStream());
				DataOutputStream out=new DataOutputStream(oneClient.getOutputStream());
				String pname=in.readUTF();
				System.out.println(pname+" csatlakozott!");
				TestPlayer np=new TestPlayer(pname, oneClient, board.get(0),in,out);
				allPlayers.add(np);
				out.writeUTF("SETID#"+String.valueOf(allPlayers.indexOf(np)));
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
					ss.setSoTimeout(wtime);
				}
				if (2<allPlayers.size()){
					time = Calendar.getInstance() ;
					wtime=wtime-(int) (time.getTimeInMillis()-time2.getTimeInMillis());
					System.out.println("Már van "+allPlayers.size()+" játékos, hátralévő idő "+wtime/1000+" másodperc!");
					ss.setSoTimeout(wtime);
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
	
	
	public void sendGameState() {
	}

	
	public void startGame() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		ss=new ServerSocket(6000);
		initFields();
		waitForPlayers(6);
		for (TestPlayer player : allPlayers) {
			player.out.writeUTF("MESSAGEFORREAD");
			player.out.writeUTF("És működik! :D ");
		}
		while (true){
			
		}
	}

	
	
	//CALLABLE METHODS OF FIELD AND LUCKYCARD COMMANDS
	private LuckyCard drawNextLuckyCard() {
		return null;
	}
	
	
	private void loseCar() {
	}
	
	
	private void loseFurnitures() {
	}
	
	
	private void moveWithQuantity(int amount) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	}
	
	
	private void moveToField(int goalFieldsNumber) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	}
	
	
	private void offerBuyCar() {
	}
	
	
	private void offerBuyFurniture(String string) {
	}
	
	
	private void offerBuyHouse() {
	}
	
	
	private void offerMakeInsurances() {
	}
	
	
	private void sendMessageForRead(String description) {
	}	
	
	
	private void set_1_6Penalty(int amount) {
	}
	
	
	private void setExclusions(int amount) {	
	}
	
	
	private void setGiftDices(int amount) {
	}
	
	
	private void wonFurniture(String furnitureName) {
	}
}
