package communication;

/**
 * Defines the set of messages that can be sent between the clients
 * and the server.
 * @author Sophie Dawson
 *
 */
public class CommunicationProtocol {
	/* A request from the client to the server asking for
	 * a keyset for the round that is about to begin.
	 */
	public static final String KEYREQUEST = "KEY";
	/* An acknowledgment message, usually from the client
	 * to the server.
	 */
	public static final String ACK = "OK";
	/* A message from the server to the clients indicating
	 * the start of a round.
	 */
	public static final String STARTROUND = "STARTROUND";
	/* A message from the client to the server indicating
	 * that the client has quit the session.
	 */
	public static final String CLIENTEXIT = "KILL";
	/* A message from the server to the clients indicating
	 * that the clients should quit.
	 */
	public static final String SHUTDOWN = "END";
}
