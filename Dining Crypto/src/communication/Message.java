package communication;

import java.io.Serializable;
import java.util.List;

/**
 * A serializable object containing a message that can
 * be passed between the clients and the server.  Messages
 * will either contain protocol strings, to facilitate the
 * passing of instructions between the clients and server,
 * or encoded inputs from the client.
 * @author soph
 *
 */
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
