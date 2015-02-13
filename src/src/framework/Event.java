package framework;

import java.util.UUID;

public class Event {
	
	protected String target;
	protected boolean isBroadcastEvent;
	protected UUID uuid;
	
	public Event() {
		this.target = "";
		this.isBroadcastEvent = true;
		uuid = java.util.UUID.randomUUID();
	}
	
	public Event(String target) {
		this.target = target;
		this.isBroadcastEvent = false;
	}
	
	@Override
	public String toString() {
		if (isBroadcastEvent) {
			return "BroadcastEvent-" + uuid.toString();
		} else {
			return "Event[" + target + "]-" + uuid.toString();
		}
	}

}
