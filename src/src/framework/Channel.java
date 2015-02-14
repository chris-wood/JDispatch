package framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import math.Distribution;

public class Channel {
	
	protected String identity;
	
	protected List<Event> queue;
	protected List<Event> buffer;
	protected Map<Event, Long> countDownMap;
	
	public Channel(String identity) {
		this.identity = identity;
		this.queue = new ArrayList<Event>();
		this.buffer = new ArrayList<Event>();
		this.countDownMap = new HashMap<Event, Long>();
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public boolean hasNext() {
		return !isEmpty();
	}
	
	public void inject(Event event) {
		queue.add(event);
	}
	
	public void schedule(Event event) {
		buffer.add(event);
	}
	
	public void schedule(Event event, Distribution distribution) {
		long timeToWait = distribution.sample();
		if (timeToWait > 0) {
			countDownMap.put(event, timeToWait);
			System.out.println("Event " + event + " waiting for " + timeToWait);
		} else {
			schedule(event);
		}
	}
	
	private void countdownWaitingEvents() {
		Set<Event> eventsToRemove = new HashSet<Event>();
		for (Event event : countDownMap.keySet()) {
			if (countDownMap.get(event) == 1) {
				schedule(event);
				eventsToRemove.add(event);
			} else {
				countDownMap.put(event, countDownMap.get(event) - 1);
			}
		}
		
		for (Event event : eventsToRemove) {
			countDownMap.remove(event);
		}
	}
	
	public void cycle() {
		countdownWaitingEvents();
		queue.addAll(buffer);
		buffer.clear();
	}
	
	public Event dequeue() {
		Event event = queue.get(0);
		queue.remove(0);
		return event;
	}
	
	public Event peek() {
		Event event = queue.get(0);
		return event;
	}
}
