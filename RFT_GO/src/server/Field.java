package server;

public class Field{
	private int number;
	private String description;
	private String command;
	
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Field(int number, String description, String command) {
		super();
		this.number = number;
		this.description = description;
		this.command = command;
	}
	@Override
	public String toString() {
		return (number + ":" + description);
	}
	
	

	

	
	
	
}
