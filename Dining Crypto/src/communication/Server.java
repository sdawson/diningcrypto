package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static final int PORT = 9876;
	
	public static void main(String[] args) throws IOException {
		ServerConnection connection = new ServerConnection(PORT);
		
		connection.acceptConnection();
		
		/*Message stuff = (Message) connection.getInputStream().readObject();
		System.out.println("received " + stuff.getMessage());
		Message finalMessage = new Message("fldkfjs");
		connection.getOutputStream().writeObject(finalMessage);*/
		Message stuff = connection.receive();
		System.out.println("received " + stuff.getMessage());
		Message finalMessage = new Message("working recv/send server connection.");
		connection.send(finalMessage);

	}
}
