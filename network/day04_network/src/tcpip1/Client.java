package tcpip1;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	Socket socket;//���� �����. 
	Sender sender;
	public Client() {
	}
	public Client(String address, int port) throws IOException{
		try {
			socket = new Socket(address, port);
		} catch (Exception e) {//�ͼ��� 2������ �Ѱ��� �����ߴ�. 
			while(true) {
				System.out.println("Retry ....");
				try {
					Thread.sleep(1000);// �ٷ� �絵�� ���� ���� 1�� ��޸� �� �� �����Ѵ�. 
					socket = new Socket(address, port);
					///���⼭ Sender�� ������ �Ѵ�. 
					break;
				} catch (Exception e1) {//�ͼ����� �ϳ��� ������. 
					e1.printStackTrace();
				}//���� �� �� 1�ʵ� �ٽ� �����ض� 
			}
		}//���� ��Ʈ�� �ּ� �����Ѵ�. exception�� ���� 
		System.out.println("Connected Server : " + address);
		sender = new Sender(socket);
	}
	
	class Sender implements Runnable{
		OutputStream os;
		DataOutputStream dos;
		String msg;
		public Sender(Socket socket) throws IOException {//Client���� ���� ������ ����ٰ� �ִ´�. 
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
					dos.writeUTF(msg);//Ŭ���̾�Ʈ���� q�� ���´µ� ������ ����. 
					//����� �߿��� ����Ʈ 

					
				} catch (IOException e) {
					e.printStackTrace();
				}//writeUTF ---> 2����Ʈ ���·� �����͸� ������ 
			}
		}
	}
	
	public void startClient() {//Ŭ���̾�Ʈ �ʿ��� �޽����� �Է��ϴ� �Լ� // ������ ���߿��� ���� �� �ְ� �Ϸ��� ������� ���� 
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.println("Input Msg");
			String msg = sc.nextLine();// �� ���ڸ� Sender���� �ش�.
			sender.serMsg(msg);
			new Thread(sender).start();//q �������� �ߴµ� ������ ������. 
			if(msg.equals("q")) {
				try {
					Thread.sleep(500);//�̰� �ذ� ��� 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		
		try {
			socket.close();//���� ���� 
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
		}//������ ���� �ּ� , ��Ʈ ��ȣ 
		client.startClient();
	}
}