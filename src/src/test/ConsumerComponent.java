package test;

import dispatch.Dispatcher;
import framework.Component;
import framework.Event;

class ConsumerComponent extends Component {

	public ConsumerComponent(String identity, Dispatcher dispatcher) {
		super(identity, dispatcher);
	}
	
	@Override
	protected void runComponent(long time) {
		Event event = new Event();
		System.out.println("Consumer " + identity + " is sending the event " + event + " at time " + time);	
		broadcast(event);
	}

	@Override
	protected void processInputEventFromInterface(String interfaceId, Event event, long time) {
		System.out.println("Consumer " + identity + " received " + event + " at time " + time);
		event.setProcessed();
	}
	
}