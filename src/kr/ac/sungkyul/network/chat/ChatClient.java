package kr.ac.sungkyul.network.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
	private final static String SERVER_IP = "172.16.106.32";
	private final static int SERVER_PORT = 1000;
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Socket socket = null;
		try {
			socket = new Socket();
			InetSocketAddress serverSocketAddress = new InetSocketAddress(SERVER_IP, SERVER_PORT);
			socket.connect(serverSocketAddress);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"),true);
			
			System.out.print("닉네임>> ");
			String nickname = scanner.nextLine();
			pw.println("join:"+nickname);
			pw.flush();
			
			ChatClientThread thread = new ChatClientThread(br);
			thread.start();
			
			while (true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.print(">> ");
				String input = scanner.nextLine();

				if ("quit".equals(input)) {
					pw.println("quit:");
					pw.flush();
					break;
				}else {
					pw.println("message:"+input);
				}
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null && socket.isClosed() == false) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}
}
