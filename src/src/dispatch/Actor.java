package dispatch;

public interface Actor {
	public void cycle(long currentTime);
	public String getIdentity();
}
