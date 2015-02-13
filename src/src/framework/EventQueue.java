package framework;

import java.util.ArrayList;
import java.util.List;

public class EventQueue {
	
	protected String identity;
	
	protected List<Event> queue;
	
	public EventQueue(String identity) {
		this.identity = identity;
		this.queue = new ArrayList<Event>();
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
	
	public void clear() {
		queue.clear();
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

	public List<Event> dequeueForTarget(String target) {
		List<Event> events = new ArrayList<Event>();
		List<Event> stack = new ArrayList<Event>();
		while (!queue.isEmpty()) {
			Event event = dequeue();
			System.out.println("Event dequeued: " + event);
			if (event.isBroadcastEvent || event.target.equals(target)) {
				System.out.println("Adding!");
				events.add(event);
			} else {
				stack.add(event);
			}
		}
		
		// Throw old elements into the queue
		queue.addAll(stack);
		
		return events;
	}
}
