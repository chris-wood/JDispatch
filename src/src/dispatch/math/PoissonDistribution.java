package dispatch.math;

import java.util.Random;

public class PoissonDistribution extends Distribution {

	private long rate;
	private Random prng = new Random();
	
	public PoissonDistribution(long arrivalRate) {
		rate = arrivalRate;
	}
	
	@Override
	public long sample() {
		double L = Math.exp(-1 * rate);
		double p = 1;
		int k = 0;
		
		do {
			k = k + 1;
			double u = prng.nextDouble();
			p = p * u;			
		} while (p > L);
		
		return (long) k;
	}

}
