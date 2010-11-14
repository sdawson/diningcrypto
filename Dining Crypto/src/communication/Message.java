package communication;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = -4317983621836587347L;
	private String message;
	private int noOfTrips; // No of clients the message has visited.
	
	public Message(String message) {
		this.message = message;
		this.noOfTrips = 0;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public synchronized void increment() {
		this.noOfTrips++;
	}
	
	public int getTrips() {
		return this.noOfTrips;
	}
}
