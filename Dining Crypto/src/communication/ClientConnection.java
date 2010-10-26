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
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(Message message) throws IOException {
		
		out.writeObject(message);
		Message reply = null;
		try {
			reply = (Message) in.readObject();
			System.out.println("returns: " + reply.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void disconnect() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Message receive() {
		return null;
		// More stuff
	}
}
