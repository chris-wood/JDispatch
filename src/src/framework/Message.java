package framework;

public class Message extends Event {
	
	protected String description;
	
	public Message(String msg) {
		description = msg;
	}
	
	public String getDescription() {
		return description;
	}
	
}
