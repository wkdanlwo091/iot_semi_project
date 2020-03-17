package tcpip1;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	Socket socket;//소켓 만든다. 
	Sender sender;
	public Client() {
	}
	public Client(String address, int port) throws IOException{
		try {
			socket = new Socket(address, port);
		} catch (Exception e) {//익셉션 2가지를 한개로 매핑했다. 
			while(true) {
				System.out.println("Retry ....");
				try {
					Thread.sleep(1000);// 바로 재도전 하지 말고 1초 기달린 후 재 접속한다. 
					socket = new Socket(address, port);
					///여기서 Sender를 만들어야 한다. 
					break;
				} catch (Exception e1) {//익셉션을 하나로 묶었다. 
					e1.printStackTrace();
				}//에러 난 후 1초뒤 다시 실행해라 
			}
		}//소켓 포트와 주소 매핑한다. exception이 나면 
		System.out.println("Connected Server : " + address);
		sender = new Sender(socket);
	}
	
	class Sender implements Runnable{
		OutputStream os;
		DataOutputStream dos;
		String msg;
		public Sender(Socket socket) throws IOException {//Client에서 만든 소켓을 여기다가 넣는다. 
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
		}
		public void serMsg(String msg) {
			this.msg = msg;
		}
		@Override
		public void run() {
			if(dos != null) {
				try {
					dos.writeUTF(msg);//클라이언트에서 q를 보냈는데 에러가 났다. 
					//여기는 중요한 포인트 

					
				} catch (IOException e) {
					e.printStackTrace();
				}//writeUTF ---> 2바이트 형태로 데이터를 보내라 
			}
		}
	}
	
	public void startClient() {//클라이언트 쪽에서 메시지를 입력하는 함수 // 보내는 와중에도 보낼 수 있게 하려고 스레드로 만듬 
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.println("Input Msg");
			String msg = sc.nextLine();// 이 문자를 Sender한테 준다.
			sender.serMsg(msg);
			new Thread(sender).start();//q 보내려고 했는데 소켓이 꺼졌다. 
			if(msg.equals("q")) {
				try {
					Thread.sleep(500);//이게 해결 방법 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		
		try {
			socket.close();//소켓 종료 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("End Client");
		sc.close();
	}
	public static void main(String[] args) {
		Client client = null;
		try {
			client = new Client("70.12.227.235", 8888);
		} catch (IOException e) {
			e.printStackTrace();
		}//접속할 서버 주소 , 포트 번호 
		client.startClient();
	}
}