package communication;

import java.io.IOException;
import java.util.ArrayList;

public class ServerThread extends Thread {
	private ArrayList<ClientSocketInfo> clients = null;
	private int clientID;
	
	public ServerThread(ArrayList<ClientSocketInfo> clients, int clientID) {
		this.clients = clients;
		this.clientID = clientID;
	}
	
	public void run() {
		System.err.println("In a server thread (right at the start)");
		try {
			ClientSocketInfo clientConnection = clients.get(clientID);
			Message stuff = clientConnection.receive();
			System.out.println("received " + stuff.getMessage());
			Message finalMessage = new Message("working recv/send server connection.");
			clientConnection.send(finalMessage);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
