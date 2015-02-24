package dispatch.event;

import java.util.UUID;

public class Event {
	
	protected UUID uuid;
	protected boolean processed;
	
	public Event() {
		this.uuid = java.util.UUID.randomUUID();
		this.processed = false;
	}
	
	public Event copy() {
		Event event = new Event();
		event.uuid = uuid;
		event.processed = false;
		return event;
	}
	
	public void setProcessed() {
		processed = true;
	}
	
	public boolean isProcessed() {
		return processed;
	}
	
	@Override
	public String toString() {
		return "Event-" + uuid.toString();
	}

}
