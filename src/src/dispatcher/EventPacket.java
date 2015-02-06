package dispatcher;

import framework.Event;

public class EventPacket {
	
	private Event event;
	private String destination;
	
	public EventPacket(Event event, String destination) {
		this.event = event;
		this.destination = destination;
	}
	
	public Event getEvent() {
		return event;
	}
	
	public String getDestination() {
		return destination;
	}

}
