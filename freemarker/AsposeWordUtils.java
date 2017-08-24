package com.fiberhome.ms.cus.dayreport.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
/*import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;*/

import com.aspose.words.BreakType;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.Paragraph;
import com.aspose.words.SaveFormat;

/**
 * Word处理工具
 * 
 * @author yangfaqiang
 * 
 */
public class AsposeWordUtils {

	private static final Logger logger = Logger
			.getLogger(AsposeWordUtils.class);

	/**
	 * WORD转PDF
	 * 
	 * @param docxContent
	 * @return
	 */
	public static byte[] convertWord2Pdf(byte[] docxContent) {
		return convertWord2Other(docxContent, SaveFormat.PDF);
	}

	/**
	 * WORD转PDF
	 * 
	 * @param docxContent
	 * @return
	 */
	public static void saveWord2Pdf(byte[] docxContent, String pdfPath) {
		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(docxContent);
			Document doc = new Document(bis);
			doc.save(pdfPath, SaveFormat.PDF);
		} catch (Exception e) {
			logger.error("调用ASPOSEWORD保存WORD为PDF失败。", e);
		} finally {
			try {
				if (null != bis) {
					bis.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
	}

	/**
	 * 存WORD
	 * 
	 * @param docxContent
	 * @return
	 */
	public static void saveWord(byte[] docxContent, String docPath) {
		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(docxContent);
			Document doc = new Document(bis);
			doc.save(docPath, SaveFormat.DOCX);
		} catch (Exception e) {
			logger.error("调用ASPOSEWORD保存WORD。", e);
		} finally {
			try {
				if (null != bis) {
					bis.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
	}

	/**
	 * WORD转XML
	 * 
	 * @param docxContent
	 * @return
	 */
	public static String convertWord2XML(byte[] docxContent) {
		String xmlContent = "";
		ByteArrayInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
			bis = new ByteArrayInputStream(docxContent);
			bos = new ByteArrayOutputStream();
			Document document = new Document(bis);
			document.save(bos, SaveFormat.WORD_ML);
			xmlContent = bos.toString("UTF-8");
		} catch (Exception e) {
			logger.error("调用ASPOSEWORD转换WORD为XML失败。", e);
		} finally {
			try {
				if (null != bis)
					bis.close();
				if (null != bos) {
					bos.flush();
					bos.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return xmlContent;
	}

	/**
	 * WORD转指定格式
	 * 
	 * @param docxContent
	 * @return
	 */
	private static byte[] convertWord2Other(byte[] docxContent, int saveFormart) {
		byte[] targetFileContent = null;
		ByteArrayInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
			bis = new ByteArrayInputStream(docxContent);
			bos = new ByteArrayOutputStream();
			Document document = new Document(bis);
			document.save(bos, saveFormart);
			targetFileContent = bos.toByteArray();
		} catch (Exception e) {
			logger.error("调用ASPOSEWORD转换WORD失败。", e);
		} finally {
			try {
				if (null != bis) {
					bis.close();
				}
				if (null != bos) {
					bos.flush();
					bos.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return targetFileContent;
	}
	
/*	*//**
	 * 合并多个pdf文件
	 * @param paths 待合并的pdf文件路径
	 * @param destinationFilePath 最终合并好的pdf文件的输出路径
	 *//*
	public static void mergerPdfDocument(List<InputStream> inputStreams,OutputStream destStream) {
		PDFMergerUtility mergePdf = new PDFMergerUtility();
		mergePdf.addSources(inputStreams);
		mergePdf.setDestinationStream(destStream);
		try {
			mergePdf.mergeDocuments();
		} catch (COSVisitorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.print("pdf文件合并成功");
	}*/
	
	
	/**
	 * 合并多个Word
	 * @param docxs
	 * @param needCatalog
	 * @return
	 */
	public static byte[] mergeWordDocument(List<byte[]> docxs, boolean needCatalog) {
		if(docxs == null || docxs.size() == 0)
			return null;
		
		ByteArrayInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
			bis = new ByteArrayInputStream(docxs.get(0));
			Document destDoc = new Document(bis);
			for (int i = 1; i < docxs.size(); i++) {
				ByteArrayInputStream bisTemp = null;
				try{
					bisTemp = new ByteArrayInputStream(docxs.get(i));
					Document srcDoc = new Document(bisTemp);
					destDoc.appendDocument(srcDoc, ImportFormatMode.KEEP_SOURCE_FORMATTING);
				}finally{
					bisTemp.close();
				}
			}
			if(needCatalog){
				DocumentBuilder builder = new DocumentBuilder(destDoc);
				destDoc.getFirstSection().getBody() .prependChild(new Paragraph(destDoc));
				builder.moveToDocumentStart();
				builder.getParagraphFormat().setAlignment(1);
				builder.getFont().setSize(24);
				builder.writeln("目录");
				builder.insertTableOfContents(" \"1-3\" ");
				builder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);
				destDoc.updateFields();
			}
			bos = new ByteArrayOutputStream();
			destDoc.save(bos, SaveFormat.DOC);
		} catch (Exception e) {
			logger.error("调用ASPOSEWORD合并文件失败。", e);
		}finally{
			try {
				if(null != bos){
					bos.flush();
					bos.close();
				}
				if(null != bis){
					bis.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return bos.toByteArray();
	}
}
