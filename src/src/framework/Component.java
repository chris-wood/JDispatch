package framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			List<Event> events = queue.dequeueForTarget(identity);
			for (Event event : events) {
				processInputEvent(event);
			}
		}
	}
	
	public void addDuplexQueue(String interfaceId, EventQueue queue) throws Exception {
		addInputQueue(interfaceId, queue);
		addOutputQueue(interfaceId, queue);
	}
	
	public void addOutputQueue(String interfaceId, EventQueue queue) throws Exception {
		if (outputQueues.containsKey(queue.getIdentity())) {
			throw new Exception("Output queue already exists");
		}
		outputQueues.put(queue.getIdentity(), queue);
	}
	
	public void addInputQueue(String interfaceId, EventQueue queue) throws Exception {
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
