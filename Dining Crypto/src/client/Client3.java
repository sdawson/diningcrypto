package client;

import java.io.EOFException;
import java.io.IOException;

import communication.Message;

public class Client3 {
	public static void main(String[] args) {
		System.out.println("Client2 has started up ok.");
		
		Message m = new Message("Hello 333333");
		ClientConnection connect = new ClientConnection("localhost", 9876);
		System.out.println("pre client->server connect");
		connect.connect();
		System.out.println("post client->server connect");
		try {
			connect.send(m);
			System.out.println("post client message send");
			Message reply;
			while (true) {
				reply = connect.receiveMessage();
				if (reply.getMessage().equals("END")) {
					connect.disconnect();
					break;
				}
				System.out.println(reply.getMessage());
				/*Message end = new Message("KILL");*/
				Message end = new Message("Roundtrip?");
				reply.increment();
				System.out.println("incrementing to " + reply.getTrips());
				connect.send(end);
			}
		} catch (EOFException e) {
			connect.disconnect();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		connect.disconnect();
	}
}
