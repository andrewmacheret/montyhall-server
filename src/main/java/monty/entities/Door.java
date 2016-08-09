package monty.entities;

public class Door extends Entity {
	public enum State {
		UNSELECTED, SELECTED, OPENED
	};
	public enum Prize {
		LOSE, WIN
	};

	private Game game;
	private int doorNumber;
	private State state;
	private Prize prize;

	public Door() {
	}

	public State getState() {
		return state;
	}
	
	public void setState(State state) {
		this.state = state;
	}

	public Prize getPrize() {
		return prize;
	}

	public Game getGame() {
		return game;
	}
	
	public int getDoorNumber() {
		return doorNumber;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setDoorNumber(int doorNumber) {
		this.doorNumber = doorNumber;
	}

	public void setPrize(Prize prize) {
		this.prize = prize;
	}

}
