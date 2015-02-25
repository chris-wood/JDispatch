package dispatch.event;

public class EventPacket {
	
	private String sourceIdentity;
	private Event event;
	private int time;
	
	public EventPacket(String sourceName, Event event, int time) {
		sourceIdentity = sourceName;
		this.event = event;
		this.time = time;
	}
	
	public String getSourceIdentity() {
		return sourceIdentity;
	}
	
	public Event getEvent() {
		return event;
	}
	
	public int getTime() {
		return time;
	}
	
	public void decrementTime() {
		time--;
	}

}
