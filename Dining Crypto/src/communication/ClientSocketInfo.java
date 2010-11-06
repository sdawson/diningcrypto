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
			System.out.println("in/out streams have been setup");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
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
