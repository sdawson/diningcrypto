package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

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
			System.out.println("waiting for client to connect");
			ClientSocketInfo csi = new ClientSocketInfo(serverSocket.accept());
			System.out.println("a client has connected " + csi.toString());
			return csi;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	public Message receive(ClientSocketInfo csi) throws IOException {
		Message newMessage = null;
		
		try {
			newMessage = (Message) csi.getInputStream().readObject();
		} catch (ClassNotFoundException e) {
			return null;
		}
		return newMessage;
	}
	
	public void send(Message message, ClientSocketInfo csi) throws IOException {
		csi.getOutputStream().writeObject(message);
	}

	public void sendKeysWithAck(Map<ClientSocketInfo, KeySet> keyMap) throws IOException {
		Iterator<Map.Entry<ClientSocketInfo, KeySet>> iterate = keyMap.entrySet().iterator();
		while (iterate.hasNext()) {
			Map.Entry<ClientSocketInfo, KeySet> entry =
				(Map.Entry<ClientSocketInfo, KeySet>) iterate.next();
			entry.getKey().getOutputStream().writeObject(keyMap);
			// Wait for ack before sending the next object
			Message reply = receive(entry.getKey());
			if (reply.getMessage().equals(new String("OK"))) {
				// successful ack
				continue;
			} else {
				// wait??  Or try to resend.....
				System.err.println("Error: Sending key to client failed");
				System.exit(1);
			}
		}
	}
	
	public void sendOutputWithAck(ArrayList<ClientSocketInfo> clients,
			Message result) throws IOException {
		for (ClientSocketInfo c : clients) {
			c.getOutputStream().writeObject(result);
			// Wait for acknowledgement before sending the result to the next client
			Message reply = receive(c);
			if (reply.getMessage().equals(new String("OK"))) {
				continue;
			} else {
				System.err.println("Error: Sending result for the round failed.");
				System.exit(1);
			}
		}
	}
	
	public void disconnect() throws IOException {
		serverSocket.close();
	}
}
