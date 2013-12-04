package my;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface IGamePlay {
	public void dice() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	public void executeFieldCommand() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	public void executeLuckyCardCommand();
	public void initFields();
	public void initLuckyCards();
	public void sendGameState();
	public void startGame() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException;
	public void waitForPlayers(int maxNumberOfPlayers) throws IOException;
}
