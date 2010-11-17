package server;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

import communication.CommunicationProtocol;
import communication.KeySet;
import communication.Message;

/**
 * The ServerThread class represents
 * @author soph
 *
 */
public class ServerThread extends Thread {
	private ArrayList<ClientSocketInfo> clients = null;
	private int clientID;
	private SharedServerInfo sharedInfo;
	
	public ServerThread(ArrayList<ClientSocketInfo> clients, int clientID,
			SharedServerInfo sharedInfo) {
		this.clients = clients;
		this.clientID = clientID;
		this.sharedInfo = sharedInfo;
	}
	
	public void run() {
		Message output = null;
		int count = 0;
		ClientSocketInfo clientConnection = clients.get(clientID);
		
		/* Want to start doing message passing rounds once a decent number (3 for a start)
		 * of clients have actually connected.  This limit can be changed, but need
		 * to stop letting them in at some stage so that the server can calculate
		 * key-pairs etc.
		 */
		try {
			// Generate the initial set of keysets.
			sharedInfo.generateKeySets();

			while (true) {
				// Assumes that all the clients have connected already
				// Send keys to the client that is connected here
				// only if the client hasn't already received a keyset

				//System.out.println("Sending keys to client " + clientID);
				if (sharedInfo.getReplies() < sharedInfo.getNumberClients())
					sendKeys(clientConnection);
				//System.out.println("Sent keys to client " + clientID);
				
				/* All the threads have received their set of keys for the round,
				 * so reset the reply count if it hasn't
				 * already been done by another thread.
				 */
				if (sharedInfo.getReplies() != 0) {
					sharedInfo.resetReplies();
				}

				// Start the round at this point
				sendStartRound(clientConnection);
				
				// Wait until all clients have send a message back
				if (sharedInfo.getNoOfMessages() < sharedInfo.getNumberClients())
					// Wait for the client to send an output for
					// the round back
					output = clientConnection.receiveMessage();
				
				if (output != null) {
					// If an output was received by the connected client
					// add it to the shared output ArrayList, as long
					// as it wasn't an exit command.
					if (output.getMessage().equals(CommunicationProtocol.CLIENT_EXIT))
						break;
					sharedInfo.add(output);
				}
				
				// Then send all the resulting messages for the round back to the client				
				// controlled by this thread.				
				if (sharedInfo.getReplies() < sharedInfo.getNumberClients())
					sendResults(clientConnection);
				
				if (sharedInfo.getReplies() != 0)
					sharedInfo.resetReplies();

				if (sharedInfo.getCurrentRoundMessages().size() > 0)
					sharedInfo.resetRoundMessages();
				count++;
			}
			Message finalMessage = new Message(CommunicationProtocol.SHUTDOWN);
			for (ClientSocketInfo c : clients) {
				System.err.println("sending a shutdown message");
				c.send(finalMessage);
			}
			System.out.println("Sent all shutdowns...exiting");
			// Close socket connections for this client
			clientConnection.close();
			System.exit(0);
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
		// Get the keyset for this round
		KeySet key = sharedInfo.getKeySet();
		
		// Send the KeySet to the client
		client.send(key);

		Message reply = client.receiveMessage();
		if (reply.getMessage().equals(CommunicationProtocol.ACK)) {
			// The keyset was received by the client correctly
			sharedInfo.incrementReplies();
		} else {
			// The keyset was not received correctly (needs to
			// be resent)
			
			System.err.println("Failed to send keys to the client");
			System.exit(0);
		}
	}
	
	private void sendResults(ClientSocketInfo client) throws IOException {
		// Get the results for this round
		ArrayList<Message> results = new ArrayList<Message>(sharedInfo.getCurrentRoundMessages());
		
		// Send the results to the client
		client.send(results);

		Message reply = client.receiveMessage();
		if (reply.getMessage().equals(CommunicationProtocol.ACK)) {
			// The results were received by the client correctly
			sharedInfo.incrementReplies();
		} else {
			// The results were not received correctly (needs to
			// be resent?)
			System.err.println("Failed to send results to the client");
			System.exit(0);
		}
	}

	private void sendStartRound(ClientSocketInfo client) throws IOException {
		client.send(new Message(CommunicationProtocol.START_ROUND));
	}
}
