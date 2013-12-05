package client;

public class StateOfPlayer {
	private String idNumber;
	private String name;
	private String houseStateBinaryFlags;
	private String carStateBinaryFlags;
	private String location;
	private int balance;
	private int _1_6Penalty;
	private int giftDices;
	private int exclusions;
	
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHouseStateBinaryFlags() {
		return houseStateBinaryFlags;
	}
	public void setHouseStateBinaryFlags(String houseStateBinaryFlags) {
		this.houseStateBinaryFlags = houseStateBinaryFlags;
	}
	public String getCarStateBinaryFlags() {
		return carStateBinaryFlags;
	}
	public void setCarStateBinaryFlags(String carStateBinaryFlags) {
		this.carStateBinaryFlags = carStateBinaryFlags;
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
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public int get_1_6Penalty() {
		return _1_6Penalty;
	}
	public void set_1_6Penalty(int _1_6Penalty) {
		this._1_6Penalty = _1_6Penalty;
	}
	public int getGiftDices() {
		return giftDices;
	}
	public void setGiftDices(int giftDices) {
		this.giftDices = giftDices;
	}
	public int getExclusions() {
		return exclusions;
	}
	public void setExclusions(int exclusions) {
		this.exclusions = exclusions;
	}
	
}
