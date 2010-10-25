package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static final int PORT = 9876;
	
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("Error: IO Error on server socket initialization.");
			System.exit(1);
		}
		
		Socket clientSocket = null;
		try {
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			System.err.println("Error: Client connection accept failure");
			System.exit(1);
		}
		
		ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
		ObjectInputStream objectIn = new ObjectInputStream(clientSocket.getInputStream());
		
		try {
			Message stuff = (Message) objectIn.readObject();
			System.out.println("received " + stuff.getMessage());
			Message finalMessage = new Message("fldkfjs");
			objectOut.writeObject(finalMessage);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		objectOut.close();
		objectIn.close();
		clientSocket.close();
		serverSocket.close();
	}
}
