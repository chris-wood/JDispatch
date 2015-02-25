package dispatch.test;

import dispatch.Dispatcher;
import dispatch.channel.Channel;
import dispatch.channel.ChannelInterface;

public class Test1 {
	
	public static void main(String[] args) {
		Dispatcher dispatcher = new Dispatcher(3L);
		
		ConsumerComponent consumer = new ConsumerComponent("C", dispatcher); 
		ProducerComponent producer = new ProducerComponent("P", dispatcher);
		
		Channel channel = new Channel("link1");
		dispatcher.addChannel(channel);
		
		ChannelInterface consumerInterface = new ChannelInterface("C-NIC");
		ChannelInterface producerInterface = new ChannelInterface("P-NIC");
		
		consumerInterface.setOutputChannel(channel);
		consumerInterface.setInputChannel(channel);
		producerInterface.setInputChannel(channel);
		producerInterface.setOutputChannel(channel);
		
		try {
			consumer.addChannelInterface(consumerInterface.getIdentity(), consumerInterface);
			producer.addChannelInterface(producerInterface.getIdentity(), producerInterface);
			
			dispatcher.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
