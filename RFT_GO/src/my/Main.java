package my;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
	public GameEngine MainController;

	public static void main(String[] args) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		GameEngine ge = new GameEngine();
		ge.initFields();
		ge.startGame();
		return;
	}
}
