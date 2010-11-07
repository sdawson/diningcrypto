package communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerProtocol {
	// Connection information needed to run protocol
	private ServerConnection connection = null;
	private ArrayList<ClientSocketInfo> clients = null;
	private HashMap<ClientSocketInfo, KeySet> clientKeys = null;
	
	// Server-side system states
	private static final int INITIAL = 0;
	private static final int SENTKEYS = 1; // Bypassed by server connection abstraction. Remove
	private static final int STARTROUND = 2;
	private static final int WAITFOROUTPUT = 3;
	private static final int BROADCASTINGRESULT = 4;

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
	
	// Do i even need to return strings at this point??
	// This function could possibly just be called repeatedly
	// in a while(readline() != null) { processInputToServer(inputLine) } etc
	public void processInputToServer(String input) {
		String output = null;
		
		if (serverState == INITIAL) {
			for (ClientSocketInfo c : clients) {
				clientKeys.put(c, new KeySet());
				// Really want to generate a non-empty key set here
				// (assuming a ring structure for keys, where the
				// arraylist is treated as a circle of participants)
			}
			try {
				connection.sendKeysWithAck(clientKeys);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			serverState = SENTKEYS;
		} else if (serverState == SENTKEYS) {
			
		}
	}

	
	public String processInputToClient(String input) { // Will split this into a different class
		return null;
	}
}
