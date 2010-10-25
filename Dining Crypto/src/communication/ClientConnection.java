package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
		PrintWriter out = null;
		BufferedReader in = null;
		
		try {
			socket = new Socket(serverAddress, serverPort);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Error: Couldn't find host");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error: IO error when connecting to " + serverAddress);
			System.exit(1);
		}
		
		out.println(message.getMessage());
		System.out.println("echo: " + in.readLine());
		
		out.close();
		in.close();
		socket.close();
	}
	
	public Message receive() {
		return null;
		// More stuff
	}
}
