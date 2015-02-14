package dispatch;

import math.Distribution;

public class Scheduler {
	
	private Dispatcher dispatcher;
	
	public Scheduler(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	private TimeBucket addNewTimeBucket(long time) {
		TimeBucket bucket = TimeBucket.createTimeBucket(time);
		dispatcher.addTimeBucket(bucket);
		return bucket;
	}
	
	public void scheduleProbabilisticEventPacket(long currentTime, Distribution distribution, EventPacket packet) {
		long targetTime = currentTime + distribution.sample();
		scheduleDeterministicEventPacket(targetTime, packet);
	}
	
	public void scheduleDeterministicEventPacket(long targetTime, EventPacket packet) {
		boolean bucketMissing = true;
		for (TimeBucket bucket : dispatcher.getTimeBuckets()) {
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