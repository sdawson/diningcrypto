package communication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {
	private static final long serialVersionUID = -4317983621836587347L;
	private String message;
	// Individual message chars are just stored as offsets
	private char indMessage;
	private final ArrayList<Key> keys;
	private static final int base = (int) '@'; // @ represents no msg
	private static final int alphaSize = 27;
	
	public Message(String message) {
		this.message = message;
		this.keys = null;
	}
	
	public Message(char indMessage, ArrayList<Key> keys) {
		this.indMessage = (char) (indMessage - base);
		this.keys = keys;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public char getIndMessage() {
		return (char) (this.indMessage + base);
	}
	
	public void encode() {
		for (Key k : keys) {
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
}
