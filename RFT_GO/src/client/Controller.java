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
	public List<StateOfPlayer> gameState=new ArrayList<StateOfPlayer>();
	
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
                /* Ejnye-bejnye J�zsik�m! :D H�t a break;-ek hol maradnak a switch case-ek v�g�r�l? :P */
                switch (message){
                	case "GETGAMESTATE":getGameState();break;
                	case "BUYHOUSE":buyHouse();break;
                	case "BUYCAR":buyCar();break;
                	case "BUYFURNITURE":buyFurnitures();break;
                	case "MAKEINSURANCES":makeInsurances();break;
                	case "MESSAGEFORREAD":getMessageForRead();break;
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
		int statement=myView.getBuyingInfos("House");
			
		try {
			if (statement==1){
				out.writeUTF("BUYFORCREDIT");
			}
			else if(statement==2){
				out.writeUTF("BUYFORCASH");
			}
			else{
				out.writeUTF("DONTBUY");
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
		
	}
	
	private void buyCar(){
		int statement=myView.getBuyingInfos("Car");
				
		try {
			if (statement==1){
				out.writeUTF("BUYFORCREDIT");
			}
			else if(statement==2){
				out.writeUTF("BUYFORCASH");
			}
			else{
				out.writeUTF("DONTBUY");
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
			
	}
	
	private void makeInsurances(){
		int statement=myView.getInsurances();
		
		try {
			if (statement==1){
				out.writeUTF("MAKEONLYCARINSURANCE");
			}
			else if(statement==2){
				out.writeUTF("MAKEONLYHOUSEINSURANCE");
			}
			else if(statement==3){
				out.writeUTF("MAKEBOTHINSURANCES");
			}
			else{
				out.writeUTF("DONTMAKEANYINSURANCES");
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
	}
	
	private void buyFurnitures(){
		try {
			int statement=0;
			String furnitureType=in.readUTF();
			switch (furnitureType){
				case "COOKER":statement=myView.getFurnitureOptions("T�zhely");break;
				case "DISHWASHER":statement=myView.getFurnitureOptions("Mosogat�g�p");break;
				case "KITCHENFURNITURE":statement=myView.getFurnitureOptions("Konyhab�tor");break;
				case "ROOMFURNITURE":statement=myView.getFurnitureOptions("Szobab�tor");break;
				case "WASHMACHINE":statement=myView.getFurnitureOptions("Mos�g�p");break;
				default:break;
			}
			
			if (statement==1){
				out.writeUTF("BUY"+furnitureType);
			}
			else{
				out.writeUTF("DONTBUY"+furnitureType);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void getMessageForRead(){
		try {
			String message=in.readUTF();
			myView.simpleMessage(message);
			out.writeUTF("OK");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void getGameState(){
		
	}

	private void giveUpAndExit(){
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		new Controller();
	}
}
