package zplus2.dao;

import java.util.List;

public class ListData extends EmptyData{
	private List<EmptyData> list;
	private String id;
	private String start;
	private String end;
	private String label;
	
	public List<EmptyData> getList() {
		return list;
	}
	public void setList(List<EmptyData> list) {
		this.list = list;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	@Override
	public String toString() {
		return "ListData [list=" + list + ", id=" + id + ", start=" + start + ", end=" + end + "]";
	}
	@Override
	public String getValue() {
		return null;
	}
	
	
}
