package server;

public class Car {
	private int debit=0;
	private Boolean isInsured=false;
	
	public int getDebit() {
		return debit;
	}
	public void setDebit(int debit) {
		this.debit = debit;
	}
	public Boolean getIsInsured() {
		return isInsured;
	}
	public void setIsInsured(Boolean isInsured) {
		this.isInsured = isInsured;
	}
	@Override
	public String toString() {
		return "TRUE:" + isInsured.toString().toUpperCase() + ":" + debit;
	}
	
	
}
