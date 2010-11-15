package communication;

import java.io.EOFException;
import java.io.IOException;

public class Client {
	public static void main(String[] args) {
		System.out.println("Client has started up ok.");
		
		Message m = new Message("GET KEYS");
		Message reply;
		ClientConnection connect = new ClientConnection("localhost", 9876);
		System.out.println("pre client->server connect");
		connect.connect();
		System.out.println("post client->server connect");
		try {
			connect.send(m);
			System.out.println("post client message send");
			while (true) {
				reply = connect.receiveMessage();
				if (reply.getMessage().equals("END")) {
					connect.disconnect();
					break;
				}
				System.out.println(reply.getMessage());
				reply.increment();
				System.out.println("incrementing to " + reply.getTrips());
				connect.send(reply);
			}
		} catch (EOFException e) {
			// The input stream from the client connection has been closed
			connect.disconnect();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		connect.disconnect();
	}
	
	private void sendOk(ClientConnection connection) throws IOException {
		Message ok = new Message("OK");
		connection.send(ok);
	}
}
