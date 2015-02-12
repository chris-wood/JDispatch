package framework;

public class Message extends Event {
	
	protected String description;
	
	public Message(String target, String msg) {
		super(target);
		description = msg;
	}
	
	public String getDescription() {
		return description;
	}
	
}
