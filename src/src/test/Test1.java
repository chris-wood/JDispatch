package test;

import dispatch.Dispatcher;
import framework.ChannelInterface;

public class Test1 {
	
	public static void main(String[] args) {
		Dispatcher dispatcher = new Dispatcher(10L);
		
		ConsumerComponent consumer = new ConsumerComponent("C", dispatcher);
		ProducerComponent producer = new ProducerComponent("P", dispatcher);
		ChannelInterface consumerInterface = new ChannelInterface("C-NIC");
		ChannelInterface producerInterface = new ChannelInterface("P-NIC");
//		Channel queue = new Channel("channel");
		
		dispatcher.addComponent(consumer);
		dispatcher.addComponent(producer);
		
		try {
//			consumer.addDuplexQueue("NIC", queue);
//			producer.addDuplexQueue("NIC", queue);
			consumer.addChannelInterface(consumerInterface.getIdentity(), consumerInterface);
			producer.addChannelInterface(producerInterface.getIdentity(), producerInterface);
			consumerInterface.connect(producerInterface);
			
			dispatcher.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
