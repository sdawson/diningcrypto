package utility;

public class ByteUtil {
	public static int bytesToInt(byte[] array) {
		return (new Byte(array[0])).intValue() +
			(new Byte(array[1])).intValue()*Byte.MAX_VALUE +
			(new Byte(array[1])).intValue()*Byte.MAX_VALUE*Byte.MAX_VALUE +
			(new Byte(array[1])).intValue()*Byte.MAX_VALUE*Byte.MAX_VALUE*Byte.MAX_VALUE;
	}
	
	public static byte[] intToBytes(int i) {
		byte[] array = new byte[4];
		array[0] = (byte)i;
		array[1] =(byte)(i / Byte.MAX_VALUE);
		array[2] =(byte)(i / (Byte.MAX_VALUE * Byte.MAX_VALUE));
		array[3] =(byte)(i / (Byte.MAX_VALUE * Byte.MAX_VALUE * Byte.MAX_VALUE));
		
		return array;
	}
}
