package dispatch.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dispatch.event.Event;

public class ChannelInterface {
	
	protected String identity;
	protected Channel outputChannel;
	protected List<Event> inputBuffer;
	
	public ChannelInterface(String interfaceIdentity) {
		identity = interfaceIdentity;
		inputBuffer = new ArrayList<Event>();
	}
	
	public void setOutputChannel(Channel channel) {
		outputChannel = channel;
	}
	
	public void setInputChannel(Channel channel) {
		channel.addOutputInterface(this);
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public void write(Event event) {
		outputChannel.write(identity, event);
	}
	
	public void receive(Event event) {
		inputBuffer.add(event);
	}
	
	public Stream<Event> read() {
//		List<Event> events = inputBuffer.stream().filter(e -> !e.isProcessed()).collect(Collectors.toList());
		List<Event> events = inputBuffer.stream().collect(Collectors.toList());
		inputBuffer.clear();
		return events.stream();
	}

}
