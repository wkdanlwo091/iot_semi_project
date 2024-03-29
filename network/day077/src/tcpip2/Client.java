package tcpip2;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import msg.Msg;

public class Client {

	Socket socket;
	Sender sender;
	boolean flag = true; //
	SendData sendData;
	Receiver receiver;

	public Client(String ip, int port, String CID) {
		while (flag) {
			try {
				socket = new Socket(ip, port);
				flag = false;
			} catch (IOException e) {
				System.out.println("Retry...");
				try {
					Thread.sleep(1000);
					socket = new Socket(ip, port);
					flag = false;
				} catch (InterruptedException e1) {
					System.out.println("ERROR : Thread.sleep(1000); | InterruptedException");
				} catch (UnknownHostException e1) {
					System.out.println("ERROR : socket = new Socket(ip, port); | UnknownHostException");
				} catch (IOException e1) {
					System.out.println("ERROR : socket = new Socket(ip, port); | IOException");
				}
			}
		}
		System.out.println("Connected to server.");
		System.out.println("Server ip : " + ip);
		System.out.println("Server port : " + port);
		sender = new Sender(socket); // Sender class is runnable implemented class.
		Msg msg = new Msg(CID, null, null); // This is for client id to be displayed in the listview of
		sender.setMsg(msg);
		sender.setIp(ip);
		sender.setPort(port);
		new Thread(sender).start(); // this line will start the Thread.
		receiver = new Receiver(socket);
		receiver.setIp(ip);
		receiver.setPort(port);
		receiver.setSender(sender);
		receiver.start();
	}

	public class SendData implements Runnable {
		AtomicBoolean finished = new AtomicBoolean(false);
		LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();// 한 쓰레드에서 다른 쓰레드로 넘기

		boolean flag = false;
		Sender sender;
		String CID;

		public SendData() {
		}

		public SendData(Sender sender) {
			this.sender = sender;
			CID = sender.getMsg().getId();
		}

		public void setFlag(boolean flag) {
			this.flag = flag;
		}

		@Override
		public void run() {
			while (true) {
				if (flag == false) {
					continue;
				}
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Sending random number");
				Random r = new Random();
				int data = r.nextInt(100);
				String createdObject = Integer.toString(data);
				queue.offer(createdObject);

				Msg msg = new Msg(CID, data + "");
				sender.setMsg(msg);
				new Thread(sender).start();
			}
		}
	}

	public class Sender implements Runnable {
		OutputStream os;
		ObjectOutput oos;
		Socket socket;
		String IP;
		int PORT;
		Msg msg;

		public Sender() {
		}

		public Sender(Socket socket) {
			this.socket = socket;
			try {
				os = socket.getOutputStream();
				oos = new ObjectOutputStream(os);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public Msg getMsg() {
			return msg;
		}

		public void setMsg(Msg msg) {
			this.msg = msg;
		}

		public void setIp(String ip) {
			this.IP = ip;
		}

		public void setPort(int port) {
			this.PORT = port;
		}

		@Override
		public void run() {
			if (oos != null) {
				try {
					oos.writeObject(msg);
				} catch (IOException e) {
					System.out.println("Error while sending msg");
					if (oos != null) {
						try {
							oos.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						try {
							socket = new Socket(IP, PORT);
							return;
						} catch (UnknownHostException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
	}

	
	public class Receiver extends Thread {
		InputStream is;
		ObjectInputStream ois;
		Socket socket;
		Sender sender;
		String IP;
		int PORT;
		String CID;

		SendData sendData;
	 
		public Receiver() {
		}

		public Receiver(Socket socket) {
			this.socket = socket;
			try {
				is = socket.getInputStream();
				ois = new ObjectInputStream(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void setSender(Sender sender) {
			this.sender = sender;
			sendData = new SendData(sender);
			new Thread(sendData).start();
			CID = sender.getMsg().getId();
		}
		public void setIp(String ip) {
			this.IP = ip;
		}
		public void setPort(int port) {
			this.PORT = port;
		}
		@Override
		public void run() {
			while (ois != null) {
				Msg msg = null;
				try {
					msg = (Msg) ois.readObject();
					System.out.println("From tabserver : " + msg.getId());
					System.out.println("From tabserver : " + msg.getTxt());
					System.out.println("From tabserver : " + msg.getTid());
					if (msg.getTxt().equals("1")) {
						sendData.setFlag(true);
						new Thread(sendData).start();
					} else if (msg.getTxt().equals("0")) {
						sendData.setFlag(false);
					}
				} catch (Exception e) {
					while (true) {
						try {
							System.out.println("Trying to reconnect to server");
							if (ois != null) {
								ois.close();
							}
							if (socket != null) {
								socket.close();
							}
							System.out.println("catch() IP : " + IP);
							System.out.println("catch() PORT : " + PORT);
							System.out.println("catch() CID : " + CID);
							new Client(IP, PORT, CID);
							break;
						} catch (IOException e1) {
							System.out.println("Receiver run() catch() while(true) catch()");
							e1.printStackTrace();
						}
					}
					return;
				}
			}
		}
	}
	
	   public static void main(String[] args) {
		   Client client = new Client("70.12.231.248", 9999, "gas");//연결할 서버 패드의 아이피 , 서버 패드의 포트 
	   }

}
