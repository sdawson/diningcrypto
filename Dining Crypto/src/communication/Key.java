package communication;

import java.security.SecureRandom;

/**
 * Represents a single key that is shared between two clients.
 * The details of the client pair the key refers to are not
 * stored within the Key object, since it is used by the client,
 * and the client should not know this information.
 * @author Joshua Torrance
 *
 */
public class Key {
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
		SecureRandom rand = new SecureRandom();
		
		byte[] b = new byte[4];
		rand.nextBytes(b);
		
		int i = (new Byte(b[0])).intValue() +
				(new Byte(b[1])).intValue()*Byte.MAX_VALUE +
				(new Byte(b[1])).intValue()*Byte.MAX_VALUE*Byte.MAX_VALUE +
				(new Byte(b[1])).intValue()*Byte.MAX_VALUE*Byte.MAX_VALUE*Byte.MAX_VALUE;
		
		return new Key(i, null);
	}
}
