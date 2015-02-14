package dispatch;

import java.util.ArrayList;
import java.util.List;

import framework.Component;
import framework.Event;
import framework.Channel;

public class Dispatcher extends Thread {
	
	private Clock clock;
	private List<Component> components;
//	private List<Channel> eventQueues;
	private List<TimeBucket> timeBuckets;
	private Scheduler scheduler;
	
	public Dispatcher(long time) {
		this.clock = new Clock(time);
		this.components = new ArrayList<Component>();
//		this.eventQueues = new ArrayList<Channel>();
		this.timeBuckets = new ArrayList<TimeBucket>();
		this.scheduler = new Scheduler(this);
	}
	
	public void addComponent(Component component) {
		components.add(component);
	}
	
//	public void addEventQueue(Channel queue) {
//		eventQueues.add(queue);
//	}
	
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
	
//	public void cycleQueues(long currentTime) {
//		for (Channel queue : eventQueues) {
//			queue.cycle();
//		}
//	}
	
	public void addTimeBucket(TimeBucket bucket) {
		timeBuckets.add(bucket);
	}
	
	public List<TimeBucket> getTimeBuckets() {
		return timeBuckets;
	}
	
	@Override
	public void run() {
		while (clock.isTimeLeft()) {
			cycleComponents(clock.getTime());
//			cycleQueues(clock.getTime());
			clock.tick();
		}
	}
}
