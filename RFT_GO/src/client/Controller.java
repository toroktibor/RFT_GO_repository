package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import client.view.IView;
import client.view.View;

public class Controller{
	private String myName="";
	private View myView=new View();
	private List<StateOfPlayer> gameState=new ArrayList<StateOfPlayer>();
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


	public List<StateOfPlayer> getGameState() {
		return gameState;
	}


	public Controller(){
		myView.showView();
		login();
	}

	/**
	 * Csatlakozás a szerverhez, a GUI-n keresztül bekért adatokkal.
	 * Név, Host, Port. A Host-hoz a Porton, a névvel.
	 * Sikeres Csatlakozás után a név küldése a szerver felé.
	 */
	private void login(){ 
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
                getInitialMessage();
            }
        } catch (IOException e) {
            System.out.println("Nem sikerült csatlakozni a szerverhez. " + e.getMessage());
        }       
	}
	
	/**
	 * Input és Output Streamek nyitása a kapcsolódás után.
	 * 
	 * @throws IOException
	 */
	private void openStreams() throws IOException {
	        in = new DataInputStream(s.getInputStream());
	        out = new DataOutputStream(s.getOutputStream());
	}
	
	
	/**
	 * Üzenet küldése az OutputStreamen keresztül a szerver felé.
	 * 
	 * @param msg a küldeni kívánt üzenet.
	 * @throws IOException
	 */
	private void sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
    }
	
	/**
	 * Streamek és Socket zárása.
	 * 
	 * @throws IOException
	 */
	private void closeConnection() throws IOException{
			out.close();
	        in.close();
	        s.close();
	}
	
	/**
	 * Üzenet fogadása az inputStreamen a szervertõl.
	 * 
	 * @return Az üzenet
	 * @throws IOException
	 */
	private String readStringFromStream() throws IOException{
		return in.readUTF();
	}
	
	
	/**
	 * Jelzõ Üzenetek fogadása a szervertõl.
	 * Majd attól függõen hogy mit kapunk, további metódusok hívása.
	 */
	private void getInitialMessage(){
		try {
            while (true) {
                String message = readStringFromStream();
                System.out.println("Üzenet a szervertõl: "+message);
                switch (message){
                	case "GETGAMESTATE":getGameStateMessage();break;
                	case "BUYHOUSE":buyHouse();break;
                	case "BUYCAR":buyCar();break;
                	case "BUYFURNITURE":buyFurnitures();break;
                	case "MAKEINSURANCES":makeInsurances();break;
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
	 * Házvásárlás felvetése a játékosnak, majd válasz küldése a szervernek.
	 */
	private void buyHouse(){
		int statement=myView.getBuyingInfos("House");
			
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
			String result = readStringFromStream();
			myView.simpleMessage(result);
			sendMessage("OK");
		} catch (IOException e) {
				e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Autó vásárlás felvetése a játékosnak, majd válasz a szervernek.
	 */
	private void buyCar(){
		int statement=myView.getBuyingInfos("Car");
				
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
			String result = readStringFromStream();
			myView.simpleMessage(result);
			sendMessage("OK");
		} catch (IOException e) {
				e.printStackTrace();
		}
			
	}
	
	
	/**
	 * Biztosítás kötés felvetése a játékosnak, majd válasz a szervernek.
	 */
	private void makeInsurances(){
		int statement=myView.getInsurances();
		
		try {
			if (statement==1){
				sendMessage("MAKEONLYCARINSURANCE");
			}
			else if(statement==2){
				sendMessage("MAKEONLYHOUSEINSURANCE");
			}
			else if(statement==3){
				sendMessage("MAKEBOTHINSURANCES");
			}
			else{
				sendMessage("DONTMAKEANYINSURANCES");
			}
			String result = readStringFromStream();
			myView.simpleMessage(result);
			sendMessage("OK");
		} catch (IOException e) {
				e.printStackTrace();
		}
	}
	
	
	/**
	 * Bútorvásárlás ajánlása, és válasz a szervernek.
	 */
	private void buyFurnitures(){
		try {
			int statement=0;
			String furnitureType = readStringFromStream();
			switch (furnitureType){
				case "COOKER":statement=myView.getFurnitureOptions("Tûzhely");break;
				case "DISHWASHER":statement=myView.getFurnitureOptions("Mosogatógép");break;
				case "KITCHENFURNITURE":statement=myView.getFurnitureOptions("Konyhabútor");break;
				case "ROOMFURNITURE":statement=myView.getFurnitureOptions("Szobabútor");break;
				case "WASHMACHINE":statement=myView.getFurnitureOptions("Mosógép");break;
				default:break;
			}
			
			if (statement==1){
				sendMessage("BUY"+furnitureType);
			}
			else{
				sendMessage("DONTBUY"+furnitureType);
			}
			String result = readStringFromStream();
			myView.simpleMessage(result);
			sendMessage("OK");			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Üzenet a szervertõl melyet közlünk a játékossal.
	 */
	private void getMessageForRead(){
		try {
			String message = readStringFromStream();
			myView.simpleMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Állapot frissítés fogadása, és TODO Gui frissítésének meghívása
	 */
	private void getGameStateMessage(){
		try {
			String message = readStringFromStream();
			String[] s=message.split("#");
			int playerid=Integer.parseInt(s[0]);
			Method[] methods = StateOfPlayer.class.getDeclaredMethods();
			boolean found=false;
			for (StateOfPlayer gs : gameState) {
				if(gs.getIdNumber()==playerid){
					found=true;
					for(int i=2;i<s.length;i=i+2){
						for(int j=0; j<methods.length; ++j) {
							if (methods[j].getName().equals(s[i])){
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
			}	
			if (found==false){
				// TODO új játékos az adatokkal, vagy majd máshogy vigyünk be újakat.
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Felad és kilép? :D
	 */
	private void giveUpAndExit(){
		// TODO Auto-generated catch block
	}
		
	
	public static void main(String[] args) {
		new Controller();
	}
}
