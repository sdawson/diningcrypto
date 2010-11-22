package communication;

import java.io.Serializable;

import utility.ByteUtil;
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
	
	private byte[] key = null;
	private final DiningKeyOp op;
	
	public DiningKey(int key, DiningKeyOp op) {
		this.setKey(key);
		this.op = op;
	}
	
	public DiningKey(byte[] key, DiningKeyOp op) {
		this.key = key;
		this.op = op;
	}
	
	public DiningKey(DiningKey dk) {
		this.setKey(dk.getKey());
		this.op = dk.getKeyOp();
	}
	
	public int getKey() {
		return ByteUtil.bytesToInt(this.key);
	}
	
	public void setKey(int key) {
		this.key = ByteUtil.intToBytes(key);
	}
	
	public byte[] getKeyAsBytes() {
		return this.key;
	}
	
	public void setKeyAsBytes(byte[] key) {
		this.key = key;
	}
	
	public DiningKeyOp getKeyOp() {
		return this.op;
	}
	
	/**
	 * Generates a random key with a key operation of null.
	 */
	public static DiningKey generateRandomKey() {
		return new DiningKey(RandomGenerator.generateInt(), null);
	}
	
	public String toString() {
		String str = "(" + getKey() + ",";
		if (op == DiningKeyOp.ADD)
			str = str + "+";
		else
			str = str + "-";
		
		return str + ")";
	}
}
