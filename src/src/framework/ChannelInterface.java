package framework;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ChannelInterface {
	
	private String interfaceIdentity;
	
	private List<Event> outputChannel;
	private List<Event> inputChannel;
	
	public ChannelInterface(String identity) {
		this.interfaceIdentity = identity;
		this.outputChannel = new ArrayList<Event>();
		this.inputChannel = new ArrayList<Event>();
	}
	
	public String getIdentity() {
		return interfaceIdentity;
	}
	
	public void connect(ChannelInterface connection) {
		connection.outputChannel = inputChannel;
		connection.inputChannel = outputChannel;
		outputChannel = connection.inputChannel;
		inputChannel = connection.outputChannel;
	}
	
	public void write(Event event) {
		outputChannel.add(event);
	}

	public Stream<Event> getInputStream() {
		return inputChannel.stream();
	}
	
	@Override
	public String toString() {
		return interfaceIdentity;
	}
}
