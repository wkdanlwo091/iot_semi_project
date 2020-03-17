package com.sds;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tcpip2.Client;
import tcpip2.Msg;
@WebServlet({ "/WebAppServlet", "/webappservlet" })
public class WebAppServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Client client;//tcpip2

    public WebAppServlet() {

    }
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ip = request.getParameter("ip");
		String port = request.getParameter("port");
		String txt = request.getParameter("txt");

		starts(ip, Integer.parseInt(port));
		
		Msg msg = null;
		if(ip == null || ip.equals("")) {
			msg = new Msg("WebAdmin", txt, null);
		}else {
			System.out.println("ip is ");
			msg = new Msg("WebAdmin", txt, ip);
		}
		client.startClient(msg);
	}
	private void starts(String ip, int port) {
		try {
			client = new Client(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}