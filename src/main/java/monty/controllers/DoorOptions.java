package monty.controllers;

import monty.entities.Door;

public class DoorOptions {
	private Door.State state;

	public Door.State getState() {
		return state;
	}

	public void setState(Door.State state) {
		this.state = state;
	}
}