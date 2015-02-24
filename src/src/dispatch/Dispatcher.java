package dispatch;

import java.util.ArrayList;
import java.util.List;

import dispatch.channel.Channel;
import dispatch.component.Component;
import dispatch.event.Event;
import dispatch.event.EventPacket;
import dispatch.scheduler.Scheduler;

public class Dispatcher {
	
	private Clock clock;
	private List<Component> components;
	private Scheduler<EventPacket> componentScheduler;
	private List<Channel> channels;
	private Scheduler<EventPacket> channelScheduler;
	
	public Dispatcher(long time) {
		this.clock = new Clock(time);
		this.channels = new ArrayList<Channel>();
		this.components = new ArrayList<Component>();
		this.componentScheduler = new Scheduler<EventPacket>();
		this.channelScheduler = new Scheduler<EventPacket>();
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
					component.inject(queue, event);
					break;
				}
			}
		}
	}
	
	public void injectChannelMessages(List<EventPacket> packets) {
		for (EventPacket packet : packets) {
			
		}
	}
	
	public void cycleComponents(long currentTime) {
		forwardScheduledEventsInBucket(componentScheduler.getScheduledCollection(currentTime));
		for (Component component : components) {
			component.cycle(currentTime);
		}
	}
	
	public void cycleChannels(long currentTime) {
		injectChannelMessages(channelScheduler.getScheduledCollection(currentTime));
	}
	
	public void scheduleEventWithDelay(EventPacket eventPacket, long delay) {
		componentScheduler.scheduleDeterministicEventPacket(clock.getTime() + delay + 1, eventPacket);
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
