package communication;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = -4317983621836587347L;
	private String message;
	
	public Message(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
