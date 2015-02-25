package dispatch;

import java.util.ArrayList;
import java.util.List;

import dispatch.channel.Channel;
import dispatch.component.Component;
import dispatch.event.FutureEvent;
import dispatch.scheduler.Scheduler;

public class Dispatcher {
	
	private Clock clock;
	private List<Actor> actors;
	private Scheduler<FutureEvent> scheduler;
	
	public Dispatcher(long time) {
		this.clock = new Clock(time);
		this.actors = new ArrayList<Actor>();
//		this.channels = new ArrayList<Channel>();
//		this.components = new ArrayList<Component>();
		this.scheduler = new Scheduler<FutureEvent>();
	}
	
	public void scheduleEvent(FutureEvent futureEvent, long delay) {
		scheduler.scheduleDeterministicEventPacket(delay, futureEvent);
	}
	
	public void addActor(Actor actor) {
//		if (!components.contains(component)) {
//			components.add(component);
//		}
		if (!actors.contains(actor)) {
			actors.add(actor);
		}
	}
	
//	public void addChannel(Channel channel) {
//		if (!channels.contains(channel)) {
//			channels.add(channel);
//		}
//	}
	
	private void handleScheduledEvent(FutureEvent futureEvent) {
		for (Actor actor : actors) {
			if (actor.getIdentity().equals(futureEvent.getIdentity()) && actor instanceof Component) {
				Component component = (Component) actor;
				component.send(futureEvent.getTargetInterface(), futureEvent.getEvent());
			}
		}
	}
	
//	public void cycleComponents(long currentTime) {
//		for (FutureEvent futureEvent : scheduler.getScheduledCollection(currentTime)) {
//			handleScheduledEvent(futureEvent);
//		}
//		for (Component component : components) {
//			component.cycle(currentTime);
//		}
//	}
//	
//	public void cycleChannels(long currentTime) {
//		for (Channel channel : channels) {
//			channel.cycle(currentTime);
//		}
//	}
	
	public void cycle(long currentTime) {
		for (FutureEvent futureEvent : scheduler.getScheduledCollection(currentTime)) {
			handleScheduledEvent(futureEvent);
		}
		for (Actor actor : actors) {
			actor.cycle(currentTime);
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
			cycle(time);
			endEpoch(time);
			clock.tick();
		}
	}
}
