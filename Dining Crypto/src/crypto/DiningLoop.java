package crypto;

import interfaces.Input;
import interfaces.Output;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Random;

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
	private final static int MAX_WAIT = 20, THROTTLE_WAIT = 500;
	private final static char NULL_CHAR = (char)0;
	
	private final ClientConnection connection;
	private Output guiRef;
	
	private StrBuffer inputBuf = new StrBuffer(20);
	private String currentMessage = null;
	private int currentMessageIndex = 0, collisionWait = 0;
	
	private boolean inMessage = false, sendingMessage = false;
		
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
					if(!inMessage) {
						try {
							// No message is being sent so slow down the loop.
							Thread.sleep(THROTTLE_WAIT);
						} catch (InterruptedException e) {/* Continue */}
					}
					
					
					// Transmit the next character, unless a collision
					// has occurred.  In the case of a collision wait
					// until a random number of rounds has passed before
					// resending.
					if (collisionWait==0) {
						if (inMessage==false || sendingMessage==true) {
							// Only transmit if no one else is and we're not waiting to resolve a collision.
							char nextChar = getNextChar();
							transmit(nextChar, keys);
						} else {
							transmit(NULL_CHAR, keys);
						}
					} else {
						transmit(NULL_CHAR, keys);
						collisionWait--;
					}
					
					// Waiting for the result of the round
					roundResults = connection.receiveRoundResults();
					if (roundResults.size()==0) {
						System.out.println("Error receiving results from server. Exiting.");
						System.exit(0);
					}
					
					// Otherwise acknowledge that the result has been received
					connection.send(new Message(CommunicationProtocol.ACK));
					
					// Collate the results for the round
					char result = collate(roundResults, outputMessage);
					
					if (result!=0) {
						// Add the result to the output array
						// for printing once the whole message
						// is received
						guiRef.outputString("" + result);
						inMessage = true;
					} else {
						if (inMessage) {
							guiRef.outputString("\n");
						}

						inMessage = false;
					}
				} else if (received.getMessage().equals(CommunicationProtocol.SHUTDOWN)) {
					break;
				} else {
					System.err.println("Server has asked something unexpected");
					break;
				}
			} catch (EOFException e) {
				System.out.println("EOFException. Exiting.");
				e.printStackTrace();
				System.exit(1);
			} catch (SocketException e) {
				System.out.println("Lost connection to the server. Exiting.");
				System.exit(1);
			} catch (IOException e) {
				System.out.println("IOException. Exiting.");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	private char collate(ArrayList<Message> messages, String outputMessage) {
		int sum = 0;	

		for (Message m : messages) {
			sum += Integer.parseInt(m.getMessage());
		}
		
		if (sum==0) {
			// no message has been transmitted
		} else if (sum > 2*Character.MAX_VALUE) {
			// There is a collision.
			dealWithCollision();
			
			// Also don't want to print the result.
			sum = 0;
		} else {
			sum -= Character.MAX_VALUE;
		}

		return (char)sum;
	}
	
	private void dealWithCollision() {
		if (sendingMessage) {
			Random rand = new Random();
			collisionWait = rand.nextInt(MAX_WAIT);
			sendingMessage = false;
			currentMessageIndex = 0;
		}
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
			return NULL_CHAR;
		} else {
			if (currentMessageIndex < currentMessage.length()) {
				sendingMessage = true;
				char ret =  currentMessage.charAt(currentMessageIndex);
				currentMessageIndex++;
				return ret;
			} else {
				// Reached the end of the currentMessage
				sendingMessage = false;
				
				currentMessage = null;
				currentMessageIndex = 0;
				
				return NULL_CHAR;
			}
		}
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
