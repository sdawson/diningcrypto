package communication;

import java.io.Serializable;
import java.util.Set;

public class KeySet implements Serializable {
	private static final long serialVersionUID = 5671467559046189587L;
	private Set<Key> keys;
	
	public KeySet() {
		keys = null;
	}
	
	public void addKey(Key key) {
		keys.add(key);
	}
	
	public Set<Key> getKeySet() {
		return keys;
	}

}
