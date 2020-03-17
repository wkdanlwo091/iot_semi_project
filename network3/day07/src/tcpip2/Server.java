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
import java.util.Set;

public class Server {
	HashMap<String, ObjectOutputStream> maps = new HashMap<>();//ip oos
	HashMap<String, String> ids = new HashMap<>();//ip id
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
						socket = serverSocket.accept();
						new Receiver(socket).start();
						System.out.println(socket
								.getInetAddress());
					} catch (IOException e) {
						break;//e.printStackTrace();
					}
				}
			}
		};
		if(serverSocket != null) {
			new Thread(r).start();
		}
	}
	
	public void sendId() {
		Collection<String> id = ids.values();
		Iterator<String> it = id.iterator();
		ArrayList<String> list = new ArrayList<>();
		while(it.hasNext()) {
			list.add(it.next());
		}
		Msg msg = new Msg();
		msg.setIps(list);
		Sender sender = new Sender(msg);
		sender.start();
	}
	public void sendIp() {
		Set<String> keys = maps.keySet();
		Iterator<String> its = keys.iterator();
		ArrayList<String> list = new ArrayList<>();
		while(its.hasNext()) {
			list.add(its.next());
		}
		Msg msg = new Msg();
		msg.setIps(list);
		Sender sender = new Sender(msg);
		sender.start();
	}
	class Receiver extends Thread{
		InputStream is;
		ObjectInputStream ois;
		OutputStream os;
		ObjectOutputStream oos;
		Socket socket;
		public Receiver(Socket socket) throws IOException {
			this.socket = socket;
			is = socket.getInputStream();
			ois = new ObjectInputStream(is);
			os = socket.getOutputStream();
			oos = new ObjectOutputStream(os);
			maps.put(socket.getInetAddress().toString(),oos);
			//clientList(maps);
			try {
				Msg msg = (Msg) ois.readObject();
				System.out.println("접속자:"+msg.getId());
				ids.put(socket.getInetAddress().toString(), msg.getId());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			//sendId(); 
			//lb.setText("접속자:"+maps.size());
			System.out.println("접속자수:"+maps.size());
		}
		@Override
		public void run() {
			while(ois != null) {
				Msg msg = null;
				try {
					msg = (Msg) ois.readObject();
					System.out.println(msg.getId()+":"+msg.getTxt());
					//leftList.add(msg.getId()+":"+msg.getMsg(), 0);
					if(msg.getTxt().equals("q")) {
						System.out.println(ids.get(socket.getInetAddress().toString())+":Exit ..");
						maps.remove(socket.getInetAddress().toString());
						ids.remove(socket.getInetAddress().toString());
						sendId();
						//clientList(maps);
						//lb.setText("접속자:"+maps.size());
						System.out.println("접속자수:"+maps.size());
						break;
					}
					sendMsg(msg);
				} catch (Exception e) {
					maps.remove(socket.getInetAddress().toString());
					System.out.println(ids.get(socket.getInetAddress().toString())+":Exit ..");
					ids.remove(socket.getInetAddress().toString());
					sendId();
					//clientList(maps);
					//lb.setText("접속자:"+maps.size());
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
			System.out.println("sender");

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
			String tid = msg.getTid();//get Target id 
			try {
				Collection<String> col = ids.keySet();
				Iterator<String> it = col.iterator();
				String sip = "";
				System.out.println("sender2");
				while(it.hasNext()) {
					String key = it.next();
					if(ids.get(key).equals(tid)) {///ids 는 (ip , id)
						sip = key;
					}
				}
				System.out.println(sip);
				maps.get(sip).writeObject(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void sendMsg(Msg msg) {
		String tid = msg.getTid();
		if(tid == null || tid.equals("")) {
			Sender sender = 
					new Sender(msg);
			sender.start();
		}else {
			System.out.println("귓말");
			Sender2 sender2 = new Sender2(msg);
			sender2.start();
		}
	}
	
	public static void main(String[] args) {
		Server server = null;
		try {
			server = new Server(8888);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}







