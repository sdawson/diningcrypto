package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import communication.KeySet;
import communication.Message;

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
	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	
	public ClientConnection(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}

	/**
	 * Connects to the server specified during the creation
	 * of the ClientConnection object.
	 */
	public String connect() {
		try {
			socket = new Socket(serverAddress, serverPort);
			System.out.println("pre client stream collection");
			// Output stream creation goes first to avoid blocking
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			System.out.println("post client stream collection");
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
		out.writeObject(message);
	}

	/**
	 * Receive a Message object from the server
	 * @return The Message object sent by the server
	 * @throws IOException
	 */
	public Message receiveMessage() throws IOException {
		Message newMessage = null;
		try {
			newMessage = (Message) in.readObject();
			//System.out.println("returns: " + newMessage.getMessage());
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
	public KeySet receiveKeySet() throws IOException, ClassNotFoundException {
		KeySet keyset = null;

		keyset = (KeySet) in.readObject();
		return keyset;
	}
	
	public ArrayList<Message> receiveRoundResults() throws IOException {
		ArrayList<Message> messages = null;
		
		try {
			messages = (ArrayList<Message>) in.readObject();
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

}
