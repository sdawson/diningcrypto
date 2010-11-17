package utility;

import java.security.SecureRandom;

public class RandomGenerator {
	public static int generateInt() {
		SecureRandom rand = new SecureRandom();
		
		byte[] b = new byte[4];
		rand.nextBytes(b);
		
		return  (new Byte(b[0])).intValue() +
				(new Byte(b[1])).intValue()*Byte.MAX_VALUE +
				(new Byte(b[1])).intValue()*Byte.MAX_VALUE*Byte.MAX_VALUE +
				(new Byte(b[1])).intValue()*Byte.MAX_VALUE*Byte.MAX_VALUE*Byte.MAX_VALUE;
	}

}
