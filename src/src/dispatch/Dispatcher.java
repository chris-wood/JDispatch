package dispatch;

import java.util.ArrayList;
import java.util.List;

import framework.Channel;
import framework.Component;
import framework.Event;

public class Dispatcher {
	
	private Clock clock;
	private List<Channel> channels;
	private List<Component> components;
	private List<TimeBucket<EventPacket>> timeBuckets;
	private Scheduler scheduler;
	
	public Dispatcher(long time) {
		this.clock = new Clock(time);
		this.channels = new ArrayList<Channel>();
		this.components = new ArrayList<Component>();
		this.timeBuckets = new ArrayList<TimeBucket<EventPacket>>();
		this.scheduler = new Scheduler(this);
	}
	
	public void addComponent(Component component) {
		if (!components.contains(component)) {
			components.add(component);
		}
	}
	
	public void addChannel(Channel channel) {
		if (!channels.contains(channel)) {
			channels.add(channel);
		}
	}
	
	public void forwardScheduledEventsInBucket(TimeBucket<EventPacket> bucket) {
		while (bucket.hasNext()) {
			EventPacket packet = bucket.pop();
			
			Event event = packet.getEvent();
			String destination = packet.getDestination();
			String queue = packet.getQueue();
			
			for (Component component : components) {
				if (component.getIdentity().equals(destination)) {
					component.injectEvent(queue, event);
					break;
				}
			}
		}
	}
	
	private void processScheduledEvents(long currentTime) {
		for (TimeBucket<EventPacket> bucket : timeBuckets) {
			if (bucket.getEventTime() < currentTime) {
				continue;
			} else if (bucket.getEventTime() == currentTime) {
				forwardScheduledEventsInBucket(bucket);
				break;
			} else {
				break;
			}
		}
	}
	
	public void cycleComponents(long currentTime) {
		processScheduledEvents(currentTime);
		for (Component component : components) {
			component.cycle(currentTime);
		}
	}
	
	public void cycleChannels(long currentTime) {
		for (Channel channel : channels) {
			channel.cycle(currentTime);
		}
	}
	
	public void addTimeBucket(TimeBucket<EventPacket> bucket) {
		timeBuckets.add(bucket);
	}
	
	public List<TimeBucket<EventPacket>> getTimeBuckets() {
		return timeBuckets;
	}
	
	public void scheduleEventForNextEpoch(Event event, String destination, String queueId) {
		scheduler.scheduleDeterministicEventPacket(clock.getTime() + 1, new EventPacket(event, destination, queueId));
	}
	
	public void beginEpoch() {
		// empty
	}
	
	public void run() {
		while (clock.isTimeLeft()) {
			beginEpoch();
			cycleComponents(clock.getTime());
			cycleChannels(clock.getTime());
			clock.tick();
		}
	}
}
