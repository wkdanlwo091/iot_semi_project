package tcpip2;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
public class Server {

	HashMap<String, ObjectOutputStream>
	maps = new HashMap<>();
	ServerSocket serverSocket;
	boolean aflag = true;
	
	public Server() {}
	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("Start Server");
		Runnable r = new Runnable() {
			@Override
			public void run() {
				while(aflag) {
					Socket socket = null;
					try {
						System.out.println("Server Ready..");
						socket = 
								serverSocket.accept();
						new Receiver(socket).start();
						System.out.println(socket.getInetAddress());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		new Thread(r).start();
	}
	

	public void sendIp() {
		Set<String> 
		keys = maps.keySet();
		Iterator<String>
		its = keys.iterator();
		ArrayList<String> list = new ArrayList<>();
		while(its.hasNext()) {
			list.add(its.next());
		}
		Msg msg = new Msg();
		msg.setIps(list);
		Sender sender = 
				new Sender(msg);
		sender.start();
	}
	
	class Receiver extends Thread{
		InputStream is;
		ObjectInputStream ois;
		OutputStream os;
		ObjectOutputStream oos;
		Socket socket;
		String ips = "";
		public Receiver(Socket socket) throws IOException {
			this.socket = socket;
			is = socket.getInputStream();
			ois = new ObjectInputStream(is);
			os = socket.getOutputStream();
			oos = new ObjectOutputStream(os);
			maps.put(socket.getInetAddress().toString(),oos);
			System.out.println("접속자수:"+maps.size());
		}
		@Override
		public void run() {
			while(ois != null) {
				Msg msg = null;
				try {
					msg = (Msg) ois.readObject();
					msg.setIps(convert(maps));
					System.out.println(msg.getId()+":"+msg.getMsg());
					if(msg.getMsg().equals("q")) {
						System.out.println(msg.getId()+":Exit ..");
						maps.remove(socket.getInetAddress().toString());
						System.out.println("접속자수:"+maps.size());
						break;
					}
					sendMsg(msg);
				} catch (Exception e) {
					maps.remove(
							socket.getInetAddress().toString()
							);
					System.out.println(
							socket.getInetAddress()+":Exit ..");
					System.out.println("접속자수:"+maps.size());
  				    break;
				}	
			}
			try {
				if(ois != null) {
					ois.close();
				}
				if(socket != null) {
					socket.close();
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}

		public ArrayList<String> convert(HashMap<String, ObjectOutputStream> sendMaps) {
			Set<String> 
			keys = sendMaps.keySet();
			Iterator<String>
			its = keys.iterator();
			ArrayList<String> list = new ArrayList<>();
			while(its.hasNext()) {
				list.add(its.next());
			}
			return list;
		}	
	}
	class Sender extends Thread{
		Msg msg;
		public Sender(Msg msg) {
			this.msg = msg;
		}
		@Override
		public void run() {
			// HashMap에 있는 oos를 꺼낸다음
			// for문을 돌리면서 전송 한다.
			Collection<ObjectOutputStream> 
			cols = maps.values();
			Iterator<ObjectOutputStream>
			its = cols.iterator();
			while(its.hasNext()) {
				try {
					its.next().writeObject(msg);
					System.out.println("--");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	class Sender2 extends Thread{
		Msg msg;
		public Sender2(Msg msg) {
			this.msg = msg;
		}
		@Override
		public void run() {
			String ip = msg.getIp();
			try {
				maps.get(ip).writeObject(msg);//ip 값 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendMsg(Msg msg) {
		String ip = msg.getIp();
		if(ip == null || ip.equals("")) {
			Sender sender = 
					new Sender(msg);
			sender.start();
		}
		else {
			Sender2 sender2 = 
					new Sender2(msg);
			sender2.start();
		}
	}
	public void serverStart() {
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.println("Input Msg & IP");
			String ip = sc.nextLine();
			String txt = sc.nextLine();
			if(txt.equals("q")) {
				break;
			}
			Msg msg = null;
			if(ip == null || ip.equals("")) {
				msg = new Msg("Admin",txt,null); 
			}else {
				msg = new Msg("Admin",txt, ip); 
			}
			sendMsg(msg);
		}
		sc.close();
	}
	
	public static void main(String[] args) {
		Server server = null;
		try {
			server = new Server(8888);
			server.serverStart();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}