package communication;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

public class ServerThread extends Thread {
	private ArrayList<ClientSocketInfo> clients = null;
	private int clientID;
	private int maxClients;
	
	public ServerThread(ArrayList<ClientSocketInfo> clients, int clientID,
			int maxClients) {
		this.clients = clients;
		this.clientID = clientID;
		this.maxClients = maxClients;
	}
	
	public void run() {
		Message stuff;
		Message ack = new Message("OK");
		System.err.println("In a server thread (right at the start)");
		ClientSocketInfo clientConnection = clients.get(clientID);
		System.out.println("perceived size of client socket array: " + clients.size());
		/* Want to start doing message passing rounds once a decent number (3 for a start)
		 * of clients have actually connected.  This limit can be changed, but need
		 * to stop letting them in at some stage so that the server can calculate
		 * key-pairs etc.
		 */
		try {
			// Acknowledge first connection, so that the client can keep sending
			// more messages
			stuff = clientConnection.receive();
			clientConnection.send(ack);
			while (true) {
				stuff = clientConnection.receive();
				if (stuff.getMessage().equals("KILL"))
					break;

				if (clients.size() == maxClients) {
					// send the message around the chain
					// to the client above your own number
					// (so that the sockets don't duplicate the sending
					System.out.println("received " + stuff.getMessage());
					if ((clientID + 1) == clients.size()) {
						// Need to send back to the first client (making array circular)
						clients.get(0).send(stuff);
						System.out.println("Thread " + clientID + " sending to client 0");
					} else {
						clients.get(clientID+1).send(stuff);
						System.out.println("Thread " + clientID + " sending to client " + clientID+1);
					}
				} else {
					// Keep waiting until all the clients have connected
					continue;
				}
				/*if (stuff.getMessage().equals("KILL"))
					break;
				System.out.println("received " + stuff.getMessage());
				ArrayList<ClientSocketInfo> otherClients =
					new ArrayList<ClientSocketInfo>(clients);
				otherClients.remove(clientID);
				// Send message to all other clients
				for (ClientSocketInfo c : otherClients) {
					// Broadcast the message just received
					c.send(stuff);
				}*/
			}
			System.out.println("Outside while loop");
			Message finalMessage = new Message("END");
			System.out.println("perceived size of client socket array: " + clients.size());
			for (ClientSocketInfo c : clients) {
				System.err.println("sending a shutdown message");
				c.send(finalMessage);
			}
			System.out.println("Sent all shutdowns...exiting");
			// Close socket connections for this client
			clientConnection.close();
		} catch (EOFException e) {
			// Server has lost the client connection due to the
			// client disconnecting, so don't need to do anything
			// else
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
