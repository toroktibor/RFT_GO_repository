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
	private static String myName="";
	private static int myID=0;
	private View myView=new View();
	private static List<StateOfPlayer> gameState=new ArrayList<StateOfPlayer>();
	private List<String> logInf=null;
	private DataInputStream in=null;
	private DataOutputStream out=null;
	private Socket s=null;

	public String getMyName() {
		return myName;
	}


	public View getMyView() {
		return myView;
	}


	public List<String> getLogInf() {
		return logInf;
	}


	public DataInputStream getIn() {
		return in;
	}


	public DataOutputStream getOut() {
		return out;
	}


	public Socket getS() {
		return s;
	}


	public static List<StateOfPlayer> getGameState() {
		return gameState;
	}


	public static int getMyID() {
		return myID;
	}
	

	public Controller(){
		myView.showView();
		login();
	}
	
	

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
	

	
	public void openStreams() throws IOException {
	        in = new DataInputStream(s.getInputStream());
	        out = new DataOutputStream(s.getOutputStream());
	}
	
	
	public void sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
    }
	

	public void closeConnection() throws IOException{
			out.close();
	        in.close();
	        s.close();
	}
	

	public String readStringFromStream() throws IOException{
		return in.readUTF();
	}
	
	private String locDesc(){
		for (StateOfPlayer gs : gameState) {
			if(gs.getIdNumber()==myID)
			{
				return gs.getLocation().toString();
			}
		}
		return null;
	}
	
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
	
	
	public void buyHouse(boolean b){
		creditOrCashBuying(locDesc(),b);
	}
	
	
	public void buyCar(boolean b){
		creditOrCashBuying(locDesc(),b);
	}

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
	
	
	public void buyFurnitures(){
		try {
			String furnitureType = readStringFromStream();
			int statement=myView.getFurnitureOptions(locDesc());

			/*switch (furnitureType){
				case "COOKER":statement=myView.getFurnitureOptions(locDesc());break;
				case "DISHWASHER":statement=myView.getFurnitureOptions(locDesc());break;
				case "KITCHENFURNITURE":statement=myView.getFurnitureOptions(locDesc());break;
				case "ROOMFURNITURE":statement=myView.getFurnitureOptions(locDesc());break;
				case "WASHMACHINE":statement=myView.getFurnitureOptions(locDesc());break;
				default:break;
			}*/
			
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

	public void getMessageForRead(){
		try {
			String message = readStringFromStream();
			
			out.writeUTF(myView.simpleMessage(message)+"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
}
