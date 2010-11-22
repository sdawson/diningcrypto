package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import communication.DiningKeySet;
import communication.Message;

import crypto.Encryption;

/**
 * The ClientSocketInfo class contains all
 * the socket and stream information relating
 * to a single server-to-client connection.
 * 
 * @author Sophie Dawson
 *
 */
public class ClientSocketInfo {
	private Socket clientSocket = null;
	private ObjectInputStream in = null;
	private ObjectOutputStream out = null;
	private PublicKey encryptionKey;
	private PrivateKey decryptionKey;

	public ClientSocketInfo(Socket clientSocket) {
		this.clientSocket = clientSocket;
		try {
			out = new ObjectOutputStream(this.clientSocket.getOutputStream());
			in = new ObjectInputStream(this.clientSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void setKeys(PublicKey encryptionKey, PrivateKey decryptionKey) {
		this.encryptionKey = encryptionKey;
		this.decryptionKey = decryptionKey;
	}

	/**
	 * Receive a message object from the client.
	 * @return The message object send by the client.
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
	 * Send a message to the connected client.
	 * 
	 * @param message The message object to send.
	 * @throws IOException
	 */
	public void send(Message message) throws IOException {
		out.writeObject(Encryption.encrypt(message, encryptionKey));
	}

	/**
	 * Send a keyset to the connected client.
	 * @param keys The keyset object to send.
	 * @throws IOException
	 */
	public void send(DiningKeySet keys) throws IOException {
		out.writeObject(Encryption.encrypt(keys, encryptionKey));
	}
	
	/**
	 * Send an encryption key to the connected client.
	 * @param key The key to be send.
	 * @throws IOException
	 */
	public void send(Key key) throws IOException {
		out.writeObject(key);
	}

	/**
	 * Send the collection of results to the client.
	 * @param currentRoundMessages The collection of results.
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void send(ArrayList<Message> currentRoundMessages) throws IOException {
		ArrayList<Message> cipher = (ArrayList<Message>) currentRoundMessages.clone();
		for (int i=0 ; i<cipher.size() ; i++) {
			cipher.set(i, Encryption.encrypt(cipher.get(i), encryptionKey));
		}
		out.writeObject(cipher);
	}

	/**
	 * Close the connection to the client.
	 * @throws IOException
	 */
	public void close() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
	}
	
	@Override
	public String toString() {
		return clientSocket.toString();
	}
}
