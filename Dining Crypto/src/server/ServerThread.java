package server;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

import communication.CommunicationProtocol;
import communication.KeySet;
import communication.Message;

/**
 * The ServerThread class represents
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
				// Send the keys for this round to the client.
				sendKeys(clientConnection);

				// Start the round at this point
				sendStartRound(clientConnection);

				// Wait for the client to send output for
				// the round back
				output = clientConnection.receiveMessage();
					
				if (output != null) {
					// If an output was received by the connected client
					// add it to the shared output ArrayList, as long
					// as it wasn't the exit command.
					if (output.getMessage().equals(CommunicationProtocol.CLIENT_EXIT)) {
						break;
					} else {
						sharedInfo.addOutput(output);
						
						// Clear output
						output = null;
					}
				} else {
					System.out.println("Output is null.");
					System.exit(0);
				}
				
				// Wait until all clients have send their output back
				synchronized (sharedInfo) {
					if (sharedInfo.getNoOfMessages() < sharedInfo.getNumberClients()) {
						try {
							sharedInfo.wait();
						} catch (InterruptedException e) {/* Continue */}
					} else {
						sharedInfo.notifyAll();
					}
				}
				
				// Then send all the outputs for the round back to the client.
				sendResults(clientConnection);
				
				// Wait until all clients have been send the collection of outputs.
				synchronized (sharedInfo) {
					sharedInfo.incrementSent();
					if (sharedInfo.getSent() < sharedInfo.getNumberClients()) {
						try {
							sharedInfo.wait();
						} catch (InterruptedException e) {/* Continue */}
					} else {
						sharedInfo.resetRoundMessages();
						sharedInfo.resetSent();
						sharedInfo.notifyAll();
					}
				}
			}
			
			Message finalMessage = new Message(CommunicationProtocol.SHUTDOWN);
			for (ClientSocketInfo c : clients) {
				System.err.println("Sending a shutdown message");
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
			return;
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
			return;
		} else {
			// The results were not received correctly
			System.err.println("Failed to send results to the client");
			System.exit(0);
		}
	}

	private void sendStartRound(ClientSocketInfo client) throws IOException {
		client.send(new Message(CommunicationProtocol.START_ROUND));
	}
}
