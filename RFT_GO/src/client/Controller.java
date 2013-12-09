package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import client.view.View;

/**
 * A kliens hálózati kommunikációját, és a felhasználói felületét vezérlő osztály.
 * 
 * @author Ölveti József
 */
public class Controller implements IController{
	private String myName="";
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
	
	
	public void getInitialMessage(){
		try {
            while (true) {
                String message = readStringFromStream();
                System.out.println("Üzenet a szervertől: "+message);
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
	
	
	public void buyHouse(){
		creditOrCashBuying("House");
	}
	
	
	public void buyCar(){
		creditOrCashBuying("Car");
	}
	
	
	private void creditOrCashBuying(String item){
		int statement=myView.getBuyingInfos(item);
		
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
	
	
	public void makeInsurances(){
		int statement=myView.getInsurances("xyz");
		
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
	
	
	public void buyFurnitures(){
		try {
			int statement=0;
			String furnitureType = readStringFromStream();
			switch (furnitureType){
				case "COOKER":statement=myView.getFurnitureOptions("Tűzhely");break;
				case "DISHWASHER":statement=myView.getFurnitureOptions("Mosogatógép");break;
				case "KITCHENFURNITURE":statement=myView.getFurnitureOptions("Konyhabútor");break;
				case "ROOMFURNITURE":statement=myView.getFurnitureOptions("Szobabútor");break;
				case "WASHMACHINE":statement=myView.getFurnitureOptions("Mosogép");break;
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

	
	public void getMessageForRead(){
		try {
			String message = readStringFromStream();
			myView.simpleMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
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
				if(gs.getIdNumber()==playerId){
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
				gameState.add(new StateOfPlayer(playerId));
				for (StateOfPlayer gs : gameState) {
					if(gs.getIdNumber()==playerId){
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
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}		
}
