package utility;

import java.security.SecureRandom;

public class RandomGenerator {
	public static int generateInt() {
		SecureRandom rand = new SecureRandom();
		
		byte[] b = new byte[4];
		rand.nextBytes(b);
		
		return  ByteUtil.bytesToInt(b);
	}

}
