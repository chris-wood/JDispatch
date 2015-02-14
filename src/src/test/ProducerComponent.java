package test;

import math.Distribution;
import math.UniformDistribution;
import dispatch.Dispatcher;
import framework.Component;
import framework.Event;

class ProducerComponent extends Component {

	public ProducerComponent(String identity, Dispatcher dispatcher) {
		super(identity, dispatcher);
	}

	@Override
	protected void processInputEvent(Event event, long time) {
	}

	@Override
	protected void runComponent(long time) {
		Event event = new Event();
		System.out.println("Producer " + identity + " is sending the event " + event + " at time " + time);	
		broadcastEvent(event);
	}
	
}