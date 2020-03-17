package tcpip2;//진만리 것 

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

   Socket socket;
   Sender sender;
   public Client() {
   }
   //클라이언트에서 서버에 접속한 사람 뿌려주기  
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
               e1.printStackTrace();
            } // second try&catch end
         } // while end
      } // first try&catch end

      // socket 이 생성되면 sender 생성 하여 socket 값 인풋 //
      System.out.println("Connected Server: " + address);
      sender = new Sender(socket);
      
      // 서버에서 보내는 메시지를 받을 수 있도록 쓰레드를 스타트 // 
      new Receiver(socket).start();

   }

   class Receiver extends Thread {
      InputStream is;
      ObjectInputStream ois;

      public Receiver(Socket socket) throws IOException {
         is = socket.getInputStream();
         ois = new ObjectInputStream(is);
      }

      @Override
      public void run() {
         while (ois != null) {
            Msg msg = null;
            try {
               msg = (Msg)ois.readObject();
               System.out.println(msg.getId() + ":" + msg.getMsg());
            } catch (Exception e) {
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

   // 소켓이 만들어지면 Sender 을 만들어야한다. //
   // Thread -> Runnable , extends -> implements //
   class Sender implements Runnable {

      OutputStream os;
      // DataOutputStream -> ObjectOutputStream
      ObjectOutputStream oos;
      // String msg;
      Msg msg;

      // Client 에서 만든 socket 을 Sender에 //
      public Sender(Socket socket) throws IOException {
         os = socket.getOutputStream();
         oos = new ObjectOutputStream(os);

      }

      // 받아온 msg 를입력 //
      // public void setMsg(String msg) {
      public void setMsg(Msg msg) {
         this.msg = msg;
      }

      @Override
      public void run() {
//         if(dos != null) {
         if (oos != null) {
            try {
               // 메시지 입력하기 //
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
         String txt = sc.nextLine();
         if(txt.equals("lookup")) {
        	 //lookup하면  모든 데이터 조회하기 
         }
         Msg msg = new Msg("Client_jmj", txt);
         // String msg = sc.nextLine();
         // 입력받은 msg 를 setter 로 저장 //
         sender.setMsg(msg);

         // startClient 쓰레드 안에 sender 쓰레드 시작 //
         new Thread(sender).start();

         if (txt.equals("q")) {
            // if (msg.equals("q")) {
            try {
               // 쓰레드가 잠시 기다리는 동안 socket이 closed 되기때문에 exception이 발생하지 않는다. //
               Thread.sleep(1000);
            } catch (InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            break;
         }
      }
      try {
         // while 루프가 끝났으니 socket 을 끊어줘야한다. //
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
         client = new Client("70.12.113.220", 8888);
      } catch (IOException e) {
         e.printStackTrace();
      }
      // Client 쓰레드 시작, Sender 쓰레드 시작, Server 에는 Receiver 필요 //
      client.startClient();
   }
}