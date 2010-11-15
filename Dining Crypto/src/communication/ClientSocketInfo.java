package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocketInfo {
	private Socket clientSocket = null;
	private ObjectInputStream in = null;
	private ObjectOutputStream out = null;

	public ClientSocketInfo(Socket clientSocket) {
		this.clientSocket = clientSocket;
		try {
			out = new ObjectOutputStream(this.clientSocket.getOutputStream());
			in = new ObjectInputStream(this.clientSocket.getInputStream());
			System.out.println("in/out streams have been set up");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public Message receiveMessage() throws IOException {
		Message newMessage = null;

		try {
			newMessage = (Message) in.readObject();
		} catch (ClassNotFoundException e) {
			return null;
		}
		return newMessage;
	}

	public KeySet receiveKeySet() throws IOException {
		KeySet keyset = null;

		try {
			keyset = (KeySet) in.readObject();
		} catch (ClassNotFoundException e) {
			return null;
		}
		return keyset;
	}

	public void send(Message message) throws IOException {
		out.writeObject(message);
	}

	public void send(KeySet keys) throws IOException {
		out.writeObject(keys);
	}

	public ObjectInputStream getInputStream() {
		return this.in;
	}

	public ObjectOutputStream getOutputStream() {
		return this.out;
	}

	public void close() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
	}
}
