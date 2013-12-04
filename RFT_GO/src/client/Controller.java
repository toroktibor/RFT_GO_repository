package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import client.view.View;

public class Controller{
	private View myView=new View();
	private DataInputStream in=null;
	private DataOutputStream out=null;
	private Socket s=null;
	private String myName="";

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
	public void login(){ 
		List<String> logInf=myView.getLoginInfos();
		myName=logInf.get(0);
		String host=logInf.get(1);
		int port=Integer.parseInt(logInf.get(2));
		
        try {
            System.out.println("Kapcsol�d�s a szerverhez: " + host + " �s port: " + port);
            s = new Socket(host, port);
            if(s.isConnected())
            {
            	open();
                System.out.println("Kapcsol�dva a szerverhez: " + host + " �s port: " + port);
                send(myName);
                //getInitialMessage();
            }
        } catch (IOException e) {
            System.out.println("Nem siker�lt csatlakozni a szerverhez. " + e.getMessage());
        }       
	}
	
	
	
	/**
	 * IO Streamek megnyit�sa kapcsol�d�s ut�n.
	 * **/
	public void open() throws IOException {
	        in = new DataInputStream(s.getInputStream());
	        out = new DataOutputStream(s.getOutputStream());
	}
	
	/**
	 * �zenet k�ld�se a szervernek.
	 * **/
	public void send(String msg) throws IOException {
        out.writeUTF(msg);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void buyHouse(){
		
	}
	
	public void buyCar(){
		
	}
	
	public void buyFurniture(String furniture){
		
	}
	
	public boolean makeInsurances(){
		return false;
	}

	
	public void getInitialMessage(){
		try {
            while (true) {
                /* A szervert�l kapott �zenetek olvas�sa. */
                String message = in.readUTF();
                System.out.println("�zenet a szervert�l: "+message);
            }
        } catch (IOException e) {
            /* Olvas�si probl�m�k, kapcsolat megszak�t�sa. */
        	try {
				out.close();
	            in.close();
	            s.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            System.out.println("Kapcsolat megszak�tva. " + e.getMessage());
        }
		
	}
	
	public boolean getMessageForRead(){
		return false;
	}
	
	public boolean getGameState(){
		return false;
	}

	public void giveUpAndExit(){
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		new Controller();
	}
}
