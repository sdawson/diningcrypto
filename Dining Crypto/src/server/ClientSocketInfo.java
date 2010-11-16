package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import communication.KeySet;
import communication.Message;

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

	public ClientSocketInfo(Socket clientSocket) {
		this.clientSocket = clientSocket;
		try {
			out = new ObjectOutputStream(this.clientSocket.getOutputStream());
			in = new ObjectInputStream(this.clientSocket.getInputStream());
			System.out.println("in/out streams have been set up");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Receive a message object from the client.
	 * @return The message object send by the client.
	 * @throws IOException
	 */
	public Message receiveMessage() throws IOException {
		Message newMessage = null;

		try {
			newMessage = (Message) in.readObject();
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
		out.writeObject(message);
	}

	/**
	 * Send a keyset to the connected client.
	 * @param keys The keyset object to send.
	 * @throws IOException
	 */
	public void send(KeySet keys) throws IOException {
		out.writeObject(keys);
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
}
