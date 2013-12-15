package client.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class PnlGazdOkStatus extends JPanel{

	private JTextField name;
	private JTextField balance;
	private JTextField houseInsurance;
	private JTextField carInsurance;
	private JTextField kitchen;
	private JTextField furniture;
	private JTextField frigo;
	private JTextField cooker;
	private JTextField dishwasher;
	private JTextField washingMachine;
	private JTextField car;
	private JTextField house;
	
	public static final Color TO_BE_COLOR = new Color(50, 255, 50);
	public static final Color NOT_TO_BE_COLOR = new Color(255, 50, 50);
	public static final Dimension FIELD_DIM = new Dimension(100, 30);
	
	/**
	 * nem kell
	 */
	public void setName(String name) {
		this.name.setText(name);
	}

	public void setBalance(int balance) {
		this.balance.setText("egyenleg: " + Integer.toString(balance) + " euró");
	}

	public void setHouseInsurance(boolean houseInsurance) {
		if(houseInsurance == true){
			this.houseInsurance.setBackground(TO_BE_COLOR);
		}
		else
			this.houseInsurance.setBackground(NOT_TO_BE_COLOR);
	}

	public void setCarInsurance(boolean carInsurance) {
		if(carInsurance == true){
			this.carInsurance.setBackground(TO_BE_COLOR);
		}
		else
			this.carInsurance.setBackground(NOT_TO_BE_COLOR);
	}

	public void setKitchen(boolean kitchen) {
		if(kitchen == true){
			this.kitchen.setBackground(TO_BE_COLOR);
		}
		else
			this.kitchen.setBackground(NOT_TO_BE_COLOR);
	}

	public void setFurniture(boolean furniture) {
		if(furniture == true){
			this.furniture.setBackground(TO_BE_COLOR);
		}
		else
			this.furniture.setBackground(NOT_TO_BE_COLOR);
	}

	public void setFrigo(boolean frigo) {
		if(frigo == true){
			this.frigo.setBackground(TO_BE_COLOR);
		}
		else
			this.frigo.setBackground(NOT_TO_BE_COLOR);
	}

	public void setCooker(boolean cooker) {
		if(cooker == true){
			this.cooker.setBackground(TO_BE_COLOR);
		}
		else
			this.cooker.setBackground(NOT_TO_BE_COLOR);
	}

	public void setDishwasher(boolean dishwasher) {
		if(dishwasher == true){
			this.dishwasher.setBackground(TO_BE_COLOR);
		}
		else
			this.dishwasher.setBackground(NOT_TO_BE_COLOR);
	}

	public void setWashingMachine(boolean washingMachine) {
		if(washingMachine == true){
			this.washingMachine.setBackground(TO_BE_COLOR);
		}
		else
			this.washingMachine.setBackground(NOT_TO_BE_COLOR);
	}

	public void setCar(boolean car) {
		if(car == true){
			this.car.setBackground(TO_BE_COLOR);
		}
		else
			this.car.setBackground(NOT_TO_BE_COLOR);
	}

	public void setHouse(boolean house) {
		if(house == true){
			this.house.setBackground(TO_BE_COLOR);
		}
		else
			this.house.setBackground(NOT_TO_BE_COLOR);
	}

	
	public PnlGazdOkStatus(String name, int balance) {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(1, 1, 1, 1);
//		this.setPreferredSize(new Dimension(300, 230));
		
		/**
		 *  mezők inicializálása
		 */
		instantiations(name, balance);
		setPreferredSizes();
		setEditables();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 8;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		add(this.name, gbc);
		
		gbc.gridy = 1;
//		gbc.fill = GridBagConstraints.VERTICAL;
		add(this.balance, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(house, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 4;
		add(houseInsurance, gbc);
		
		gbc.gridx = 4;
		gbc.gridy = 3;
		gbc.gridwidth = 4;
		add(carInsurance, gbc);
		
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		add(kitchen, gbc);

		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		add(furniture, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 1;
		add(frigo, gbc);

		gbc.gridx = 1;
		gbc.gridy = 5;
		add(cooker, gbc);

		gbc.gridx = 2;
		gbc.gridy = 5;
		add(dishwasher, gbc);

		gbc.gridx = 3;
		gbc.gridy = 5;
		add(washingMachine, gbc);

		gbc.gridx = 4;
		gbc.gridy = 4;
		gbc.gridheight = 2;
		gbc.gridwidth = 4;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		add(car, gbc);

		/**
		 * Alaphelyzetbe állítások
		 */
		setHouseInsurance(false);
		setCarInsurance(false);
		setHouse(false);
		setKitchen(false);
		setFurniture(false);
		setFrigo(false);
		setCooker(false);
		setDishwasher(false);
		setWashingMachine(false);
		setCar(false);
	}
	
	private void instantiations(String name, int balance){
		this.name = new JTextField(name);
		this.name.setBackground(TO_BE_COLOR);
		
		this.balance = new JTextField(balance + " euro");
		this.balance.setBackground(TO_BE_COLOR);
		
		this.house = new JTextField("house");
		this.houseInsurance = new JTextField("home insurance");
		this.carInsurance = new JTextField("car insurance");
		this.kitchen = new JTextField("kitchen");
		this.furniture = new JTextField("furniture");
		this.frigo = new JTextField("frigo");
		this.cooker = new JTextField("cooker");
		this.dishwasher = new JTextField("dishwasher");
		this.washingMachine = new JTextField("washingMachine");
		this.car = new JTextField("car");	
	}
	
	private void setPreferredSizes(){
		this.name.setPreferredSize(FIELD_DIM);
		this.balance.setPreferredSize(FIELD_DIM);
		this.house.setPreferredSize(FIELD_DIM);
		this.houseInsurance.setPreferredSize(FIELD_DIM);
		this.carInsurance.setPreferredSize(FIELD_DIM);
		this.kitchen.setPreferredSize(FIELD_DIM);		
		this.furniture.setPreferredSize(FIELD_DIM);
		this.frigo.setPreferredSize(FIELD_DIM);
		this.cooker.setPreferredSize(FIELD_DIM);
		this.dishwasher.setPreferredSize(FIELD_DIM);
		this.washingMachine.setPreferredSize(FIELD_DIM);
		this.car.setPreferredSize(FIELD_DIM);

	}
	
	private void setEditables(){
		this.name.setEditable(false);
		this.balance.setEditable(false);
		this.house.setEditable(false);
		this.houseInsurance.setEditable(false);
		this.carInsurance.setEditable(false);
		this.kitchen.setEditable(false);		
		this.furniture.setEditable(false);
		this.frigo.setEditable(false);
		this.cooker.setEditable(false);
		this.dishwasher.setEditable(false);
		this.washingMachine.setEditable(false);
		this.car.setEditable(false);
	}

}
