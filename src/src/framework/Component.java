package framework;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Component {
	
	protected String identity;
	protected Map<String, EventQueue> inputQueues;
	protected Map<String, EventQueue> outputQueues;
	protected Set<Event> broadcastEvents;
	private final static Logger LOGGER = Logger.getLogger(Component.class.getName()); 
	
	public Component(String identity) {
		this.identity = identity;
		this.inputQueues = new HashMap<String, EventQueue>();
		this.outputQueues = new HashMap<String, EventQueue>();
		this.broadcastEvents = new HashSet<Event>(); 
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
			List<Event> events = queue.dequeueForTarget(identity);
			for (Event event : events) {
				if (!event.isBroadcastEvent || (event.isBroadcastEvent && !broadcastEvents.contains(event))) {
					processInputEvent(event);
				}
			}
		}
	}
	
	public void addDuplexQueue(String interfaceId, EventQueue queue) throws Exception {
		LOGGER.log(Level.INFO, identity + " adding duplex queue on interface " + interfaceId);
		addInputQueue(interfaceId, queue);
		addOutputQueue(interfaceId, queue);
	}
	
	public void addOutputQueue(String interfaceId, EventQueue queue) throws Exception {
		if (outputQueues.containsKey(queue.getIdentity())) {
			throw new Exception("Output queue already exists");
		}
		outputQueues.put(interfaceId, queue);
	}
	
	public void addInputQueue(String interfaceId, EventQueue queue) throws Exception {
		if (inputQueues.containsKey(queue.getIdentity())) {
			throw new Exception("Input queue already exists");
		}
		inputQueues.put(interfaceId, queue);
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
			LOGGER.log(Level.WARNING, "Error: " + this.identity + " output queue key: " + queueKey + " not found");
			return false;
		} else {
			LOGGER.log(Level.INFO, identity + " is sending command " + event.toString());
			outputQueues.get(queueKey).enqueue(event);
			if (event.isBroadcastEvent) {
				broadcastEvents.add(event);
			}
			return true;
		}
	}

	public boolean receiveEvent(String queueKey, Event event) {
		if (!inputQueues.containsKey(queueKey)) {
			LOGGER.log(Level.WARNING, "Error: " + this.identity + " output queue key: " + queueKey + " not found");
			return false;
		} else {
			LOGGER.log(Level.INFO, identity + " is sending command " + event.toString());
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
