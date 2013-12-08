package client;

public class StateOfPlayer {
	private int idNumber=0;
	private String name="";
	private String houseStateBinaryFlags="";
	private String carStateBinaryFlags="";
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
	
	public StateOfPlayer(int idNumber, String name,
			String houseStateBinaryFlags, String carStateBinaryFlags,
			String location, int balance, int _1_6Penalty, int giftDices,
			int exclusions) {
		super();
		this.idNumber = idNumber;
		this.name = name;
		this.houseStateBinaryFlags = houseStateBinaryFlags;
		this.carStateBinaryFlags = carStateBinaryFlags;
		this.location = location;
		this.balance = balance;
		this._1_6Penalty = _1_6Penalty;
		this.giftDices = giftDices;
		this.exclusions = exclusions;
	}
	
	
	
}
