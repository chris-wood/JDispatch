package framework;

public class Message extends Event {
	
	protected String description;
	
	public Message(String msg) {
		this.description = msg;
	}
	
	public String getDescription() {
		return description;
	}
	
}
