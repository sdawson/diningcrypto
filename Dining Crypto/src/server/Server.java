package server;

import java.io.IOException;
import java.util.ArrayList;

import communication.Message;

/**
 * The main server class.  This class simply
 * waits for a given number of clients to connect,
 * then starts client-server communication with
 * each of the clients in a separate thread.
 * 
 * @author Sophie Dawson
 *
 */
public class Server {
	private static final int PORT = 9876;
	private static final int MAXCLIENTS = 1;
	
	public static void main(String[] args) throws IOException {
		ServerConnection connection;
		if (args.length == 1) {
			connection = new ServerConnection(new Integer(args[0]).intValue());
		} else {
			connection = new ServerConnection(PORT);
		}
		System.out.println("Server initialized on port " + PORT);
		ArrayList<ClientSocketInfo> clients = new ArrayList<ClientSocketInfo>();
		int noOfReplies = 0;
		SharedServerInfo serverInfo = new SharedServerInfo(noOfReplies, 
				MAXCLIENTS, new ArrayList<Message>());
		
		/* Only accept MAXCLIENTS clients */
		for (int i=0; i<MAXCLIENTS; i++) {
			System.out.println((i+1) + " client connected.");
			clients.add(connection.acceptConnection());
		}
		
		for (int i=0; i<MAXCLIENTS; i++) {
			System.out.println("Server starting thread " + i);
			new ServerThread(clients, i, serverInfo).start();
		}
	}
}
