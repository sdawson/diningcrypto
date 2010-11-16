package server;

import java.util.ArrayList;

import communication.Message;

/** The set of information that needs to be shared
 * by all the threads running on the server.  All
 * methods relating to the contents of this class
 * must be synchronized, to ensure all threads work
 * from the same set of information.
 * 
 * @author Sophie Dawson
 *
 */
public class SharedServerInfo {
	private int noOfReplies;
	private int maxClients;
	private ArrayList<Message> currentRoundMessages;
	private Message currentRoundResult;
	
	public SharedServerInfo(int noOfReplies, int maxClients,
			ArrayList<Message> currentRoundMessages) {
		this.noOfReplies = noOfReplies;
		this.maxClients = maxClients;
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
	
	public synchronized int getMaxClients() {
		return this.maxClients;
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
}
