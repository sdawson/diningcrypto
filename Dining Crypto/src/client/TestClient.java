package client;

import java.io.EOFException;
import java.io.IOException;

import communication.CommunicationProtocol;
import communication.KeySet;
import communication.Message;


public class TestClient {
	public static void main(String[] args) {
		System.out.println("TestClient has started up ok.");
		
		ClientConnection connection = new ClientConnection("localhost", 9876);
		System.out.println("pre client->server connect");
		connection.connect();
		System.out.println("post client->server connect");
		Message received;
		while (true) {
			try {
				// Get keyset for the round
				try {
				KeySet keys = connection.receiveKeySet();
				} catch (ClassNotFoundException e) {
					/* A message has been sent by the server instead,
					 * Indicating that the server wants the client to
					 * shut down, so break out of the client execution loop. 
					 */
					break;
				}
				System.out.println("Got a keyset");
				connection.send(new Message(CommunicationProtocol.ACK));
				System.out.println("Send an ack");
				received = connection.receiveMessage();
				if (received.getMessage().equals(CommunicationProtocol.STARTROUND)) {
					System.out.println("Server has requested the start of a round");
					/* Sending a message (or nothing, if the client doesn't want to
					 * send anything this round.
					 */
					connection.send(new Message("TC1Send"));
					// Waiting for the result of the round
					received = connection.receiveMessage();
					if (received.getMessage().equals(CommunicationProtocol.SHUTDOWN)) {
						connection.disconnect();
						System.exit(0);
					}
					// Otherwise acknowledge that the result has been
					// received and display it
					connection.send(new Message(CommunicationProtocol.ACK));
					System.out.println("Round result: " + received.getMessage());
				} else if (received.getMessage().equals(CommunicationProtocol.SHUTDOWN)) {
					connection.disconnect();
					System.exit(0); // would normally break out of the overall while loop.
				} else {
					System.err.println("Server has asked something unexpected");
					connection.disconnect();
					System.exit(1);
				}
			} catch (EOFException e) {
				connection.disconnect();
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		connection.disconnect();
	}
}
