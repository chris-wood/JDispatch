package framework;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import dispatch.Dispatcher;

public abstract class Component {
	
	protected String identity;
	protected Dispatcher dispatcher;
	protected Map<String, ChannelInterface> channelInterfaces;
//	protected Map<String, Stream<Event>> inputStreams;
	private final static Logger LOGGER = Logger.getLogger(Component.class.getName());
	
	public Component(String identity, Dispatcher dispatcher) {
		this.identity = identity;
		this.channelInterfaces = new HashMap<String, ChannelInterface>();
//		this.inputStreams = new HashMap<String, Stream<Event>>();
//		this.inputQueues = new HashMap<String, Channel>();
//		this.outputQueues = new HashMap<String, Channel>();
	}
	
	public String getIdentity() {
		return identity;
	}
	
	protected void processInputEvents(long time) {
		for (String queueKey : channelInterfaces.keySet()) {
			Stream<Event> eventStream = channelInterfaces.get(queueKey).getInputStream();
			eventStream.forEach(e -> processInputEvent(e, time));
		}
	}
	
	protected abstract void processInputEvent(Event event, long time);
	protected abstract void runComponent(long time);
	
	public void cycle(long currentTime) {
		processInputEvents(currentTime);
		runComponent(currentTime);		
	}
	
	public void addChannelInterface(String interfaceId, ChannelInterface channelInterface) {
		channelInterfaces.put(interfaceId, channelInterface);
	}
	
//	public void addDuplexQueue(String interfaceId, Channel queue) throws Exception {
//		addInputQueue(interfaceId, queue);
//		addOutputQueue(interfaceId, queue);
//	}
	
//	public void addOutputQueue(String interfaceId, Channel queue) throws Exception {
//		if (outputQueues.containsKey(queue.getIdentity())) {
//			throw new Exception("Output queue already exists");
//		}
//		outputQueues.put(interfaceId, queue);
//	}
//	
//	public void addInputQueue(String interfaceId, Channel queue) throws Exception {
//		if (inputQueues.containsKey(queue.getIdentity())) {
//			LOGGER.log(Level.WARNING, "Error: " + this.identity + " Input queue " + interfaceId + " already exists.");
//			throw new Exception("Input queue already exists");
//		}
//		inputQueues.put(interfaceId, queue);
//	}
	
	public boolean broadcastEvent(Event event) {
		boolean result = true;
		for (String channelId : channelInterfaces.keySet()) {
			result = result && sendEvent(channelId, event);
		}
		return result;
	}
	
//	public boolean broadcastEvent(Event event, Distribution distribution) {
//		boolean result = true;
//		for (String channelId : channelInterfaces.keySet()) {
//			result = result && sendEvent(channelId, event, distribution);
//		}
//		return result;
//	}
	
	public boolean sendEvent(String queueKey, Event event) {
		if (!channelInterfaces.containsKey(queueKey)) {
			LOGGER.log(Level.WARNING, "Error: " + this.identity + " output queue key: " + queueKey + " not found");
			return false;
		} else {
			channelInterfaces.get(queueKey).write(event);
			return true;
		}
	}
	
//	public boolean sendEvent(String queueKey, Event event, Distribution distribution) {
//		if (!channelInterfaces.containsKey(queueKey)) {
//			LOGGER.log(Level.WARNING, "Error: " + this.identity + " output queue key: " + queueKey + " not found");
//			return false;
//		} else {
//			channelInterfaces.get(queueKey).write(event, distribution);
//			return true;
//		}
//	}

	public boolean injectEvent(String queueKey, Event event) {
		if (!channelInterfaces.containsKey(queueKey)) {
			LOGGER.log(Level.WARNING, "Error: " + this.identity + " output queue key: " + queueKey + " not found");
			return false;
		} else {
			channelInterfaces.get(queueKey).write(event);
			return true;
		}
	}
}
