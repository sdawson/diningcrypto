package server;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

import communication.CommunicationProtocol;
import communication.KeySet;
import communication.Message;

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

				System.out.println("Sending keys to client " + clientID);
				if (sharedInfo.getReplies() < sharedInfo.getMaxClients())
					sendKeys(clientConnection);
				System.out.println("Sent keys to client " + clientID);
				
				/*while (sharedInfo.getReplies() < sharedInfo.getMaxClients())
					ServerThread.sleep(1000);
				*/
				
				/* All the threads have received their set of keys for the round,
				 * so reset the reply count if it hasn't
				 * already been done by another thread.
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
				while (sharedInfo.getNoOfMessages() < sharedInfo.getNumberClients())
					; // Wait until all clients have send a message back
				// need to collate all the replies here
				if (sharedInfo.getRoundResult() == null) {
					// TODO: collation goes here
					sharedInfo.setRoundResult(new Message("The result"));
				}
				
				// broadcasting the message back to the client
				// controlled by this thread.
				sendResult(clientConnection);
				while (sharedInfo.getReplies() < sharedInfo.getMaxClients())
					;
				
				if (sharedInfo.getReplies() != 0)
					sharedInfo.resetReplies();
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
			
			sendKeys(client);
		}
	}
	
	private void sendResult(ClientSocketInfo client) throws IOException {
		client.send(sharedInfo.getRoundResult());
		
		Message reply = client.receiveMessage();
		if (reply.getMessage().equals(CommunicationProtocol.ACK)) {
			System.out.println("Incrementing no of replies");
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
