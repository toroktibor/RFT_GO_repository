package server;

public class House {
	private int debit;
	private Boolean isInsured=false;
	private Boolean hasKitchen=false;
	private Boolean hasRoomFurniture=false;
	private Boolean hasFrigo=false;
	private Boolean hasCooker=false;
	private Boolean hasWashMachine=false;
	private Boolean hasDishwasher=false;
	
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
	public Boolean getHasKitchen() {
		return hasKitchen;
	}
	public void setHasKitchen(Boolean hasKitchen) {
		this.hasKitchen = hasKitchen;
	}
	public Boolean getHasRoomFurniture() {
		return hasRoomFurniture;
	}
	public void setHasRoomFurniture(Boolean hasRoomFurniture) {
		this.hasRoomFurniture = hasRoomFurniture;
	}
	public Boolean getHasFrigo() {
		return hasFrigo;
	}
	public void setHasFrigo(Boolean hasFrigo) {
		this.hasFrigo = hasFrigo;
	}
	public Boolean getHasCooker() {
		return hasCooker;
	}
	public void setHasCooker(Boolean hasCooker) {
		this.hasCooker = hasCooker;
	}
	public Boolean getHasWashMachine() {
		return hasWashMachine;
	}
	public void setHasWashMachine(Boolean hasWashMachine) {
		this.hasWashMachine = hasWashMachine;
	}
	public Boolean getHasDishwasher() {
		return hasDishwasher;
	}
	public void setHasDishwasher(Boolean hasDishwasher) {
		this.hasDishwasher = hasDishwasher;
	}
	@Override
	public String toString() {
		return "TRUE:" 	+ hasCooker.toString().toUpperCase() + ":" 
						+ hasDishwasher.toString().toUpperCase() + ":"
						+ hasFrigo.toString().toUpperCase() + ":"
						+ hasKitchen.toString().toUpperCase() + ":"
						+ hasRoomFurniture.toString().toUpperCase() + ":"
						+ hasWashMachine.toString().toUpperCase() + ":"
						+ isInsured.toString().toUpperCase() + ":"
						+ debit;
	}
	
	
}
