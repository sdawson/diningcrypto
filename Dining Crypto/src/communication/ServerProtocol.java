package communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerProtocol {
	// Connection information needed to run protocol
	private ServerConnection connection = null;
	private ArrayList<ClientSocketInfo> clients = null;
	private HashMap<ClientSocketInfo, KeySet> clientKeys = null;
	private char result;
	
	// Server-side system states
	private static final int INITIAL = 0;
	private static final int STARTROUND = 1;
	private static final int WAITFOROUTPUT = 2;
	private static final int BROADCASTRESULT = 3;

	// Client-side system states
	private static final int WAITFORKEYS = 5;
	private static final int WAITFORROUND = 6;
	private static final int GENERATEOUTPUT = 7; // Need?
	private static final int WAITFORRESULT = 8;
	
	private int serverState = INITIAL;
	private int clientState = WAITFORKEYS;
	
	public ServerProtocol(ServerConnection connection, ArrayList<ClientSocketInfo> clients) {
		this.connection = connection;
		this.clients = clients;
	}
	
	// This function should just be called repeatedly
	// in a while(readline() != null) { processInputToServer(inputLine) } etc
	public void processInputToServer(String input) throws IOException {
		String output = null;
		
		if (serverState == INITIAL) {
			for (ClientSocketInfo c : clients) {
				clientKeys.put(c, new KeySet());
				// Really want to generate a non-empty key set here
				// (assuming a ring structure for keys, where the
				// arraylist is treated as a circle of participants)
			}
			connection.sendKeysWithAck(clientKeys);
			serverState = STARTROUND;
		} else if (serverState == STARTROUND) {
			// Sending a starting round message to every single client
			for (ClientSocketInfo c : clients) {
				connection.send(new Message("STARTROUND"), c);
			}
			serverState = WAITFOROUTPUT;
		} else if (serverState == WAITFOROUTPUT) {
			// Waits for all of the clients to return outputs,
			// then collates the inputs into a single result
			// (using the combineMessages function currently in Message)
			result = 'A'; // Just default for test
			serverState = BROADCASTRESULT;
		} else if (serverState == BROADCASTRESULT) {
			connection.sendOutputWithAck(clients,
					new Message(new Character(result).toString()));
			serverState = STARTROUND;
		} else {
			// Unknown server state, so just ignore
		}
	}

	
	public String processInputToClient(String input) { // Will split this into a different class
		return null;
	}
}
