package dispatcher;

import java.util.List;

public class TimeBucket {

	private long eventTime;
	private List<EventPacket> events;
	
	private TimeBucket(long time) {
		eventTime = time;
	}
	
	public static TimeBucket createTimeBucket(long time) {
		TimeBucket bucket = new TimeBucket(time);
		return bucket;
	}
	
	public void addEvent(EventPacket packet) {
		events.add(packet);
	}
	
	public long getEventTime() {
		return eventTime;
	}
	
	public boolean hasNext() {
		return !events.isEmpty();
	}
	
	public EventPacket pop() {
		EventPacket packet = events.get(0);
		events.remove(0);
		return packet;
	}
	
	public EventPacket next() {
		return events.get(0);
	}
	
	public void remove() {
		events.remove(0);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("TimeBucket[" + eventTime + "] = [");
		if (!events.isEmpty()) {
			for (int i = 0; i < events.size() - 1; i++) {
				builder.append(events.get(i) + ",");
			}
			builder.append(events.get(events.size() - 1));
		}
		builder.append("]");
		
		return builder.toString();
	}
	
}