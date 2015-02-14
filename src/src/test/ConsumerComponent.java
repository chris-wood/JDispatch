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
		// TODO Auto-generated method stub
	}

	@Override
	protected void processInputEvent(Event event, long time) {
		System.out.println("Consumer " + identity + " received " + event + " at time " + time);
	}
	
}