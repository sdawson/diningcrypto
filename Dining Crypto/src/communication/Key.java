package communication;

import java.security.SecureRandom;

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
