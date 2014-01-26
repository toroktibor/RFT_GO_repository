package com.example.mapdemo;

public class Session {
	private static Session instance;
	private User actualUser = new User(0);
	
	public Session getInstance() {
		return instance;
	}

	public void setInstance(Session instance) {
		this.instance = instance;
	}

	public User getActualUser() {
		return actualUser;
	}

	public void setActualUser(User actualUser) {
		this.actualUser = actualUser;
	}
	
	
}
