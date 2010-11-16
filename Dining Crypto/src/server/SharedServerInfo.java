package server;

import java.util.ArrayList;

import communication.Key;
import communication.KeySet;
import communication.Keyop;
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
	private int noOfReplies, numberClients, keysDistributed = 0;
	private ArrayList<Message> currentRoundMessages;
	private Message currentRoundResult;
	private KeySet[] keysets = null;
	
	public SharedServerInfo(int noOfReplies, int numberClients,
			ArrayList<Message> currentRoundMessages) {
		this.noOfReplies = noOfReplies;
		this.numberClients = numberClients;
		this.currentRoundMessages = currentRoundMessages;
		this.currentRoundResult = null;
	}
	
	public synchronized void incrementReplies() {
		this.noOfReplies++;
	}
	
	public synchronized void resetReplies() {
		this.noOfReplies = 0;
	}
	
	public synchronized int getReplies() {
		return this.noOfReplies;
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
	
	public synchronized void add(Message message) {
		this.currentRoundMessages.add(message);
	}
	
	public synchronized void resetRoundMessages() {
		this.currentRoundMessages.clear();
	}
	
	public synchronized void setRoundResult(Message message) {
		this.currentRoundResult = message; //?
	}
	
	public synchronized Message getRoundResult() {
		return this.currentRoundResult;
	}
	
	public synchronized KeySet getKeySet() {
		if (keysDistributed == numberClients) {
			generateKeySets();
			keysDistributed = 0;
		}
		
		KeySet set = keysets[keysDistributed];
		keysDistributed++;
		
		return set; 
	}
	
	private void generateKeySets() {
		// Create a set for each client
		KeySet[] sets = new KeySet[numberClients];
		for (int i=0 ; i<numberClients ; i++ ) {
			sets[i] = new KeySet();
		}
		
		// Populate each set
		for (int i=0 ; i<numberClients-1 ; i++) {
			for (int j=i+1 ; j<numberClients ; j++) {
				// Create the key
				Key k = Key.generateRandomKey();
				
				// Set the keyops
				Key kp = new Key(k.getKey(), Keyop.ADD),
					kn = new Key(k.getKey(), Keyop.SUBTRACT);
				
				// Add the keys to the sets
				sets[i].addKey(kp);
				sets[j].addKey(kn);
			}
		}
		
		keysets = sets;
	}
}
