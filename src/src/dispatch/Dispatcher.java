package dispatch;

import java.util.ArrayList;
import java.util.List;

import dispatch.channel.Channel;
import dispatch.component.Component;
import dispatch.event.Event;
import dispatch.event.EventPacket;
import dispatch.scheduler.Scheduler;
import dispatch.scheduler.TimeBucket;

public class Dispatcher {
	
	private Clock clock;
	
//	private List<TimeBucket<EventPacket>> timeBuckets;
	
	private List<Component> components;
	private Scheduler<EventPacket> componentScheduler;
	
	private List<Channel> channels;
	private Scheduler<Event> channelScheduler;
	
	public Dispatcher(long time) {
		this.clock = new Clock(time);
		this.channels = new ArrayList<Channel>();
		this.components = new ArrayList<Component>();
		this.componentScheduler = new Scheduler<EventPacket>();
		this.channelScheduler = new Scheduler<Event>();
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
	
	public void forwardScheduledEventsInBucket(List<EventPacket> packets) {
		for (EventPacket packet : packets) {
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
	
	public void injectChannelMessages(List<Event> events) {
		// TODO
	}
	
//	private void processScheduledEvents(long currentTime) {
//		for (TimeBucket<EventPacket> bucket : timeBuckets) {
//			if (bucket.getEventTime() < currentTime) {
//				continue;
//			} else if (bucket.getEventTime() == currentTime) {
//				forwardScheduledEventsInBucket(bucket);
//				break;
//			} else {
//				break;
//			}
//		}
//	}
	
	public void cycleComponents(long currentTime) {
		forwardScheduledEventsInBucket(componentScheduler.getScheduledCollection(currentTime));
		for (Component component : components) {
			component.cycle(currentTime);
		}
	}
	
	public void cycleChannels(long currentTime) {
		injectChannelMessages(channelScheduler.getScheduledCollection(currentTime));
	}
	
//	public void addTimeBucket(TimeBucket<EventPacket> bucket) {
//		timeBuckets.add(bucket);
//	}
//	
//	public List<TimeBucket<EventPacket>> getTimeBuckets() {
//		return timeBuckets;
//	}
	
	public void scheduleEventForNextEpoch(Event event, String destination, String queueId) {
		componentScheduler.scheduleDeterministicEventPacket(clock.getTime() + 1, new EventPacket(event, destination, queueId));
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
