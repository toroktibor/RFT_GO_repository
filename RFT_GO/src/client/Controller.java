package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import client.view.View;

public class Controller{
	private View myView=new View();
	private DataInputStream in=null;
	private DataOutputStream out=null;
	private Socket s=null;
	private String myName="";
	private List<String> logInf=null;
	private List<StateOfPlayer> gameState=new ArrayList<StateOfPlayer>();
	
	/**
	 * Controller létrejön, majd megjeleníti a Gui-alap ablakát.
	 * Végül csatlakozni akar a szerverhez, azaz login() hívás.
	 * **/
	public Controller(){
		myView.showView();
		login();
	}

	
	/**
	 * Bekérjük a Gui-n keresztül a nevet, hostot, portot.
	 * Majd megpróbálunk ezekkel kapcsólódni, ha sikerül nyitjuk a streameket, és elküldjük a nevünket.
	 * Ha nem hiba üzenet.
	 * **/
	private void login(){ 
		logInf=myView.getLoginInfos();
		myName=logInf.get(0);
		String host=logInf.get(1);
		int port=Integer.parseInt(logInf.get(2));
		
        try {
            System.out.println("Kapcsolódás a szerverhez: " + host + " és port: " + port);
            s = new Socket(host, port);
            /* TIBI OKOSKODÁSA: ide nem while ciklus kellene? Ha éppen még nem isConnected(), akkor nem is lesz játék??? 
             * Ez csak egyszer ellenõriz, de ha nem igaz, már meg is hasalt a progi... */
            if(s.isConnected())
            {
            	open();
                System.out.println("Kapcsolódva a szerverhez: " + host + " és port: " + port);
                send(myName);
                getInitialMessage();
            }
        } catch (IOException e) {
        	/* TIBI OKOSKODÁSA: az IOException nem azt jelzi, hogy nem sikerült csatlakozni, hanem hogy a socket
        	 * megnyitása közben IO hiba történt, legalább is a dokumentáció alapján, idézem:
        	 * Throws: 
					UnknownHostException - if the IP address of the host could not be determined. 
					IOException - if an I/O error occurs when creating the socket. 
        	 */
            System.out.println("Nem sikerült csatlakozni a szerverhez. " + e.getMessage());
        }       
	}
	
	
	
	/**
	 * IO Streamek megnyitása kapcsolódás után.
	 * **/
	private void open() throws IOException {
	        in = new DataInputStream(s.getInputStream());
	        out = new DataOutputStream(s.getOutputStream());
	}
	
	/**
	 * Üzenet küldése a szervernek.
	 * **/
	private void send(String msg) throws IOException {
        out.writeUTF(msg);
    }
	
	private void getInitialMessage(){
		try {
            while (true) {
                /* A szervertõl kapott üzenetek olvasása. */
                String message = in.readUTF();
                System.out.println("Üzenet a szervertõl: "+message);
                switch (message){
                	case "GETGAMESTATE":getGameState();
                	case "BUYHOUSE":buyHouse();;
                	case "BUYCAR":buyCar();;
                	case "MAKEINSURANCES":makeInsurances();
                	case "MESSAGEFORREAD":getMessageForRead();
                	default:break;
                }
            }
        } catch (IOException e) {
            /* Olvasási problémák, kapcsolat megszakítása. */
        	try {
				out.close();
	            in.close();
	            s.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            System.out.println("Kapcsolat megszakítva. " + e.getMessage());
        }
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void buyHouse(){

	}
	
	private void buyCar(){
		
	}
	
	private void buyFurniture(String furniture){
		
	}
	
	private boolean makeInsurances(){
		return false;
	}
	
	private boolean getMessageForRead(){
		return false;
	}
	
	private boolean getGameState(){
		return false;
	}

	private void giveUpAndExit(){
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		new Controller();
	}
}
