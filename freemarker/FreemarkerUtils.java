package com.fiberhome.ms.cus.dayreport.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 * @description 用于动态生成word文档
 */
@Component
public class FreemarkerUtils {

	static Logger logger = Logger.getLogger(FreemarkerUtils.class);

	private Configuration configuration = null;


	/**
	 * 构造函数
	 */
	public FreemarkerUtils() {
		configuration = new Configuration();
		configuration.setDefaultEncoding("utf-8");
		configuration.setClassicCompatible(true);
	}

	/**
	 * 
	 * @param fileName
	 *            ftl文件
	 * @param listDataMap
	 *            填充数据
	 * @return docx 文件字节流
	 * @throws IOException
	 */
	public byte[] createDoc(String fileName, Map<String, Object> contentMap ) throws IOException {
		String path = FreemarkerUtils.class.getClassLoader().getResource("config/"+fileName).getPath();
		System.out.println("++++"+path);
		File file = new File(path);
//		Resource filePath = resourceLoader.getResource(path);
//		byte[] fileContent = FileUtils.readFileToByteArray(filePath.getFile());
		byte[] fileContent = FileUtils.readFileToByteArray(file);
		Set<String> keySet = contentMap.keySet();
		String ftlStr = this.changeDocXml2FtlTemplate(fileContent,keySet);
		Template t = new Template("name", new StringReader(ftlStr), configuration); // 获取模板文件
		t.setEncoding("utf-8");
		ByteArrayOutputStream bos = null;
		OutputStreamWriter bw = null;
		try {
			bos = new ByteArrayOutputStream();
			bw = new OutputStreamWriter(bos, "utf-8");
			t.process(contentMap, bw);
			return bos.toByteArray();
//			return fileContent;
		} catch (TemplateException e) {
			logger.error("ftl文档转换为doc字节流出错", e);
		} finally {
			if (null != bw) {
				bw.flush();
				bw.close();
			}
			if (null != bos) {
				bos.flush();
				bos.close();
			}
		}

		return null;
	}

	/**
	 * 将xml内容转换成ftl模板
	 * 
	 * @param docxContent
	 *            Word的内容
	 * @param tabName
	 *            表名
	 * @return
	 * @throws IOException
	 */
	private String changeDocXml2FtlTemplate(byte[] docxContent,Set<String> set) throws IOException {
		String docXmlString = AsposeWordUtils.convertWord2XML(docxContent);
		Pattern pattern = Pattern.compile("<w:tr.*?</w:tr>");
		Matcher matcher = pattern.matcher(docXmlString);
		List<String> find = new ArrayList<String>();
		while (matcher.find()) {
			find.add(matcher.group());
		}
		//表格佔位符
		if (find.size() > 0) {
			for (int j = 0; j < find.size(); j++) {
				String strResult = null;
				String tableName = "";
				for (String item : set) {
					tableName = item;
					Pattern p = Pattern.compile("<w:t>\\$\\{" + tableName +"\\." + "(.)*</w:t>");
					Matcher m = p.matcher(find.get(j));
					if (m.find()) {
						strResult = "<#list " + tableName + " as " + tableName + ">" + "\n";
						strResult = strResult + find.get(j) + "\n";
						strResult = strResult + "</#list>" + "\n";
						docXmlString = docXmlString.replace(find.get(j), strResult);
					}
				}
			}
		}
		//查找圖片
		Pattern pattern2 = Pattern.compile("<w:binData[\\s\\S]*?</w:binData>");
		Matcher matcher2 = pattern2.matcher(docXmlString);
		List<String> imageList = new ArrayList<>();
		while (matcher2.find()) {
			imageList.add(matcher2.group());
		}
		if (imageList.size()>0) {
			String[] positionArray = FileName.IMAGE_POSITION_ARRAY;
			for (int i = 0; i < imageList.size(); i++) {
				Pattern pattern3 = Pattern.compile(">[\\s\\S]*?</w:binData>");
				Matcher matcher3 = pattern3.matcher(imageList.get(i));
				if (matcher3.find()) {
					String temp = ">" + positionArray[i] + "</w:binData>";
					docXmlString = docXmlString.replace(matcher3.group(), temp);
				}
			}
		}
		return docXmlString;
	}

}
