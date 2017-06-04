package zplus2.service;

public class Item {
	private String id;
	private String msg;
	private String value;
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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "Item [id=" + id + ", msg=" + msg + ", value=" + value + "]";
	}
	
	
	
	
}
