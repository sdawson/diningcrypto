package client;

import gui.GuiManager;

/**
 * The main function that runs the GUI-based client,
 * and initializes theh connection between the client
 * and server.
 * 
 * @author Sophie Dawson
 *
 */
public class Client {
	private static ClientConnection connection = null;

	public static void main(String[] args) {		
		if (args.length == 2) {
			connection = new ClientConnection(args[0], 
					new Integer(args[1]).intValue());
		} else {
			connection = new ClientConnection("localhost", 9876); 
		}
		
		// Start the GUI
		GuiManager guiManager = new GuiManager();
		
		
		// Connect to the chosen server
		connection.connect();
		ClientLoop loop = new ClientLoop(connection, guiManager);
		guiManager.setInput(loop);
		loop.run();
		connection.disconnect();
		
		
	}
}
