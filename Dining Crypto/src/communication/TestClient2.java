package communication;

import java.io.EOFException;
import java.io.IOException;

public class TestClient2 {
	public static void main(String[] args) {
		System.out.println("TestClient has started up ok.");
		
		ClientConnection connection = new ClientConnection("localhost", 9876);
		System.out.println("pre client->server connect");
		connection.connect();
		System.out.println("post client->server connect");
		Message received;
		try {
			KeySet keys = connection.receiveKeySet();
			connection.send(new Message("OK"));
			received = connection.receiveMessage();
			if (received.getMessage().equals("STARTROUND")) {
				System.out.println("Server has requested the start of a round");
				while (true) {
					received = connection.receiveMessage();
					if (received.getMessage().equals("END")) {
						connection.disconnect();
						break;
					}
				}
			} else {
				System.err.println("Server has asked something unexpected");
				connection.disconnect();
				System.exit(1);
			}
		} catch (EOFException e) {
			connection.disconnect();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		connection.disconnect();
	}
}
