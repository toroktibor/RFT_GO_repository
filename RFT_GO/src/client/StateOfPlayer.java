package client;

public class StateOfPlayer {
	private int idNumber;
	private String name;
	private String houseStateBinaryFlags;
	private String carStateBinaryFlags;
	private String location;
	private int balance;
	private int _1_6Penalty;
	private int giftDices;
	private int exclusions;
	
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
	
}
