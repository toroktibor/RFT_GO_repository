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
		
		/* Prob�lkozunk kapcsolatot l�tes�teni a szerverrel */
        try {
            System.out.println("Kapcsol�d�s a szerverhez: " + host + " �s port: " + port);
            s = new Socket(host, port);
            if(s.isConnected())
            {
                /* Ha siker�lt kapcsol�dni, akkor megny�tjuk a stream-eket �s egy �dv�zl� �zenetet k�ld�nk a szervernek. */
            	in = new DataInputStream(s.getInputStream());
                out = new DataOutputStream(s.getOutputStream());
                System.out.println("Kapcsol�dva a szerverhez: " + host + " �s port: " + port);
                out.writeUTF(myName);
                getInitialMessage();
                return true;
            }
        } catch (IOException e) {
            /* Sikertelen kapcsol�d�s eset�n hiab�zenet.. */
            System.out.println("Nem siker�lt csatlakozni a szerverhez. " + e.getMessage());
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
