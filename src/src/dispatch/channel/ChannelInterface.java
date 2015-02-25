package dispatch.channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dispatch.Actor;
import dispatch.event.Event;
import dispatch.event.EventPacket;

public class ChannelInterface implements Actor {
	
	protected String identity;
	protected Channel outputChannel;
	protected List<EventPacket> transmittingPackets;
	protected List<Event> inputBuffer;
	
	public ChannelInterface(String interfaceIdentity) {
		identity = interfaceIdentity;
		inputBuffer = new ArrayList<Event>();
		transmittingPackets = new ArrayList<EventPacket>(); 
	}
	
	protected void propagateEvents(long currentTime) {
		Iterator<EventPacket> iterator = transmittingPackets.iterator();
		while (iterator.hasNext()) {
			EventPacket packet = iterator.next();
			if (packet.getTime() == 0) {
				outputChannel.write(identity, packet.getEvent());
				iterator.remove();
			} else {
				packet.decrementTime();
			}
		}
	}
	
	public void cycle(long currentTime) {
		propagateEvents(currentTime);
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
	
	public void write(Event event, int delay) {
		transmittingPackets.add(new EventPacket(identity, event, delay));
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
