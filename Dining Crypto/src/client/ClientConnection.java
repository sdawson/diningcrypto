package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

import communication.DiningKeySet;
import communication.Message;

import crypto.Encryption;

/**
 * An encapsulation of all client-side communication with
 * the server.  Connecting to the server and sending/receiving
 * messages and keysets are all handled by this class, in
 * order to provide a simple interface to functions that
 * interact with the network.
 * 
 * @author Sophie Dawson
 *
 */
public class ClientConnection {
	private String serverAddress;
	private int serverPort;
	private Socket socket = null;
	private PublicKey encryptionKey;
	private PrivateKey decryptionKey;
	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	
	public ClientConnection(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}

	public void setKeys(PublicKey encryptionKey, PrivateKey decryptionKey) {
		this.encryptionKey = encryptionKey;
		this.decryptionKey = decryptionKey;
	}
	
	/**
	 * Connects to the server specified during the creation
	 * of the ClientConnection object.
	 */
	public String connect() {
		try {
			socket = new Socket(serverAddress, serverPort);
			// Output stream creation goes first to avoid blocking
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			return new String("Unknown host");
		} catch (ConnectException e) {
			return new String("No server currently running.");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return new String("");
	}

	/**
	 * Sends a single Message object to the server
	 * 
	 * @param message The Message to send
	 * @throws IOException
	 */
	public void send(Message message) throws IOException {
		out.writeObject(Encryption.encrypt(message, encryptionKey));
	}

	/**
	 * Receive a Message object from the server
	 * @return The Message object sent by the server
	 * @throws IOException
	 */
	public Message receiveMessage() throws IOException {
		Message newMessage = null;
		try {
			newMessage = Encryption.decrypt((Message) in.readObject(), decryptionKey);
		} catch (ClassNotFoundException e) {
			return null;
		}
		return newMessage;
	}

	/**
	 * Receives a single KeySet object from the server.
	 * @return The KeySet object sent by the server
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public DiningKeySet receiveKeySet() throws IOException, ClassNotFoundException {
		return Encryption.decrypt((DiningKeySet) in.readObject(), decryptionKey);
	}
	
	/**
	 * Receives an encryption key from the server.
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public RSAPublicKey receiveKey() throws IOException, ClassNotFoundException {
		return (RSAPublicKey)in.readObject();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Message> receiveRoundResults() throws IOException {
		ArrayList<Message> messages = null;
		
		try {
			messages = (ArrayList<Message>) in.readObject();
			for (int i=0 ; i<messages.size() ; i++) {
				messages.set(i, Encryption.decrypt(messages.get(i), decryptionKey));
			}
		} catch (ClassNotFoundException e) {
			return null;
		}
		return messages;
	}

	/**
	 * Disconnects the client from the server.
	 */
	public void disconnect() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return serverAddress + ":" + serverPort;
	}

	public void setEncryptionKey(PublicKey key) {
		this.encryptionKey = key;
	}
	
	public void setDecryptionKey(PrivateKey key) {
		this.decryptionKey = key;
	}
}
