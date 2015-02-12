package dispatch;

import framework.Event;

public class EventPacket {
	
	private Event event;
	private String destination;
	private String queue;
	
	public EventPacket(Event event, String destination, String queue) {
		this.event = event;
		this.destination = destination;
		this.queue = queue;
	}
	
	public Event getEvent() {
		return event;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public String getQueue() {
		return queue;
	}

}
