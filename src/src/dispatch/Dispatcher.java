package dispatch;

import java.util.ArrayList;
import java.util.List;

import dispatch.channel.Channel;
import dispatch.component.Component;

public class Dispatcher {
	
	private Clock clock;
	private List<Component> components;
	private List<Channel> channels;
	
	public Dispatcher(long time) {
		this.clock = new Clock(time);
		this.channels = new ArrayList<Channel>();
		this.components = new ArrayList<Component>();
	}
	
	public void addComponent(Component component) {
		if (!components.contains(component)) {
			components.add(component);
		}
	}
	
	public void addChannel(Channel channel) {
		if (!channels.contains(channel)) {
			channels.add(channel);
		}
	}
	
	public void cycleComponents(long currentTime) {
		for (Component component : components) {
			component.cycle(currentTime);
		}
	}
	
	public void cycleChannels(long currentTime) {
		for (Channel channel : channels) {
			channel.cycle(currentTime);
		}
	}
	
	public void beginEpoch(long time) {
		// empty
	}
	
	public void endEpoch(long time) {
		// empty
	}
	
	public void run() {
		while (clock.isTimeLeft()) {
			long time = clock.getTime();
			beginEpoch(time);
			cycleComponents(time);
			cycleChannels(time);
			endEpoch(time);
			clock.tick();
		}
	}
}
