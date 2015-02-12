package dispatch;

import java.util.ArrayList;
import java.util.List;

import math.Distribution;
import framework.Component;
import framework.Event;

public class Dispatcher extends Thread {
	
	private Clock clock;
	private List<Component> components;
	private List<TimeBucket> timeBuckets;
	
	public Dispatcher(long time, List<Component> components) {
		this.clock = new Clock(time);
		this.components = new ArrayList<Component>();
		this.timeBuckets = new ArrayList<TimeBucket>();
		
		for (Component component : components) {
			this.components.add(component);
		}
	}
	
	public void forwardScheduledEventsInBucket(TimeBucket bucket) {
		while (bucket.hasNext()) {
			EventPacket packet = bucket.pop();
			
			Event event = packet.getEvent();
			String destination = packet.getDestination();
			String queue = packet.getQueue();
			
			for (Component component : components) {
				if (component.getIdentity().equals(destination)) {
					component.receiveEvent(queue, event);
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
			component.processOutputQueueEvents(currentTime);
		}
		
		for (Component component : components) {
			component.processInputQueueEvents(currentTime);
		}
		
		for (Component component : components) {
			component.clearInputQueueEvents();
		}
	}

	@Override
	public void run() {
		while (clock.isTimeLeft()) {
			cycleComponents(clock.getTime());
			clock.tick();
		}
	}
	
	class DispatchScheduler {
		private TimeBucket addNewTimeBucket(long time) {
			TimeBucket bucket = TimeBucket.createTimeBucket(time);
			timeBuckets.add(bucket);
			return bucket;
		}
		
		public void scheduleProbabilisticEventPacket(long currentTime, Distribution distribution, EventPacket packet) {
			long targetTime = currentTime + distribution.sample();
			scheduleDeterministicEventPacket(targetTime, packet);
		}
		
		public void scheduleDeterministicEventPacket(long targetTime, EventPacket packet) {
			boolean bucketMissing = true;
			for (TimeBucket bucket : timeBuckets) {
				long bucketTime = bucket.getEventTime();
				if (bucketTime < targetTime) {
					continue;
				} else if (bucketTime == targetTime) {
					bucket.addEvent(packet);
					bucketMissing = false;
				} else {
					break;
				}
			}
			
			if (bucketMissing) {
				addNewTimeBucket(targetTime).addEvent(packet);
			}
		}
	}

}
