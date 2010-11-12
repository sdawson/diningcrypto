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
		Message stuff;
		Message ack = new Message("OK");
		System.err.println("In a server thread (right at the start)");
		try {
			ClientSocketInfo clientConnection = clients.get(clientID);
			// Acknowledge first connection, so that the client can keep sending
			// more messages
			stuff = clientConnection.receive();
			clientConnection.send(ack);
			while (true) {
				stuff = clientConnection.receive();
				if (stuff.getMessage().equals("KILL"))
					break;
				System.out.println("received " + stuff.getMessage());
				ArrayList<ClientSocketInfo> otherClients =
					new ArrayList<ClientSocketInfo>(clients);
				otherClients.remove(clientID);
				// Send message to all other clients
				for (ClientSocketInfo c : otherClients) {
					// Broadcast the message just received
					c.send(stuff);
				}
			}
			System.out.println("Outside while loop");
			Message finalMessage = new Message("END");
			for (ClientSocketInfo c : clients) {
				System.err.println("sending a shutdown message");
				c.send(finalMessage);
			}
			System.out.println("Sent all shutdowns...exiting");
			// Close socket connections for this client
			clientConnection.close();
			//System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
