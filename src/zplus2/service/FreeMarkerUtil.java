package zplus2.service;

import java.io.File;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FreeMarkerUtil {
	public static Template getTemplate(String name,String templateDir) throws Exception {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
		try {
			cfg.setDirectoryForTemplateLoading(new File(templateDir));
			Template template = cfg.getTemplate(name,"utf-8");
			return template;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	

	
	
/*	public static Writer getWriter(String name){
		
		Writer writer = null;
		try {
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer;
	}*/
}
