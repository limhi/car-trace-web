package org.luke.ct.api.upload;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.luke.ct.core.CTCommon;
import org.luke.ct.dao.CarRegService;
import org.luke.ct.dao.CarRegServiceImpl;
import org.luke.ct.dao.DeviceUploadService;
import org.luke.ct.dao.DeviceUploadServiceImpl;
import org.luke.ct.dao.PhoneRegService;
import org.luke.ct.dao.PhoneRegServiceImpl;
import org.luke.ct.model.CarReg;
import org.luke.ct.model.DeviceUpload;
import org.luke.ct.model.PhoneReg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

public class ImageUploadServlet extends HttpServlet {
  private static final long serialVersionUID = -1170774733534547201L;
  private static CarRegService cr_service = new CarRegServiceImpl();
  private static PhoneRegService pr_service = new PhoneRegServiceImpl();
  private static DeviceUploadService du_service = new DeviceUploadServiceImpl();
  private static final Logger log = Logger.getLogger(ImageUploadServlet.class.getName());
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    String devID = req.getParameter("devID");
    if (StringUtils.isBlank(devID)) {
      sendError(res, "未提供devID");
      return;
    }

    String mySerialString = req.getParameter("mySerial");
    Integer mySerial = 0;
    if (StringUtils.isBlank(mySerialString)) {
      sendError(res, "未提供mySerial");
      return;
    }
    try {
      mySerial = Integer.parseInt(mySerialString);
    } catch (NumberFormatException e) {
      sendError(res, "mySerial必須是數字");
      return;
    }

    // 檢查是否為合法的carID
    CarReg cr = cr_service.getDataByID(devID);
    if (null == cr) {
      // 檢查是否為合法的phoneID
      PhoneReg pr = pr_service.getDataByID(devID);
      if (null == pr) {
        sendError(res, "提供的ID尚未註冊, devID = " + devID);
        return;
      }
    }

    List<BlobKey> blobs = blobstoreService.getUploads(req).get("myFile");
    BlobKey blobKey = blobs.get(0);
    if (null == blobKey) {
      sendError(res, "無法正常儲存上傳的檔案");
      return;
    }

    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions servingOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);

    String servingUrl = imagesService.getServingUrl(servingOptions);

    res.setStatus(HttpServletResponse.SC_OK);
    res.setContentType("application/json");

    // 二種方式都能存取上傳的圖檔
    JSONObject json = new JSONObject();
    json.put("serving_url", servingUrl);
    json.put("blob_key", blobKey.getKeyString());
    json.put("direct_url", "/serve?blob_key=" + blobKey.getKeyString());

    // 儲存在db的動作

    DeviceUpload du = new DeviceUpload();
    du.setDevID(devID);
    du.setMySerial(mySerial);
    du.setServing_url(servingUrl);
    du.setBlob_key(blobKey.getKeyString());
    du.setAddTime(CTCommon.getNowTime());
    du_service.add(du);
    log.info("新增一筆設備註冊記錄：" + JSON.toJSONString(du));

    PrintWriter out = res.getWriter();
    out.print(json.toString());
    out.flush();
    out.close();
  }

  private void sendError(HttpServletResponse res, String errorMessage) throws IOException {
    JSONObject json = new JSONObject();
    json.put("errorMessage", errorMessage);

    res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    res.setContentType("application/json");
    PrintWriter out = res.getWriter();
    out.print(json.toString());
    out.flush();
    out.close();
  }
}
