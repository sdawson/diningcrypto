package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

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
	
	public void connect() {
		try {
			socket = new Socket(serverAddress, serverPort);
			System.out.println("pre client stream collection");
			// Output stream creation goes first to avoid blocking
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			System.out.println("post client stream collection");
		} catch (UnknownHostException e) {
			System.err.println("Error: Unknown host");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void send(Message message) throws IOException {
		out.writeObject(message);
	}

	public Message receive() throws IOException {
		Message newMessage = null;
		try {
			newMessage = (Message) in.readObject();
		} catch (ClassNotFoundException e) {
			return null;
		}
		return newMessage;
	}
	
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
