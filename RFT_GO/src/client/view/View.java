package client.view;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
		Scanner sc = new Scanner(System.in);
	    System.out.println("Enter Your Name:");
		String name = sc.nextLine();
		System.out.println("Enter Server Host:");
		String host = sc.nextLine();
		System.out.println("Enter Server Port:");
		String port = sc.nextLine();
		
		/*példa visszatérési értékek, ezek ugye a bekértek lesznek*/
			List<String> l=new ArrayList<String>();
			l.add(name);
			l.add(host);
			l.add(port);
			return l;
		/*--*/
	}
	
	
	public int getBuyingInfos(String s){
		Scanner sc = new Scanner(System.in);
	    System.out.println(s);
	    String st=sc.nextLine();
	    if (st=="h")
	    	return 1;
	    else if(st=="k")
	    	return 2;
	    else
		return 0;
	}
	
	
	public int getInsurances(String s){
		Scanner sc = new Scanner(System.in);
	    System.out.println(s);
	    String st=sc.nextLine();
	    if (st=="a")
	    	return 1;
	    else if(st=="h")
	    	return 2;
	    else if(st=="m")
	    	return 3;
	    else
		return 0;
	}
	
	
	public int getFurnitureOptions(String s){
		Object[] options = {"Megvásárol",
        "Nem kérem!"};
		int n = JOptionPane.showOptionDialog(frame,
				s,
				"Berendezés vásárlás",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,    
				options,  
				options[0]); 
		return n;
	}

	
	public void simpleMessage(String s) {
		System.out.println(s);
	}
	

	
	public void refreshView() {
		
		List<StateOfPlayer> sp=Controller.getGameState();

	}
	
	
	
}
