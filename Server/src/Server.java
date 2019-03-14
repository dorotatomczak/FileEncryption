import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	static final int port = 1234;

	public static void main(String[] args) {
		
		try (ServerSocket serverSocket = new ServerSocket(port)){
			
			while(true) {
				final Socket socket = serverSocket.accept();
				System.out.println("Received a connection from "+socket);
				socket.close();
				// TODO receive file
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
