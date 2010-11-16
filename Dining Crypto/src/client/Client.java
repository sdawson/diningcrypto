package client;

import java.io.EOFException;
import java.io.IOException;

import communication.CommunicationProtocol;
import communication.KeySet;
import communication.Message;

import interfaces.Input;

public class Client {
	private static ClientConnection connection = null;

	public static void main(String[] args) {		
		if (args.length == 2) {
			connection = new ClientConnection(args[0], 
					new Integer(args[1]).intValue());
		} else {
			connection = new ClientConnection("localhost", 9876); 
		}
		
		// Connect to the chosen server
		connection.connect();
		ClientLoop loop = new ClientLoop(connection, asldkf);
		loop.run();
		connection.disconnect();
	}
}
