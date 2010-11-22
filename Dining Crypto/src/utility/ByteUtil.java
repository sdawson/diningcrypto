package utility;

import java.util.ArrayList;

public class ByteUtil {
	public static int bytesToInt(byte[] array) {
		return (array[0] << 24) +
			((array[1] & 0xff) << 16) +
			((array[2] & 0xff) << 8) +
			(array[3] & 0xff);
	}
	
	public static byte[] intToBytes(int i) {
		return new byte[] {
				(byte)(i >>> 24),
				(byte)(i >>> 16),
				(byte)(i >>> 8),
				(byte)i };
	}
	
	public static byte[] arrayListToArray(ArrayList<Byte> arrList) {
		byte[] array = new byte[arrList.size()];
		
		int i=0;
		for (Byte b : arrList) {
			array[i] = b.byteValue();
			i++;
		}
		
		return array;
	}
}
