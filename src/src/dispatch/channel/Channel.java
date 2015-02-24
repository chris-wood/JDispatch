package dispatch.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dispatch.event.Event;

public class Channel {
	
	private String identity;
	
	private List<Event> outputChannel;
	private List<Event> inputChannel;
	
	public Channel(String identity) {
		this.identity = identity;
		this.outputChannel = new ArrayList<Event>();
		this.inputChannel = new ArrayList<Event>();
	}
	
	public String getIdentity() {
		return identity;
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

	public Stream<Event> getInputStream() {
		List<Event> events = inputChannel.stream().filter(e -> !e.isProcessed()).collect(Collectors.toList());
		inputChannel.clear();
		inputChannel.addAll(events);
		return events.stream();
	}
	
	@Override
	public String toString() {
		return identity;
	}
}
