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
	 * Controller l�trej�n, majd megjelen�ti a Gui-alap ablak�t.
	 * V�g�l csatlakozni akar a szerverhez, azaz login() h�v�s.
	 * **/
	public Controller(){
		myView.showView();
		login();
	}

	
	/**
	 * Bek�rj�k a Gui-n kereszt�l a nevet, hostot, portot.
	 * Majd megpr�b�lunk ezekkel kapcs�l�dni, ha siker�l nyitjuk a streameket, �s elk�ldj�k a nev�nket.
	 * Ha nem hiba �zenet.
	 * **/
	private void login(){ 
		logInf=myView.getLoginInfos();
		myName=logInf.get(0);
		String host=logInf.get(1);
		int port=Integer.parseInt(logInf.get(2));
		
        try {
            System.out.println("Kapcsol�d�s a szerverhez: " + host + " �s port: " + port);
            s = new Socket(host, port);
            /* TIBI OKOSKOD�SA: ide nem while ciklus kellene? Ha �ppen m�g nem isConnected(), akkor nem is lesz j�t�k??? 
             * Ez csak egyszer ellen�riz, de ha nem igaz, m�r meg is hasalt a progi... */
            if(s.isConnected())
            {
            	open();
                System.out.println("Kapcsol�dva a szerverhez: " + host + " �s port: " + port);
                send(myName);
                getInitialMessage();
            }
        } catch (IOException e) {
        	/* TIBI OKOSKOD�SA: az IOException nem azt jelzi, hogy nem siker�lt csatlakozni, hanem hogy a socket
        	 * megnyit�sa k�zben IO hiba t�rt�nt, legal�bb is a dokument�ci� alapj�n, id�zem:
        	 * Throws: 
					UnknownHostException - if the IP address of the host could not be determined. 
					IOException - if an I/O error occurs when creating the socket. 
        	 */
            System.out.println("Nem siker�lt csatlakozni a szerverhez. " + e.getMessage());
        }       
	}
	
	
	
	/**
	 * IO Streamek megnyit�sa kapcsol�d�s ut�n.
	 * **/
	private void open() throws IOException {
	        in = new DataInputStream(s.getInputStream());
	        out = new DataOutputStream(s.getOutputStream());
	}
	
	/**
	 * �zenet k�ld�se a szervernek.
	 * **/
	private void send(String msg) throws IOException {
        out.writeUTF(msg);
    }
	
	private void getInitialMessage(){
		try {
            while (true) {
                /* A szervert�l kapott �zenetek olvas�sa. */
                String message = in.readUTF();
                System.out.println("�zenet a szervert�l: "+message);
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
            /* Olvas�si probl�m�k, kapcsolat megszak�t�sa. */
        	try {
				out.close();
	            in.close();
	            s.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            System.out.println("Kapcsolat megszak�tva. " + e.getMessage());
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
