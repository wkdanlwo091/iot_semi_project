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


@WebServlet({ "/WebServerServlet", "/webapp" })//  http://localhost/web/webapp�� Ÿ���� �ϸ� main.jsp�� ������ �ȴ�.
public class ClientManageController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Client client;
	Msg msg;
	protected void service(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		client = new Client("70.12.227.235", 9999); // ������ Ŭ���̾�Ʈ�� ���� 
		String target_ip = request.getParameter("ip");
		String state = request.getParameter("state");
		System.out.println(target_ip + " " + state);
		if (target_ip == null || target_ip.equals("")) {
			System.out.println("AAA");
			msg = new Msg("Webserver", state, null); // ��ε�ĳ�����Ѵ�. 
		} else {
			System.out.println("BBB");
			msg = new Msg("Webserver", state, target_ip); // �ӼӸ� �Ѵ�.
		}
		client.putMsg(msg);//Ŭ���̾�Ʈ���� ���� ������. 
		response.sendRedirect("main.jsp");// main.jsp�� �ٽ� ������.
	}
}

*/