package kr.ac.sungkyul.network.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class ChatServerThread extends Thread {
	private String nickname;
	private Socket socket;
	List<Writer> listWriters;
	private PrintWriter pw;

	public ChatServerThread(Socket socket, List<Writer> listWriters) {
		this.socket = socket;
		this.listWriters = listWriters;
	}

	@Override
	public void run() {
		InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
		String remoteHostAddress = remoteAddress.getAddress().getHostAddress();
		int remoteHostPort = remoteAddress.getPort();
		log("연결 성공 from" + remoteHostAddress + ":" + remoteHostPort);

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

			while (true) {
				String request = br.readLine();
				log(request);
				if (request == null) {
					log("클라이언트로 부터 연결 끊김");
					doQuit(pw);
					break;
				}
				String[] tokens = request.split(":");
				if ("join".equals(tokens[0])) {
					doJoin(tokens[1], pw);
				} else if ("message".equals(tokens[0])) {
					doMessage(tokens[1]);
				} else if ("quit".equals(tokens[0])) {
					doQuit(pw);
					break;
				} else {
					log("에러:알수 없는 요청(" + tokens[0] + ")");
				}
			}
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

	private void doJoin(String nickName, Writer writer) {
		this.nickname = nickName;
		String data = nickName + "님이 참여하였습니다.";
		broadcast(data);
		addWriter(writer);
		pw.println("채팅에 입장하셨습니다.");
		pw.flush();
	}

	private void doMessage(String message) {
		String data = nickname + ":" + message;
		broadcast(data);
	}

	private void doQuit(Writer writer) {
		removeWriter(writer);
		String data = nickname + "님이 퇴장 하였습니다.";
		broadcast(data);
	}

	private void addWriter(Writer writer) {
		synchronized (listWriters) {
			listWriters.add(writer);
		}
	}

	private void removeWriter(Writer writer) {
		synchronized (listWriters) {
			listWriters.remove(writer);
		}
	}

	private void broadcast(String data) {
		synchronized (listWriters) {
			for (Writer writer : listWriters) {
				PrintWriter pw = (PrintWriter) writer;
				pw.println(data);
				pw.flush();
			}
		}
	}

	public void log(String message) {
		System.out.println("[Server Thread#" + getId() + "]" + message);
	}
}