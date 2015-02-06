package framework;

import java.util.ArrayList;
import java.util.List;

public class EventQueue {
	
	protected Component parentComponent;
	protected String identity;
	protected List<Event> queue;
	
	public EventQueue(Component parent, String identity) {
		this.parentComponent = parent;
		this.identity = identity;
		queue = new ArrayList<Event>();
	}
	
	public Component getParentComponent() {
		return parentComponent;
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public boolean hasNext() {
		return !isEmpty();
	}
	
	public void enqueue(Event event) {
		queue.add(event);
	}
	
	public Event dequeue() {
		Event event = queue.get(0);
		queue.remove(0);
		return event;
	}
	
	public Event peek() {
		Event event = queue.get(0);
		return event;
	}

}
