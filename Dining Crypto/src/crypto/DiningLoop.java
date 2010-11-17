package crypto;

import interfaces.Input;
import interfaces.Output;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

import client.ClientConnection;

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
public class DiningLoop implements Input {
	private final static int MAX_CHAR = '\uffff';
	
	private final ClientConnection connection;
	private Output guiRef = null;
	
	private StrBuffer inputBuf = new StrBuffer(20);
	private String currentMessage = null;
	private int currentMessageIndex = 0;
	
	public DiningLoop(ClientConnection connection, Output output) {
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
		ArrayList<Message> roundResults;
		boolean inMessageFlag = false;
		
		while (true) {
			try {
				// Get keyset for the round
				KeySet keys = getKeySet();
				if (keys == null) {
					// Something has gone wrong or we've
					// received a shutdown command.
					break;
				}
				
				received = connection.receiveMessage();
				if (received.getMessage().equals(CommunicationProtocol.START_ROUND)) {
					// Transmit the next character.
					transmit(getNextChar(), keys);
					
					// Waiting for the result of the round
					roundResults = connection.receiveRoundResults();
					
					// If any of the messages returned are shutdown messages from
					// the server, start the client shutdown process.
					for (Message m : roundResults) {
						if (m.getMessage().equals(CommunicationProtocol.SHUTDOWN))
							break;
					}
					
					// Otherwise acknowledge that the result has been received
					connection.send(new Message(CommunicationProtocol.ACK));
					
					// Collate the results for the round
					char r = collate(roundResults);
					
					if (r!=0) {
						// Display the result
						guiRef.outputString("" + r);
						inMessageFlag = true;
					} else if ( inMessageFlag ) {
						guiRef.outputString("\n");
						inMessageFlag = false;
					}
					
				} else if (received.getMessage().equals(CommunicationProtocol.SHUTDOWN)) {
					break;
				} else {
					System.err.println("Server has asked something unexpected");
					break;
				}
			} catch (EOFException e) {
				/* 
				 * This is expected behaviour, since it indicates
				 * one of the other clients/the server has disconnected. 
				 */
				
				// TODO: do something about this exception
			} catch (IOException e) {
				// TODO: do something about this exception
				e.printStackTrace();
			}
		}
	}
	
	private char collate(ArrayList<Message> messages) {
		int sum = 0;	

		for (Message m : messages) {
			sum += Integer.parseInt(m.getMessage());
		}
		
		if (sum==0) {
			// no message has been transmitted
		} else if (sum > 2*MAX_CHAR) {
			// There is a collision.
		} else {
			sum -= MAX_CHAR;
		}

		return (char)sum;
	}
	
	private KeySet getKeySet() throws IOException {
		KeySet keys = null;
		try {
			 keys = connection.receiveKeySet();
		} catch (ClassNotFoundException e) {
				/* A message has been sent by the server instead,
				 * Indicating that the server wants the client to
				 * shut down, so break out of the client execution loop. 
				 */
				return null;
		}
		
		connection.send(new Message(CommunicationProtocol.ACK));
		
		return keys;
	}
	
	private char getNextChar() {
		if (currentMessage == null) {
			currentMessage = inputBuf.next();	
		}
		
		if (currentMessage == null) {
			return (char)0;
		} else {
			if (currentMessageIndex < currentMessage.length()) {
				char ret =  currentMessage.charAt(currentMessageIndex);
				currentMessageIndex++;
				return ret;
			} else {
				// Reached the end of the currentMessage
				currentMessage = null;
				currentMessageIndex = 0;
				
				return (char)0;
			}
		}
	}
	
	private void transmit(char c, KeySet keys) throws IOException {
		/*
		 * Send the message (or nothing, if the client doesn't want to
		 * send anything this round.
		 */
		int cint = c;
		if (c!= 0) {
			// Add \uffff (max unicode value to the output so we can detect collisions
			cint += MAX_CHAR;
		}
		
		int output = keys.sum() + cint;
		
		connection.send(new Message("" +  output));
	}

	@Override
	public void inputString(String str) {
		inputBuf.add(str);
		System.out.println("Message " + str + " added to buffer.");
	}
}
