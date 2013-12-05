package server;

public interface ICashier {
	public Boolean deductMoney(int amount);
	public void addMoney(int amount);
	public Boolean checkBalance(int amount);
	public Boolean handleDebits();
	public void addPercentage(int amount);
}
