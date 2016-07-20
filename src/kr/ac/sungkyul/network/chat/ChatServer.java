package kr.ac.sungkyul.network.chat;

import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	private final static int SERVER_PORT = 1000;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		List<Writer> listWriters = new ArrayList<Writer>();
		try {

			serverSocket = new ServerSocket();

			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			serverSocket.bind( new InetSocketAddress( hostAddress, SERVER_PORT ) );
			System.out.println(( "bind " + hostAddress + ":" + SERVER_PORT ));

			while (true) {
				Socket socket = serverSocket.accept(); // blocking
				ChatServerThread thread = new ChatServerThread(socket, listWriters);
				thread.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null && serverSocket.isClosed() == false) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
