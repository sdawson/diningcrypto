package server;

import java.io.IOException;
import java.net.ServerSocket;


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
	
	public void disconnect() throws IOException {
		serverSocket.close();
	}
}
