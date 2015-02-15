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
	
	public Component(String identity) {
		this.identity = identity;
		this.channelInterfaces = new HashMap<String, Channel>();
	}
	
	public Component(String identity, Dispatcher dispatcher) {
		this.identity = identity;
		this.channelInterfaces = new HashMap<String, Channel>();
		setDispatcher(dispatcher);
	}
	
	public void setDispatcher(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
		dispatcher.addComponent(this);
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public Channel getChannelByName(String name) {
		if (channelInterfaces.containsKey(name)) {
			return channelInterfaces.get(name);
		} else {
			return null;
		}
	}
	
	protected void processInputEvents(long time) {
		for (String queueKey : channelInterfaces.keySet()) {
			Stream<Event> eventStream = channelInterfaces.get(queueKey).getInputStream();
			eventStream.forEach(e -> processInputEventFromInterface(queueKey, e, time));
		}
	}
	
	protected abstract void processInputEventFromInterface(String interfaceId, Event event, long time);
	protected abstract void runComponent(long time);
	
	public void cycle(long currentTime) {
		processInputEvents(currentTime);
		runComponent(currentTime);		
	}
	
	public void addChannelInterface(String interfaceId, Channel channelInterface) {
		channelInterfaces.put(interfaceId, channelInterface);
	}
	
	public boolean broadcast(Event event) {
		boolean result = true;
		for (String channelId : channelInterfaces.keySet()) {
			result = result && send(channelId, event);
		}
		return result;
	}
	
	public boolean send(String queueKey, Event event) {
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
