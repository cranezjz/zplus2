package zplus2.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class ReadErrCodeConf {
	private String preValue="0";
	private String start="0";
	private String end="9999";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List readXML(File codeMapFile) throws Exception {
		List list = new ArrayList();
		SAXBuilder builder = new SAXBuilder();    
		Document doc = builder.build(codeMapFile);    
		Element root = doc.getRootElement();    
		start = root.getAttributeValue("start");
		end = root.getAttributeValue("end");
		validateListCode(root);//验证分类节点划定的错误码范围是否有重叠、越界的问题
		List allChildren = root.getChildren();    
		for(int i=0;i<allChildren.size();i++) {
			Element e = (Element)allChildren.get(i);
			String nodeName = e.getName();
			if("item".equals(nodeName)){
				addItem(e,list);
			}else{
				addListData(e,list);
			}
		}   
		return list;
	}

	
	@SuppressWarnings("unchecked")
	private void validateListCode(Element element) throws Exception {
		String parentStart = element.getAttributeValue("start");
		String parentEnd = element.getAttributeValue("end");
		List<Element> allChildren = element.getChildren();
		String prevEnd = "0";
		for (int i = 0; i < allChildren.size(); i++) {
			Element self = allChildren.get(i);
			if("list".equals(self.getName())){
				String selfStart = self.getAttributeValue("start");
				String selfEnd = self.getAttributeValue("end");
				String selfId = self.getAttributeValue("id");
				validateSelfElement(selfStart,selfEnd,selfId);//验证自身节点
				validateParentSubElement(parentStart, parentEnd,selfStart,selfEnd,selfId);//验证自身与父节点的包含关系
				if(i>0){
					validatePrevElement(prevEnd,selfStart,selfId);//验证与兄弟节点间的重叠关系
				}
				prevEnd = selfEnd;
				validateListCode(self);//递归验证
			}
		}
	}

	/**
	 * 检查自身的start是否小于end
	 * @param selfStart
	 * @param selfEnd
	 * @param selfNodeId
	 * @throws Exception
	 */
	private void validateSelfElement(String selfStart,String selfEnd,String selfNodeId) throws Exception {
		if(getInt(selfStart)>getInt(selfEnd)){
			throw new Exception("节点["+selfNodeId + "]end的值应大于start的值");
		}
	}
	/**
	 * 检查子节点的错误码是否在父节点范围内
	 * @param parentStart
	 * @param parentEnd
	 * @param subStart
	 * @param subEnd
	 * @param subNodeId
	 * @throws Exception
	 */
	private void validateParentSubElement(String parentStart, String parentEnd,String subStart,String subEnd,String subNodeId) throws Exception {
		if(getInt(parentStart)>getInt(subStart)){//子节点start<父节点的start 则抛出异常
			throw new Exception("校验失败！节点["+subNodeId+"]中start的值["+subStart+"]小于父节点的值。");
		}
		if(getInt(parentEnd)<getInt(subEnd)){//子节点end>父节点的end 则抛出异常
			throw new Exception("校验失败！节点["+subNodeId+"]中end的值["+subEnd+"]大于父节点的值。");
		}
	}
	/**
	 * 检查本节点的start 是否大于前一个兄弟节点的end
	 * @param selfStart
	 * @param selfEnd
	 * @param selfNodeId
	 * @throws Exception
	 */
	private void validatePrevElement(String prevEnd,String selfStart,String selfNodeId) throws Exception {
		if(getInt(selfStart)<=getInt(prevEnd)){
			throw new Exception("节点["+selfNodeId + "]的取值范围与上list节点的取值范围有交叉。");
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addListData(Element e, List list) throws Exception {
		ListData listData = new ListData();
		listData.setId(e.getAttributeValue("id"));
		listData.setStart(e.getAttributeValue("start"));
		listData.setEnd(e.getAttributeValue("end"));
		listData.setLabel(e.getAttributeValue("label"));
		
		this.start = e.getAttributeValue("start");
		this.end = e.getAttributeValue("end");
		this.preValue = String.valueOf(Integer.parseInt(this.start)-1);
		
		List listItem = new ArrayList();
		listData.setList(listItem);
		List allChildren =e.getChildren();
		for(int i=0;i<allChildren.size();i++) {
			Element subE = (Element)allChildren.get(i);
			String nodeName = subE.getName();
			if("item".equals(nodeName)){
				try {
					addItem(subE,listItem);
				} catch (Exception e1) {
					throw new Exception("["+subE.getAttributeValue("msg")+"]的错误码越界。");
				}
			}else{
				addListData(subE, listItem);
			}
		}
		list.add(listData);
	}

	private void addItem(Element e, List<Item> list) throws Exception {
		Item item = new Item();
		item.setId(e.getAttributeValue("id"));
		item.setMsg(e.getAttributeValue("msg"));
		String value = getValue(e.getAttributeValue("value"));
		item.setValue(value);
		list.add(item);
	}
	
	private String getValue(String attributeValue) throws Exception {
		if(attributeValue==null){
			preValue =String.valueOf(getInt(preValue)+1);
		}else{
			preValue = attributeValue;
		}
		if(getInt(preValue)<getInt(start) || getInt(preValue)>getInt(end) ){
			throw new Exception("错误码编号越界");
		}
		return preValue;
	}
	private int getInt(String value){
		return Integer.parseInt(value);
	}
}
