package client;

import interfaces.Input;
import interfaces.Output;

import java.io.EOFException;
import java.io.IOException;

import communication.CommunicationProtocol;
import communication.KeySet;
import communication.Message;

public class ClientLoop implements Input {
	private final ClientConnection connection;
	private String input = null;
	private Output guiRef = null;
	
	public ClientLoop(ClientConnection connection, Output output) {
		this.connection = connection;
		this.guiRef = output;
	}
	
	public void run() {
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
					if (input != null) {
						// TODO: actually create a term for the client to
						// use that is translated into the protocol quit message
						connection.send(new Message(input));
					} else {
						connection.send(new Message("TC1Send"));
					}
					// Waiting for the result of the round
					received = connection.receiveMessage();
					if (received.getMessage().equals(CommunicationProtocol.SHUTDOWN)) {
						break;
					}
					// Otherwise acknowledge that the result has been
					// received and display it
					connection.send(new Message(CommunicationProtocol.ACK));
					System.out.println("Round result: " + received.getMessage());
					guiRef.outputString(received.getMessage());
				} else if (received.getMessage().equals(CommunicationProtocol.SHUTDOWN)) {
					break;
				} else {
					System.err.println("Server has asked something unexpected");
					break;
				}
			} catch (EOFException e) {
				/* This is expected behaviour, since it indicates
				 * one of the other clients/the server has disconnected. 
				 */
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void inputString(String str) {
		this.input = str;
	}
}
