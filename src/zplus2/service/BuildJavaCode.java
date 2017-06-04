package zplus2.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Template;
import zplus2.Log;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BuildJavaCode {
	private static String javaSrcDir = "C:/work/java_workspace/eclipse_workspace/buildErrCode/conf/";
	private static String packageName = "com.zjz";
	private static String className = "CodeMsg";
	//private static String codeMapFile = javaSrcDir+"codeMap.xml";
	
	/**
	 * @param args
	 * args[0] templateFilePath
	 * args[1] javaSrcDir
	 * args[2] packageName
	 * args[3] className
	 */
	public static void build(File codeMapFile) {
		try {
			Log.debug("开始装载"+codeMapFile.getPath());
			List list = new ReadErrCodeConf().readXML(codeMapFile);
			Log.debug("装载完成"+list.toString());
			javaSrcDir = codeMapFile.getParent();
			javaSrcDir = javaSrcDir.replace("\\", "/")+"/";
			String codeMapFileName = codeMapFile.getName();
			className = codeMapFileName.substring(0,codeMapFileName.indexOf("."));
			Log.debug("javaSrcDir:"+javaSrcDir);
			Log.debug("className"+className);
			//在插件所在路径创建模板并返回文件名和路径
			File templateFile = Helper.createTemplate();
			Log.debug("templateFile:"+templateFile.getPath());
			buildJavaFromTemplate(templateFile,list);
			buildTxtFromList(list);
		} catch (Exception e) {
			Log.error("文件构建失败！"+e.getMessage());
			//System.out.println("-->文件构建失败！"+e.getMessage());
		}
		
	}
	private static void buildTxtFromList(List list) {
		String txtFileName = javaSrcDir + getPackageDir(packageName)+File.separator+className+".properties";
		OutputStreamWriter txtFile=null;
		try {
			File file = new File(txtFileName);
			file.setWritable(true); //设置文件为可修改
			txtFile = new OutputStreamWriter (new FileOutputStream(file),"UTF-8");
	
			for (Object item : list) {
				if(item instanceof Item){
					writeItem(txtFile,(Item)item);
				}else{
					writeListData(txtFile,(ListData)item);
				}
			}
			txtFile.flush();
			file.setReadOnly();//设置为只读
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				txtFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private static void writeListData(OutputStreamWriter txtFile, ListData listData) throws IOException {
		for (Object item : listData.getList()) {
			if(item instanceof Item){
				writeItem(txtFile,(Item)item);
			}else{
				writeListData(txtFile,(ListData)item);
			}
		}
		
	}
	private static void writeItem(OutputStreamWriter txtFile, Item item) throws IOException {
		txtFile.write(item.getValue()+"="+item.getMsg()+System.getProperty("line.separator"));
	}
	
	private static void buildJavaFromTemplate(File templateFile,List list) {
		Map root = new HashMap();
		root.put("packageName", packageName);
		root.put("className", className);
		root.put("list", list);
		try {
			Log.debug("templateFile.getName():"+templateFile.getName());
			Log.debug("templateFile.getParent():"+templateFile.getParent());
			Template template = FreeMarkerUtil.getTemplate(templateFile.getName(),templateFile.getParent());
			String javaFileName = javaSrcDir + getPackageDir(packageName)+"/"+className+".java";
			File javaFile = new File(javaFileName);
			if(!javaFile.getParentFile().exists()){
				javaFile.getParentFile().mkdirs();
			}
			javaFile.setWritable(true); //设置文件为可修改
			
			Writer writer = new OutputStreamWriter (new FileOutputStream(javaFile),"UTF-8");
			
			template.process(root, writer);
			writer.flush();
			javaFile.setReadOnly();//设置文件为只读
			writer.close();
			String separator = System.getProperty("line.separator");
			Log.debug("-->文件构建成功！"+separator+"-->java文件在["+javaSrcDir+"]目录中."+separator+"-->请刷新相应文件夹");
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("-->文件构建失败！"+e.getMessage());
		}
		
	}
	private static String getPackageDir(String packageName) {
		return packageName.replace('.', '/');
	}
}
