package top.controller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import msg.Msg;
import top.model.Client;

@Controller//@Controller 어노테이션
public class ClientManageController {
	@RequestMapping("/main.top")// main.top로 연결하면 여기로 간다.
	public ModelAndView viewMain(ModelAndView mv) {//responsebody 없어도 무방 
		mv.setViewName("main");
		return mv;
	}
	
	@RequestMapping("/webclient.top")
	public void changeState(HttpServletRequest req, HttpServletResponse res) {
		Client client = null;
		try {
			//JMJ aws ip should be come here 
			String ServerIp = "52.9.16.167";//장민재 aws 주소 
			client = new Client(ServerIp, 8888);//aws 안의 클라이언트 
			Msg msg = new Msg(ServerIp, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String tid = req.getParameter("ip");// 매니지 앱에서 쓴 클라이언트 ip 
		String txt = req.getParameter("state");// 0 혹은 1
		System.out.println(tid + "--------servletCheck-------" + txt);
		Msg msg = new Msg("fromservlet", txt, tid);
		client.startClient(msg);
		try {
			res.sendRedirect("main.top");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("/iotclient.top")///이 파트는 manage_app으로 보낸다. 
	public void sendNotiFromIoTClient(HttpServletRequest req, HttpServletResponse res) {
		// 주소에다가 
		String id = req.getParameter("id");
		String txt = req.getParameter("txt");
		if(txt == null || txt.equals("null")) {
			System.out.println("txt is null");
			return;
		}
		int val = Integer.parseInt(txt);
		if (val >= 90) {
			try {
				res.sendRedirect("sendnotitoclient.top?id=" + id + "&txt=" + val);// 여기로 리다이렉트 한다. 
				//여기로 리다이렉트 해준다. 
			} catch (IOException e) {
				System.out.println("error with res.sendRedirect()");
				e.printStackTrace();
			}
		}else if(val <= 20 ){
			val = 0;
			try {
				res.sendRedirect("sendnotitopad.top?id=" + id + "&txt=" + val);
			} catch (IOException e) {
				System.out.println("Error while redirecting to sendnotitopad.top when val <=20 | IOException");
				e.printStackTrace();
			}
		}
		URL url = null;
		try {
			url = new URL("https://fcm.googleapis.com/fcm/send");
		}catch(Exception e) {
			System.out.println("Error while creating Firebase URL | MalformedURLException");
			e.printStackTrace();
		}
		HttpURLConnection conn = null;
		try {
			conn =  (HttpURLConnection) url.openConnection();
		}catch(Exception e) {
			System.out.println("Error while createing connection with Firebase URL | IOException");
			e.printStackTrace();
		}
		
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Authorization", "key="+ "AAAAZ5QtaP8:APA91bEsHwR9cnY3dBxPoPi84Mdh06BLrAAVjZQYnESJz5Tk7XjXDfLvC-AbsVAvyH3-YUr-cVVZVZPzNp8FAb80WTgb8zGrq728J93x4glDaIVzzYPyUGmEqC-DkLUlLn5nw6RD7lH_");
		conn.setRequestProperty("Content-Type", "application/json");


		try {
			JSONObject json = new JSONObject();
			json.put("to","/topics/temperature_manage");
			JSONObject info = new JSONObject();
			info.put("title", id);
			info.put("body", txt);
			json.put("notification", info);

			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
			out.write(json.toString());
			out.flush();
			conn.getInputStream();//이거는 나중에 보낸 곳에서 수신 할 경우를 위하여 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("/sendnotitoclient.top")// 이 부분은 패드로 보낸다.  
	public void sendNotiFromManager(HttpServletRequest req) {
		String id = req.getParameter("id");
		String txt = req.getParameter("txt");
		System.out.println("hahahdddd");
		URL url;
		try {
			url = new URL("https://fcm.googleapis.com/fcm/send");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", "key="
					+ "AAAAZ5QtaP8:APA91bEsHwR9cnY3dBxPoPi84Mdh06BLrAAVjZQYn"
					+ "ESJz5Tk7XjXDfLvC-AbsVAvyH3-YUr-cVVZVZPzNp8FAb80WTgb8zGr"
					+ "q728J93x4glDaIVzzYPyUGmEqC-DkLUlLn5nw6RD7lH_");

			conn.setRequestProperty("Content-Type", "application/json");
			JSONObject message = new JSONObject();
			message.put("to","/topics/temperature");
			message.put("priority", "high");
			JSONObject notification = new JSONObject();
			notification.put("title", id);
			notification.put("body", "WARNING! 온도가 50도 이상 입니다. 서버에 받은 값 : "+ txt);
			message.put("notification", notification);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			out.write(message.toString());
			out.flush();
			conn.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
