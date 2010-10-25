package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientConnection {
	private String serverAddress;
	private int serverPort;
	
	public ClientConnection(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}
	
	public void send(Message message) throws IOException {
		Socket socket = null;
		ObjectOutputStream objectOut= null;
		ObjectInputStream objectIn = null;

		try {
			socket = new Socket(serverAddress, serverPort);
			objectOut = new ObjectOutputStream(socket.getOutputStream());
			objectIn = new ObjectInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Error: Couldn't find host");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error: IO error when connecting to " + serverAddress);
			System.exit(1);
		}
		
		objectOut.writeObject(message);
		Message reply = null;
		try {
			reply = (Message) objectIn.readObject();
			System.out.println("returns: " + reply.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		objectIn.close();
		objectOut.close();
		socket.close();
	}
	
	public Message receive() {
		return null;
		// More stuff
	}
}
