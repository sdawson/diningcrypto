package communication;

import java.io.IOException;
import java.util.ArrayList;

public class Server {
	private static final int PORT = 9876;
	private static final int MAXCLIENTS = 3;
	
	public static void main(String[] args) throws IOException {
		ServerConnection connection = new ServerConnection(PORT);
		ArrayList<ClientSocketInfo> clients = new ArrayList<ClientSocketInfo>();
		int count = 0;
		
		/* Only accept three clients */
		for (int i=0; i<MAXCLIENTS; i++) {
			clients.add(connection.acceptConnection());
			new ServerThread(clients, i, MAXCLIENTS).start();
			count++;
		}
	}
}
