package monty.services;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import monty.entities.Door;
import monty.entities.Door.Prize;
import monty.entities.Game;
import monty.entities.Game.State;

@Service
public class MontyHallService {
	public static final int MIN_DOORS = 3;
	public static final int DEFAULT_DOORS = 3;
	
	private Map<String, Game> gamesById = Collections.synchronizedMap(new TreeMap<>());
	
	private Map<String, Integer> stats = Collections.synchronizedMap(new TreeMap<>());
	
	public Game newGame() {
		return newGame(DEFAULT_DOORS);
	}
	
	public Game newGame(int numberOfDoors) {
		if (numberOfDoors < MIN_DOORS) {
			throw new IllegalArgumentException("numberOfDoors must be at least " + MIN_DOORS + ", is: " + numberOfDoors);
		}
		
		Game game = new Game();
		game.setState(State.SELECT_A_DOOR);
		
		int winningDoorNumber = (int)(Math.random() * numberOfDoors) + 1;
		
		for (int doorNumber = 1; doorNumber <= numberOfDoors; doorNumber++) {
			Prize prize = winningDoorNumber == doorNumber ? Prize.WIN : Prize.LOSE;
			
			Door door = new Door();
			
			door.setState(Door.State.UNSELECTED);
			door.setGame(game);
			door.setDoorNumber(doorNumber);
			door.setPrize(prize);
			
			game.addDoor(door);
		}
		
		gamesById.put(game.getId(), game);
		return game;
	}
	
	public Game removeGame(String gameId) {
		return gamesById.remove(gameId);
	}
	
	
	public void selectDoor(Game game, int doorNumber) {
		assertGameState(game, Game.State.SELECT_A_DOOR);
		
		Door door = getDoor(game, doorNumber);
		door.setState(Door.State.SELECTED);
		
		// choose a random unselected losing door
		Door doorToOpen = game.getRandomUnselectedLosingDoor();
		
		if (doorToOpen == null) {
			door.setState(Door.State.UNSELECTED);
			throw new IllegalStateException("Unable to open a random unselected losing door");
		}
		
		// select the selected door, open another door, and move the game state forward 
		doorToOpen.setState(Door.State.OPENED);
		game.setState(Game.State.SECOND_CHANCE);
	}

	public void chooseDoor(Game game, int doorNumber) {
		assertGameState(game, Game.State.SECOND_CHANCE);
		
		Door door = getDoor(game, doorNumber);
		
		// prevent choosing an already opened door (that's cheating!)
		if (door.getState() == Door.State.OPENED) {
			throw new IllegalArgumentException("Door already opened: " + doorNumber);
		}
		boolean switched = door.getState() != Door.State.SELECTED;

		// select the open the door and mark the game as finished 
		door.setState(Door.State.OPENED);
		game.setState(Game.State.FINISHED);
		game.setPrize(door.getPrize());
		game.setSwitched(switched);	
		
		updateStats(switched, door.getPrize());
	}
	
	private void updateStats(boolean switched, Door.Prize prize) {
		String statName = (switched ? "switched" : "stayed") + '-' + prize.name().toLowerCase();
		Integer count = stats.get(statName);
		stats.put(statName, count != null ? count + 1 : 1);

		statName = "totals" + '-' + prize.name().toLowerCase();
		count = stats.get(statName);
		stats.put(statName, count != null ? count + 1 : 1);
	}
	
	public Map<String, Integer> getStats() {
		return Collections.unmodifiableMap(stats);
	}
	
	public Collection<Door> getDoors(Game game) {
		return game.getDoors(Game.Order.NUMBER);
	}
	
	public Game getGame(String gameId) {
		Game game = gamesById.get(gameId);
		
		if (game == null) {
			throw new IllegalArgumentException("Game not found: " + gameId);
		}
		
		return game;
	}

	private Game assertGameState(Game game, Game.State state) {
		if (game.getState() != state) {
			throw new IllegalStateException("Game not in the " + state + " state: " + game.getState());
		}
		
		return game;
	}
	
	public Door getDoor(Game game, int doorNumber) {
		Door door = game.getDoorByNumber(doorNumber);
		
		if (door == null) {
			throw new IllegalArgumentException("Door not found: " + doorNumber);
		}
		
		return door;
	}
}
