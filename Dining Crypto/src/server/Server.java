package server;

import java.io.IOException;
import java.util.ArrayList;

import communication.Message;

public class Server {
	private static final int PORT = 9876;
	private static final int MAXCLIENTS = 1;
	
	public static void main(String[] args) throws IOException {
		//TODO: Add some feedback
		ServerConnection connection = new ServerConnection(PORT);
		ArrayList<ClientSocketInfo> clients = new ArrayList<ClientSocketInfo>();
		int noOfReplies = 0;
		SharedServerInfo serverInfo = new SharedServerInfo(noOfReplies, 
				MAXCLIENTS, new ArrayList<Message>());
		
		/* Only accept three clients */
		for (int i=0; i<MAXCLIENTS; i++) {
			clients.add(connection.acceptConnection());
		}
		
		for (int i=0; i<MAXCLIENTS; i++) {
			new ServerThread(clients, i, serverInfo).start();
		}
	}
}
