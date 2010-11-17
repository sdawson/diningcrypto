package utility;


/**
 * 
 * @author joshuat
 *
 */
public class StrBuffer {
	private final static int DEFAULT_LENGTH = 10;
	
	private String[] array = null;
	private int length = DEFAULT_LENGTH;
	private int readPosition = 0, writePosition = 0;
	
	public StrBuffer(int bufferLength) {
		length = bufferLength;
		array = new String[length];
		
		for (int i=0 ; i<length ; i++) {
			array[i] = null;
		}
	}
	
	public StrBuffer() {
		this(DEFAULT_LENGTH);
	}
	
	public void add(String str) {
		array[writePosition] = str;
		writePosition++;
	}
	
	public String next() {
		String ret = array[readPosition];
		array[readPosition] = null;
		
		if (ret!=null) {
			readPosition++;
		}
		
		return ret;
	}
}
