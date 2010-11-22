package server;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.util.ArrayList;

import communication.CommunicationProtocol;
import communication.DiningKey;
import communication.DiningKeyOp;
import communication.DiningKeySet;
import communication.Message;

import crypto.Encryption;

/** The set of information that needs to be shared
 * by all the threads running on the server.  All
 * methods relating to the contents of this class
 * must be synchronised, to ensure all threads work
 * from the same set of information.
 * 
 * @author Sophie Dawson
 *
 */
public class SharedServerInfo {
	private int noSent = 0, keysDistributed = 0, noStart = 0;
	private ArrayList<Message> currentRoundMessages = new ArrayList<Message>();
	private ArrayList<ClientSocketInfo> clients = new ArrayList<ClientSocketInfo>(),
			newClients = new ArrayList<ClientSocketInfo>(),
			clientsToRemove = new ArrayList<ClientSocketInfo>();
	private DiningKeySet[] keysets = null;
	private Key publicKey, privateKey;
	
	public SharedServerInfo() {
		// Generate the keys for encryption
		KeyPair keys = Encryption.generateRSAKeys();
		this.publicKey = keys.getPublic();
		this.privateKey = keys.getPrivate();
	}
	
	public synchronized void incrementStart() {
		this.noStart++;
	}
	
	public synchronized void resetStart() {
		this.noStart = 0;
	}
	
	public synchronized int getStart() {
		return this.noStart;
	}
	
	public synchronized void incrementSent() {
		this.noSent++;
	}
	
	public synchronized void resetSent() {
		this.noSent = 0;
	}
	
	public synchronized int getSent() {
		return this.noSent;
	}
	
	public synchronized int getNumberClients() {
		return this.clients.size();
	}
	
	public synchronized int getNoOfMessages() {
		return this.currentRoundMessages.size();
	}
	
	public synchronized ArrayList<Message> getCurrentRoundMessages() {
		return this.currentRoundMessages;
	}
	
	public synchronized void addOutput(Message message) {
		this.currentRoundMessages.add(message);
	}
	
	public synchronized void resetRoundMessages() {
		this.currentRoundMessages.clear();
	}
	
	public synchronized void addClient(ClientSocketInfo newClient) {
		newClients.add(newClient);
		
		System.out.println("Server starting thread " + clients.size());
		new ServerThread(this, newClient).start();
	}
	
	public synchronized DiningKeySet getKeySet() {
		if (keysets == null || keysDistributed == clients.size()) {
			
		}
		
		DiningKeySet set = keysets[keysDistributed];
		keysDistributed++;
		
		return set; 
	}
	
	private void generateKeySets() {
		System.out.println("Generating keys " + clients.size()); System.out.flush();
		// Create a set for each client
		keysets = new DiningKeySet[clients.size()];
		for (int i=0 ; i<clients.size() ; i++ ) {
			keysets[i] = new DiningKeySet();
		}
		
		// Populate each set
		for (int i=0 ; i<clients.size()-1 ; i++) {
			for (int j=i+1 ; j<clients.size() ; j++) {
				// Create the key
				DiningKey k = DiningKey.generateRandomKey();
				
				// Set the keyops
				DiningKey kp = new DiningKey(k.getKey(), DiningKeyOp.ADD),
					kn = new DiningKey(k.getKey(), DiningKeyOp.SUBTRACT);
				
				// Add the keys to the sets
				keysets[i].addKey(kp);
				keysets[j].addKey(kn);
			}
		}
	}

	public synchronized void waitForOutputs() {
		if (getNoOfMessages() < getNumberClients()) {
			try {
				System.out.println("wait outputs"); System.out.flush();
				wait();
			} catch (InterruptedException e) {/* Continue */}
		} else {
			System.out.println("done outputs"); System.out.flush();
			notifyAll();
		}
	}

	public synchronized void waitForOutputsToBeSent() {
		incrementSent();
		if (getSent() < getNumberClients()) {
			try {
				System.out.println("wait outputs sent"); System.out.flush();
				wait();
			} catch (InterruptedException e) {/* Continue */}
		} else {
			resetRoundMessages();
			resetSent();
			System.out.println("done outputs sent"); System.out.flush();
			notifyAll();
		}
	}

	public synchronized void checkForNewClients() {
		incrementStart();
		if (getStart() < getNumberClients() + newClients.size()) {
			try {
				wait();
				
			} catch (InterruptedException e) {/* Continue */}
		} else {
			// Add any new clients.
			for (ClientSocketInfo newClient : newClients) {
				clients.add(newClient);
			}
			newClients.clear();
			
			// Remove any dead clients.
			for (ClientSocketInfo deadClient : clientsToRemove) {
				clients.remove(deadClient);
			}
			clientsToRemove.clear();
			
			// Generate the keys for this round.
			generateKeySets();
			keysDistributed = 0;
			System.out.println("done start"); System.out.flush();

			resetStart();
			notifyAll();
		}
	}
	
	public synchronized void sendShutdownMessages() throws IOException{
		Message finalMessage = new Message(CommunicationProtocol.SHUTDOWN);
		for (ClientSocketInfo c : clients) {
			System.err.println("Sending a shutdown message");
			c.send(finalMessage);
		}
	}

	public synchronized void removeClient(ClientSocketInfo clientConnection) {
		this.clientsToRemove.add(clientConnection);
	}

	public Key getPublicKey() {
		return publicKey;
	}

	public Key getPrivateKey() {
		return privateKey;
	}
}
