package top.controller;

/*
import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import top.model.Client;
import msg.Msg;


@WebServlet({ "/WebServerServlet", "/webapp" })//  http://localhost/web/webapp을 타이핑 하면 main.jsp로 매핑이 된다.
public class ClientManageController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Client client;
	Msg msg;
	protected void service(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		client = new Client("70.12.227.235", 9999); // 웹서버 클라이언트로 연결 
		String target_ip = request.getParameter("ip");
		String state = request.getParameter("state");
		System.out.println(target_ip + " " + state);
		if (target_ip == null || target_ip.equals("")) {
			System.out.println("AAA");
			msg = new Msg("Webserver", state, null); // 브로드캐스팅한다. 
		} else {
			System.out.println("BBB");
			msg = new Msg("Webserver", state, target_ip); // 귓속말 한다.
		}
		client.putMsg(msg);//클라이언트에게 값을 보낸다. 
		response.sendRedirect("main.jsp");// main.jsp를 다시 보낸다.
	}
}

*/