package tcpip2__;//�������� 

import java.io.Serializable;

public class Msg implements Serializable{
	
	private static final long serialVersionUID = 1L;

	String id;
	String msg;
	String ip;//���� �ӼӸ��� �ϰ��� �ϴ� ����� IP

	
	public Msg() {
		super();
	}



	public Msg(String id, String msg, String ip) {
		super();
		this.id = id;
		this.msg = msg;
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



	public String getIp() {
		return ip;
	}



	public void setIp(String ip) {
		this.ip = ip;
	}
	
	
	
	
}
