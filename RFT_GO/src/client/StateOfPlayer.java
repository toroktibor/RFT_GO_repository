package client;

public class StateOfPlayer {
	private int idNumber=0;
	private String name="";
	private String house="";
	private String car="";
	private String location="";
	private int balance=0;
	private int _1_6Penalty=0;
	private int giftDices=0;
	private int exclusions=0;
	
	public int getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = Integer.parseInt(idNumber);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHouseStateBinaryFlags() {
		return house;
	}
	public void setHouseStateBinaryFlags(String houseStateBinaryFlags) {
		this.house = houseStateBinaryFlags;
	}
	public String getCarStateBinaryFlags() {
		return car;
	}
	public void setCarStateBinaryFlags(String carStateBinaryFlags) {
		this.car = carStateBinaryFlags;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = Integer.parseInt(balance);
	}
	public int get_1_6Penalty() {
		return _1_6Penalty;
	}
	public void set_1_6Penalty(String _1_6Penalty) {
		this._1_6Penalty = Integer.parseInt(_1_6Penalty);
	}
	public int getGiftDices() {
		return giftDices;
	}
	public void setGiftDices(String giftDices) {
		this.giftDices = Integer.parseInt(giftDices);
	}
	public int getExclusions() {
		return exclusions;
	}
	public void setExclusions(String exclusions) {
		this.exclusions = Integer.parseInt(exclusions);
	}
	
	public StateOfPlayer(int idNumber, String name,
			String houseStateBinaryFlags, String carStateBinaryFlags,
			String location, String balance, String _1_6Penalty, String giftDices,
			String exclusions) {
		super();
		this.idNumber = idNumber;
		this.name = name;
		this.house = houseStateBinaryFlags;
		this.car = carStateBinaryFlags;
		this.location = location;
		this.balance = Integer.parseInt(balance);
		this._1_6Penalty = Integer.parseInt(_1_6Penalty);
		this.giftDices = Integer.parseInt(giftDices);
		this.exclusions = Integer.parseInt(exclusions);
	}
	
	public StateOfPlayer(int idNumber) {
		super();
		this.idNumber = idNumber;
	}
	
	
	
}
