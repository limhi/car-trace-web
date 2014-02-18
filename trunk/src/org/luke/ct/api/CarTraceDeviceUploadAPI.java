package org.luke.ct.api;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.luke.ct.dao.CarPhonePushNotificationService;
import org.luke.ct.dao.CarPhonePushNotificationServiceImpl;
import org.luke.ct.dao.CarPhoneRelationService;
import org.luke.ct.dao.CarPhoneRelationServiceImpl;
import org.luke.ct.dao.CarRegService;
import org.luke.ct.dao.CarRegServiceImpl;
import org.luke.ct.dao.DeviceUploadService;
import org.luke.ct.dao.DeviceUploadServiceImpl;
import org.luke.ct.dao.PhoneCarPushNotificationService;
import org.luke.ct.dao.PhoneCarPushNotificationServiceImpl;
import org.luke.ct.dao.PhoneRegService;
import org.luke.ct.dao.PhoneRegServiceImpl;
import org.luke.ct.dao.PushNotificationMessageService;
import org.luke.ct.dao.PushNotificationMessageServiceImpl;
import org.luke.ct.model.CarPhoneRelation;
import org.luke.ct.model.CarReg;
import org.luke.ct.model.DeviceUpload;
import org.luke.ct.model.PhoneReg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(name = "ctdu", version = "v1")
public class CarTraceDeviceUploadAPI {
  private static CarRegService cr_service = new CarRegServiceImpl();
  private static PhoneRegService pr_service = new PhoneRegServiceImpl();
  private static CarPhoneRelationService cpr_service = new CarPhoneRelationServiceImpl();
  private static CarPhonePushNotificationService cppn_service = new CarPhonePushNotificationServiceImpl();
  private static PhoneCarPushNotificationService pcpn_service = new PhoneCarPushNotificationServiceImpl();
  private static PushNotificationMessageService pnm_service = new PushNotificationMessageServiceImpl();
  private static DeviceUploadService du_service = new DeviceUploadServiceImpl();
  private static final Logger log = Logger.getLogger(CarTraceDeviceUploadAPI.class.getName());

  @ApiMethod(name = "du.listall", httpMethod = HttpMethod.GET)
  public List<DeviceUpload> getDeviceUploadList() {
    return du_service.getPaginationData(null, "addTime desc").getResultList();
  }

  @ApiMethod(name = "du.list", httpMethod = HttpMethod.POST)
  public JSONObject postDeviceUploadList(JSONObject json) {
    JSONObject retJson = null;

    // 檢查push notification 是否有 phoneID 和 carID
    if (null == json || null == json.getString("phoneID") || null == json.getString("carID"))
      throw new Error("提供的訊息內容字串格式不正確");

    String phoneID = json.getString("phoneID");
    String carID = json.getString("carID");
    // String uploadID = json.getString("uploadID");
    String lastTime = json.getString("lastTime");
    if (StringUtils.isBlank(phoneID) || StringUtils.isBlank(carID))
      throw new Error("提供的訊息內容中，phoneID 或 carID字串錯誤 !");

    if (StringUtils.isBlank(lastTime))
      log.info("提供的訊息內容中，lastTime字串錯誤 !, 將提供全部資料");
    log.info("get phoneID = " + phoneID);
    log.info("get carID = " + carID);
    log.info("get lastTime = " + lastTime);

    // 檢查phoneID是否合法
    PhoneReg pr = pr_service.getDataByID(phoneID);
    if (null == pr)
      throw new Error("該phoneID尚未註冊");

    // 檢查carID是否合法
    CarReg cr = cr_service.getDataByID(carID);
    if (null == cr)
      throw new Error("該carID尚未註冊");

    // 找出和該phoneID連結的carID list
    String filter = String.format("phoneID=='%s' && carID=='%s'", phoneID, carID);
    List<CarPhoneRelation> cpr_list = cpr_service.getPaginationData(filter).getResultList();
    if (null != cpr_list && cpr_list.size() > 1) {
      throw new Error("連結資料異常:carID:" + carID + ", phoneID=" + phoneID);
    }
    if (null == cpr_list || cpr_list.size() == 0)
      throw new Error("該phoneID與carID並未配對");

    // 是否有指定某時段之後的資料?
    if (StringUtils.isBlank(lastTime))
      filter = String.format("devID=='%s'", carID);
    else
      filter = String.format("devID=='%s' && addTime>'%s'", carID, lastTime);

    List<DeviceUpload> du_list = du_service.getPaginationData(filter, "addTime asc").getResultList();
    retJson = new JSONObject();
    JSONArray array = new JSONArray();
    for (DeviceUpload du : du_list) {
      JSONObject duJson = new JSONObject();
      duJson.put("addTime", du.getAddTime());
      duJson.put("mySerial", du.getMySerial());
      duJson.put("blob_key", du.getBlob_key());
      duJson.put("serving_url", du.getServing_url());
      array.add(duJson);
    }
    retJson.put("items", array);

    return retJson;
  }
}
