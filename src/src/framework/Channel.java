package framework;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dispatch.EventPacket;
import dispatch.TimeBucket;

public class Channel {
	
	private String interfaceIdentity;
	
	private List<TimeBucket<Event>> timeBuckets;
	private List<Event> outputChannel;
	private List<Event> inputChannel;
	
	public Channel(String identity) {
		this.interfaceIdentity = identity;
		this.outputChannel = new ArrayList<Event>();
		this.inputChannel = new ArrayList<Event>();
		this.timeBuckets = new ArrayList<TimeBucket<Event>>();
	}
	
	public String getIdentity() {
		return interfaceIdentity;
	}
	
	private void processScheduledEvents(long currentTime) {
		for (TimeBucket<Event> bucket : timeBuckets) {
			if (bucket.getEventTime() < currentTime) {
				continue;
			} else if (bucket.getEventTime() == currentTime) {
				writeEventsFromBucket(bucket);
				break;
			} else {
				break;
			}
		}
	}
	
	public void writeEventsFromBucket(TimeBucket<Event> bucket) {
		while (bucket.hasNext()) {
			Event event = bucket.pop();
			write(event);
		}
	}
	
	public void cycle(long currentTime) {
		processScheduledEvents(currentTime);
	}
	
	public void connect(Channel connection) {
		connection.outputChannel = inputChannel;
		connection.inputChannel = outputChannel;
		outputChannel = connection.inputChannel;
		inputChannel = connection.outputChannel;
	}
	
	public void write(Event event) {
		outputChannel.add(event);
	}
	
	public void delayedWrite(int delay, Event event) {
		outputChannel.add(event);
	}

	public Stream<Event> getInputStream() {
		List<Event> events = inputChannel.stream().filter(e -> !e.isProcessed()).collect(Collectors.toList());
		inputChannel.clear();
		inputChannel.addAll(events);
		return events.stream();
	}
	
	@Override
	public String toString() {
		return interfaceIdentity;
	}
}
