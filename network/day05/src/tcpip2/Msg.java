package tcpip2;//진만리거 

import java.io.Serializable;
import java.util.ArrayList;
public class Msg implements Serializable{
	
	private static final long serialVersionUID = 1L;

	String id;
	String msg;
	ArrayList<String> ips;//내가 귓속말을 하고자 하는 사람의 IP
	String ip;
	
	public Msg(String id, String msg, ArrayList<String> ips, String ip) {
		super();
		this.id = id;
		this.msg = msg;
		this.ips = ips;
		this.ip = ip;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public ArrayList<String> getIps() {
		return ips;
	}

	public void setIps(ArrayList<String> ips) {
		this.ips = ips;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
