package monty;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;
import java.util.Arrays;
import java.util.TreeSet;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	private static final HttpHeaders ACCEPT_JSON = new HttpHeaders();
	static {
		ACCEPT_JSON.setContentType(MediaType.APPLICATION_JSON);
	}

	@Test
	public void basicTest() {
		String version = "UNSTABLE";

		JsonObject body = get("/");
		assertThat(parseJsonObject("{\"version\":\"" + version + "\",\"apis\":[\"/games\",\"/stats\"]}"))
				.isEqualTo(body);
	}

	@Test
	public void noSwitchTest() {
		// CREATE NEW GAME
		JsonObject body = post("/games", "{\"numberOfDoors\": 3}");
		// System.out.println(body);
		assertThat(body.keySet()).isEqualTo(new TreeSet<>(Arrays.asList(new String[] { "doors", "state", "id" })));
		assertThat("SELECT_A_DOOR").isEqualTo(body.getString("state"));
		assertThat(parseJsonArray(
				"[{\"number\":1,\"state\":\"UNSELECTED\"},{\"number\":2,\"state\":\"UNSELECTED\"},{\"number\":3,\"state\":\"UNSELECTED\"}]"))
						.isEqualTo(body.getJsonArray("doors"));
		assertThat(body.getString("id").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"))
				.isEqualTo(true);

		// SELECT DOOR
		String id = body.getString("id");
		put("/games/" + id + "/doors/1", "{\"state\": \"SELECTED\"}");

		// GET GAME STATE (might be finished, might need to choose a door)
		JsonObject body2 = get("/games/" + id);
		// System.out.println(body2);
		assertThat(body.keySet()).isEqualTo(new TreeSet<>(Arrays.asList(new String[] { "doors", "state", "id" })));
		JsonArray option2a = parseJsonArray(
				"[{\"number\":1,\"state\":\"SELECTED\"},{\"number\":2,\"state\":\"UNSELECTED\"},{\"number\":3,\"state\":\"OPENED\",\"prize\":\"LOSE\"}]");
		JsonArray option2b = parseJsonArray(
				"[{\"number\":1,\"state\":\"SELECTED\"},{\"number\":2,\"state\":\"OPENED\",\"prize\":\"LOSE\"},{\"number\":3,\"state\":\"UNSELECTED\"}]");
		assertThat(Arrays.asList(new JsonArray[] { option2a, option2b })).contains(body2.getJsonArray("doors"));
		assertThat(id).isEqualTo(body2.getString("id"));
		String state2 = body2.getString("state");
		assertThat("SECOND_CHANCE").isEqualTo(state2);

		// OPEN DOOR
		put("/games/" + id + "/doors/1", "{\"state\": \"OPENED\"}");

		// GET GAME STATE (expect finished)
		JsonObject body3 = get("/games/" + id);
		// System.out.println(body3);
		assertThat(body3.keySet())
				.isEqualTo(new TreeSet<>(Arrays.asList(new String[] { "doors", "state", "id", "prize", "switched" })));
		JsonArray option3a = parseJsonArray(
				"[{\"number\":1,\"state\":\"OPENED\",\"prize\":\"LOSE\"},{\"number\":2,\"state\":\"UNSELECTED\"},{\"number\":3,\"state\":\"OPENED\",\"prize\":\"LOSE\"}]");
		JsonArray option3b = parseJsonArray(
				"[{\"number\":1,\"state\":\"OPENED\",\"prize\":\"WIN\"},{\"number\":2,\"state\":\"UNSELECTED\"},{\"number\":3,\"state\":\"OPENED\",\"prize\":\"LOSE\"}]");
		JsonArray option3c = parseJsonArray(
				"[{\"number\":1,\"state\":\"OPENED\",\"prize\":\"LOSE\"},{\"number\":2,\"state\":\"OPENED\",\"prize\":\"LOSE\"},{\"number\":3,\"state\":\"UNSELECTED\"}]");
		JsonArray option3d = parseJsonArray(
				"[{\"number\":1,\"state\":\"OPENED\",\"prize\":\"WIN\"},{\"number\":2,\"state\":\"OPENED\",\"prize\":\"LOSE\"},{\"number\":3,\"state\":\"UNSELECTED\"}]");
		assertThat(Arrays.asList(new JsonArray[] { option3a, option3b, option3c, option3d }))
				.contains(body3.getJsonArray("doors"));
		assertThat("FINISHED").isEqualTo(body3.getString("state"));
		assertThat(Arrays.asList(new String[] { "WIN", "LOSE" })).contains(body3.getString("prize"));
		assertThat(body3.getString("prize")).isEqualTo(body3.getJsonArray("doors").getJsonObject(0).getString("prize"));
		assertThat(false).isEqualTo(body3.getBoolean("switched"));
		assertThat(id).isEqualTo(body3.getString("id"));

		// DELETE GAME
		delete("/games/" + id);
	}

	@Test
	public void switchTest() {
		// CREATE NEW GAME
		JsonObject body = post("/games", "{\"numberOfDoors\": 3}");
		//System.out.println(body);
		assertThat(body.keySet()).isEqualTo(new TreeSet<>(Arrays.asList(new String[] { "doors", "state", "id" })));
		assertThat("SELECT_A_DOOR").isEqualTo(body.getString("state"));
		assertThat(parseJsonArray(
				"[{\"number\":1,\"state\":\"UNSELECTED\"},{\"number\":2,\"state\":\"UNSELECTED\"},{\"number\":3,\"state\":\"UNSELECTED\"}]"))
						.isEqualTo(body.getJsonArray("doors"));
		assertThat(body.getString("id").matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"))
				.isEqualTo(true);
		
		// SELECT DOOR
		String id = body.getString("id");
		put("/games/" + id + "/doors/1", "{\"state\": \"SELECTED\"}");
		
		// GET GAME STATE (might be finished, might need to choose a door)
		JsonObject body2 = get("/games/" + id);
		//System.out.println(body2);
		assertThat(body.keySet()).isEqualTo(new TreeSet<>(Arrays.asList(new String[] { "doors", "state", "id" })));
		JsonArray option2a = parseJsonArray(
				"[{\"number\":1,\"state\":\"SELECTED\"},{\"number\":2,\"state\":\"UNSELECTED\"},{\"number\":3,\"state\":\"OPENED\",\"prize\":\"LOSE\"}]");
		JsonArray option2b = parseJsonArray(
				"[{\"number\":1,\"state\":\"SELECTED\"},{\"number\":2,\"state\":\"OPENED\",\"prize\":\"LOSE\"},{\"number\":3,\"state\":\"UNSELECTED\"}]");
		assertThat(Arrays.asList(new JsonArray[] { option2a, option2b })).contains(body2.getJsonArray("doors"));
		assertThat(id).isEqualTo(body2.getString("id"));
		String state2 = body2.getString("state");
		assertThat("SECOND_CHANCE").isEqualTo(state2);
		
		// OPEN DOOR
		int door = body2.getJsonArray("doors").getJsonObject(1).getString("state").equals("UNSELECTED") ? 2 : 3;
		put("/games/" + id + "/doors/" + door, "{\"state\": \"OPENED\"}");
		
		// GET GAME STATE (expect finished)
		JsonObject body3 = get("/games/" + id);
		System.out.println(body3);
		assertThat(body3.keySet()).isEqualTo(new TreeSet<>(Arrays.asList(new String[] { "doors", "state", "id", "prize", "switched" })));
		JsonArray option3a = parseJsonArray(
				"[{\"number\":1,\"state\":\"SELECTED\"},{\"number\":2,\"state\":\"OPENED\",\"prize\":\"LOSE\"},{\"number\":3,\"state\":\"OPENED\",\"prize\":\"LOSE\"}]");
		JsonArray option3b = parseJsonArray(
				"[{\"number\":1,\"state\":\"SELECTED\"},{\"number\":2,\"state\":\"OPENED\",\"prize\":\"" + (door == 2 ? "WIN" : "LOSE") + "\"},{\"number\":3,\"state\":\"OPENED\",\"prize\":\"" + (door == 3 ? "WIN" : "LOSE") + "\"}]");
		assertThat(Arrays.asList(new JsonArray[] { option3a, option3b })).contains(body3.getJsonArray("doors"));
		assertThat("FINISHED").isEqualTo(body3.getString("state"));
		assertThat(Arrays.asList(new String[] {"WIN", "LOSE"})).contains(body3.getString("prize"));
		assertThat(body3.getString("prize")).isEqualTo(body3.getJsonArray("doors").getJsonObject(door - 1).getString("prize"));
		assertThat(true).isEqualTo(body3.getBoolean("switched"));
		assertThat(id).isEqualTo(body3.getString("id"));
		
		// DELETE GAME
		delete("/games/" + id);
	}

	private JsonObject get(String path) {
		return parseJsonObject(this.restTemplate.getForObject(path, String.class));
	}

	private JsonObject post(String path, String body) {
		HttpEntity<String> httpEntity = new HttpEntity<String>(body, ACCEPT_JSON);
		return parseJsonObject(this.restTemplate.postForObject(path, httpEntity, String.class));
	}

	private void put(String path, String body) {
		HttpEntity<String> httpEntity = new HttpEntity<String>(body, ACCEPT_JSON);
		this.restTemplate.put(path, httpEntity);
	}

	private void delete(String path) {
		this.restTemplate.delete(path);
	}

	private JsonObject parseJsonObject(String jsonString) {
		try (JsonReader jsonReader = Json.createReader(new StringReader(jsonString))) {
			return jsonReader.readObject();
		}
	}

	private JsonArray parseJsonArray(String jsonString) {
		try (JsonReader jsonReader = Json.createReader(new StringReader(jsonString))) {
			return jsonReader.readArray();
		}
	}
}
