package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection {
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private ObjectInputStream in = null;
	private ObjectOutputStream out = null;
	
	public ServerConnection(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void acceptConnection() {
		try {
			System.out.println("waiting for client to connect");
			clientSocket = serverSocket.accept();
			System.out.println("a client has connected " + clientSocket.toString());
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("in/out streams have been setup");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
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
	
	public void send(Message message) throws IOException {
		out.writeObject(message);
	}
	
	public void disconnect() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
		serverSocket.close();
	}
}
