package tcpip2;

import java.io.Serializable;

public class Msg implements Serializable{
	String id;
	String msg;
	public Msg() {
	}
	public Msg(String id, String msg) {
		this.id = id;
		this.msg = msg;
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
	
}
