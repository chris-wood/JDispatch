package dispatch;

public class Clock {
	public long time;
	public long endTime;
	
	public Clock(long endTime) {
		initialize(endTime);
	}
	
	private void initialize(long endTime) {
		time = 0L;
		this.endTime = endTime;
	}
	
	public long getTime() {
		return time;
	}
	
	public boolean isTimeLeft() {
		return (time < endTime);
	}
	
	public void tick() {
		time++;
	}
}
