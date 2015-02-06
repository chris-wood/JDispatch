package framework;

import java.util.HashMap;
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
	
	public abstract void processInputEvent(Event event);
	 
	public abstract void cycle(long currentTime);

	public final void processOutputQueueEvents(long currentTime) {
		cycle(currentTime);
	}
	
	public final void processInputQueueEvents(long currentTime) {
		for (String queueKey : inputQueues.keySet()) {
			EventQueue queue = inputQueues.get(queueKey);
			System.out.println(identity + " is processing input queue " + queue.toString() + " at time " + currentTime);
			while (!queue.isEmpty()) {
				processInputEvent(queue.dequeue());
			}
		}
	}
	
	public void connect(Component component, EventQueue queue) throws Exception {
		addOutputQueue(queue);
		component.addInputQueue(queue);
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public void addOutputQueue(EventQueue queue) throws Exception {
		if (outputQueues.containsKey(queue.getIdentity())) {
			throw new Exception("Output queue already exists");
		}
		outputQueues.put(queue.getIdentity(), queue);
	}
	
	public void addInputQueue(EventQueue queue) throws Exception {
		if (inputQueues.containsKey(queue.getIdentity())) {
			throw new Exception("Input queue already exists");
		}
		inputQueues.put(queue.getIdentity(), queue);
	}
	
	public void sendMessage(String queueKey, Message msg) {
		if (!outputQueues.containsKey(queueKey)) {
			System.err.println("Error: " + this.identity + " output queue key: " + queueKey + " not found");
		} else {
			System.out.println(identity + " is sending message " + msg.toString());
			outputQueues.get(queueKey).enqueue(msg);
		}
	}
	
	public void sendCommand(String queueKey, Command cmd) {
		if (!outputQueues.containsKey(queueKey)) {
			System.err.println("Error: " + this.identity + " output queue key: " + queueKey + " not found");
		} else {
			System.out.println(identity + " is sending command " + cmd.toString());
			outputQueues.get(queueKey).enqueue(cmd);
		}
	}
	
}
