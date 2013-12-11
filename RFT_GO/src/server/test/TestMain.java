package server.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import server.GameEngine;

public class TestMain {

	public static void main(String[] args) {
		TestGameEngine ge = new TestGameEngine();
		try {
			ge.startGame();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

}
