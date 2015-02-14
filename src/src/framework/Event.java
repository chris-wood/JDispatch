package framework;

import java.util.UUID;

public class Event {
	
	protected UUID uuid;
	
	public Event() {
		uuid = java.util.UUID.randomUUID();
	}
	
	@Override
	public String toString() {
		return "Event-" + uuid.toString();
	}

}
