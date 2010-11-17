package client;

import javax.swing.JOptionPane;

import crypto.DiningLoop;
import gui.GuiManager;

/**
 * The main function that runs the GUI-based client,
 * and initialises the connection between the client
 * and server.
 * 
 * @author Sophie Dawson
 *
 */
public class Client {
	private static ClientConnection connection = null;

	public static void main(String[] args) {
		System.out.println("Starting Client.");
		if (args.length == 2) {
			connection = new ClientConnection(args[0], 
					new Integer(args[1]).intValue());
		} else {
			connection = new ClientConnection("localhost", 9876); 
		}
		
		// Start the GUI
		GuiManager guiManager = new GuiManager();
		
		
		// Connect to the chosen server
		System.out.println("Connecting to server: " + connection.toString());
		String error = connection.connect();
		if (error.length() != 0) {
			guiManager.outputString(error);
			JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		DiningLoop loop = new DiningLoop(connection, guiManager);
		guiManager.setInput(loop);
		loop.run();
		connection.disconnect();
		
		System.exit(0);
	}
}
