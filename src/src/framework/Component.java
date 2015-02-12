package framework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Component {
	
	protected String identity;
	
	protected Map<String, EventQueue> inputQueues;
	protected Map<String, EventQueue> outputQueues;
	
	public Component(String identity) {
		this.identity = identity;
		inputQueues = new HashMap<String, EventQueue>();
		outputQueues = new HashMap<String, EventQueue>();
	}
	
	public String getIdentity() {
		return identity;
	}
	
	protected abstract void processInputEvent(Event event);
	 
	protected abstract void cycle(long currentTime);

	public final void processOutputQueueEvents(long currentTime) {
		cycle(currentTime);
	}
	
	public final void processInputQueueEvents(long currentTime) {
		for (String queueKey : inputQueues.keySet()) {
			EventQueue queue = inputQueues.get(queueKey);
			
			System.out.println(identity + " is processing input queue " + queue.toString() + " at time " + currentTime);
			
			Set<Event> visitedBroadcastEvents = new HashSet<Event>();
			while (!queue.isEmpty()) {
				Event event = queue.dequeue();
				if (!event.isBroadcastEvent && event.target.equals(identity)) {
					processInputEvent(event);
				} else if (event.isBroadcastEvent && !visitedBroadcastEvents.contains(event)) {
					processInputEvent(event);
					queue.enqueue(event); 
					visitedBroadcastEvents.add(event);
				} else {
					break; // cycle
				}
			}
		}
	}
	
	public void connect(Component component, EventQueue queue) throws Exception {
		addOutputQueue(queue);
		component.addInputQueue(queue);
	}
	
	private void addOutputQueue(EventQueue queue) throws Exception {
		if (outputQueues.containsKey(queue.getIdentity())) {
			throw new Exception("Output queue already exists");
		}
		outputQueues.put(queue.getIdentity(), queue);
	}
	
	private void addInputQueue(EventQueue queue) throws Exception {
		if (inputQueues.containsKey(queue.getIdentity())) {
			throw new Exception("Input queue already exists");
		}
		inputQueues.put(queue.getIdentity(), queue);
	}
	
	public boolean sendMessage(String queueKey, Message msg) {
		return sendEvent(queueKey, msg);
	}
	
	public boolean sendCommand(String queueKey, Command cmd) {
		return sendEvent(queueKey, cmd);
	}
	
	public boolean receiveMessage(String queueKey, Message msg) {
		return receiveEvent(queueKey, msg);
	}
	
	public boolean receiveCommand(String queueKey, Command cmd) {
		return receiveEvent(queueKey, cmd);
	}
	
	public boolean sendEvent(String queueKey, Event event) {
		if (!outputQueues.containsKey(queueKey)) {
			System.err.println("Error: " + this.identity + " output queue key: " + queueKey + " not found");
			return false;
		} else {
			System.out.println(identity + " is sending command " + event.toString());
			outputQueues.get(queueKey).enqueue(event);
			return true;
		}
	}

	public boolean receiveEvent(String queueKey, Event event) {
		if (!inputQueues.containsKey(queueKey)) {
			System.err.println("Error: " + this.identity + " output queue key: " + queueKey + " not found");
			return false;
		} else {
			System.out.println(identity + " is sending command " + event.toString());
			inputQueues.get(queueKey).enqueue(event);
			return true;
		}
	}
	
	public void clearInputQueueEvents() {
		for (String queueIdentity : inputQueues.keySet()) {
			inputQueues.get(queueIdentity).clear();
		}
	}
}
