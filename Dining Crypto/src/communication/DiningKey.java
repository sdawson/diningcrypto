package communication;

import java.io.Serializable;

import utility.RandomGenerator;

/**
 * Represents a single key that is shared between two clients.
 * The details of the client pair the key refers to are not
 * stored within the Key object, since it is used by the client,
 * and the client should not know this information.
 * @author Joshua Torrance
 *
 */
public class DiningKey implements Serializable {
	private static final long serialVersionUID = -709609931265527197L;
	
	private final int key;
	private final DiningKeyOp op;
	
	public DiningKey(int key, DiningKeyOp op) {
		this.key = key;
		this.op = op;
	}
	
	public int getKey() {
		return this.key;
	}
	
	public DiningKeyOp getKeyop() {
		return this.op;
	}
	
	/**
	 * Generates a random key with a key operation of null.
	 */
	public static DiningKey generateRandomKey() {
		return new DiningKey(RandomGenerator.generateInt(), null);
	}
	
	public String toString() {
		String str = "(" + key + ",";
		if (op == DiningKeyOp.ADD)
			str = str + "+";
		else
			str = str + "-";
		
		return str + ")";
	}
}
