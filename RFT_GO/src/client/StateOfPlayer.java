package client;

/**
 * Játékos állapotát reprezentáló osztály, kliens oldalon.
 * 
 * @author Ölveti József
 *
 */
public class StateOfPlayer {
	/**
	 * A játékos sorszáma.
	 */
	private int idNumber=0;
	/**
	 * A játékos neve.
	 */
	private String name="NIL";
	/**
	 * A játékos ház információit jelölő String.
	 */
	private String house="NIL";
	/**
	 * A játékos autó információit jelölő String.
	 */
	private String car="NIL";
	/**
	 * A játékos pozíció információit jelölő String.
	 */
	private String location="NIL";
	/**
	 * A játékos egyenlege.
	 */
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
