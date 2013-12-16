package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


//import oracle.jrockit.jfr.Options;
import client.view.View;

/**
 * A kliens hálózati kommunikációját, és a felhasználói felületét vezérlő osztály.
 * 
 * @author Ölveti József
 */
public class Controller implements IController{
	/**
	 * A játékos neve.
	 */
	private static String myName="";
	/**
	 * A játékos sorszáma.
	 */
	private static int myID=0;
	/**
	 * A grafikus felület objektuma.
	 */
	private View myView=new View();
	/**
	 * A játék állapotát reprezentáló StateOfPlayer objektum lista.
	 */
	private static List<StateOfPlayer> gameState=new ArrayList<StateOfPlayer>();
	/**
	 * A csatlakozási adatokat tároló String lista.
	 */
	private List<String> logInf=null;
	/**
	 * InputStream a szerver felöl érkező üzenetekhez.
	 */
	private DataInputStream in=null;
	/**
	 * OutputStream a szerver felé tartó üzenetekhez.
	 */
	private DataOutputStream out=null;
	/**
	 * Socket a kapcsolódáshoz.
	 */
	private Socket s=null;

	/**
	 * Vissza adja a játékos nevét.
	 * 
	 * @return a játékos neve
	 */
	public String getMyName() {
		return myName;
	}
	
	/**
	 * Vissza adja a grafikus felület objektumát.
	 * 
	 * @return a grafikus felület objektuma
	 */
	public View getMyView() {
		return myView;
	}
	
	/**
	 * Vissza adja a csatlakozási adatok listáját.
	 * 
	 * @return a csatlakozási adatok listája
	 */
	public List<String> getLogInf() {
		return logInf;
	}
	
	/**
	 * Vissza adja a klienshez rendelt DataInputStream objektumot.
	 * 
	 * @return a DataInputStream objektum
	 */
	public DataInputStream getIn() {
		return in;
	}
	
	/**
	 * Vissza adja a klienshez rendelt DataOutputStream objektumot.
	 * 
	 * @return a DataOutputStream objektum
	 */
	public DataOutputStream getOut() {
		return out;
	}
	
	/**
	 * Vissza adja a klienshez rendelt Socket objektumot.
	 * 
	 * @return a Socket objektum
	 */
	public Socket getS() {
		return s;
	}
	
	/**
	 * Vissza adja a játék állapotát.
	 * 
	 * @return a játék állapota
	 */
	public static List<StateOfPlayer> getGameState() {
		return gameState;
	}

	/**
	 * Vissza adja a játékos sorszámát.
	 * 
	 * @return a játékos sorszáma
	 */
	public static int getMyID() {
		return myID;
	}
	
	/**
	 * Az osztály konstruktora.
	 */
	public Controller(){
		myView.showView();
		login();
	}
	
	/**
	 * A kliens, szerverhez való kapcsolódását megvalósító metódus.
	 */
	public void login(){ 
		logInf=myView.getLoginInfos();
		myName=logInf.get(0);
		String host=logInf.get(1);
		int port=Integer.parseInt(logInf.get(2));
        try {
        	System.out.println("Kapcsolódás a szerverhez: " + host + " és port: " + port);
            s = new Socket(host, port);
            if(s.isConnected())
            {
            	openStreams();
            	System.out.println("Kapcsolódva a szerverhez: " + host + " és port: " + port);
                sendMessage(myName);
                String[] id=readStringFromStream().split("#");
                if (id[0].equals("SETID")){
                	myID=Integer.parseInt(id[1]);
                }
                System.out.println("your ID: "+myID);
                getInitialMessage();
            }
        } catch (IOException e) {
            System.out.println("Nem sikerült csatlakozni a szerverhez. " + e.getMessage());
        }       
	}
	

	/**
	 * Metódus az IO Streamek nyitásához.
	 * 
	 * @throws IOException
	 */
	public void openStreams() throws IOException {
	        in = new DataInputStream(s.getInputStream());
	        out = new DataOutputStream(s.getOutputStream());
	}
	
