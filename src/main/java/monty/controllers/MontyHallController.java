package monty.controllers;

import java.util.Collection;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import monty.entities.Door;
import monty.entities.Game;
import monty.services.MontyHallService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "https://andrewmacheret.com")
public class MontyHallController {
	@Autowired
	MontyHallService montyHallService;

	// give basic info at the root path
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getVersion() {
		// get the version - this only works if there's a manifest file
		String version = getClass().getPackage().getImplementationVersion();
		if (version == null) {
			version = "UNSTABLE";
		}
		
		return Json.createObjectBuilder()
				.add("version", version)
				.add("apis", Json.createArrayBuilder()
						.add("/game")
						.add("/stats")
						.build())
				.build()
				.toString();
	}
	
	// get game state and doors
	@RequestMapping(value = "/game/{gameId}", method = RequestMethod.GET)
	public String getGame(@PathVariable String gameId) {
		Game game = montyHallService.getGame(gameId);

		return getGameJson(game).toString();
	}

	// create new game
	@RequestMapping(value = "/game", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String createGame(@RequestBody GameOptions gameOptions) {
		Game game = gameOptions.getNumberOfDoors() != null
				? montyHallService.newGame(gameOptions.getNumberOfDoors())
				: montyHallService.newGame();

		return getGameJson(game).toString();
	}

	// select / open a door
	@RequestMapping(value = "/game/{gameId}/door/{doorNumber}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String changeDoor(@PathVariable String gameId, @PathVariable int doorNumber,
			@RequestBody DoorOptions doorOptions) {
		if (doorOptions.getState() == null) {
			throw new NullPointerException("state must be specified");
		}

		Game game = montyHallService.getGame(gameId);

		switch (doorOptions.getState()) {
		case SELECTED:
			montyHallService.selectDoor(game, doorNumber);
			break;
		case OPENED:
			montyHallService.chooseDoor(game, doorNumber);
			break;
		default:
			throw new IllegalArgumentException("Unsupported state: " + doorOptions.getState());
		}

		return Json.createObjectBuilder()
				.add("updated", true)
				.build()
				.toString();
	}

	// delete game
	@RequestMapping(value = "/game/{gameId}", method = RequestMethod.DELETE)
	public String deleteGame(@PathVariable String gameId) {
		Game game = montyHallService.removeGame(gameId);

		return Json.createObjectBuilder()
				.add("deleted", game != null)
				.build()
				.toString();
	}
	
	// get stats
	@RequestMapping(value = "/stats", method = RequestMethod.GET)
	public String getStats() {
		JsonObjectBuilder statsObject = Json.createObjectBuilder();
		for (Map.Entry<String, Integer> statEntry : montyHallService.getStats().entrySet()) {
			statsObject.add(statEntry.getKey(), statEntry.getValue());
		}
		return statsObject.build().toString();
	}

	// select / open a door
	// convert a game to a json, hiding unopened doors
	private JsonObject getGameJson(Game game) {
		Collection<Door> doors = montyHallService.getDoors(game);

		JsonArrayBuilder doorsArray = Json.createArrayBuilder();
		for (Door door : doors) {
			doorsArray.add(getDoorJson(door));
		}

		JsonObjectBuilder gameObject = Json.createObjectBuilder()
				.add("id", game.getId())
				.add("state", game.getState().name())
				.add("doors", doorsArray.build());
		
		if (game.getState() == Game.State.FINISHED) {
			gameObject.add("prize", game.getPrize().name());
			gameObject.add("switched", game.isSwitched());
		}
		
		return gameObject.build();
	}

	private JsonObject getDoorJson(Door door) {
		JsonObjectBuilder doorObject = Json.createObjectBuilder();
		doorObject.add("number", door.getDoorNumber());
		doorObject.add("state", door.getState().name());
		if (door.getState() == Door.State.OPENED) {
			doorObject.add("prize", door.getPrize().name());
		}
		return doorObject.build();
	}
}
