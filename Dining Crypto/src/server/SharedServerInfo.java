package server;

import java.util.ArrayList;

import communication.DiningKey;
import communication.DiningKeySet;
import communication.DiningKeyOp;
import communication.Message;

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
	private int noSent = 0, numberClients, keysDistributed = 0;
	private ArrayList<Message> currentRoundMessages;
	private DiningKeySet[] keysets = null;
	
	public SharedServerInfo(int noOfReplies, int numberClients,
			ArrayList<Message> currentRoundMessages) {
		this.noSent = noOfReplies;
		this.numberClients = numberClients;
		this.currentRoundMessages = currentRoundMessages;
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
		return this.numberClients;
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
	
	public synchronized DiningKeySet getKeySet() {
		if (keysDistributed == numberClients) {
			generateKeySets();
			keysDistributed = 0;
		}
		
		DiningKeySet set = keysets[keysDistributed];
		keysDistributed++;
		
		return set; 
	}
	
	public void generateKeySets() {
		// Create a set for each client
		keysets = new DiningKeySet[numberClients];
		for (int i=0 ; i<numberClients ; i++ ) {
			keysets[i] = new DiningKeySet();
		}
		
		// Populate each set
		for (int i=0 ; i<numberClients-1 ; i++) {
			for (int j=i+1 ; j<numberClients ; j++) {
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
}
