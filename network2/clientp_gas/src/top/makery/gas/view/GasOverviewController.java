package top.makery.gas.view;


import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import top.makery.gas.MainApp;

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
import serial.SerialConnect;

//label에 접근하기 위해 인스턴스 변수를 추가한다.
//여기서 sender receiver thread 구현??
// run에서 task를 돌리는게 최선인 것 같다. 

public class GasOverviewController {//테이블에 gas 데이터와 sent sending 데이터를 보낸다. 
	 	
    @FXML
    private Label ppm;
    @FXML
    private Label sendingOrstopped;
    private MainApp mainApp;
    
    Socket socket;
	Sender sender;
	boolean flag = true; //
	SendData sendData;
	Receiver receiver;
	
	String myIp;
	int myPort;
	String myCID;

	public void ClientExample(String ip, int port, String CID) {
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
		myIp = ip;
		myPort = port;
		myCID = CID;
		sender = new Sender(socket); // Sender class is runnable implemented class.
		Msg msg = new Msg(CID, null, null); // This is for client id to be displayed in the listview of
		sender.setMsg(msg);
		sender.setIp(ip);
		sender.setPort(port);
		new Thread(sender).start(); // this line will start the Thread.
		//여기 아래서부터는 Task 이용
		receiver = new Receiver(socket);
		receiver.setIp(ip);
		receiver.setPort(port);
		receiver.setSender(sender);
		receiver.start();
	}	
		
	public class SendData implements Runnable{// 랜덤 데이터 보내기 
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
			IntegerProperty value = new SimpleIntegerProperty(0);
	        //여기 아래서부터 Task 이용
			Task<Void> task = new Task<Void>() {
	                @Override
	                protected Void call() {
	                	while(true) {
	                		if(flag == false) {
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

	        				Msg msg = new Msg(CID, data + "");
	        				sender.setMsg(msg);
	        				new Thread(sender).start();// 값 한개 보내기 
	                        Platform.runLater(() -> value.setValue(data));
	                	}
	                }
	            };
	            Thread th = new Thread(task);
	            th.setDaemon(true);
	            th.start();//여기서 리시버를 구현해야 한다. 
	            ppm.textProperty().bind(value.asString());
	            
				StringProperty value2 = new SimpleStringProperty("");

				Task<Void> task2 = new Task<Void>() {
	                @Override
	                protected Void call() {
	                		
	                        Platform.runLater(() -> value2.setValue("Receiving"));
						return null;

	                }
	            };
	            Thread th2 = new Thread(task2);
	            th2.setDaemon(true);
	            th2.start();//여기서 리시버를 구현해야 한다. 
	            sendingOrstopped.textProperty().bind(value2);

	            /*
	            
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

				Msg msg = new Msg(CID, data + "");
				sender.setMsg(msg);
				new Thread(sender).start();// 값 한개 보내기 
			}
			*/
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
	 
		SerialConnect mj;
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
			new Thread(sendData).start();/// sendData 스레드 시작 
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
						
						
				        StringProperty value = new SimpleStringProperty("");
				        Task<Void> task = new Task<Void>() {
			                @Override
			                protected Void call() {
			                    // Update the GUI on the JavaFX Application Thread
			                    Platform.runLater(() -> value.setValue("stopped"));
			                    return null;
			                }
			            };
			            Thread th = new Thread(task);
			            th.setDaemon(true);
			            th.start();//여기서 리시버를 구현해야 한다. 
			            
			            
			            
			        //아래 코드들을 위에서 작성하였다. 
			        sendingOrstopped.textProperty().bind(value);

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
							//new ClientExample(IP, PORT, CID);
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
    public GasOverviewController() {
    }
	public void setMainApp(MainApp mainApp, int i, String condition) {
        this.mainApp = mainApp;

        //sending 하는 label을 멈추었다고 하는 task
        // 단 하나의 label을 건드리려고 해도 task를 사용해야 한다. 
        // gui는 task로 접근 
        StringProperty value = new SimpleStringProperty("");

        Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    // Update the GUI on the JavaFX Application Thread
                    Platform.runLater(() -> value.setValue("stopped"));
                    return null;
                }
            };
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();//여기서 리시버를 구현해야 한다. 
            
        //아래 코드들을 위에서 작성하였다. 
        sendingOrstopped.textProperty().bind(value);
        ClientExample("70.12.231.248", 9999, "gas"); //이게 sender 가져온다.

        
    }
}