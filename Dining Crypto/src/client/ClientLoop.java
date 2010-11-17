package client;

import interfaces.Input;
import interfaces.Output;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

import utility.StrBuffer;

import communication.CommunicationProtocol;
import communication.KeySet;
import communication.Message;

/**
 * Defines the main loop the client cycles through
 * as it communicates with the server.
 * 
 * @author Sophie Dawson
 *
 */
public class ClientLoop implements Input {
	private final ClientConnection connection;
	private StrBuffer inputBuf = new StrBuffer(20);
	private Output guiRef = null;
	private boolean killFlag = false;
	
	public ClientLoop(ClientConnection connection, Output output) {
		this.connection = connection;
		this.guiRef = output;
	}
	
	/**
	 * Starts running the client operation loop.
	 * This consists of the following repeated steps:
	 *  - Get a keyset for the round from the server
	 *  - Acknowledge the receipt of the keyset
	 *  - Wait for the server to indicate the current round
	 *    has started
	 *  - Send any client-submitted input to the server
	 *    (this will also be enciphered using the keyset
	 *    previously obtained)
	 *  - Receive the result of the round from the server
	 *    and display it
	 *  Note: As the loop is running the client also checks
	 *  to see if the server has send out a shutdown message,
	 *  and if so the client breaks out of the operation loop
	 *  and lets the calling class close the client-server
	 *  connection. 
	 */
	public void run() {
		Message received;
		String currentMessage = null;
		ArrayList<Message> roundResults;
		
		while (true) {
			try {
				// Get keyset for the round
				KeySet keys = getKeySet();
				
				
				received = connection.receiveMessage();
				if (received.getMessage().equals(CommunicationProtocol.STARTROUND)) {
					System.out.println("Server has requested the start of a round");
					/* Sending a message (or nothing, if the client doesn't want to
					 * send anything this round.
					 */
					if (input != null) {
						/* TODO: THIS IS WHERE THE CLIENT SHOULD TAKE THE MESSAGE
						 * SUBMITTED BY THE CLIENT (IF ANY), APPLY THE TRANSFORMS
						 * IN KEYSET TO IT, THEN SUBMIT IT TO THE SERVER USING
						 * THE FUNCTION CALL connection.send(new Message(resultOf
						 * KeysetGoesHere).
						 */
						// TODO: actually create a term for the client to
						// use that is translated into the protocol quit message
						connection.send(new Message(input));
					} else {
						connection.send(new Message(""));
					}
					// Waiting for the result of the round
					roundResults = connection.receiveRoundResults();
					// If any of the message returned are shutdown messages from
					// the server, start the client shutdown process.
					for (Message m : roundResults) {
						if (m.getMessage().equals(CommunicationProtocol.SHUTDOWN))
							break;
					}
					// Otherwise acknowledge that the result has been
					// received and display it
					connection.send(new Message(CommunicationProtocol.ACK));
					// Collate the results for the round and display them TODO: alter line below
					String r = collate(roundResults);
					System.out.println("Round result: " + r);
					guiRef.outputString(r);
				} else if (received.getMessage().equals(CommunicationProtocol.SHUTDOWN)) {
					break;
				} else {
					System.err.println("Server has asked something unexpected");
					break;
				}
			} catch (EOFException e) {
				/* This is expected behavior, since it indicates
				 * one of the other clients/the server has disconnected. 
				 */
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String collate(ArrayList<Message> messages) {
		String s = new String();
		
		for (Message m : messages) {
			s.concat(m.getMessage());
		}
		return s;
	}
	
	private KeySet getKeySet() throws IOException {
		KeySet keys;
		try {
			 keys = connection.receiveKeySet();
		} catch (ClassNotFoundException e) {
				/* A message has been sent by the server instead,
				 * Indicating that the server wants the client to
				 * shut down, so break out of the client execution loop. 
				 */
				killFlag = true;
		}
		
		System.out.println("Got a keyset");
		connection.send(new Message(CommunicationProtocol.ACK));
		System.out.println("Send an ack");
		
		return keys;
	}

	@Override
	public void inputString(String str) {
		inputBuf.add(str);
		System.out.println("Message " + str + " added to buffer.");
	}
}
