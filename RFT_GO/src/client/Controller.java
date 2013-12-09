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

public class Controller{
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

	/**
	 * Csatlakoz�s a szerverhez, a GUI-n kereszt�l bek�rt adatokkal.
	 * N�v, Host, Port. A Host-hoz a Porton, a n�vvel.
	 * Sikeres Csatlakoz�s ut�n a n�v k�ld�se a szerver fel�.
	 */
	public void login(){ 
		logInf=myView.getLoginInfos();
		myName=logInf.get(0);
		String host=logInf.get(1);
		int port=Integer.parseInt(logInf.get(2));
		
        try {
            System.out.println("Kapcsol�d�s a szerverhez: " + host + " �s port: " + port);
            s = new Socket(host, port);
            if(s.isConnected())
            {
            	openStreams();
                System.out.println("Kapcsol�dva a szerverhez: " + host + " �s port: " + port);
                sendMessage(myName);
                getInitialMessage();
            }
        } catch (IOException e) {
            System.out.println("Nem siker�lt csatlakozni a szerverhez. " + e.getMessage());
        }       
	}
	
	/**
	 * Input �s Output Streamek nyit�sa a kapcsol�d�s ut�n.
	 * 
	 * @throws IOException
	 */
	public void openStreams() throws IOException {
	        in = new DataInputStream(s.getInputStream());
	        out = new DataOutputStream(s.getOutputStream());
	}
	
	
	/**
	 * �zenet k�ld�se az OutputStreamen kereszt�l a szerver fel�.
	 * 
	 * @param msg a k�ldeni k�v�nt �zenet.
	 * @throws IOException
	 */
	public void sendMessage(String msg) throws IOException {
        out.writeUTF(msg);
    }
	
	/**
	 * Streamek �s Socket z�r�sa.
	 * 
	 * @throws IOException
	 */
	public void closeConnection() throws IOException{
			out.close();
	        in.close();
	        s.close();
	}
	
	/**
	 * �zenet fogad�sa az inputStreamen a szervert�l.
	 * 
	 * @return Az �zenet
	 * @throws IOException
	 */
	public String readStringFromStream() throws IOException{
		return in.readUTF();
	}
	
	
	/**
	 * Jelz� �zenetek fogad�sa a szervert�l.
	 * Majd att�l f�gg�en hogy mit kapunk, tov�bbi met�dusok h�v�sa.
	 */
	public void getInitialMessage(){
		try {
            while (true) {
                String message = readStringFromStream();
                System.out.println("�zenet a szervert�l: "+message);
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
            System.out.println("Kapcsolat megszak�tva. " + e.getMessage());
        }
	}
	
	
	/**
	 * H�zv�s�rl�s felvet�se a j�t�kosnak.
	 */
	public void buyHouse(){
		creditOrCashBuying("House");
	}
	
	
	/**
	 * Aut� v�s�rl�s felvet�se a j�t�kosnak.
	 */
	public void buyCar(){
		creditOrCashBuying("Car");
	}
	
	
	/**
	 * Felaj�nl valamit a j�t�kosnak, amit credit�rt vagy p�nz�rt vehet meg, v�lasz a szervernek.
	 * 
	 * @param item
	 */
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
	
	
	/**
	 * Biztos�t�s k�t�s felvet�se a j�t�kosnak, majd v�lasz a szervernek.
	 */
	public void makeInsurances(){
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
	 * B�torv�s�rl�s aj�nl�sa, �s v�lasz a szervernek.
	 */
	public void buyFurnitures(){
		try {
			int statement=0;
			String furnitureType = readStringFromStream();
			switch (furnitureType){
				case "COOKER":statement=myView.getFurnitureOptions("T�zhely");break;
				case "DISHWASHER":statement=myView.getFurnitureOptions("Mosogat�g�p");break;
				case "KITCHENFURNITURE":statement=myView.getFurnitureOptions("Konyhab�tor");break;
				case "ROOMFURNITURE":statement=myView.getFurnitureOptions("Szobab�tor");break;
				case "WASHMACHINE":statement=myView.getFurnitureOptions("Mos�g�p");break;
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
	 * �zenet a szervert�l melyet k�zl�nk a j�t�kossal.
	 */
	public void getMessageForRead(){
		try {
			String message = readStringFromStream();
			myView.simpleMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * �llapot friss�t�s fogad�sa, �s TODO Gui friss�t�s�nek megh�v�sa
	 */
	public void getGameStateMessage(){
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
				// TODO �j j�t�kos az adatokkal, vagy majd m�shogy vigy�nk be �jakat.
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Felad �s kil�p? :D
	 */
	public void giveUpAndExit(){
		// TODO Auto-generated catch block
	}
		
	
	public static void main(String[] args) {
		new Controller();
	}
}
