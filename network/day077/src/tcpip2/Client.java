package tcpip2;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

   Socket socket;
   Sender sender;
   boolean flag = false;
   SendData sendData;
   
   public Client() {}
   public Client(String address,int port) throws IOException {
      try {
         socket = new Socket(address, port);
      
      }catch(Exception e) {
         while(true) {
            System.out.println("Retry..");
            try {
               Thread.sleep(1000);
               socket = new Socket(address, port);
               break;
            } catch (Exception e1) {
               //e1.printStackTrace();
            }
         }
      }
      
      System.out.println("Connected Server:"+address);
      
      sender = new Sender(socket);
      
      Msg msg = new Msg("MJ2", null, null);
      sender.setMsg(msg);
      new Thread(sender).start();
      
      new Receiver(socket).start();
      sendData = new SendData();
   }
   
   class Receiver extends Thread{
      InputStream is;
      ObjectInputStream ois;
      
      public Receiver(Socket socket) throws IOException {
         is = socket.getInputStream();
         ois = new ObjectInputStream(is);
      }

      @Override
      public void run() {
         while(ois != null) {
            Msg msg = null;
            try {
               msg = (Msg) ois.readObject();
               if(msg.getTxt().equals("1")) {
                  sendData.setFlag(true);
                  new Thread(sendData).start();
               }else {
                  sendData.setFlag(false);
               }
               
               if(msg.getIps() == null || msg.getIps().size() == 0) {
                  System.out.println(
                        msg.getId()+":"+msg.getTxt()   
                           );
               }else {
                  ArrayList<String> list
                  = msg.getIps();
                  System.out.println(list);
               }
               
               
            }catch(Exception e) {
               System.out.println("Server Die");
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
   
   
   class Sender implements Runnable{

      OutputStream os;
      ObjectOutputStream oos;
      Msg msg;
      
      public Sender(Socket socket) throws IOException {
         os = socket.getOutputStream();
         oos = new ObjectOutputStream(os);
      }
      public void setMsg(Msg msg) {
         msg.setTxt(Integer.toString((int) (Math.random()*100)));
         this.msg = msg;
      }
      @Override
      public void run() {
         if(oos != null) {
            while(true) {
               try {
                  Thread.sleep(1000);
                  msg.setTxt(Integer.toString((int) (Math.random()*100)));
                  System.out.println("msg is : "+msg.getTxt() + " id is :" + msg.getId());
                  oos.writeObject(msg);
               } catch (IOException e) {
                  e.printStackTrace();
               } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }

            }
         }
      }
      
   }
   
   
   
   
   class SendData implements Runnable{
      boolean flag = false;
      
      public void setFlag(boolean flag) {
         this.flag = flag;
      }

      @Override
      public void run() {
         while(true) {
            if(this.flag == false) {
               continue;
            }
            try {
               Thread.sleep(500);
            } catch (InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      }
   }
   public static void main(String[] args) {
      Client client = null;
      try {
         client = new Client("70.12.231.248", 8888);
         //client.startClient2();
      } catch (IOException e) {
         e.printStackTrace();
      }
      //client.startClient();
   }
}