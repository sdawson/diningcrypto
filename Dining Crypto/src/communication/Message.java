package communication;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = -4317983621836587347L;
	private String message;
	// Individual message chars are just stored as offsets
	private char indMessage;
	private static final int base = (int) '@'; // @ represents no msg
	private static final int alphaSize = 27;
	
	public Message(String message) {
		this.message = message;
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
	
	public void encode(char key, Keyop op) {
		int keyOffset = key - base;
		if (op == Keyop.ADD) {
			System.out.println("key offset " + keyOffset);
			System.out.println("added key offset " + (this.indMessage + keyOffset));
			this.indMessage = (char) ((this.indMessage + keyOffset) % alphaSize);
			System.out.println("final message (with unneccessary +base) " + (int) this.indMessage);
		} else if (op == Keyop.SUBTRACT) {
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
