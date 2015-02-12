package framework;

public class Event {
	
	protected String target;
	protected boolean isBroadcastEvent;
	
	public Event(String target) {
		this.target = target;
		this.isBroadcastEvent = false;
	}
	
	@Override
	public String toString() {
		return this.toString();
	}

}
