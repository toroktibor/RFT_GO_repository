package my;

public class Player {
	private String name;
	private int balance;
	private House house;
	private Car car;
	private Field location;
	private Boolean isActive;
	private int giftDices;
	private int _1_6Penalty;
	private int exclusions;
	
	// CONSTRUCTOR
	public Player(String name, Field location) {
		super();
		this.name = name;
		this.balance = 18000;
		this.car = null;
		this.exclusions = 0;
		this.giftDices = 0;
		this.house = null;
		this.isActive = true;
		this.location =  location;
	}
	
	// GETTERS AND SETTERS
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public House getHouse() {
		return house;
	}
	public void setHouse(House house) {
		this.house = house;
	}
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	public Field getLocation() {
		return location;
	}
	public int getLocationNumber() {
		return getLocation().getNumber();
	}
	public void setLocation(Field location) {
		this.location = location;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public int getGiftDices() {
		return giftDices;
	}
	public void setGiftDices(int giftDices) {
		this.giftDices = giftDices;
	}
	public int get_1_6Penalty() {
		return _1_6Penalty;
	}
	public void set_1_6Penalty(int _1_6Penalty) {
		this._1_6Penalty = _1_6Penalty;
	}
	public int getExclusions() {
		return exclusions;
	}
	public void setExclusions(int exclusions) {
		this.exclusions = exclusions;
	}
	
	//Player specific methods
	public Boolean isWinner() {
		Boolean result = false;
		if (( house != null) && ( house.getDebit() == 0 ) &&
			( car != null ) && 	( car.getDebit() == 0 ) &&
			( house.getHasCooker() ) && ( house.getHasDishwasher() ) &&
			( house.getHasFrigo() ) &&	( house.getHasKitchen() ) &&
			( house.getHasRoomFurniture() ) &&	( house.getHasWashMachine() ) &&
			( house.getIsInsured() ) &&	( car.getIsInsured() ) &&
			( 2000 <= getBalance() )) {
			result = true;
		}
		return result;
	}
}
