package monty.entities;

import java.util.UUID;

public class Entity {
	private String id = UUID.randomUUID().toString();
	
	public String getId() {
		return id;
	}
}
