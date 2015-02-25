package dispatch.scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dispatch.math.Distribution;

public class Scheduler<T> {
	
	private List<TimeBucket<T>> timeBuckets;
	
	public Scheduler() {
		this.timeBuckets = new ArrayList<TimeBucket<T>>();
	}
	
	private TimeBucket<T> addNewTimeBucket(long time) {
		TimeBucket<T> bucket = new TimeBucket<T>(time);
		timeBuckets.add(bucket);
		return bucket;
	}
	
	public void scheduleProbabilisticEventPacket(long currentTime, Distribution distribution, T packet) {
		long targetTime = currentTime + distribution.sample();
		scheduleDeterministicEventPacket(targetTime, packet);
	}
	
	public void scheduleDeterministicEventPacket(long targetTime, T packet) {
		boolean bucketMissing = true;
		for (TimeBucket<T> bucket : timeBuckets) {
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
	
	public List<T> getScheduledCollection(long targetTime) {
		for (TimeBucket<T> bucket : timeBuckets) {
			if (bucket.getEventTime() == targetTime) {
				return bucket.getContents();
			}
		}
		return Collections.emptyList();
	}
}