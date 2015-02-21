package dispatch;

import java.util.ArrayList;
import java.util.List;

import framework.Component;
import framework.Event;

public class Dispatcher {
	
	private Clock clock;
	private List<Component> components;
	private List<TimeBucket> timeBuckets;
	private Scheduler scheduler;
	
	public Dispatcher(long time) {
		this.clock = new Clock(time);
		this.components = new ArrayList<Component>();
		this.timeBuckets = new ArrayList<TimeBucket>();
		this.scheduler = new Scheduler(this);
	}
	
	public void addComponent(Component component) {
		components.add(component);
	}
	
	public void forwardScheduledEventsInBucket(TimeBucket bucket) {
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
		for (TimeBucket bucket : timeBuckets) {
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
	
	public void addTimeBucket(TimeBucket bucket) {
		timeBuckets.add(bucket);
	}
	
	public List<TimeBucket> getTimeBuckets() {
		return timeBuckets;
	}
	
	public void scheduleEventForNextEpoch(Event event, String destination, String queueId) {
		scheduler.scheduleDeterministicEventPacket(clock.getTime() + 1, new EventPacket(event, destination, queueId));
	}
	
	public void run() {
		while (clock.isTimeLeft()) {
			cycleComponents(clock.getTime());
			clock.tick();
			System.out.println();
		}
	}
}
