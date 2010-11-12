package communication;

import java.io.IOException;
import java.util.ArrayList;

public class Server {
	public static final int PORT = 9876;
	
	public static void main(String[] args) throws IOException {
		ServerConnection connection = new ServerConnection(PORT);
		ArrayList<ClientSocketInfo> clients = new ArrayList<ClientSocketInfo>();
		int count = 0;
		
		while (true) {
			clients.add(connection.acceptConnection());
			new ServerThread(clients, count).start();
			count++;
		}
	}
}
