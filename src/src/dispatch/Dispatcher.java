package dispatch;

import java.util.ArrayList;
import java.util.List;

import dispatch.channel.Channel;
import dispatch.component.Component;
import dispatch.event.FutureEvent;
import dispatch.scheduler.Scheduler;

public class Dispatcher {
	
	private Clock clock;
	private List<Component> components;
	private List<Channel> channels;
	private Scheduler<FutureEvent> scheduler;
	
	public Dispatcher(long time) {
		this.clock = new Clock(time);
		this.channels = new ArrayList<Channel>();
		this.components = new ArrayList<Component>();
		this.scheduler = new Scheduler<FutureEvent>();
	}
	
	public void scheduleEvent(FutureEvent futureEvent, long delay) {
		scheduler.scheduleDeterministicEventPacket(delay, futureEvent);
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
	
	private void handleScheduledEvent(FutureEvent futureEvent) {
		for (Component component : components) {
			if (component.getIdentity().equals(futureEvent.getIdentity())) {
				component.send(futureEvent.getTargetInterface(), futureEvent.getEvent());
			}
		}
	}
	
	public void cycleComponents(long currentTime) {
		for (FutureEvent futureEvent : scheduler.getScheduledCollection(currentTime)) {
			handleScheduledEvent(futureEvent);
		}
		for (Component component : components) {
			component.cycle(currentTime);
		}
	}
	
	public void cycleChannels(long currentTime) {
		for (Channel channel : channels) {
			channel.cycle(currentTime);
		}
	}
	
	public void beginEpoch(long time) {
		// empty
	}
	
	public void endEpoch(long time) {
		// empty
	}
	
	public void run() {
		while (clock.isTimeLeft()) {
			long time = clock.getTime();
			beginEpoch(time);
			cycleComponents(time);
			cycleChannels(time);
			endEpoch(time);
			clock.tick();
		}
	}
}
