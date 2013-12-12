package server;

import java.io.IOException;

public interface ICashier {
	public void deductMoney(int amount) throws IOException;
	public void addMoney(int amount) throws IOException;
	public Boolean checkBalance(int amount);
	public void handleDebits() throws IOException;
	public void addPercentage(int amount) throws IOException;
}

