package tcpip2;//������ ��  ��Ű�� �̸��� ���ƾ� ģ���� ���� ��� �ȴ�. 

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Client {
   Socket socket;
   Sender sender;
   public Client() {
   }
   //Ŭ���̾�Ʈ���� ������ ������ ��� �ѷ��ֱ�  
   public Client(String address, int port) throws IOException {
      try {
         socket = new Socket(address, port);
      } catch (Exception e) {
         while (true) {
            System.out.println("ReConnecting...");
            try {
               Thread.sleep(1000);
               socket = new Socket(address, port);
               System.out.println("Connected Server: " + address);
               break;
            } catch (Exception e1) {
               //e1.printStackTrace();//�̰� ��������� ���� �ȳ���. 
            } // second try&catch end
         } // while end
      } // first try&catch end
      // socket �� �����Ǹ� sender ���� �Ͽ� socket �� ��ǲ //
      System.out.println("Connected Server: " + address);
      sender = new Sender(socket);
      // �������� ������ �޽����� ���� �� �ֵ��� �����带 ��ŸƮ // 
      new Receiver(socket).start();
   }
   class Receiver extends Thread {
      InputStream is;
      ObjectInputStream ois;
      HashMap<String, ObjectOutputStream>
  	  maps = new HashMap<>();

      public Receiver(Socket socket) throws IOException {
         is = socket.getInputStream();
         ois = new ObjectInputStream(is);
      }
      @Override
      public void run() {
         while (ois != null) {
            Object obj = null;
            try {
            	obj = ois.readObject();
               if(obj instanceof Msg) {
            	   //�޽��� ��ü�� ��� 
                   Msg msg = (Msg)obj;
                   System.out.println(msg.getId() + ":" + msg.getMsg() );
                   for(int i= 0 ;i < msg.getIps().size();i++) {
                       System.out.println(msg.getIps().get(i));
                   }
               }
            } catch (Exception e) {
            	System.out.println(e);
               System.out.println("Server off");
               break;
            }
         }
         try {
            if (ois != null)
               ois.close();
            if (socket != null)
               socket.close();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
   // ������ ��������� Sender �� �������Ѵ�. //
   // Thread -> Runnable , extends -> implements //
   class Sender implements Runnable {

      OutputStream os;
      // DataOutputStream -> ObjectOutputStream
      ObjectOutputStream oos;
      // String msg;
      Msg msg;
      // Client ���� ���� socket �� Sender�� //
      public Sender(Socket socket) throws IOException {
         os = socket.getOutputStream();
         oos = new ObjectOutputStream(os);

      }
      // �޾ƿ� msg ���Է� //
      // public void setMsg(String msg) {
      public void setMsg(Msg msg) {
         this.msg = msg;
      }
      @Override
      public void run() {
//         if(dos != null) {
         if (oos != null) {
            try {
               // �޽��� �Է��ϱ� //
//               dos.writeUTF(msg);
               oos.writeObject(msg);
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }
   public void startClient() {
      Scanner sc = new Scanner(System.in);
      while (true) {
         System.out.println("Input Msg");
         String ip = sc.nextLine();
         String txt = sc.nextLine();
         System.out.println(ip + " " + txt);
         Msg msg = null;
         if(ip == null || ip.equals("")) {//�׻� null�� �տ��� üũ�ؾ� �Ѵ�.
        	 msg = new Msg("jmj", txt, null, null);
         }else {
        	 ArrayList<String> temp = new ArrayList<String>();
        	 temp.add(ip);
        	 msg = new Msg("jmj", txt, temp, ip);
         }
         sender.setMsg(msg);

         // startClient ������ �ȿ� sender ������ ���� //
         new Thread(sender).start();

         if (txt.equals("q")) {
            // if (msg.equals("q")) {
            try {
               // �����尡 ��� ��ٸ��� ���� socket�� closed �Ǳ⶧���� exception�� �߻����� �ʴ´�. //
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            break;
         }
      }
      try {
         // while ������ �������� socket �� ��������Ѵ�. //
         socket.close();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      System.out.println("Client App End");
      sc.close();
   }
   public static void main(String[] args) {
      Client client = null;
      try {
         client = new Client("70.12.227.235", 8888);
      } catch (IOException e) {
         e.printStackTrace();
      }
      // Client ������ ����, Sender ������ ����, Server ���� Receiver �ʿ� //
      client.startClient();
   }
}