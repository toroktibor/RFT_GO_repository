package client.view;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import client.Controller;
import client.StateOfPlayer;

public class View implements IView{

	private JFrame frame;

	public View() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	
		public void showView() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					View window = new View();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public List<String> getLoginInfos() {
		
		/*példa visszatérési értékek, ezek ugye a bekértek lesznek*/
			List<String> l=new ArrayList<String>();
			l.add("Jani");
			l.add("localhost");
			l.add("7777");
			return l;
		/*--*/
	}
	
	
	public int getBuyingInfos(String p){
		
			return 0;
	}
	
	
	public int getInsurances(){
		
		return 0;
	}
	
	
	public int getFurnitureOptions(String furniture){
		return 0;
	}

	
	public void simpleMessage(String message) {
		
	}
	

	
	public void refreshView() {
		
		List<StateOfPlayer> sp=Controller.getGameState();

	}
	
	
	
}
