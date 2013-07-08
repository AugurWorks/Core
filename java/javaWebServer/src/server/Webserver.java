package server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

// A Webserver waits for clients to connect, then starts a separate
// thread to handle the request.

public class Webserver {
	private static ServerSocket serverSocket;

	public static void main(String[] args) {
		Thread t = new Thread(new ReaderManager());
		t.start();
		try {
			serverSocket = new ServerSocket(8000); // Start, listen on
															// port 1020
			while (true) {
				Socket s = serverSocket.accept(); // Wait for a client to
													// connect
				new ClientHandler(s); // Handle the client in a separate thread
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}
}