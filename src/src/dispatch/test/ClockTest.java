package dispatch.test;

import static org.junit.Assert.*;

import org.junit.Test;

import dispatch.Clock;

public class ClockTest {

	@Test
	public void testTicks() {
		Clock clock = new Clock(10L);
		
		for (int i = 0; i < 5; i++) {
			clock.tick();
		}
		
		assertTrue("Expected time to be remaining", clock.isTimeLeft());
		assertTrue("Expected time to be equal to 5, got " + clock.getTime(), clock.getTime() == 5L);
		
		for (int i = 0; i < 5; i++) {
			clock.tick();
		}
		
		assertFalse("Expected time to be expired", clock.isTimeLeft());
	}

}
