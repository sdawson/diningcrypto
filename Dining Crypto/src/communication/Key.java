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
public class Key implements Serializable {
	private static final long serialVersionUID = -709609931265527197L;
	
	private final int key;
	private final Keyop op;
	
	public Key(int key, Keyop op) {
		this.key = key;
		this.op = op;
	}
	
	public int getKey() {
		return this.key;
	}
	
	public Keyop getKeyop() {
		return this.op;
	}
	
	/**
	 * Generates a random key with a key operation of null.
	 */
	public static Key generateRandomKey() {
		return new Key(RandomGenerator.generateInt(), null);
	}
}
