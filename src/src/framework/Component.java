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
	protected Map<String, Channel> channelInterfaces;
	private final static Logger LOGGER = Logger.getLogger(Component.class.getName());
	
	public Component(String identity, Dispatcher dispatcher) {
		this.identity = identity;
		this.channelInterfaces = new HashMap<String, Channel>();
		this.dispatcher = dispatcher;
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
	
	public void addChannelInterface(String interfaceId, Channel channelInterface) {
		channelInterfaces.put(interfaceId, channelInterface);
	}
	
	public boolean broadcastEvent(Event event) {
		boolean result = true;
		for (String channelId : channelInterfaces.keySet()) {
			result = result && sendEvent(channelId, event);
		}
		return result;
	}
	
	public boolean sendEvent(String queueKey, Event event) {
		if (!channelInterfaces.containsKey(queueKey)) {
			LOGGER.log(Level.WARNING, "Error: " + this.identity + " output queue key: " + queueKey + " not found");
			return false;
		} else {
			dispatcher.scheduleEventForNextEpoch(event, identity, queueKey);
			return true;
		}
	}
	
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
