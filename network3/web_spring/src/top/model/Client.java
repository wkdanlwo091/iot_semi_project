package top.model;
import msg.Msg;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Client {
	Socket socket;
	Sender sender;
	String ip;
	int port;
	final static String CID = "web_server_client";
	public Client() {
	}
	public Client(String sIP, int sPort) throws IOException {
		this.ip = sIP;
		this.port = sPort;
		try {
			socket = new Socket(ip, port);
		} catch (Exception e) {
			while (true) {
				System.out.println("Retry..");
				try {
					Thread.sleep(1000);
					socket = new Socket(ip, port);
					break;
				} catch (Exception e1) {
					// e1.printStackTrace();
				}
			}
		}
		System.out.println("Connected Server:" + ip);

		// sender = new Sender(socket);
		// Msg msg = new Msg(CID, null, null);
		// sender.setMsg(msg);
		// new Thread(sender).start();
	}

	// class Receiver extends Thread {
	// InputStream is;
	// ObjectInputStream ois;
	//
	// public Receiver(Socket socket) throws IOException {
	// is = socket.getInputStream();
	// ois = new ObjectInputStream(is);
	// }
	//
	// @Override
	// public void run() {
	// while (ois != null) {
	// Msg msg = null;
	// try {
	// msg = (Msg) ois.readObject();
	//// sender.setMsg(msg);
	//// new Thread(sender).start();
	//
	//// if (msg.getTxt().equals("1")) {
	//// sendData.setFlag(true);
	//// new Thread(sendData).start();
	//// } else {
	//// sendData.setFlag(false);
	//// }
	//
	// if (msg.getIps() == null || msg.getIps().size() == 0) {
	// System.out.println(msg.getId() + ":" + msg.getTxt());
	// } else {
	// ArrayList<String> list = msg.getIps();
	// System.out.println(list);
	// }
	//
	// } catch (Exception e) {
	// System.out.println("Server Die");
	// break;
	// }
	//
	// try {
	// if (ois != null) {
	// ois.close();
	// }
	// if (socket != null) {
	// socket.close();
	// }
	//// new Client(ip, port);
	//// new Client("70.12.231.255", 9999);
	//// return;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// }
	class Sender implements Runnable {
		OutputStream os;
		ObjectOutputStream oos;
		Msg msg;
		public Sender(Socket socket) throws IOException {
			os = socket.getOutputStream();
			oos = new ObjectOutputStream(os);
			// oos.reset();
		}
		public void setMsg(Msg msg) {
			this.msg = msg;
		}
		@Override
		public void run() {
			if (oos != null) {
				try {
					if (socket == null) {
						System.out.println("Socket is null");
					}
					oos.writeObject(msg);
					System.out.println("/////" + msg.getId());
					System.out.println("/////" + msg.getTxt());
					System.out.println("/////" + msg.getTid());
					// oos.flush();
				} catch (IOException e) {
					// e.printStackTrace();
					// try {
					//// if (oos != null) {
					//// oos.close();
					//// }
					// if (socket != null) {
					// socket.close();
					// }
					//
					// System.out.println("socket is null");
					//// new Client("192.168.43.2", 9999);
					//// new Client("70.12.231.255", 9999);
					//
					// } catch (Exception e1) {
					// System.out.println("oos.writeObject(msg) error");
					// e1.printStackTrace();
					// }
				}
				// finally {
				// try {
				// if (oos != null) {
				// System.out.println("oos!=null");
				// oos.close();
				// }
				// if (socket != null) {
				// System.out.println("socket!=null");
				// socket.close();
				// }
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }
				// sender.setMsg(msg);
				// new Thread(sender).start();
				// return;
			}
		}
	}

	// class SendData implements Runnable {
	// boolean flag = false;
	//
	// public void setFlag(boolean flag) {
	// this.flag = flag;
	// }
	//
	// @Override
	// public void run() {
	// while (true) {
	// if (this.flag == false) {
	// continue;
	// }
	// try {
	// Thread.sleep(500);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// System.out.println("Random...");
	// Random r = new Random();
	// int data = r.nextInt(100);
	// Msg msg = new Msg(cid, data + "");
	// sender.setMsg(msg);
	// new Thread(sender).start();
	// }
	// }
	//
	// }
	public void putMsg(Msg msg) {
		System.out.println("state : " + msg.getTxt());
		System.out.println("ip : " + msg.getTid());
		msg.setId(CID);
		try {
			sender = new Sender(socket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sender.setMsg(msg);
		new Thread(sender).start();
	}
}