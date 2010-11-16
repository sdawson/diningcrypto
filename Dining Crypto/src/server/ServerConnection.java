package server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * The ServerConnection class encapsulates the process
 * of waiting for a client to connect, then passing
 * this connection back to the calling class.
 * 
 * @author Sophie Dawson
 *
 */
public class ServerConnection {
	private ServerSocket serverSocket = null;
	
	public ServerConnection(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Accept a connection from a client attempting
	 * to connect to the server, then create a new
	 * ClientSocketInfo object and return this.
	 * @return The ClientSocketInfo object containing
	 * all connection information.
	 */
	public ClientSocketInfo acceptConnection() {
		try {
			ClientSocketInfo csi = new ClientSocketInfo(serverSocket.accept());
			System.out.println("a client has connected " + csi.toString());
			return csi;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	/**
	 * Disconnect the server-side socket.
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		serverSocket.close();
	}
}
