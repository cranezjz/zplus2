package zplus2.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import zplus2.dao.EmptyData;
import zplus2.dao.Item;
import zplus2.dao.ListData;
import zplus2.util.Log;

public class ReadErrCodeConf {
	
	public List<EmptyData> readXML(File codeMapFile) throws Exception {
		List<EmptyData> list = new ArrayList<EmptyData>();
		SAXBuilder builder = new SAXBuilder();    
		Document doc = builder.build(codeMapFile);    
		Element root = doc.getRootElement();    
		validateListCode(root);//验证分类节点划定的错误码范围是否有重叠、越界的问题
		//保存基准值，下个一个item的值应在该值基础上 + 1；
		int baseValue=getInt(root.getAttributeValue("start"))-1;
		@SuppressWarnings("unchecked")
		List<Element> allChildren = root.getChildren();    
		for(int i=0;i<allChildren.size();i++) {
			Element e = allChildren.get(i);
			String nodeName = e.getName();
			if("item".equals(nodeName)){
				addItem(e,baseValue,root,list);
				baseValue = getInt(list.get(list.size()-1).getValue());
			}else{
				validateStart(baseValue,e);
				addListData(e,list);
				baseValue=getInt(e.getAttributeValue("end"));
			}
		}   
		return list;
	}

	private void validateStart(int baseValue, Element e) throws Exception {
		if(getInt(e.getAttributeValue("start"))<=baseValue){
			throw new Exception("节点["+e.getAttributeValue("id")+"]start属性的值小于前一个item的值");
		}
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
	
	private void addListData(Element e, List<EmptyData> list) throws Exception {
		ListData listData = new ListData();
		listData.setId(e.getAttributeValue("id"));
		listData.setStart(e.getAttributeValue("start"));
		listData.setEnd(e.getAttributeValue("end"));
		listData.setLabel(e.getAttributeValue("label"));
		
		List<EmptyData> listItem = new ArrayList<EmptyData>();
		listData.setList(listItem);
		@SuppressWarnings("unchecked")
		List<Element> allChildren =e.getChildren();
		int baseValue = getInt(e.getAttributeValue("start"))-1;
		for(int i=0;i<allChildren.size();i++) {
			Element subE = (Element)allChildren.get(i);
			String nodeName = subE.getName();
			if("item".equals(nodeName)){
				addItem(subE,baseValue,e,listItem);
				baseValue = getInt(listItem.get(listItem.size()-1).getValue());
			}else{
				validateStart(baseValue,subE);
				addListData(subE, listItem);
				baseValue=getInt(subE.getAttributeValue("end"));
			}
		}
		list.add(listData);
	}

	private void addItem(Element e, int baseValue,Element parentElement,List<EmptyData> list) throws Exception {
		Item item = new Item();
		item.setId(e.getAttributeValue("id"));
		item.setMsg(e.getAttributeValue("msg"));
		item.setValue(getValue(e,baseValue,parentElement,list));
		list.add(item);
	}
	
	private String getValue(Element selfElement,int baseValue,Element parentElement,List<EmptyData> list) throws Exception {
		String start = parentElement.getAttributeValue("start");
		String end = parentElement.getAttributeValue("end");
		String attributeValue=selfElement.getAttributeValue("value");
		if(attributeValue==null || "".equals(attributeValue)){
			attributeValue = String.valueOf(baseValue+1);
		}
		Log.debug("节点["+selfElement.getAttributeValue("id")+"]的值["+attributeValue+"]");
		if(getInt(attributeValue)<getInt(start) || getInt(attributeValue)>getInt(end) ){
			throw new Exception("id为["+selfElement.getAttributeValue("id")+"]的错误码编号值["+attributeValue+"]越父节点["+parentElement.getAttributeValue("id")+"]的界限");
		}
		return attributeValue;
	}
	private int getInt(String value){
		return Integer.parseInt(value);
	}
}
