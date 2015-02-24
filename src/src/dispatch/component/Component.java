package dispatch.component;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import dispatch.Dispatcher;
import dispatch.channel.Channel;
import dispatch.event.Event;
import dispatch.event.EventPacket;

public abstract class Component {
	
	private int sleepTimer;
	protected String identity;
	protected Dispatcher dispatcher;
	protected Map<String, Channel> channelInterfaces;
	private final static Logger LOGGER = Logger.getLogger(Component.class.getName());
	
	public Component(String componentIdentity) {
		initialize(componentIdentity);
	}
	
	public Component(String componentIdentity, Dispatcher dispatcher) {
		initialize(componentIdentity);
		setDispatcher(dispatcher);
	}
	
	private void initialize(String componentIdentity) {
		identity = componentIdentity;
		channelInterfaces = new HashMap<String, Channel>();
		sleepTimer = 0;
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
		
		if (sleepTimer == 0) {
			runComponent(currentTime);
		} else {
			sleepTimer--;
		}
	}
	
	public void yield(int time) {
		sleepTimer += time;
	}
	
	public void addChannelInterface(String interfaceId, Channel channelInterface) {
		channelInterfaces.put(interfaceId, channelInterface);
	}
	
	public boolean broadcast(Event event) {
		boolean result = true;
		for (String channelId : channelInterfaces.keySet()) {
			result = result && writeEventToChannel(channelId, event);
		}
		return result;
	}
	
	public boolean broadcast(Event event, String arrivalInterface) {
		boolean result = true;
		for (String channelId : channelInterfaces.keySet()) {
			if (!channelId.equals(arrivalInterface)) {
				result = result && writeEventToChannel(channelId, event);
			}
		}
		return result;
	}
	
	public boolean writeEventToChannel(String queueKey, Event event) {
		return writeEventToChannel(queueKey, event, 0);
	}
	
	public boolean writeEventToChannel(String queueKey, Event event, long delay) {
		if (!channelInterfaces.containsKey(queueKey)) {
			LOGGER.log(Level.WARNING, "Error: " + this.identity + " output queue key: " + queueKey + " not found");
			return false;
		} else {
			EventPacket packet = new EventPacket(event, identity, queueKey);
			dispatcher.scheduleEventWithDelay(packet, delay);
			return true;
		}
	}
	
	public boolean inject(String queueKey, Event event) {
		if (!channelInterfaces.containsKey(queueKey)) {
			LOGGER.log(Level.WARNING, "Error: " + this.identity + " output queue key: " + queueKey + " not found");
			return false;
		} else {
			channelInterfaces.get(queueKey).write(event);
			return true;
		}
	}
}
