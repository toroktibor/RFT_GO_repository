package client;

public class StateOfPlayer {
	private int idNumber=0;
	private String name="NIL";
	private String house="NIL";
	private String car="NIL";
	private String location="NIL";
	private int balance=0;
	
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

	
	public StateOfPlayer(int idNumber, String name,
			String houseStateBinaryFlags, String carStateBinaryFlags,
			String location, String balance) {
		super();
		this.idNumber = idNumber;
		this.name = name;
		this.house = houseStateBinaryFlags;
		this.car = carStateBinaryFlags;
		this.location = location;
		this.balance = Integer.parseInt(balance);
	}
	
	public StateOfPlayer(int idNumber) {
		super();
		this.idNumber = idNumber;
	}
	
	
	
}
