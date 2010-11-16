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
	// Individual message chars are just stored as offsets
	private char indMessage;
	private static final int base = (int) '@'; // @ represents no message
	private static final int alphaSize = 27;
	private int noOfTrips;

	public Message(String message) {
		this.message = message;
		this.noOfTrips = 0;
	}

	public Message(char indMessage) {
		this.indMessage = (char) (indMessage - base);
	}

	public String getMessage() {
		return this.message;
	}

	public char getIndMessage() {
		return (char) (this.indMessage + base);
	}

	public void encode(KeySet keys) {
		for (Key k : keys.getKeySet()) {
			int keyOffset = k.getKey() - base;
			if (k.getKeyop() == Keyop.ADD) {
				System.out.println("key offset " + keyOffset);
				System.out.println("added key offset " + (this.indMessage + keyOffset));
				this.indMessage = (char) ((this.indMessage + keyOffset) % alphaSize);
				System.out.println("final message (with unneccessary +base) " + (int) this.indMessage);
			} else if (k.getKeyop() == Keyop.SUBTRACT) {
				System.out.println("subtraction with mod " + ((this.indMessage - keyOffset) % alphaSize));
				System.out.println("subtraction, mod, +base " + (((this.indMessage - keyOffset) + alphaSize) % alphaSize));
				System.out.println("subtraction, mod, +base " + (((7 - 5) + alphaSize) % alphaSize));
				this.indMessage = (char) (((this.indMessage - keyOffset) + alphaSize) % alphaSize);
			} else {
				System.err.println("Error: Invalide encoding operation requested.");
				System.exit(1);
			}
		}
	}

	public char combineMessages(List<Character> messages) {
		char temp = this.indMessage;
		for (Character m : messages) {
			int offset = m.charValue() - base;
			temp = (char) (((temp + offset) + alphaSize) % alphaSize);
		}
		return (char) (temp + base);
	}

	public synchronized void increment() {
		this.noOfTrips++;
	}

	public int getTrips() {
		return this.noOfTrips;
	}
}
