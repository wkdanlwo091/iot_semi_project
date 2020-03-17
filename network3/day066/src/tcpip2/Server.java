package tcpip2;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
	HashMap<String, ObjectOutputStream> maps = new HashMap<>();//브로드 캐스트 할 때 사용 
	HashMap<String, String> ids = new HashMap<>();//ip address와, id 접속자 표현 
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
						System.out.println(socket.getInetAddress());
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
	public void sendIp() {//ip address를 broadcast 해준다. 
		Set<String> keys = maps.keySet();
		Iterator<String> its = keys.iterator();
		ArrayList<String> list = new ArrayList<>();
		while(its.hasNext()) {
			list.add(its.next());//ip address를 list에 담는다.
		}
		Msg msg = new Msg();
		msg.setIps(list);
		Sender sender = new Sender(msg);
		sender.start();
	}
	class Receiver extends Thread{//접속된 클라이언트 당 리시버가 생긴다. 서버에는 리시버 쓰레드 여러개 
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
			sendId();//모든 클라이언트들에게 브로드 캐스팅 하는 것 
			//lb.setText("접속자:"+maps.size());
			System.out.println("접속자수:"+maps.size());
		}
		@Override
		public void run() {
			while(ois != null) {
				Msg msg = null;
				try {
					msg = (Msg) ois.readObject();
					System.out.println(
						msg.getId()+":"+msg.getMsg());
					//leftList.add(msg.getId()+":"+msg.getMsg(), 0);
					if(msg.getMsg().equals("q")) {
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
				} catch (Exception e) {//그냥 나갔을 때도 소켓은 끊기지 않는다.  
					maps.remove(socket.getInetAddress().toString());
					System.out.println(	ids.get(socket.getInetAddress().toString())+":Exit ..");
					ids.remove(socket.getInetAddress().toString());
					sendId();
					//clientList(maps);
					//lb.setText("접속자:"+maps.size());
					System.out.println("접속자수:"+maps.size());
  				    break;
				}	
			}
			//혹시 ois, socket from client의 리소스를 모두 release한다. 
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
			Collection<ObjectOutputStream> cols = maps.values();
			Iterator<ObjectOutputStream> its = cols.iterator();
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
	
	class Sender2 extends Thread{//귓속말 
		Msg msg;
		public Sender2(Msg msg) {
			this.msg = msg;
		}
		@Override
		public void run() {
			String ip = msg.getIp();
			try {
				Collection<String> col = ids.keySet(); //ids --> id와 ip col에는 ip들이 있다. 
				Iterator<String> it = col.iterator();//col에는 ip가 있다. 
				String sip = "";
				System.out.println("sender2");
				while(it.hasNext()) {
					String key = it.next();
					if(ids.get(key).equals(ip)) {
						sip = key;//key값이 ip가 된다. 
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
		String ip = msg.getIp();
		if(ip == null || ip.equals("")) {
			Sender sender = 
					new Sender(msg);
			sender.start();
		}else{
			System.out.println("귓말");
			Sender2 sender2 = 
					new Sender2(msg);
			sender2.start();
		}
	}
	
	Frame frame;
	List leftList,rightList;
	Panel panel,centerPanel;
	TextField ipTx, txtTx;
	Label lb;
	
	Button button;
	
	ArrayList<String> ipList;
	
	public void clientList(
	HashMap<String,ObjectOutputStream> maps) {
		Set<String> keys = maps.keySet();
		Iterator<String> skeys = 
				keys.iterator();
		rightList.removeAll();
		ipList = new ArrayList<>();
		while(skeys.hasNext()) {
			String ip = skeys.next();
			ipList.add(ip);
			rightList.add(ip);
		}
		
	}
	
	
	public void serverStart() {
		frame = new Frame();
		leftList = new List();
		rightList = new List();
		panel = new Panel();
		centerPanel = new Panel();
		ipTx = new TextField(8); 
		txtTx = new TextField(8);
		button = new Button("SEND");
		lb = new Label("접속자수:0");
		
		panel.add(lb);
		panel.add(ipTx);
		panel.add(txtTx);
		panel.add(button);
		
		
		frame.add(panel,"North");
		centerPanel.setLayout(
				new GridLayout(1, 2));
		centerPanel.add(leftList);
		centerPanel.add(rightList);
		frame.add(centerPanel,"Center");
		
		frame.setSize(500,400);
		frame.setVisible(true);
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = ipTx.getText();
				String txt = txtTx.getText();
				if(ip == null || ip.equals("")) {
					Msg msg = new Msg("Admin", txt, "");
					Sender sender = new Sender(msg);
					sender.start();
				}else {
					Msg msg = new Msg("Admin", txt, ip);
					Sender2 sender2 = new Sender2(msg);
					sender2.start();
				}
			}
		});
		
		rightList.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				String ip = 
						ipList.get(
					         (int)e.getItem()
						);
				ipTx.setText(ip);
				
			}
		});
		
		
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
				if(serverSocket != null) {
					try {
						serverSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				System.exit(0);
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
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







