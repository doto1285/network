package kr.ac.sungkyul.network.chat;

import java.io.BufferedReader;
import java.io.IOException;

public class ChatClientThread extends Thread{
	private BufferedReader br;
	
	public ChatClientThread(BufferedReader br) {
		this.br = br;
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				String message = br.readLine();
				System.out.println(message);
			}
		} catch (Exception e) {
			
		}
	}
}