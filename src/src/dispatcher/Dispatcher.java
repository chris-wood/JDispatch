package dispatcher;

import java.util.ArrayList;
import java.util.List;

import framework.Component;
import framework.Event;

public class Dispatcher extends Thread {
	
	private List<Component> components;
	private List<TimeBucket> timeBuckets;
	
	public Dispatcher(List<Component> components) {
		this.components = new ArrayList<Component>();
		this.timeBuckets = new ArrayList<TimeBucket>();
		for (Component component : components) {
			this.components.add(component);
		}
	}
	
	private TimeBucket addNewTimeBucket(long time) {
		TimeBucket bucket = TimeBucket.createTimeBucket(time);
		timeBuckets.add(bucket);
		return bucket;
	}
	
	public void scheduleEvent(long targetTime, Event event, String targetQueue) {
		EventPacket packet = new EventPacket(event, targetQueue);
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
	
	public void cycleComponents() {
		for (Component component : components) {
			component.processOutputQueueEvents(Clock.time);
		}
		for (Component component : components) {
			component.processInputQueueEvents(Clock.time);
		}
	}

	@Override
	public void run() {
		while (Clock.isTimeLeft()) {
			cycleComponents();
			Clock.tick();
		}
	}

}
