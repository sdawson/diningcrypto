package communication;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerThread extends Thread {
	private ArrayList<ClientSocketInfo> clients = null;
	private HashMap<ClientSocketInfo, KeySet> keys = null;
	private int clientID;
	private SharedServerInfo sharedInfo;
	
	public ServerThread(ArrayList<ClientSocketInfo> clients, int clientID,
			HashMap<ClientSocketInfo, KeySet> keys,
			SharedServerInfo sharedInfo) {
		this.clients = clients;
		this.clientID = clientID;
		this.sharedInfo = sharedInfo;
		this.keys = keys;
	}
	
	public void run() {
		Message output;
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
				sendKeys(clientConnection);
				while (sharedInfo.getReplies() < sharedInfo.getMaxClients())
					;
				/* All the threads have received their set of keys for the round,
				 * so reset the reply count if it hasn't already been done by another
				 * thread.
				 */
				if (sharedInfo.getReplies() != 0) {
					sharedInfo.resetReplies();
				}

				// Start the round at this point
				sendStartRound(clientConnection);
				// Wait for the client to send an output for the
				// round back
				output = clientConnection.receiveMessage();

				if (output.getMessage().equals(CommunicationProtocol.CLIENTEXIT))
					break;

				sharedInfo.add(output);
				// incrementing is auto taken care of
				// by keeping track of the size of the message array for
				// the current round
				while (sharedInfo.getNoOfMessages() < sharedInfo.getMaxClients())
					; // Wait until all clients have send a message back
				// need to collate all the replies here
				if (sharedInfo.getRoundResult() == null) {
					// TODO: collation goes here
					sharedInfo.setRoundResult(new Message("The result"));
				}
				// broadcasting the message back to the client
				// controlled by this thread.
				if (sharedInfo.getReplies() < sharedInfo.getMaxClients()) {
					sendResult(clientConnection);
				} else {
					sharedInfo.resetReplies();
				}
			}
			Message finalMessage = new Message(CommunicationProtocol.SHUTDOWN);
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
		if (reply.getMessage().equals(CommunicationProtocol.ACK)) {
			// The keyset was received by the client correctly
			sharedInfo.incrementReplies();
		} else {
			// The keyset was not received correctly (needs to
			// be resent TODO)
			return;
		}
	}
	
	private void sendResult(ClientSocketInfo client) throws IOException {
		client.send(sharedInfo.getRoundResult());
		
		Message reply = client.receiveMessage();
		if (reply.getMessage().equals(CommunicationProtocol.ACK)) {
			sharedInfo.incrementReplies();
		} else {
			return;  // probably want to reset here instead
		}
	}

	/* TODO: Change hardcoded strings */
	private void sendStartRound(ClientSocketInfo client) throws IOException {
		client.send(new Message(CommunicationProtocol.STARTROUND));
	}
}
