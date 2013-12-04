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

	public Controller(){
		myView.showView();
		login();
	}

	public boolean login(){ 
		List<String> logInf =myView.getLoginInfos();
		String host=logInf.get(1);
		int port=Integer.parseInt(logInf.get(2));
		myName=logInf.get(0);
		
		/* Probálkozunk kapcsolatot létesíteni a szerverrel */
        try {
            System.out.println("Kapcsolódás a szerverhez: " + host + " és port: " + port);
            s = new Socket(host, port);
            if(s.isConnected())
            {
                /* Ha sikerült kapcsolódni, akkor megnyítjuk a stream-eket és egy üdvözlõ üzenetet küldünk a szervernek. */
            	in = new DataInputStream(s.getInputStream());
                out = new DataOutputStream(s.getOutputStream());
                System.out.println("Kapcsolódva a szerverhez: " + host + " és port: " + port);
                out.writeUTF(myName);
                return true;
            }
        } catch (IOException e) {
            /* Sikertelen kapcsolódás esetén hiabüzenet.. */
            System.out.println("Nem sikerült csatlakozni a szerverhez. " + e.getMessage());
        }
		return false;
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

	public boolean getInitialMessage(){
		while(true){
			try {
				String message=in.readUTF();
				System.out.println(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
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
