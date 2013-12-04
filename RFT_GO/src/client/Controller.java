package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import client.view.View;

public class Controller extends Thread {
	public String myName;
	private Socket s;
	private View myView;
	private DataInputStream in;
	private DataOutputStream out;
	
	public Controller(){}

    
	public boolean login(){ 
		
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
		return false;
	}
	
	public boolean getMessageForRead(){
		return false;
	}
	
	public boolean getGameState(){
		return false;
	}

	public void giveUpAndExit(){
		
	}
}