	/**
	 * Üzenet küldő metódus.
	 * Üzenet küldése a szervernek az OutputStreamen
	 * 
	 * @param msg a küldeni kívánt üzenet
	 * @throws IOException
	 */
	public void sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
    }
	
	/**
	 * Az IO Streamek és Socket kapcsolat zárása.
	 * 
	 * @throws IOException
	 */
	public void closeConnection() throws IOException{
			out.close();
	        in.close();
	        s.close();
	}
	
	/**
	 * Üzenet fogadó metódus.
	 * Üzenet fogadása a szervertől, az InputStreamen keresztül.
	 * 
	 * @return a fogadott üzenet
	 * @throws IOException
	 */
	public String readStringFromStream() throws IOException{
		return in.readUTF();
	}
	
	/**
	 * A klienshez tartozó játékos táblán elfoglalt mezőjének leírását szolgáltató metódus.
	 * 
	 * @return a mező leírása
	 */
	private String locDesc(){
		for (StateOfPlayer gs : gameState) {
			if(gs.getIdNumber()==myID)
			{
				return gs.getLocation().toString();
			}
		}
		return null;
	}
	
	/**
	 * Előüzenet feldolgozó metódus.
	 * A szervertől érkező úgynevezett inicializáló előüzenetek fogadása, ennek megfelelően további metódusok meghívása.
	 */
	public void getInitialMessage(){
		try {
            while (true) {
            	System.out.println("Üzenetre várunk a szervertől!");
                String message = readStringFromStream();
                System.out.println(message);
                switch (message){
                	case "GETGAMESTATE":getGameStateMessage();break;
                	case "BUYCARFORCASH":buyCar(true);break;
                	case "BUYCARFORCREDIT":buyCar(false);break;
                	case "BUYHOUSEFORCASH":buyHouse(true);break;
                	case "BUYHOUSEFORCREDIT":buyHouse(false);break;
                	case "BUYFURNITURE":buyFurnitures();break;
                	case "MAKEINSURANCE":makeInsurance();break;
                	case "MESSAGEFORREAD":getMessageForRead();break;
                	default:break;
                }
            }
        } catch (IOException e) {
        	try {
				closeConnection();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            System.out.println("Kapcsolat megszakítva. " + e.getMessage());
        }
	}
	
	/**
	 * Ház vásárlás lebonyolítását segítő metódus.
	 */
	public void buyHouse(boolean b){
		creditOrCashBuying(locDesc(),b);
	}
	
	/**
	 * Autó vásárlás lebonyolítását segítő metódus.
	 */
	public void buyCar(boolean b){
		creditOrCashBuying(locDesc(),b);
	}

	/**
	 * Metódus mely hitel vagy készpénz fizetésű vásárlási ajánlatot tesz a játékosnak, a GUI-n keresztül.
	 * 
	 * @param desc a mező leírása melyen a játékos áll
	 * @param b	logikai érték, mely azt jelöli hogy a játékos vehet-e készpénzre házat
	 */
	private void creditOrCashBuying(String desc,boolean b){
		int statement=myView.getBuyingInfos(desc,b);
		try {
			if (statement==1){
				sendMessage("BUYFORCREDIT");
			}
			else if(statement==2){
				sendMessage("BUYFORCASH");
			}
			else{
				sendMessage("DONTBUY");
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
	}
	
	/**
	 * Biztosítások kötését segítő metódus.
	 */
	public void makeInsurance(){

		try {
			String options=in.readUTF();
			if (options.equals("CAR") || options.equals("HOUSE")){
				int statement=myView.getInsurance(locDesc());
				if (statement==0){
					sendMessage("MAKEINSURANCE");
				}
				else if (statement == 1){
					sendMessage("DONTMAKEINSURANCE");
				}			
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
	}
	
	/**
	 * Berendezések vásárlását segítő metódus.
	 */
	public void buyFurnitures(){
		try {
			String furnitureType = readStringFromStream();
			int statement=myView.getFurnitureOptions(locDesc());	
			if (statement==0){
				sendMessage("BUY"+furnitureType);
			}
			else{
				sendMessage("DONTBUY"+furnitureType);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * A szervertől érkező úgynevezett csak olvasni való üzenetek fogadását lebonyolító metódus.
	 */
	public void getMessageForRead(){
		try {
			String message = readStringFromStream();
			myView.simpleMessage(message);
			System.out.println(message);
			out.writeUTF("SYNC");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Állapot objektum frissítését segítő metódus.
	 * 
	 * @param methods metódus tömb az objektum osztályából
	 * @param s String tömb melyben megtalálható a végrehajtandó metódusok neve, paramétere
	 * @param gs az állapot objektum melyre a metódusok alkalmazandóak
	 */
	private void applyState(Method[] methods, String s[], StateOfPlayer gs){
		for(int i=2;i<s.length;i=i+2){
			for(int j=0; j<methods.length; ++j) {
				if (methods[j].getName().toUpperCase().equals(s[i])){
					try {
						methods[j].invoke(gs, s[i+1]);
					} catch (IllegalAccessException
							| IllegalArgumentException
							| InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}	
		}
	}
	
	/**
	 * Állapot frissítő üzeneteket fogadó metódus.
	 */
	public void getGameStateMessage(){
		try {
			String message = readStringFromStream();
			String[] s=message.split("#");
			int playerId=Integer.parseInt(s[0]);
			Method[] methods = StateOfPlayer.class.getDeclaredMethods();
			boolean found=false;
			for (StateOfPlayer gs : gameState) {
				if(gs.getIdNumber()==playerId && found == false){
					found=true;
					applyState(methods, s, gs);
				}
			}	
			if (found==false){
				StateOfPlayer gs=new StateOfPlayer(playerId);
				applyState(methods, s, gs);
				gameState.add(gs);
			}
			myView.refreshView();
			for (StateOfPlayer gs : gameState) {
				if(gs.getIdNumber()==myID){
					System.out.println(gs.getLocation());
				}
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
