package server;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.security.Key;
import java.util.ArrayList;

import communication.CommunicationProtocol;
import communication.DiningKeySet;
import communication.Message;

/**
 * The ServerThread class represents
 * @author soph
 *
 */
public class ServerThread extends Thread {
	private SharedServerInfo sharedInfo;
	private ClientSocketInfo clientConnection;
	
	public ServerThread(SharedServerInfo sharedInfo, ClientSocketInfo clientConnection) {
		this.sharedInfo = sharedInfo;
		this.clientConnection = clientConnection;
	}
	
	public void run() {
		/* Want to start doing message passing rounds once a decent number (3 for a start)
		 * of clients have actually connected.  This limit can be changed, but need
		 * to stop letting them in at some stage so that the server can calculate
		 * key-pairs etc.
		 */
		try {
			// Send the public key to the client.
			sendKey();
			
			while (true) {
				// Check for new clients.
				sharedInfo.checkForNewClients();
		
				// Send the keys for this round to the client.
				sendKeys();

				// Start the round at this point
				sendStartRound();

				// Wait for the client to send output for
				// the round back
				getOutput();
				
				// Wait until all clients have send their output back
				sharedInfo.waitForOutputs();
				
				// Then send all the outputs for the round back to the client.
				sendResults();
				
				// Wait until all clients have been send the collection of outputs.
				sharedInfo.waitForOutputsToBeSent();
			}
			
		} catch (SocketException e) {
			// Client lost.
			try {
				abort();
			} catch (Exception ex) {}
		} catch (EOFException e) {
			// Server has lost the client connection due to the
			// client disconnecting, so don't need to do anything
			// else
			try {
				abort();
			} catch (Exception ex) {/* Don't care at this stage */}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void sendKey() throws IOException {
		Key publicKey = sharedInfo.getPublicKey();
		
		clientConnection.send(publicKey);
		
	}

	private void sendKeys() throws IOException {
		// Get the keyset for this round
		DiningKeySet key = sharedInfo.getKeySet();
		
		// Send the KeySet to the client
		clientConnection.send(key);

		Message reply = clientConnection.receiveMessage();
		if (reply.getMessage().equals(CommunicationProtocol.ACK)) {
			// The keyset was received by the client correctly
			return;
		} else {
			System.err.println("Failed to send keys to the client");
			System.exit(0);
		}
	}
	
	private void getOutput() throws IOException {
		Message output = clientConnection.receiveMessage();
		
		if (output != null) {
			// If an output was received by the connected client
			// add it to the shared output ArrayList, as long
			// as it wasn't the exit command.
			if (output.getMessage().equals(CommunicationProtocol.CLIENT_EXIT)) {
				abort();
			} else {
				sharedInfo.addOutput(output);
				
				// Clear output
				output = null;
			}
		} else {
			System.out.println("Output is null. Exiting.");
			System.exit(0);
		}
	}
	
	private void sendResults() throws IOException {
		// Get the results for this round
		ArrayList<Message> results = new ArrayList<Message>(sharedInfo.getCurrentRoundMessages());
		
		// Send the results to the client
		clientConnection.send(results);

		Message reply = clientConnection.receiveMessage();
		if (reply.getMessage().equals(CommunicationProtocol.ACK)) {
			// The results were received by the client correctly
			return;
		} else {
			// The results were not received correctly
			System.err.println("Failed to send results to the client");
			System.exit(0);
		}
	}

	private void sendStartRound() throws IOException {
		clientConnection.send(new Message(CommunicationProtocol.START_ROUND));
	}
	
	private void abort() throws IOException{
		//sharedInfo.sendShutdownMessages();
		//System.out.println("Sent all shutdowns...exiting");
		
		// Close socket connections for this client
		clientConnection.close();
		
		sharedInfo.removeClient(clientConnection);
	}
}
