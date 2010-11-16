package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import communication.KeySet;
import communication.Message;

public class Server {
	private static final int PORT = 9876;
	private static final int MAXCLIENTS = 2;
	
	public static void main(String[] args) throws IOException {
		ServerConnection connection = new ServerConnection(PORT);
		ArrayList<ClientSocketInfo> clients = new ArrayList<ClientSocketInfo>();
		HashMap<ClientSocketInfo, KeySet> keysets =
			new HashMap<ClientSocketInfo, KeySet>();
		int noOfReplies = 0;
		SharedServerInfo serverInfo = new SharedServerInfo(noOfReplies, 
				MAXCLIENTS, new ArrayList<Message>());
		
		/* Only accept three clients */
		for (int i=0; i<MAXCLIENTS; i++) {
			clients.add(connection.acceptConnection());
		}
		
		// TODO: Generate a set of keys for each client.
		
		for (int i=0; i<MAXCLIENTS; i++) {
			new ServerThread(clients, i, keysets,
					serverInfo).start();
		}
	}
}
