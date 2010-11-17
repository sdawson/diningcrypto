package communication;

import java.io.Serializable;

/**
 * A enum representing the two possible operations that
 * can be associated with a key when it is applied to
 * a message submitted to a client.
 * @author Sophie Dawson
 *
 */
public enum Keyop implements Serializable {
	ADD,
	SUBTRACT;
}
