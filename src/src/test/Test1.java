package test;

import dispatch.Dispatcher;
import framework.Channel;

public class Test1 {
	
	public static void main(String[] args) {
		Dispatcher dispatcher = new Dispatcher(2L);
		
		ConsumerComponent consumer = new ConsumerComponent("C", dispatcher);
		ProducerComponent producer = new ProducerComponent("P", dispatcher);
		Channel consumerInterface = new Channel("C-NIC");
		Channel producerInterface = new Channel("P-NIC");
		
		dispatcher.addComponent(consumer);
		dispatcher.addComponent(producer);
		
		try {
			consumer.addChannelInterface(consumerInterface.getIdentity(), consumerInterface);
			producer.addChannelInterface(producerInterface.getIdentity(), producerInterface);
			consumerInterface.connect(producerInterface);
			
			dispatcher.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
