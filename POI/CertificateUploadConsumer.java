package com.fiberhome.smartas.fitec.ui;

import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

import com.fiberhome.smartas.fitec.Certificate;
import com.fiberhome.smartas.fitec.inner.CommonTools;
import com.fiberhome.smartas.fitec.service.CertificateService;
import com.fiberhome.smartas.upload.MultipartFormData;
import com.fiberhome.smartas.upload.UpLoadStatus;
import com.fiberhome.smartas.upload.impl.BaseUpLoadConsumer;

@SuppressWarnings("rawtypes")
@Component
public class CertificateUploadConsumer extends BaseUpLoadConsumer implements ServletContextAware, InitializingBean {

	@Override
	public String getType() {
		return "certificateConsumer";
	}

	@Autowired
	private CommonTools commonTools;
	
	@Autowired
	private CertificateService service;
	
	@Override
	protected UpLoadStatus doUpload(MultipartFormData data) throws IOException {
		List<MultipartFile> files = data.getFiles();
		String tagretUserId = data.getParameters().get("tagretUserId").get(0);
		String targetSource = data.getParameters().get("targetSource").get(0);
		//String fileName = files.get(0).getOriginalFilename();
		HSSFWorkbook wookbook = new HSSFWorkbook(files.get(0).getInputStream());
		HSSFSheet sheet = wookbook.getSheetAt(0);
		int firstRowNum = 1;
		int lastRowNum = sheet.getLastRowNum();
		Certificate certificate = null;
		for (int i = firstRowNum; i <= lastRowNum; i++) {
			HSSFRow row = sheet.getRow(i);
			certificate = new Certificate();
			String certificateTypeDesp = row.getCell(0).getStringCellValue().trim();
			String certificateType = commonTools.getCodeBygCode("CERTIFICATE_TYPE", certificateTypeDesp);
			certificate.setCertificateType(certificateType);
			certificate.setName(row.getCell(1).getStringCellValue().trim());
			String genderDesp = row.getCell(2).getStringCellValue().trim();
			String gender = commonTools.getCodeBygCode("GENDER", genderDesp);
			certificate.setSex(gender);
			
			certificate.setCountry(row.getCell(3).getStringCellValue().trim());
			certificate.setProvince(row.getCell(4).getStringCellValue().trim());
			certificate.setCity(row.getCell(5).getStringCellValue().trim());
			
			String cerTypeDesp = row.getCell(6).getStringCellValue().trim();
			String cerType = commonTools.getCodeBygCode("CER_TYPE", cerTypeDesp);
			certificate.setCerType(cerType);
			certificate.setCerNumber(row.getCell(7).getStringCellValue().trim());
			certificate.setTitle(row.getCell(8).getStringCellValue().trim());
			certificate.setCompanyname(row.getCell(9).getStringCellValue().trim());
			certificate.setMobile(row.getCell(10).getStringCellValue().trim());
			certificate.setEmail(row.getCell(11).getStringCellValue().trim());
			certificate.setTargetUserId(Long.parseLong(tagretUserId));
			certificate.setTargetSource(targetSource);
			certificate.setHasPhoto("0");
			certificate.setStatus("01");
			certificate.setStage("01");
			service.save(certificate);
		}
		wookbook.close();
		UpLoadStatus us = new UpLoadStatus();
		us.setSucess(true);
		return us;
	}

	
	
}

