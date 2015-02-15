package test;

import dispatch.Dispatcher;
import framework.Component;
import framework.Event;

class ProducerComponent extends Component {

	public ProducerComponent(String identity, Dispatcher dispatcher) {
		super(identity, dispatcher);
	}

	@Override
	protected void processInputEventFromInterface(String interfaceId, Event event, long time) {
		System.out.println("Producer " + identity + " received " + event + " at time " + time);
		event.setProcessed();
	}

	@Override
	protected void runComponent(long time) {
		Event event = new Event();
		System.out.println("Producer " + identity + " is sending the event " + event + " at time " + time);	
		broadcastEvent(event);
	}
	
}