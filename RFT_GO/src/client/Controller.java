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
		List<String> logInf=myView.getLoginInfos();
		myName=logInf.get(0);
		String host=logInf.get(1);
		int port=Integer.parseInt(logInf.get(2));
		
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
                getInitialMessage();
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

	
	public void getInitialMessage(){
		try {
            while (true) {
                /* A szervertõl kapott üzenetek olvasása. */
                String message = in.readUTF();
                System.out.println("Üzenet a szervertõl: "+message);
            }
        } catch (IOException e) {
            /* Olvasási problémák, kapcsolat megszakítása. */
        	try {
				out.close();
	            in.close();
	            s.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            System.out.println("Kapcsolat megszakítva. " + e.getMessage());
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
