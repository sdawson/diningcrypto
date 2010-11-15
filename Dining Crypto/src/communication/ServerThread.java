package communication;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerThread extends Thread {
	private ArrayList<ClientSocketInfo> clients = null;
	private HashMap<ClientSocketInfo, KeySet> keys = null;
	private int clientID;
	private int maxClients;
	private int noOfReplies;
	
	public ServerThread(ArrayList<ClientSocketInfo> clients, int clientID,
			int maxClients, HashMap<ClientSocketInfo, KeySet> keys,
			int noOfReplies) {
		this.clients = clients;
		this.clientID = clientID;
		this.maxClients = maxClients;
		this.keys = keys;
		this.noOfReplies = noOfReplies;
	}
	
	public void run() {
		Message stuff;
		System.err.println("In a server thread (right at the start)");
		ClientSocketInfo clientConnection = clients.get(clientID);
		System.out.println("perceived size of client socket array: " + clients.size());
		/* Want to start doing message passing rounds once a decent number (3 for a start)
		 * of clients have actually connected.  This limit can be changed, but need
		 * to stop letting them in at some stage so that the server can calculate
		 * key-pairs etc.
		 */
		try {
			while (true) { // Assumes that all the clients have connected already
				// Send keys to the client that is connected here
				// only if the client hasn't already received a keyset
				if (noOfReplies < maxClients) {
					sendKeys(clientConnection);
				} else {
					resetReplies();
				}
				// Start the round at this point
				sendStartRound(clientConnection);
				// Wait for the client to send an output for the
				// round back
				stuff = clientConnection.receiveMessage();

				if (stuff.getMessage().equals("KILL"))
					break;

				incrementReplies();
				while (noOfReplies < maxClients)
					; // Wait until all clients have send a message back
				// need to collate all the replies here
				// if any of the messages are kill, then
				// send a shutdown message back
				// then broadcast
			}
			Message finalMessage = new Message("END");
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

	private void sendKeys(ClientSocketInfo client) throws IOException {
		// Get corresponding keyset, then send it to the
		// client
		KeySet clientKeys = keys.get(client);
		client.send(clientKeys);

		Message reply = client.receiveMessage();
		if (reply.getMessage().equals("OK")) {
			// The keyset was received by the client correctly
			incrementReplies();
		} else {
			// The keyset was not received correctly (needs to
			// be resent TODO)
			return;
		}
	}

	private void sendStartRound(ClientSocketInfo client) throws IOException {
		client.send(new Message("STARTROUND"));
	}

	private synchronized void incrementReplies() {
		this.noOfReplies++;
	}

	private synchronized void resetReplies() {
		this.noOfReplies = 0;
	}
}
