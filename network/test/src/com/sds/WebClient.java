package com.sds;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tcpip2.Client;
import tcpip2.Msg;

/**
 * Servlet implementation class WebClient
 */
@WebServlet({ "/WebClient", "/webclient" })
public class WebClient extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Client client;//tcpip2
    public WebClient() {
    }
    public WebClient(int port) {

    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String ip = request.getParameter("ip");
		String txt = request.getParameter("txt");
		String port = request.getParameter("port");
		WebClient(ip, Integer.parseInt(port));
		Msg msg = null;
		
		if(ip == null || ip.equals("")) {
			msg = new Msg("WebAdmin", txt, null);
		}else {
			msg = new Msg("WebAdmin", txt, null);
		}
		client.startClient(msg);
	}
	private void WebClient(String ip, int port) {
    	try {
    		client = new Client(ip, port);
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
	}
}
