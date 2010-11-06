package communication;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private static final int PORT = 9876;
	private ArrayList<Socket> clients = new ArrayList<Socket>();
	
	public static void main(String[] args) throws IOException {
		ServerConnection connection = new ServerConnection(PORT);
		
		ClientSocketInfo first = connection.acceptConnection();
		
		/*Message stuff = (Message) connection.getInputStream().readObject();
		System.out.println("received " + stuff.getMessage());
		Message finalMessage = new Message("fldkfjs");
		connection.getOutputStream().writeObject(finalMessage);*/
		Message stuff = connection.receive(first);
		System.out.println("received " + stuff.getMessage());
		Message finalMessage = new Message("working recv/send server connection.");
		connection.send(finalMessage, first);

	}
}
