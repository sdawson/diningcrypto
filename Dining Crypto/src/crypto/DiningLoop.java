package crypto;

import interfaces.Input;
import interfaces.Output;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

import utility.StrBuffer;
import client.ClientConnection;

import communication.CommunicationProtocol;
import communication.DiningKeySet;
import communication.Message;

/**
 * Defines the main loop the client cycles through
 * as it communicates with the server.
 * 
 * @author Sophie Dawson
 *
 */
public class DiningLoop implements Input {
	private final static int MAX_WAIT = 10;
	
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
		String outputMessage = new String();
		
		// Receive the public key from the server.
		getKey();
		
		while (true) {
			try {
				// Get keyset for the round
				DiningKeySet keys = getKeySet();
				if (keys == null) {
					// Something has gone wrong or we've
					// received a shutdown command.
					break;
				}
				
				received = connection.receiveMessage();
				if (received.getMessage().equals(CommunicationProtocol.START_ROUND)) {
					// Transmit the next character, unless a collision
					// has occurred.  In the case of a collision wait
					// until a random number of rounds has passed before
					// resending.
					transmit(getNextChar(), keys);
					
					// Waiting for the result of the round
					roundResults = connection.receiveRoundResults();
					if (roundResults.size()==0) {
						System.err.println("roundresults has size zero!!! exiting");
						System.exit(0);
					}
					
					// Otherwise acknowledge that the result has been received
					connection.send(new Message(CommunicationProtocol.ACK));
					
					// Collate the results for the round
					char r = collate(roundResults, outputMessage);
					
					if (r!=0) {
						// Add the result to the output array
						// for printing once the whole message
						// is received
						outputMessage = outputMessage + String.valueOf(r);
						inMessageFlag = true;
					} else if ( inMessageFlag ) {
						guiRef.outputString(outputMessage.toString() + "\n");
						outputMessage = "";
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
			} catch (SocketException e) {
				System.out.println("Lost connection to the server. Exiting.");
				System.exit(1);
			} catch (IOException e) {
				// TODO: do something about this exception
				e.printStackTrace();
			}
		}
	}
	
	/* Need to check if we are already in a collision.
	 * if this is the first collision, then set the round
	 * counter starting.  if isCollision already true
	 */
	private char collate(ArrayList<Message> messages,
			String outputMessage) {
		int sum = 0;	

		for (Message m : messages) {
			sum += Integer.parseInt(m.getMessage());
		}
		
		if (sum==0) {
			// no message has been transmitted
		} else if (sum > 2*Character.MAX_VALUE) {
			// There is a collision.
			/* Adds a random delay to the buffer that
			 * collided, so that the collision can be resolved.
			 */
			// TODO: put currentMessage set inside addDelay func
			currentMessage = addDelayToBuffer();
			outputMessage = new String();
			
			// Also don't want to print the collision,
			// since it's usually a non-alphanumeric char
			sum = 0;
		} else {
			sum -= Character.MAX_VALUE;
		}

		return (char)sum;
	}
	
	private DiningKeySet getKeySet() throws IOException {
		DiningKeySet keys = null;
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
	
	private String addDelayToBuffer() {
		// The extra 0 is just to pad out the section where
		// the original value in the message used to be, since
		// the index always gets higher (and otherwise you'll
		// always have a delay one short of what was randomly
		// generated.
		SecureRandom rand = new SecureRandom();
		
		char nothing = (char) 0;
		int delay = rand.nextInt(MAX_WAIT);
		System.out.println("delay size: " + delay);
		String messageDelay = repeat(String.valueOf(nothing), delay);
		StringBuilder sb = new StringBuilder(currentMessage);
		
		sb.insert(0, messageDelay);
		// Reset the counter used to send message
		currentMessageIndex = 0;

		return sb.toString();
	}
	
	private String repeat(String s, int n) {
		final StringBuilder sb = new StringBuilder();
		for (int i=0; i<n; i++) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	private void transmit(char c, DiningKeySet keys) throws IOException {
		/*
		 * Send the message (or nothing, if the client doesn't want to
		 * send anything this round.
		 */
		int cint = c;
		if (c!= 0) {
			// Add \uffff (max unicode value to the output so we can detect collisions
			cint += Character.MAX_VALUE;
		}
		
		int output = keys.sum() + cint;
		
		connection.send(new Message("" +  output));
	}

	public void getKey() {
			RSAPublicKey publicKey = null;
			try {
				publicKey = connection.receiveKey();
			} catch (IOException e1) {
				System.out.println("Failed to acquire server's public key. Exiting.");
				System.exit(0);
			} catch (ClassNotFoundException e1) {
				System.out.println("Failed to acquire server's public key. Exiting.");
				System.exit(0);
			}
			connection.setKeys(publicKey, Encryption.publicToPrivate(publicKey));
	}
	
	
	@Override
	public void inputString(String str) {
		inputBuf.add(str);
	}
}
