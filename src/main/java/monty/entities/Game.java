package monty.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Game extends Entity {
	public enum State {
		SELECT_A_DOOR, SECOND_CHANCE, FINISHED
	};
	public enum Order {
		ID, NUMBER
	};

	private Map<String, Door> doorsById = new TreeMap<>();
	private Map<Integer, Door> doorsByNumber = new TreeMap<>();

	private State state;
	private boolean switched;
	private Door.Prize prize;
	
	public Game() {
	}
	
	public void addDoor(Door door) {
		doorsById.put(door.getId(), door);
		doorsByNumber.put(door.getDoorNumber(), door);
	}

	public Door getDoorByNumber(int doorNumber) {
		return doorsByNumber.get(doorNumber);
	}

	public Door getDoorById(String doorId) {
		return doorsById.get(doorId);
	}

	public List<Door> getDoors(Order order) {
		Collection<Door> doors;
		switch (order) {
		case ID:
			doors = doorsById.values();
			break;
		case NUMBER:
			doors = doorsByNumber.values();
			break;
		default:
			throw new IllegalArgumentException("Unsupported Order: " + order);
		}

		return new ArrayList<>(doors);
	}

	public Door getRandomUnselectedLosingDoor() {
		List<Door> doors = new ArrayList<Door>();
		
		for (Door door : doorsByNumber.values()) {
			if (door.getState() == Door.State.UNSELECTED && door.getPrize() != Door.Prize.WIN) {
				doors.add(door);
			}
		}
		
		int doorIndexToOpen = (int)(Math.random() * doors.size());
		return doors.get(doorIndexToOpen);
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public boolean isSwitched() {
		return switched;
	}

	public void setSwitched(boolean switched) {
		this.switched = switched;
	}

	public Door.Prize getPrize() {
		return prize;
	}

	public void setPrize(Door.Prize prize) {
		this.prize = prize;
	}
}
