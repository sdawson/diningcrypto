package communication;

import java.io.IOException;

public class Client {
	public static void main(String[] args) {
		System.out.println("Client has started up ok.");
		
		Message m = new Message("Hello world");
		ClientConnection connect = new ClientConnection("localhost", 9876);
		connect.connect();
		try {
			connect.send(m);
			Message reply = connect.receive();
		} catch (IOException e) {
			e.printStackTrace();
		}
		connect.disconnect();
	}
}
