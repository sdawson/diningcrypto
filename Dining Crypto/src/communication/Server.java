package communication;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private static final int PORT = 9876;
	private static ArrayList<ClientSocketInfo> clients = new ArrayList<ClientSocketInfo>();
	
	public static void main(String[] args) throws IOException {
		ServerConnection connection = new ServerConnection(PORT);
		
		clients.add(connection.acceptConnection());

		Message stuff = connection.receive(clients.get(0));
		System.out.println("received " + stuff.getMessage());
		Message finalMessage = new Message("working recv/send server connection.");
		connection.send(finalMessage, clients.get(0));

	}
}
