package com.fiberhome.smartas.fitec.ui;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;


import com.fiberhome.smartas.core.annotation.Operation;
import com.fiberhome.smartas.upload.ui.BaseUpLoadResource;

@Path("/fitec/certificateupload")
public class CertificateUpload extends BaseUpLoadResource{
	
	
	/*上传文件入口*/
	@POST
	@Path("/upload/{consumer}")
	@Consumes("multipart/form-data")
	@Operation(code = Operation.FILE_UPLOAD,desc = Operation.FILE_UPLOAD_DESC)
	public Response oaFileUpload(@PathParam("consumer") String consumer){
		Response response = this.doConsume(consumer);
		return response;
		
	}
}
