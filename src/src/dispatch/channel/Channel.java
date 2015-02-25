package dispatch.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dispatch.Dispatcher;
import dispatch.event.Event;
import dispatch.event.EventPacket;

public class Channel {
	
	private String identity;
	
	private Map<String, ChannelInterface> outputInterfaces;
	private List<EventPacket> eventPackets;
	
	public Channel(String identity, Dispatcher dispatcher) {
		this.identity = identity;
		this.outputInterfaces = new HashMap<String, ChannelInterface>();
		this.eventPackets = new ArrayList<EventPacket>();
		dispatcher.addChannel(this);
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public void addOutputInterface(ChannelInterface channelInterface) {
		outputInterfaces.put(channelInterface.getIdentity(), channelInterface);
	}
	
	protected void propagateEvents(long currentTime) {
		Iterator<EventPacket> iterator = eventPackets.iterator();
		while (iterator.hasNext()) {
			EventPacket packet = iterator.next();
			if (packet.getTime() == 0) {
				iterator.remove();
				for (String outputInterface : outputInterfaces.keySet()) {
					if (!packet.getSourceIdentity().equals(outputInterface)) {
						outputInterfaces.get(outputInterface).receive(packet.getEvent());
					}
				}
			} else {
				packet.decrementTime();
			}
		}
	}
	
	public void cycle(long currentTime) {
		propagateEvents(currentTime);
	}
	
	public void write(String source, Event event) {
		eventPackets.add(new EventPacket(source, event, 0));
	}
	
	@Override
	public String toString() {
		return identity;
	}
}
