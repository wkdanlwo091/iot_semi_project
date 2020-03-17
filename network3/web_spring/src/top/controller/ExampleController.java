package top.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import msg.Msg;
import top.model.Client;

@Controller
public class ExampleController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Client client;
	Msg msg;
	
	@RequestMapping("/webapp.mc")
	@ResponseBody
	public ModelAndView service2(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException {
		ModelAndView mv = new ModelAndView();
		System.out.println("yes");
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

		
		mv.setViewName("main");// modelandview를 main으로 설정하면 main.jsp로 넘어간다. 
		return mv;
	}

}