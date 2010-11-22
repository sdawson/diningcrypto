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
	
	public static void main(String[] args) throws IOException {
		ServerConnection connection;
		
		if (args.length == 1) {
			connection = new ServerConnection(new Integer(args[0]).intValue());
		} else {
			connection = new ServerConnection(PORT);
		}
		
		System.out.println("Server initialized on port " + PORT);
		
		int noOfReplies = 0;
		SharedServerInfo serverInfo = new SharedServerInfo(noOfReplies, new ArrayList<Message>());
		
		/* Only accept MAXCLIENTS clients */
		while (true) {
			serverInfo.addClient(connection.acceptConnection());
			System.out.println("Client connected.");
		}
		
		/*while (true) {
			serverInfo.addClient(connection.acceptConnection());
		}*/
	}
}
