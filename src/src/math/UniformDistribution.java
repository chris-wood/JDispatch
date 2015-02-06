package math;

import java.util.Random;

public class UniformDistribution extends Distribution {
	
	private long[] instances;
	private Random prng = new Random();
	
	public UniformDistribution(long a, long b) {
		instances = new long[(int)(b - a)];
		for (int i = 0; i < instances.length; i++) {
			instances[i] = a + i;
		}
	}
	
	public long sample() {
		int index = (int)Math.ceil(prng.nextDouble() * instances.length);
		return instances[index - 1];
	}

}
