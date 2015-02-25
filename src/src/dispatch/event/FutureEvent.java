package dispatch.event;

public class FutureEvent {

	public String identity;
	public String interfaceId;
	public Event event;
	
	public FutureEvent(String identity, String interfaceId, Event event) {
		this.identity = identity;
		this.interfaceId = interfaceId;
		this.event = event;
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public String getTargetInterface() {
		return interfaceId;
	}
	
	public Event getEvent() {
		return event;
	}
	
}
