package framework;

import java.util.UUID;

public class Event {
	
	protected UUID uuid;
	protected boolean processed;
	
	public Event() {
		this.uuid = java.util.UUID.randomUUID();
		this.processed = false;
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
