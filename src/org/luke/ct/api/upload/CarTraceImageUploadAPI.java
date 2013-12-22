package org.luke.ct.api.upload;

import java.util.logging.Logger;

import org.luke.ct.dao.CarRegService;
import org.luke.ct.dao.CarRegServiceImpl;
import org.luke.ct.dao.PhoneRegService;
import org.luke.ct.dao.PhoneRegServiceImpl;
import org.luke.ct.model.CarReg;
import org.luke.ct.model.PhoneReg;

import com.alibaba.fastjson.JSONObject;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@Api(name = "ctimageupload", version = "v1")
public class CarTraceImageUploadAPI {
  private static CarRegService cr_service = new CarRegServiceImpl();
  private static PhoneRegService pr_service = new PhoneRegServiceImpl();
  private static final Logger log = Logger.getLogger(CarTraceImageUploadAPI.class.getName());

  @ApiMethod(name = "image_upload_url", httpMethod = HttpMethod.POST)
  public JSONObject postCTUploadURL(@Named("devID") String devID) {
    JSONObject retJson = null;
    // 檢查是否為合法的carID
    CarReg cr = cr_service.getDataByID(devID);
    if (null == cr) {
      // 檢查是否為合法的phoneID
      PhoneReg pr = pr_service.getDataByID(devID);
      if (null == pr)
        throw new Error("提供的ID尚未註冊, devID = " + devID);
    }

    retJson = new JSONObject();
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadURL = blobstoreService.createUploadUrl("/image_upload");
    retJson.put("image_upload_url", uploadURL);

    return retJson;
  }
}
