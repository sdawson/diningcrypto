package communication;

public class Key {
	private final char key;
	private final Keyop op;
	
	public Key(char key, Keyop op) {
		this.key = key;
		this.op = op;
	}
	
	public char getKey() {
		return this.key;
	}
	
	public Keyop getKeyop() {
		return this.op;
	}
}
